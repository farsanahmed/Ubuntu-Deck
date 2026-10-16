package com.ubuntudeck.ubuntudeck.terminal

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ubuntudeck.ubuntudeck.R
import com.ubuntudeck.ubuntudeck.utils.ProotLauncher
import com.ubuntudeck.ubuntudeck.utils.ProotLauncher.ProotRunResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Field
import java.util.Arrays
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Terminal view - PTY-based terminal connected to proot.
 * If PTY setup fails, falls back to [STUB] state.
 *
 * Implementation uses:
 * - TermuxPTY to create a master/slave PTY pair
 * - ProotLauncher to build the proot command and environment
 * - Separate threads to bridge between the PTY slave and the proot process streams
 * - TermuxEmulatorView for the terminal UI (master side of PTY)
 *
 * Note: This implementation is UNTESTED ON DEVICE and may require adjustments.
 */
@Composable
fun TerminalView(prootLauncher: ProotLauncher) {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isInitializing by remember { mutableStateOf(true) }
    var ptyMasterFD by remember { mutableStateOf<Int?>(null) }
    var ptySlaveFD by remember { mutableStateOf<Int?>(null) }

    // Fallback stub UI if PTY setup fails or we are still initializing
    if (isInitializing || errorMessage != null || ptyMasterFD == null || ptySlaveFD == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val message = if (isInitializing) {
                "Initializing terminal..."
            } else if (errorMessage != null) {
                "PTY setup failed: $errorMessage\nFalling back to stub."
            } else {
                "[STUB] Terminal view - PTY wiring to proot pending device test.\n" +
                        "proot binary found at ${prootLauncher.getProotPath() ?: "unknown"}"
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Attempt PTY setup in background
        if (isInitializing) {
            isInitializing = false
            launchPTY(context, prootLauncher) { result ->
                when (result) {
                    is PTYSetupSuccess -> {
                        ptyMasterFD = result.masterFD
                        ptySlaveFD = result.slaveFD
                    }
                    is PTYSetupFailure -> {
                        errorMessage = result.error
                    }
                }
            }
        }
        return
    }

    // PTY setup succeeded - show the real terminal view
    val emulatorViewState = remember { EmulatorViewState() }
    DisposableEffect(ptyMasterFD) {
        // Start the emulator view with the master FD
        emulatorViewState.start(ptyMasterFD!!)
        // Clean up when the view is disposed
        onDispose {
            emulatorViewState.stop()
            // Close the PTY file descriptors (best effort)
            try {
                libc.close(ptyMasterFD!!)
                libc.close(ptySlaveFD!!)
            } catch (e: Exception) {
                Timber.e(e, "Error closing PTY FDs")
            }
        }
    }

    AndroidEmulatorView(
        state = emulatorViewState,
        modifier = Modifier.fillMaxSize()
    )
}

/**
 * Launches the PTY setup in a coroutine.
 * Returns the master and slave file descriptors or an error.
 */
private suspend fun launchPTY(
    context: Context,
    prootLauncher: ProotLauncher,
    onResult: (PTYSetupResult) -> Unit
) = launch(Dispatchers.Default) {
    try {
        // 1. Create the PTY pair using TermuxPTY
        val pty = com.termux.terminal.pty.TermuxPTY()
        pty.setup()

        // 2. Get the master and slave file descriptors using reflection
        val masterFD = getFDValue(pty, "mMasterFileDescriptor")
        val slaveFD = getFDValue(pty, "mSlaveFileDescriptor")

        if (masterFD == -1 || slaveFD == -1) {
            throw IllegalStateException("Invalid PTY file descriptors")
        }

        // 3. Build the proot command and environment for an interactive shell
        val rootfsDir = prootLauncher.context.getFileStreamPath("rootfs").absolutePath
        val command = prootLauncher.buildProotCommand(rootfsDir, "/")
        val environment = prootLauncher.buildProotEnvironment()

        // 4. Start the proot process with PIPE for stdin/stdout/stderr
        val pb = java.lang.ProcessBuilder(*command.toTypedArray())
        pb.environment().putAll(environment)
        pb.redirectInput(java.lang.ProcessBuilder.Redirect.PIPE)
        pb.redirectOutput(java.lang.ProcessBuilder.Redirect.PIPE)
        pb.redirectError(java.lang.ProcessBuilder.Redirect.PIPE)
        val process = pb.start()

        // 5. Get the process streams
        val processInput = process.inputStream   // stdout of the process
        val processError = process.errorStream   // stderr of the process
        val processOutput = process.outputStream // stdin of the process

        // 6. Create FileInputStream and FileOutputStream for the slave FD using reflection
        val slaveInput = createFileInputStream(slaveFD)
        val slaveOutput = createFileOutputStream(slaveFD)

        // 7. Set up threading to bridge the streams
        val executor = Executors.newFixedThreadPool(3)
        executor.execute { bridgeStream(slaveInput, processOutput) } // slave -> process stdin
        executor.execute { bridgeStream(processInput, slaveOutput) } // process stdout -> slave
        executor.execute { bridgeStream(processError, slaveOutput) } // process stderr -> slave

        // 8. Wait for the process to finish (optional, we can also detach)
        process.waitFor()
        executor.shutdown()

        onResult(PTYSetupSuccess(masterFD, slaveFD))
    } catch (e: Exception) {
        Timber.e(e, "PTY setup failed")
        onResult(PTYSetupFailure(e.localizedMessage ?: "Unknown error"))
    }
}

/**
 * Extracts the integer value of a FileDescriptor field using reflection.
 */
@SuppressLint("DiscouragedPrivateApi", "PrivateApi")
private fun getFDValue(pty: Any, fieldName: String): Int {
    val field = pty.java.class.getDeclaredField(fieldName)
    field.isAccessible = true
    val fdObj = field.get(pty) as FileDescriptor
    val valueField = FileDescriptor::class.java.getDeclaredField("value")
    valueField.isAccessible = true
    return valueField.getInt(fdObj)
}

/**
 * Creates a FileInputStream from a raw file descriptor using reflection.
 */
@Throws(IOException::class)
private fun createFileInputStream(fd: Int): FileInputStream {
    val fdObj = FileDescriptor()
    val valueField = FileDescriptor::class.java.getDeclaredField("value")
    valueField.isAccessible = true
    valueField.setInt(fdObj, fd)
    return FileInputStream(fdObj)
}

/**
 * Creates a FileOutputStream from a raw file descriptor using reflection.
 */
@Throws(IOException::class)
private fun createFileOutputStream(fd: Int): FileOutputStream {
    val fdObj = FileDescriptor()
    val valueField = FileDescriptor::class.java.getDeclaredField("value")
    valueField.isAccessible = true
    valueField.setInt(fdObj, fd)
    return FileOutputStream(fdObj)
}

/**
 * Bridges data from an input stream to an output stream.
 */
private fun bridgeStream(input: InputStream, output: OutputStream) {
    val buffer = ByteArray(8192)
    var bytesRead: Int
    try {
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    } catch (e: IOException) {
        // Stream closed or error, exit gracefully
    } finally {
        try {
            input.close()
        } catch (ignored: Exception) {}
        try {
            output.close()
        } catch (ignored: Exception) {}
    }
}

/**
 * Represents the state of the PTY setup.
 */
sealed interface PTYSetupResult
data class PTYSetupSuccess(val masterFD: Int, val slaveFD: Int) : PTYSetupResult
data class PTYSetupFailure(val error: String) : PTYSetupResult

/**
 * Simple state holder for the emulator view.
 */
class EmulatorViewState {
    private var emulatorView: com.termux.terminal.emulator.TermuxEmulatorView? = null
    fun start(masterFD: Int) {
        emulatorView = com.termux.terminal.emulator.TermuxEmulatorView().apply {
            // The TermuxEmulatorView expects a PTY master file descriptor
            // We set it via reflection (this is hacky and may break)
            try {
                val field = java.class.getDeclaredField("mPTY")
                field.isAccessible = true
                field.set(this, masterFD)
            } catch (e: Exception) {
                Timber.e(e, "Failed to set PTY on TermuxEmulatorView")
            }
        }
    }
    fun stop() {
        emulatorView?.onDetachedFromWindow()
        emulatorView = null
    }
}

/**
 * Android wrapper for the TermuxEmulatorView.
 */
@Composable
fun AndroidEmulatorView(
    state: EmulatorViewState,
    modifier: Modifier = Modifier
) = AndroidView(
    factory = { context ->
        state.emulatorView ?: com.termux.terminal.emulator.TermuxEmulatorView(context).apply {
            // Initialize with default settings
            setFontSize(14)
            setTheme(com.termux.terminal.emulator.TermuxEmulatorView.TermuxTheme.DARK)
            setBackgroundColor(0xff000000.toInt()) // Black background
            setForegroundColor(0xffffffff.toInt()) // White foreground
        }
    },
    update = { view ->
        // Update the view if needed (e.g., font size, theme)
    },
    modifier = modifier
)

/**
 * libc.close for closing file descriptors.
 */
private external fun libc_close(fd: Int): Int
private val libc.close ::= ::libc_close

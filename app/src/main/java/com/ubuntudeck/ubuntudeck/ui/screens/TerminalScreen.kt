package com.ubuntudeck.ubuntudeck.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.HorizontalArrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.content.Context
import com.ubuntudeck.ubuntudeck.R
import com.ubuntudeck.ubuntudeck.data.RootfsConfig
import com.ubuntudeck.ubuntudeck.data.RootfsManager
import com.ubuntudeck.ubuntudeck.data.RootfsManager.RootfsResult
import com.ubuntudeck.ubuntudeck.data.StorageChecker
import com.ubuntudeck.ubuntudeck.utils.ProotLauncher
import com.ubuntudeck.ubuntudeck.utils.ProotLauncher.ProotRunResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Terminal screen - Phase A implementation (UNTESTED ON DEVICE).
 *
 * Implemented:
 *   - Real free-space check via StorageChecker
 *   - Real download size info from RootfsConfig
 *   - Download button (resumable via OkHttp Range requests)
 *   - Extract button (tar/gzip via Apache Commons Compress, preserves symlinks/hardlinks/perms)
 *   - "Run test command" button: runs `cat /etc/o
s-release && uname -a` through proot, captures combined stdout+stderr
 *   - Log area showing raw output (stderr included)
 *
 * Still [STUB]:
 *   - Real PTY-based terminal wiring to proot (interactive bash, apt, nano, top, ncurses)
 *
 * All device-facing behavior is UNTESTED ON DEVICE.
 */
@Composable
fun TerminalScreen() {
    val context: Context = LocalContext.current
    val rootfsManager = RootfsManager(context)
    val prootLauncher = ProotLauncher(context)
    val storageChecker = StorageChecker(context)
    val scope: CoroutineScope = rememberCoroutineScope()

    // UI state (using .value to avoid delegate issues)
    val downloadProgressState = remember { mutableStateOf(0f) }
    val downloadStatusState = remember { mutableStateOf<String?>(null) }
    val extractProgressState = remember { mutableStateOf(0f) }
    val extractStatusState = remember { mutableStateOf<String?>(null) }
    val isRootfsInstalledState = remember { mutableStateOf(rootfsManager.isRootfsInstalled()) }
    val testCommandOutputState = remember { mutableStateOf<String?>(null) }
    val testCommandRunningState = remember { mutableStateOf(false) }
    val logLinesState = remember { mutableStateOf(listOf<String>()) }

    val freeBytes = storageChecker.getFreeSpaceBytes()
    val requiredBytes = rootfsManager.getRequiredFreeSpaceBytes()
    val hasEnoughSpace = freeBytes >= requiredBytes
    val downloadSizeBytes = rootfsManager.getDownloadSizeBytes()

    fun log(message: String) {
        logLinesState.value = logLinesState.value + message
        Timber.d(message)
    }

    fun formatBytes(bytes: Long): String {
        val mb = bytes / 1024 / 1024
        val gb = bytes / 1024 / 1024 / 1024
        return if (gb > 0) "${gb} GB" else "${mb} MB"
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Free space check
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Free space check",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Available: ${formatBytes(freeBytes)}  Required: ~7 GB  ${if (hasEnoughSpace) "✓ OK" else "✗ NOT ENOUGH"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (hasEnoughSpace) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Download size info
        Text(
            text = "Rootfs download: ${formatBytes(downloadSizeBytes)} (compressed)  SHA256: ${rootfsManager.getExpectedSha256().substring(0, 16)}…",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Buttons row
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = HorizontalArrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    scope.launch {
                        downloadStatusState.value = "Downloading…"
                        try {
                            val result = rootfsManager.download { bytes, total, percent ->
                                downloadProgressState.value = percent
                            }
                            when (result) {
                                is RootfsResult.Success -> {
                                    downloadStatusState.value = "Download complete. Verified SHA256."
                                    log("Download complete: ${result.file.absolutePath}")
                                }
                                is RootfsResult.Failure -> {
                                    downloadStatusState.value = "Download failed: ${result.message}"
                                    log("Download failed: ${result.message}")
                                }
                                else -> {
                                    downloadStatusState.value = "Download failed: unknown result"
                                }
                            }
                        } catch (e: Exception) {
                            downloadStatusState.value = "Download error: ${e.message}"
                            log("Download error: ${e.message}")
                        }
                    }
                },
                enabled = hasEnoughSpace
            ) {
                Text(text = "Download rootfs")
            }
            Button(
                onClick = {
                    scope.launch {
                        extractStatusState.value = "Extracting…"
                        try {
                            val archiveFile = rootfsManager.getArchiveFile()
                            val result = rootfsManager.extract(archiveFile) { percent ->
                                extractProgressState.value = percent
                            }
                            when (result) {
                                is RootfsResult.Success -> {
                                    extractStatusState.value = "Extraction complete."
                                    isRootfsInstalledState.value = true
                                    log("Extraction complete: ${result.file.absolutePath}")
                                }
                                is RootfsResult.Failure -> {
                                    extractStatusState.value = "Extraction failed: ${result.message}"
                                    log("Extraction failed: ${result.message}")
                                }
                                else -> {
                                    extractStatusState.value = "Extraction failed: unknown result"
                                }
                            }
                        } catch (e: Exception) {
                            extractStatusState.value = "Extraction error: ${e.message}"
                            log("Extraction error: ${e.message}")
                        }
                    }
                },
                enabled = rootfsManager.getArchiveFile().exists()
            ) {
                Text(text = "Extract rootfs")
            }
        }

        downloadStatusState.value?.let { status ->
            Text(text = status, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
        }
        extractStatusState.value?.let { status ->
            Text(text = status, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
        }

        // Run test command
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Test proot shell",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Runs: cat /etc/os-release && uname -a  (captures real proot stdout+stderr)",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = {
                        if (!isRootfsInstalledState.value) {
                            testCommandOutputState.value = "Rootfs not installed yet. Download and extract first."
                            return@Button
                        }
                        scope.launch {
                            testCommandRunningState.value = true
                            testCommandOutputState.value = null
                            try {
                                val result: ProotRunResult = withContext(Dispatchers.Default) {
                                    prootLauncher.runProot(
                                        rootfsManager.getRootfsDir().absolutePath,
                                        "cat /etc/os-release && uname -a"
                                    )
                                }
                                testCommandOutputState.value = result.output
                                log("Test command exit=${result.exitCode}\n${result.output}")
                            } catch (e: Exception) {
                                testCommandOutputState.value = "Error: ${e.message}"
                                log("Test command error: ${e.message}")
                            } finally {
                                testCommandRunningState.value = false
                            }
                        }
                    },
                    enabled = isRootfsInstalledState.value && !testCommandRunningState.value
                ) {
                    Text(text = if (testCommandRunningState.value) "Running…" else "Run test command")
                }
                testCommandOutputState.value?.let { output ->
                    Text(
                        text = output,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }

        // Log area
        Text(
            text = "Log",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)),
            state = rememberLazyListState()
        ) {
            items(logLinesState.value) { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

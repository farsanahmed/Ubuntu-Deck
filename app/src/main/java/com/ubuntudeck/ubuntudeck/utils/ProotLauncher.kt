package com.ubuntudeck.ubuntudeck.utils

import android.content.Context
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Launches proot from nativeLibraryDir with proper environment, binds, and tmp dir.
 * proot is shipped in jniLibs/arm64-v8a/proot (GPLv2, checksum verified) and extracted
 * to nativeLibraryDir at install time via useLegacyPackaging = true + extractNativeLibs=true.
 */
class ProotLauncher(private val context: Context) {
    private val prootBinaryName = "proot"

    fun getProotPath(): String? {
        val libDir = context.applicationInfo.nativeLibraryDir
        val prootFile = File(libDir, prootBinaryName)
        return if (prootFile.exists()) prootFile.absolutePath else null
    }

    fun isProotAvailable(): Boolean {
        val path = getProotPath()
        return path != null && File(path).exists()
    }

    /**
     * Build the proot command line for launching an Ubuntu shell.
     * Sets PROOT_TMP_DIR to a writable app dir (not data exec path).
     * Binds /dev, /proc, /sys, shared storage.
     */
    fun buildProotCommand(rootfsDir: String, workingDir: String = "/"): List<String> {
        val prootPath = getProotPath() ?: throw IllegalStateException("proot binary not found")

        val tmpDir = File(context.cacheDir, "proot_tmp")
        tmpDir.mkdirs()

        val binds = mutableListOf<String>()
        binds.add("/dev:/dev")
        binds.add("/proc:/proc")
        binds.add("/sys:/sys")

        val appInternal = context.filesDir.absolutePath
        binds.add("$appInternal:$appInternal")

        val extDirs = context.getExternalFilesDirs(null)
        for (i in extDirs.indices) {
            val dir = extDirs[i]
            if (dir != null) {
                val abs = dir.absolutePath
                if (abs != appInternal) {
                    binds.add("$abs:$abs")
                }
            }
        }

        val storageRoots = listOf("/storage", "/sdcard")
        for (root in storageRoots) {
            if (File(root).exists()) {
                binds.add("$root:$root")
            }
        }

        val args = mutableListOf<String>()
        args.add(prootPath)
        args.add("-r")
        args.add(rootfsDir)
        args.add("-0") // run as root (fake uid 0)
        args.add("-k") // dummy sysctl
        args.add("-b")
        args.add(binds.joinToString(","))
        args.add("-w")
        args.add(workingDir)
        args.add("/bin/bash")
        args.add("-l")

        return args
    }

    /**
     * Build environment for proot process.
     * PROOT_TMP_DIR -> writable app cache dir (not data exec path).
     */
    fun buildProotEnvironment(): Map<String, String> {
        val tmpDir = File(context.cacheDir, "proot_tmp")
        tmpDir.mkdirs()

        return mapOf(
            "PROOT_TMP_DIR" to tmpDir.absolutePath,
            "HOME" to "/root",
            "PATH" to "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
            "TERM" to "xterm-256color",
            "LANG" to "C.UTF-8"
        )
    }

    /**
     * Launch proot with the given command and capture both stdout and stderr.
     * Returns the exit code and combined output (so real proot errors are visible).
     */
    data class ProotRunResult(
        val exitCode: Int,
        val output: String  // combined stdout+stderr
    )

    fun runProot(rootfsDir: String, command: String, workingDir: String = "/"): ProotRunResult {
        val cmd = buildProotCommand(rootfsDir, workingDir)
        // Append the command to run instead of an interactive shell
        val fullCmd = cmd.toMutableList()
        // Replace "/bin/bash -l" with "/bin/bash -c \"<command>\"" if we want a one-shot command
        // For now we keep the interactive shell; caller decides.
        Timber.d("ProotLauncher: running $fullCmd")

        val pb = java.lang.ProcessBuilder(*fullCmd.toTypedArray())
        pb.environment().putAll(buildProotEnvironment())
        // Redirect error stream so stderr is captured in the same output
        pb.redirectErrorStream(true)

        return try {
            val process = pb.start()
            val output = StringBuilder()
            // Read process output (stdout + merged stderr) line by line
            process.inputStream.bufferedReader().use { reader ->
                var line: String?
                line = reader.readLine()
                while (line != null) {
                    output.append(line).append("\n")
                    line = reader.readLine()
                }
            }
            val exitCode = process.waitFor()
            ProotRunResult(exitCode, output.toString())
        } catch (e: Exception) {
            Timber.e(e, "Failed to run proot")
            ProotRunResult(-1, "Failed to run proot: ${e.message}")
        }
    }

    fun logProotInfo() {
        Timber.d("ProotLauncher: libDir=${context.applicationInfo.nativeLibraryDir}")
        Timber.d("ProotLauncher: proot available=${isProotAvailable()}")
        val path = getProotPath()
        if (path != null) {
            Timber.d("ProotLauncher: proot path=$path exists=${File(path).exists()}")
        }
    }
}

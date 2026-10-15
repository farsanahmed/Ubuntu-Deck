package com.ubuntudeck.ubuntudeck.utils

import android.content.Context
import timber.log.Timber
import java.io.File

/**
 * Proot launcher for running Ubuntu shell.
 * Requires proot binary and loader in nativeLibraryDir.
 * Must set PROOT_LOADER and PROOT_TMP_DIR to writable app directories.
 */
class ProotLauncher(private val context: Context) {
    companion object {
        const val PROOT_BIN = "proot"
        const val PROOT_LOADER = "proot-loader" // may be same as proot or separate
    }

    fun getNativeLibraryDir(): File = File(context.applicationInfo.nativeLibraryDir)

    fun getProotPath(): File = File(getNativeLibraryDir(), PROOT_BIN)

    fun getLoaderPath(): File = File(getNativeLibraryDir(), "loader") // [STUB] - verify actual name

    fun isProotAvailable(): Boolean {
        val available = getProotPath().exists() && getProotPath().canExecute()
        Timber.d("ProotLauncher.isProotAvailable() = $available [STUB check - binary may not be present]")
        return available
    }

    fun getProotTmpDir(): File {
        val dir = File(context.cacheDir, "proot_tmp")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun buildProotCommand(rootfsDir: File, command: Array<String>): Array<String> {
        // [STUB] - Command construction will be implemented once proot is available
        val prootPath = getProotPath().absolutePath
        val cmd = mutableListOf<String>()
        cmd.add(prootPath)
        cmd.add("--kill-on-exit")
        cmd.add("-r")
        cmd.add(rootfsDir.absolutePath)
        cmd.add("-b")
        cmd.add("/dev:/dev")
        cmd.add("-b")
        cmd.add("/proc:/proc")
        cmd.add("-b")
        cmd.add("/sys:/sys")
        cmd.add("-b")
        cmd.add("/storage:/storage")
        cmd.addAll(command)
        Timber.d("Proot command built [STUB]: ${cmd.take(5)}...")
        return cmd.toTypedArray()
    }

    fun getEnvironment(): Map<String, String> {
        val env = mutableMapOf<String, String>()
        env["PROOT_TMP_DIR"] = getProotTmpDir().absolutePath
        env["PROOT_LOADER"] = getLoaderPath().absolutePath
        env["HOME"] = "/root"
        env["TERM"] = "xterm-256color"
        env["PATH"] = "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
        return env
    }
}
[took 0.2s]

package com.ubuntudeck.ubuntudeck.data

import android.content.Context
import timber.log.Timber
import java.io.File

/**
 * [STUB] Rootfs manager - not yet implemented.
 * Will handle rootfs download (with resume), extraction, verification, and storage checks.
 * Free space required: ~7GB (not download size).
 * Rootfs source, checksum, and download size must be determined and displayed before download.
 */
class RootfsManager(private val context: Context) {
    fun getRootfsDir(): File = File(context.filesDir, "rootfs")
    
    fun isRootfsInstalled(): Boolean {
        // STUB - not implemented
        Timber.d("RootfsManager.isRootfsInstalled() - STUB, not implemented")
        return false
    }
    
    fun getRequiredFreeSpaceBytes(): Long {
        // STUB - to be updated with actual values
        return 7L * 1024 * 1024 * 1024 // ~7GB as specified
    }
}

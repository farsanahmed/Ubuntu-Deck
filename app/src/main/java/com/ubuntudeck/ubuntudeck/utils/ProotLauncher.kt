package com.ubuntudeck.ubuntudeck.utils

import android.content.Context
import timber.log.Timber

/**
 * [STUB] Proot launcher - not yet implemented.
 * Will handle launching proot with proper environment, loader, and binds.
 * Requires proot binary and loader in nativeLibraryDir.
 */
class ProotLauncher(private val context: Context) {
    fun isProotAvailable(): Boolean {
        // STUB - not implemented
        Timber.d("ProotLauncher.isProotAvailable() - STUB, not implemented")
        return false
    }

    fun getNativeLibraryDir(): String {
        return context.applicationInfo.nativeLibraryDir
    }
}

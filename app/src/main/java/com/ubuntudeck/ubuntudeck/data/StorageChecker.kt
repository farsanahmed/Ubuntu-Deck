package com.ubuntudeck.ubuntudeck.data

import android.content.Context
import android.os.StatFs
import java.io.File

class StorageChecker(private val context: Context) {
    fun hasEnoughFreeSpace(requiredBytes: Long): Boolean {
        val filesDir = context.filesDir
        val stat = StatFs(filesDir.path)
        val availableBytes = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            stat.availableBytes
        } else {
            @Suppress("DEPRECATION")
            stat.blockSizeLong * stat.availableBlocksLong
        }
        return availableBytes >= requiredBytes
    }
    
    fun getFreeSpaceBytes(): Long {
        val filesDir = context.filesDir
        val stat = StatFs(filesDir.path)
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            stat.availableBytes
        } else {
            @Suppress("DEPRECATION")
            stat.blockSizeLong * stat.availableBlocksLong
        }
    }
}

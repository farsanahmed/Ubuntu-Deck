package com.ubuntudeck.ubuntudeck.data

import android.content.Context
import android.os.StatFs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

/**
 * Manages Ubuntu rootfs download, extraction, verification, and storage checks.
 * [STUB] Some functionality requires actual rootfs URL/checksum to be configured.
 */
class RootfsManager(private val context: Context) {
    private val _downloadProgress = MutableStateFlow(DownloadState.Idle)
    val downloadProgress: Flow<DownloadState> = _downloadProgress.asStateFlow()

    data class RootfsConfig(
        val name: String = "jammy-xfce4-arm64",
        val downloadUrl: String? = null, // [STUB] - must be pinned with checksum
        val downloadSizeBytes: Long? = null, // [STUB] - actual size
        val sha256: String? = null // [STUB] - must verify after download
    )

    companion object {
        const val REQUIRED_FREE_SPACE_GB = 7L
        const val REQUIRED_FREE_SPACE_BYTES = REQUIRED_FREE_SPACE_GB * 1024L * 1024L * 1024L
    }

    sealed class DownloadState {
        object Idle : DownloadState()
        data class Downloading(val progressPercent: Int, val bytesDownloaded: Long, val totalBytes: Long?) : DownloadState()
        data class Extracting(val progressPercent: Int) : DownloadState()
        object Completed : DownloadState()
        data class Error(val message: String) : DownloadState()
    }

    fun getRootfsDir(): File = File(context.filesDir, "rootfs")

    fun getRootfsConfig(): RootfsConfig {
        // [STUB] - Rootfs source not yet pinned. Must document URL, size, SHA256 before download
        return RootfsConfig(
            downloadUrl = null,
            downloadSizeBytes = null,
            sha256 = null
        )
    }

    fun isRootfsInstalled(): Boolean {
        val rootfsDir = getRootfsDir()
        val etcOsRelease = File(rootfsDir, "etc/os-release")
        val isInstalled = rootfsDir.exists() && etcOsRelease.exists()
        Timber.d("RootfsManager.isRootfsInstalled() = $isInstalled [STUB check]")
        return isInstalled
    }

    fun getRequiredFreeSpaceBytes(): Long = REQUIRED_FREE_SPACE_BYTES

    fun getAvailableFreeSpaceBytes(): Long {
        val stat = StatFs(context.filesDir.path)
        return stat.availableBytes
    }

    fun hasEnoughFreeSpace(): Boolean {
        return getAvailableFreeSpaceBytes() >= getRequiredFreeSpaceBytes()
    }

    fun getDownloadSizeMB(): Long? {
        return getRootfsConfig().downloadSizeBytes?.div(1024L * 1024L)
    }

    suspend fun startDownload() = withContext(Dispatchers.IO) {
        val config = getRootfsConfig()
        if (config.downloadUrl.isNullOrEmpty()) {
            _downloadProgress.value = DownloadState.Error(
                "Rootfs URL not configured. [STUB] Must pin rootfs source with checksum before download"
            )
            return@withContext
        }
        // [STUB] - Resumable download not implemented yet
        _downloadProgress.value = DownloadState.Error("Download not implemented yet - Phase 3 in progress [STUB]")
    }
}
[took 0.2s]

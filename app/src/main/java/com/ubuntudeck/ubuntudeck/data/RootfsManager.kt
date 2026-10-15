package com.ubuntudeck.ubuntudeck.data

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

object RootfsConfig {
    const val DOWNLOAD_URL = "https://github.com/RandomCoderOrg/ubuntu-on-android/releases/download/v3/udroid-arm64-xfce4-V3MBB3.tar.gz"
    const val SHA256 = "d5290068b99740da5198a355b5152e0198fff126f0865f4c4634e509508a6da8"
    const val EXPECTED_SIZE_BYTES = 2_012_215_629L
    const val REQUIRED_FREE_SPACE_BYTES = 7L * 1024 * 1024 * 1024 // ~7GB
    const val FILE_NAME = "udroid-arm64-xfce4-V3MBB3.tar.gz"
}

class RootfsManager(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun getRootfsDir(): File = File(context.filesDir, "rootfs")

    fun getArchiveFile(): File = File(context.filesDir, RootfsConfig.FILE_NAME)

    fun isRootfsInstalled(): Boolean {
        val dir = getRootfsDir()
        return dir.exists() && dir.listFiles()?.isNotEmpty() == true &&
            File(dir, "bin").exists() && File(dir, "etc").exists() && File(dir, "usr").exists()
    }

    fun getRequiredFreeSpaceBytes(): Long {
        return RootfsConfig.REQUIRED_FREE_SPACE_BYTES
    }

    fun getDownloadSizeBytes(): Long {
        return RootfsConfig.EXPECTED_SIZE_BYTES
    }

    fun getExpectedSha256(): String {
        return RootfsConfig.SHA256
    }

    suspend fun download(
        onProgress: (bytesDownloaded: Long, totalBytes: Long, percent: Float) -> Unit
    ): RootfsResult {
        val archiveFile = getArchiveFile()
        val url = RootfsConfig.DOWNLOAD_URL

        // Check existing partial download for resume
        val existingSize = archiveFile.length()
        var currentOffset = existingSize

        val headRequest = Request.Builder().url(url).head().build()
        val headResponse = client.newCall(headRequest).execute()
        val serverContentLength = headResponse.header("content-length")?.toLongOrNull() ?: -1L
        headResponse.close()

        if (serverContentLength > 0 && existingSize >= serverContentLength) {
            // Already fully downloaded
            Timber.d("Archive already fully downloaded: $archiveFile")
            return if (verifyChecksum(archiveFile)) {
                RootfsResult.Success(archiveFile)
            } else {
                // Bad checksum, re-download from scratch
                archiveFile.delete()
                downloadInternal(url, archiveFile, 0L, serverContentLength, onProgress)
            }
        }

        return downloadInternal(url, archiveFile, currentOffset, serverContentLength, onProgress)
    }

    private suspend fun downloadInternal(
        url: String,
        archiveFile: File,
        resumeOffset: Long,
        serverContentLength: Long,
        onProgress: (bytesDownloaded: Long, totalBytes: Long, percent: Float) -> Unit
    ): RootfsResult {
        val request = Request.Builder()
            .url(url)
            .header("Range", "bytes=$resumeOffset-${serverContentLength - 1}")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful && response.code != 206) {
                Timber.e("Download failed with code ${response.code}")
                return RootfsResult.Failure("Server returned ${response.code}")
            }

            val totalBytes = if (serverContentLength > 0) serverContentLength else resumeOffset + response.body!!.contentLength()
            val body = response.body ?: return RootfsResult.Failure("Empty response body")

            var downloaded = resumeOffset
            archiveFile.parentFile.mkdirs()
            val out = RandomAccessFile(archiveFile, "rw")
            out.seek(resumeOffset)
            val buffer = ByteArray(8192)
            val input = body.byteStream()

            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
                downloaded += read
                if (serverContentLength > 0) {
                    val percent = (downloaded.toFloat() / serverContentLength) * 100f
                    onProgress(downloaded, serverContentLength, percent)
                }
            }

            out.close()
        }

        // Verify checksum after download completes
        if (!verifyChecksum(archiveFile)) {
            return RootfsResult.Failure("Checksum verification failed")
        }

        return RootfsResult.Success(archiveFile)
    }

    suspend fun verifyChecksum(file: File): Boolean {
        return try {
            val actual = sha256(file)
            val expected = RootfsConfig.SHA256
            val ok = actual.equals(expected, ignoreCase = true)
            if (!ok) {
                Timber.e("Checksum mismatch: expected=$expected actual=$actual")
            }
            ok
        } catch (e: IOException) {
            Timber.e(e, "Failed to verify checksum")
            false
        }
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val fis = FileInputStream(file).buffered()
        val buffer = ByteArray(8192)
        var read: Int
        while (fis.read(buffer).also { read = it } != -1) {
            digest.update(buffer, 0, read)
        }
        fis.close()
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    suspend fun extract(archiveFile: File, onProgress: (percent: Float) -> Unit): RootfsResult {
        val destDir = getRootfsDir()
        destDir.mkdirs()

        return try {
            val fis = FileInputStream(archiveFile).buffered()
            val gzip = GzipCompressorInputStream(fis)
            val tar = TarArchiveInputStream(gzip.buffered())
            var entry: TarArchiveEntry?
            var entriesProcessed = 0

            while (tar.nextTarEntry.also { entry = it } != null) {
                entriesProcessed++
                val entryName = entry?.name ?: continue
                val entryFile = File(destDir, entryName)

                if (entry?.isDirectory == true) {
                    entryFile.mkdirs()
                } else {
                    entryFile.parentFile.mkdirs()
                    val out = FileOutputStream(entryFile).buffered()
                    val buf = ByteArray(8192)
                    var n: Int
                    while (tar.read(buf).also { n = it } != -1) {
                        out.write(buf, 0, n)
                    }
                    out.close()
                    // Set executable bit for scripts/binaries
                    entryFile.setExecutable(true, false)
                }



                if (entriesProcessed % 100 == 0) {
                    // Estimate progress (no exact total available without scanning tar first)
                    onProgress(Math.min(99f, (entriesProcessed / 1000f).coerceAtMost(99f)))
                }
            }

            tar.close()
            gzip.close()
            fis.close()
            onProgress(100f)
            RootfsResult.Success(destDir)
        } catch (e: Exception) {
            Timber.e(e, "Extraction failed")
            RootfsResult.Failure("Extraction failed: ${e.message}")
        }
    }

    fun getProotBinaryPath(): String? {
        // proot is in jniLibs/arm64-v8a/proot, extracted to nativeLibraryDir at install
        val libDir = context.applicationInfo.nativeLibraryDir
        val prootFile = File(libDir, "proot")
        return if (prootFile.exists()) prootFile.absolutePath else null
    }
}

sealed class RootfsResult {
    data class Success(val file: File) : RootfsResult()
    data class Failure(val message: String) : RootfsResult()
}

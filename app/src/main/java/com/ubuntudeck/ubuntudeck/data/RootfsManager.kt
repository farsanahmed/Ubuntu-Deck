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
import java.nio.file.Files
import java.nio.file.Paths
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

        val existingSize = archiveFile.length()
        var resumeOffset = existingSize

        val headRequest = Request.Builder().url(url).head().build()
        val headResponse = client.newCall(headRequest).execute()
        val serverContentLength = headResponse.header("content-length")?.toLongOrNull() ?: -1L
        headResponse.close()

        if (serverContentLength > 0 && existingSize >= serverContentLength) {
            Timber.d("Archive already fully downloaded: $archiveFile")
            return if (verifyChecksum(archiveFile)) {
                RootfsResult.Success(archiveFile)
            } else {
                archiveFile.delete()
                downloadInternal(url, archiveFile, 0L, serverContentLength, onProgress)
            }
        }

        return downloadInternal(url, archiveFile, resumeOffset, serverContentLength, onProgress)
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

            val hardlinkTargets = mutableMapOf<String, File>()

            while (tar.nextTarEntry.also { entry = it } != null) {
                val currentEntry = entry ?: continue
                entriesProcessed++
                val entryName = currentEntry.name
                val entryFile = File(destDir, entryName)
                val mode = currentEntry.mode

                when {
                    currentEntry.isDirectory() -> {
                        entryFile.mkdirs()
                        applyPosixPermissions(entryFile, mode)
                    }
                    currentEntry.isSymbolicLink() -> {
                        val target = currentEntry.linkName
                        entryFile.parentFile.mkdirs()
                        entryFile.delete()
                        try {
                            Files.createSymbolicLink(Paths.get(entryFile.absolutePath), Paths.get(target))
                        } catch (e: Exception) {
                            entryFile.writeText(target)
                        }
                        applyPosixPermissions(entryFile, mode)
                    }
                    currentEntry.getLinkFlag() == TarArchiveEntry.LF_LINK -> {
                        val target = hardlinkTargets[currentEntry.linkName]
                        entryFile.parentFile.mkdirs()
                        entryFile.delete()
                        if (target != null && target.exists()) {
                            try {
                                Files.createLink(Paths.get(entryFile.absolutePath), Paths.get(target.absolutePath))
                            } catch (e: Exception) {
                                Timber.w("Hardlink link() failed for $entryName -> $target: ${e.message}. Falling back to copy.")
                                copyFile(target, entryFile)
                            }
                        } else {
                            writeEntryContent(tar, entryFile, mode)
                        }
                    }
                    else -> {
                        entryFile.parentFile.mkdirs()
                        writeEntryContent(tar, entryFile, mode)
                    }
                }

                if (currentEntry.linkName.isNotBlank() && currentEntry.getLinkFlag() != TarArchiveEntry.LF_LINK && currentEntry.getLinkFlag() != TarArchiveEntry.LF_SYMLINK) {
                    hardlinkTargets[currentEntry.linkName] = entryFile
                }

                if (entriesProcessed % 100 == 0) {
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

    private fun writeEntryContent(tar: TarArchiveInputStream, entryFile: File, mode: Int) {
        val out = FileOutputStream(entryFile).buffered()
        val buf = ByteArray(8192)
        var n: Int
        while (tar.read(buf).also { n = it } != -1) {
            out.write(buf, 0, n)
        }
        out.close()
        applyPosixPermissions(entryFile, mode)
    }

    private fun applyPosixPermissions(file: File, mode: Int) {
        try {
            val readable = (mode and 0x124) != 0
            val writable = (mode and 0x092) != 0
            val executable = (mode and 0x049) != 0
            file.setReadable(readable, false)
            file.setWritable(writable, false)
            file.setExecutable(executable, false)
        } catch (e: Exception) {
            Timber.e(e, "Failed to set permissions on ${file.absolutePath}")
        }
    }

    private fun copyFile(source: File, dest: File) {
        FileInputStream(source).use { fis ->
            FileOutputStream(dest).use { fos ->
                fis.copyTo(fos)
            }
        }
        dest.setExecutable(true, false)
    }

    fun getProotBinaryPath(): String? {
        val libDir = context.applicationInfo.nativeLibraryDir
        val prootFile = File(libDir, "libproot.so")
        return if (prootFile.exists()) prootFile.absolutePath else null
    }
}

sealed class RootfsResult {
    data class Success(val file: File) : RootfsResult()
    data class Failure(val message: String) : RootfsResult()
}

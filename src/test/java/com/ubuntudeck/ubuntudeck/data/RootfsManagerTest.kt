package com.ubuntudeck.ubuntudeck.data

import android.content.Context
import android.content.ContextWrapper
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.Arrays

class RootfsManagerTest {
    private lateinit var tempDir: Path
    private lateinit var context: Context
    private lateinit var rootfsManager: RootfsManager

    @Before
    fun setUp() throws Exception {
        tempDir = Files.createTempDirectory("ubuntudeck_test")
        context = fakeContext(tempDir)
        rootfsManager = RootfsManager(context)
    }

    @After
    fun tearDown() throws Exception {
        Files.walk(tempDir)
            .sorted(compareBy { it } .reversed())
            .forEach { path ->
                try { Files.deleteIfExists(path) } catch (e: Exception) {}
            }
    }

    private fun fakeContext(root: Path): Context {
        return object : ContextWrapper(ApplicationProvider.getApplicationContext()) {
            override fun getFilesDir(): File {
                return root.resolve("files").toFile()
            }

            override fun getCacheDir(): File {
                return root.resolve("cache").toFile()
            }

            override fun getExternalFilesDirs(type: String?): Array<out File> {
                // Return an empty array for simplicity
                return emptyArray()
            }
        }
    }

    @Test
    fun `getRequiredFreeSpaceBytes returns expected value`() {
        assertThat(rootfsManager.getRequiredFreeSpaceBytes()).isEqualTo(7L * 1024 * 1024 * 1024L)
    }

    @Test
    fun `getDownloadSizeBytes returns expected value`() {
        assertThat(rootfsManager.getDownloadSizeBytes()).isEqualTo(2_012_215_629L)
    }

    @Test
    fun `getExpectedSha256 returns expected value`() {
        assertThat(rootfsManager.getExpectedSha256()).isEqualTo("d5290068b99740da5198a355b5152e0198fff126f0865f4c4634e509508a6da8")
    }

    @Test
    fun `getRootfsDir returns correct file`() {
        val expected = File(context.filesDir, "rootfs")
        assertThat(rootfsManager.getRootfsDir()).isEqualTo(expected)
    }

    @Test
    fun `getArchiveFile returns correct file`() {
        val expected = File(context.filesDir, "udroid-arm64-xfce4-V3MBB3.tar.gz")
        assertThat(rootfsManager.getArchiveFile()).isEqualTo(expected)
    }

    @Test
    fun `isRootfsInstalled returns false when directory does not exist`() {
        assertThat(rootfsManager.isRootfsInstalled()).isFalse()
    }

    @Test
    fun `isRootfsInstalled returns false when directory exists but empty`() {
        val dir = rootfsManager.getRootfsDir()
        dir.mkdirs()
        assertThat(rootfsManager.isRootfsInstalled()).isFalse()
    }

    @Test
    fun `isRootfsInstalled returns true when directory has required subdirectories`() {
        val dir = rootfsManager.getRootfsDir()
        dir.mkdirs()
        File(dir, "bin").mkdirs()
        File(dir, "etc").mkdirs()
        File(dir, "usr").mkdirs()
        assertThat(rootfsManager.isRootfsInstalled()).isTrue()
    }
}

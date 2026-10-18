package com.droidlab.droidlab.ui.screens

import android.content.Context
import android.os.Build
import android.text.format.Formatter
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droidlab.droidlab.R

@Composable
fun DeviceInfoScreen(context: Context = android.context) {
    val totalMem = getTotalRAM()
    val availMem = getAvailableRAM()
    val storage = getStorageInfo(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Device Information",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                infoRow("Model", "${Build.MANUFACTURER} ${Build.MODEL}")
                infoRow("Manufacturer", Build.MANUFACTURER)
                infoRow("Android Version", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                infoRow("CPU ABI", Build.SUPPORTED_ABIS.joinToString(", "))
                infoRow("Total RAM", formatRam(totalMem))
                infoRow("Available RAM", formatRam(availMem))
                infoRow("Storage", storage)
            }
        }
    }
}

@Composable
private fun infoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}

private fun getTotalRAM(): Long {
    // Returns total RAM in bytes
    val memInfo = java.io.File("/proc/meminfo")
    if (!memInfo.exists()) return 0
    val lines = memInfo.readLines()
    for (line in lines) {
        if (line.startsWith("MemTotal:")) {
            val parts = line.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size >= 2) {
                val kb = parts[1].toLongOrNull() ?: return 0
                return kb * 1024 // Convert to bytes
            }
        }
    }
    return 0
}

private fun getAvailableRAM(): Long {
    val memInfo = java.io.File("/proc/meminfo")
    if (!memInfo.exists()) return 0
    val lines = memInfo.readLines()
    for (line in lines) {
        if (line.startsWith("MemAvailable:")) {
            val parts = line.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size >= 2) {
                val kb = parts[1].toLongOrNull() ?: return 0
                return kb * 1024
            }
        }
    }
    return 0
}

private fun getStorageInfo(context: Context): String {
    val stat = android.os.StatFs(android.os.Environment.getDataDirectory().path)
    val blockSize = stat.blockSizeLong
    val totalBlocks = stat.blockCountLong
    val availableBlocks = stat.availableBlocksLong
    val total = totalBlocks * blockSize
    val avail = availableBlocks * blockSize
    return "${Formatter.formatShortFileSize(context, avail)}/${Formatter.formatShortFileSize(context, total)}"
}

private fun formatRam(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    if (bytes < 1024 * 1024) return "%.2f KB".format(bytes / 1024.0)
    if (bytes < 1024 * 1024 * 1024) return "%.2f MB".format(bytes / (1024.0 * 1024))
    return "%.2f GB".format(bytes / (1024.0 * 1024 * 1024))
}

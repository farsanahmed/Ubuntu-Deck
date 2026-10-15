package com.ubuntudeck.ubuntudeck.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ubuntudeck.ubuntudeck.R

sealed class SettingItem {
    data class Section(val title: String) : SettingItem()
    data class TextItem(
        val title: String,
        val summary: String,
        val onClick: () -> Unit
    ) : SettingItem()
}

/**
 * Settings screen - dark minimal theme.
 * Includes license / NOTICE info, storage, and VNC resolution settings placeholders.
 */
@Composable
fun SettingsScreen() {
    val items = listOf<SettingItem>(
        SettingItem.Section("Session"),
        SettingItem.TextItem(
            title = "Ubuntu rootfs",
            summary = "Download and extract Ubuntu Jammy XFCE4 (~7GB free space required)",
            onClick = {}
        ),
        SettingItem.TextItem(
            title = "VNC / Desktop",
            summary = "Resolution, password, and TigerVNC settings",
            onClick = {}
        ),
        SettingItem.TextItem(
            title = "Foreground service",
            summary = "Keep session alive with persistent notification",
            onClick = {}
        ),
        SettingItem.Section("About"),
        SettingItem.TextItem(
            title = "License",
            summary = "GPLv3 - see LICENSE and NOTICE",
            onClick = {}
        ),
        SettingItem.TextItem(
            title = "Android 12+ warning",
            summary = "Disable phantom process killing via ADB if session stops",
            onClick = {}
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            items(items) { item ->
                when (item) {
                    is SettingItem.Section -> {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 8.dp)
                                .padding(start = 8.dp)
                        )
                    }
                    is SettingItem.TextItem -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { item.onClick() }
                        ) {
                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = item.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(2f),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.ubuntudeck.ubuntudeck.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed class InstallerItem {
    data class Section(val title: String) : InstallerItem()
    data class Package(
        val title: String,
        val summary: String,
        val onInstall: () -> Unit
    ) : InstallerItem()
    data class Preset(
        val title: String,
        val summary: String,
        val onLaunch: () -> Unit
    ) : InstallerItem()
}

/**
 * Installer screen - [STUB].
 *
 * Real implementation (not yet implemented, untested without device):
 * - One-tap installs inside proot rootfs:
 *   * Ollama (curl -fsSL https://ollama.com/install.sh | sh)
 *   * Claude Code (curl -fsSL https://claude.ai/install.sh | bash)
 *     PATH fix: echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.bashrc
 *   * Firefox (apt install firefox)
 * - Launch presets:
 *   * Cursor: ./cursor --no-sandbox --disable-gpu
 *   * Antigravity: env OPENSSL_ia32cap="~0x4000000000000000" /usr/share/antigravity/antigravity --no-sandbox --disable-gpu --disable-software-rasterizer
 * - Error handling, retry, log viewer
 *
 * Until then, this tab shows a [STUB] placeholder.
 */
@Composable
fun InstallerScreen() {
    val items = listOf<InstallerItem>(
        InstallerItem.Section("One-tap installs (inside Ubuntu)"),
        InstallerItem.Package(
            title = "Ollama",
            summary = "curl -fsSL https://ollama.com/install.sh | sh",
            onInstall = {}
        ),
        InstallerItem.Package(
            title = "Claude Code",
            summary = "curl -fsSL https://claude.ai/install.sh \| bash\nPATH fix: ~/.bashrc",
            onInstall = {}
        ),
        InstallerItem.Package(
            title = "Firefox",
            summary = "apt install firefox",
            onInstall = {}
        ),
        InstallerItem.Section("Launch presets"),
        InstallerItem.Preset(
            title = "Cursor",
            summary = "./cursor --no-sandbox --disable-gpu",
            onLaunch = {}
        ),
        InstallerItem.Preset(
            title = "Antigravity",
            summary = "OPENSSL_ia32cap=~0x4000000000000000 --no-sandbox --disable-gpu --disable-software-rasterizer",
            onLaunch = {}
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
                    is InstallerItem.Section -> {
                        androidx.compose.material3.Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 8.dp)
                                .padding(start = 8.dp)
                        )
                    }
                    is InstallerItem.Package -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                androidx.compose.foundation.layout.Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = item.summary,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2
                                    )
                                }
                                Button(onClick = item.onInstall) {
                                    Text(text = "[STUB] Install")
                                }
                            }
                        }
                    }
                    is InstallerItem.Preset -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                androidx.compose.foundation.layout.Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = item.summary,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2
                                    )
                                }
                                Button(onClick = item.onLaunch) {
                                    Text(text = "[STUB] Launch")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

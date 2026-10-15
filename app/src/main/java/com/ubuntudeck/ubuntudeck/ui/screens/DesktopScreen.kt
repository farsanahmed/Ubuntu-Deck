package com.ubuntudeck.ubuntudeck.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ubuntudeck.ubuntudeck.R

/**
 * Desktop screen - [STUB].
 *
 * Real implementation (not yet implemented, untested without device):
 * - TigerVNC server runs inside Ubuntu rootfs on localhost:1 with random password
 * - XFCE4 (startxfce4) launched inside proot session
 * - VNC client view embedded here with:
 *   * touch-as-mouse
 *   * on-screen keyboard
 *   * pinch zoom
 *   * resolution settings
 *
 * Until then, this tab shows a [STUB] placeholder.
 */
@Composable
fun DesktopScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "Desktop",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "[STUB] Desktop view - VNC client + XFCE4 pending device test.\n" +
                    "TigerVNC + XFCE4 will run inside proot rootfs on localhost.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

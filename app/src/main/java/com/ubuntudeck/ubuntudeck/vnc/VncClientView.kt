package com.ubuntudeck.ubuntudeck.vnc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * [STUB] VNC client view - not yet implemented.
 * Will show TigerVNC client connected to localhost VNC server inside Ubuntu.
 * Features: touch-as-mouse, on-screen keyboard, pinch zoom, resolution settings.
 */
@Composable
fun VncClientView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "[STUB] VNC client - TigerVNC integration pending (Phase 4)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Requires VNC server (tigervnc) running inside Ubuntu rootfs",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
[took 0.1s]

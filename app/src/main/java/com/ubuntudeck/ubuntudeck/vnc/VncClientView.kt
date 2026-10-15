package com.ubuntudeck.ubuntudeck.vnc

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
        Text(
            text = "[STUB] VNC client - not implemented",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

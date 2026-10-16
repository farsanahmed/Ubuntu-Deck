package com.ubuntudeck.ubuntudeck.terminal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.ubuntudeck.ubuntudeck.utils.ProotLauncher
import timber.log.Timber

/**
 * Terminal view - [STUB] until a real PTY-based terminal is wired to proot and tested on a device.
 *
 * Plan for real implementation (not yet implemented):
 * - Use Termux terminal-emulator + terminal-view (GPLv3, added via Gradle dependency).
 * - Start proot via ProotLauncher.buildProotCommand(...) into a PTY.
 * - Pipe proot stdout -> TerminalEmulator input, user input -> proot stdin.
 * - Supports bash line editing, apt, nano, top, ncurses (real terminal semantics).
 *
 * Until then, this tab shows a [STUB] placeholder and reports proot availability.
 */
@Composable
fun TerminalView(prootLauncher: ProotLauncher) {
    val prootAvailable = prootLauncher.isProotAvailable()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (prootAvailable) {
            ColumnStub(
                title = "Terminal",
                message = "[STUB] Terminal view - PTY wiring to proot pending device test.\n" +
                        "proot binary found at ${prootLauncher.getProotPath() ?: "unknown"}"
            )
        } else {
            ColumnStub(
                title = "Terminal",
                message = "[STUB] proot binary not available in jniLibs/nativeLibraryDir.\n" +
                        "Add proot binary to app/src/main/jniLibs/arm64-v8a/proot."
            )
        }

        Timber.d("TerminalView rendered, prootAvailable=$prootAvailable")
    }
}

@Composable
private fun ColumnStub(title: String, message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

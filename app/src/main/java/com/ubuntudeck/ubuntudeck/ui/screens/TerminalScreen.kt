package com.ubuntudeck.ubuntudeck.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import com.ubuntudeck.ubuntudeck.terminal.TerminalView
import com.ubuntudeck.ubuntudeck.utils.ProotLauncher

@Composable
fun TerminalScreen() {
    val context: Context = LocalContext.current
    val prootLauncher = ProotLauncher(context)
    TerminalView(prootLauncher = prootLauncher)
}

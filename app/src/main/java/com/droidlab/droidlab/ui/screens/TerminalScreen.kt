package com.droidlab.droidlab.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.droidlab.droidlab.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TerminalScreen(context: Context = android.context) {
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf(listOf<TerminalLine>()) }
    val scope = rememberCoroutineScope()

    // Process command when user presses send
    fun onSend() {
        val cmd = input.trim()
        if (cmd.isEmpty()) return
        // Add command to output
        output = output + TerminalLine.TerminalInput(cmd)
        input = ""
        // Run command in background
        scope.launch {
            val result = runCommand(cmd)
            withContext(Dispatchers.Main) {
                output = output + TerminalLine.TerminalOutput(result)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            itemsIndexed(output) { index, line ->
                when (line) {
                    is TerminalLine.TerminalInput -> {
                        Text(
                            text = "\$ ${line.command}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .anchorScroller(id("line_$index"))
                        )
                    }
                    is TerminalLine.TerminalOutput -> {
                        Text(
                            text = line.output,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .anchorScroller(id("line_$index"))
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                label = { Text("Command") },
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Enter command (e.g., ls, pwd, echo hello)") },
                imeAction = ImeAction.Done,
                onImeActionPerformed = { onSend() },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onSend() },
                enabled = input.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send"
                )
            }
        }
    }
}

sealed class TerminalLine {
    data class TerminalInput(val command: String) : TerminalLine()
    data class TerminalOutput(val output: String) : TerminalLine()
}

private fun runCommand(command: String): String {
    return try {
        val process = ProcessBuilder("/system/bin/sh", "-c", command)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        output.trim()
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

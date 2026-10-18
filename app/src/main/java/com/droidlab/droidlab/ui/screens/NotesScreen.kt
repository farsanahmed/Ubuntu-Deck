package com.droidlab.droidlab.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.droidlab.droidlab.R
import com.droidlab.droidlab.utils.DataStoreUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun NotesScreen(context: Context = android.context) {
    var notes by remember { mutableStateOf(listOf<String>()) }
    var newNote by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Load notes from DataStore
    LaunchedEffect(Unit) {
        context.loadNotes().collectLatest { notesJson ->
            if (notesJson.isNotEmpty()) {
                try {
                    val list = com.google.gson.Gson().fromJson(notesJson, com.google.gson.reflect.TypeToken.getParameterized(List::class.java, String::class.java).type)
                    notes = list ?: emptyList()
                } catch (e: Exception) {
                    notes = emptyList()
                }
            }
        }
    }

    // Save notes when they change
    LaunchedEffect(notes) {
        val json = com.google.gson.Gson().toJson(notes)
        context.saveNotes(json)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Notes",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            label = { Text("New note") },
            value = newNote,
            onValueChange = { newNote = it },
            placeholder = { Text("Enter a new note...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (newNote.isNotBlank()) {
                        notes = notes + newNote.trim()
                        newNote = ""
                    }
                },
                enabled = newNote.isNotBlank()
            ) {
                Text("Add")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your Notes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (notes.isEmpty()) {
            Text(
                text = "No notes yet. Add one above!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(align = androidx.compose.ui.Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(notes) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = note,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

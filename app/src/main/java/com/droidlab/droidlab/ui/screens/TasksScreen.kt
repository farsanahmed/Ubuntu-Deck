package com.droidlab.droidlab.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.droidlab.droidlab.R
import com.droidlab.droidlab.utils.DataStoreUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun TasksScreen(context: Context = android.context) {
    var tasks by remember { mutableStateOf(listOf<TaskItem>()) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDescription by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Load tasks from DataStore
    LaunchedEffect(Unit) {
        context.loadTasks().collectLatest { tasksJson ->
            if (tasksJson.isNotEmpty()) {
                try {
                    val list = com.google.gson.Gson().fromJson(tasksJson, com.google.gson.reflect.TypeToken.getParameterized(List::class.java, TaskItem::class.java).type)
                    tasks = list ?: emptyList()
                } catch (e: Exception) {
                    tasks = emptyList()
                }
            }
        }
    }

    // Save tasks when they change
    LaunchedEffect(tasks) {
        val json = com.google.gson.Gson().toJson(tasks)
        context.saveTasks(json)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tasks",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            label = { Text("Task title") },
            value = newTaskTitle,
            onValueChange = { newTaskTitle = it },
            placeholder = { Text("Enter task title...") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            label = { Text("Task description (optional)") },
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            placeholder = { Text("Enter task description...") },
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
                    if (newTaskTitle.isNotBlank()) {
                        tasks = tasks + TaskItem(
                            id = UUID.randomUUID().toString(),
                            title = newTaskTitle.trim(),
                            description = newTaskDescription.trim(),
                            completed = false,
                            timestamp = System.currentTimeMillis()
                        )
                        newTaskTitle = ""
                        newTaskDescription = ""
                    }
                },
                enabled = newTaskTitle.isNotBlank()
            ) {
                Text("Add Task")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your Tasks",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (tasks.isEmpty()) {
            Text(
                text = "No tasks yet. Add one above!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(align = androidx.compose.ui.Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tasks) { task ->
                    TaskItemView(
                        task = task,
                        onTaskToggle = { updatedTask ->
                            val index = tasks.indexOfFirst { it.id == updatedTask.id }
                            if (index != -1) {
                                tasks = tasks.copyWith(index, updatedTask)
                            }
                        },
                        onTaskDelete = {
                            tasks = tasks.filterNot { it.id == task.id }
                        }
                    )
                }
            }
        }
    }
}

data class TaskItem(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val timestamp: Long
)

@Composable
private fun TaskItemView(
    task: TaskItem,
    onTaskToggle: (TaskItem) -> Unit,
    onTaskDelete: () -> Unit
) {
    val checked by remember { mutableStateOf(task.completed) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    onTaskToggle(task.copy(completed = it))
                },
                colors = CheckboxDefaults.colors(
                    uncheckedColor = MaterialTheme.colorScheme.outline,
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onTaskDelete,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}

package com.droidlab.droidlab.ui.screens

import android.content.Context
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.droidlab.droidlab.R
import com.droidlab.droidlab.ui.theme.DroidLabTheme

@Composable
fun DashboardScreen(context: Context = android.context) {
    val navController = rememberNavController()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Welcome to DroidLab",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your native Android developer toolkit",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Device info preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Device Information",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${Build.MANUFACTURER} ${Build.MODEL}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Quick actions
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickActions) { action ->
                ActionItem(
                    title = action.title,
                    icon = action.icon,
                    onClick = { action.onClick(navController) }
                )
            }
        }
    }
}

private data class QuickAction(
    val title: String,
    val icon: Int,
    val onClick: (NavController) -> Unit
)

private val quickActions: List<QuickAction> = listOf(
    QuickAction(
        title = "JSON Tools",
        icon = R.string.tab_tools, // We'll change this to an icon resource later, but for now we use a placeholder
        onClick = { navController -> navController.navigate("tools") }
    ),
    QuickAction(
        title = "Base64",
        icon = R.string.tab_tools,
        onClick = { navController -> navController.navigate("tools") }
    ),
    QuickAction(
        title = "SHA-256",
        icon = R.string.tab_tools,
        onClick = { navController -> navController.navigate("tools") }
    ),
    QuickAction(
        title = "Unit Converter",
        icon = R.string.tab_tools,
        onClick = { navController -> navController.navigate("tools") }
    ),
    QuickAction(
        title = "Notes",
        icon = R.string.tab_notes,
        onClick = { navController -> navController.navigate("notes") }
    ),
    QuickAction(
        title = "Tasks",
        icon = R.string.tab_tasks,
        onClick = { navController -> navController.navigate("tasks") }
    ),
    QuickAction(
        title = "Terminal",
        icon = R.string.tab_terminal,
        onClick = { navController -> navController.navigate("terminal") }
    ),
    QuickAction(
        title = "Device Info",
        icon = R.string.tab_device_info,
        onClick = { navController -> navController.navigate("device_info") }
    )
)

@Composable
private fun ActionItem(
    title: String,
    icon: Int,
    onClick: (NavController) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* onClick will be called with navController in the items block */ },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info, // Placeholder, we need to set proper icons
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

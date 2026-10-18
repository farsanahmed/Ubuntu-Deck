package com.droidlab.droidlab.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droidlab.droidlab.R

@Composable
fun SettingsScreen(context: Context = android.context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Settings items
        SettingsItem(
            title = "Theme",
            onClick = { /* TODO: Implement theme toggle */ }
        )
        SettingsItem(
            title = "About",
            onClick = { /* TODO: Show about dialog */ }
        )
        SettingsItem(
            title = "Feedback",
            onClick = { /* TODO: Open feedback form */ }
        )
        SettingsItem(
            title = "Rate this app",
            onClick = { /* TODO: Open Play Store */ }
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

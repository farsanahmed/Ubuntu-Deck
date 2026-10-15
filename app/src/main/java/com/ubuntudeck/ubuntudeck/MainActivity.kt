package com.ubuntudeck.ubuntudeck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ubuntudeck.ubuntudeck.ui.screens.DesktopScreen
import com.ubuntudeck.ubuntudeck.ui.screens.InstallerScreen
import com.ubuntudeck.ubuntudeck.ui.screens.SettingsScreen
import com.ubuntudeck.ubuntudeck.ui.screens.TerminalScreen
import com.ubuntudeck.ubuntudeck.ui.theme.UbuntuDeckTheme

sealed class Screen(val route: String, val resourceId: Int, val iconId: Int) {
    object Terminal : Screen("terminal", R.string.tab_terminal, android.R.drawable.ic_menu_edit)
    object Desktop : Screen("desktop", R.string.tab_desktop, android.R.drawable.ic_menu_gallery)
    object Installer : Screen("installer", R.string.tab_installer, android.R.drawable.ic_menu_add)
    object Settings : Screen("settings", R.string.tab_settings, android.R.drawable.ic_menu_preferences)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UbuntuDeckTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Terminal,
        Screen.Desktop,
        Screen.Installer,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.iconId),
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Terminal.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Terminal.route) { TerminalScreen() }
                composable(Screen.Desktop.route) { DesktopScreen() }
                composable(Screen.Installer.route) { InstallerScreen() }
                composable(Screen.Settings.route) { SettingsScreen() }
            }
        }
    }
}

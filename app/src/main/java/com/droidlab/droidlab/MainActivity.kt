package com.droidlab.droidlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.droidlab.droidlab.ui.theme.DroidLabTheme

sealed class Screen(val route: String, val resourceId: Int, val iconId: Int) {
    object Dashboard : Screen("dashboard", R.string.tab_dashboard, android.R.drawable.ic_menu_dashboard)
    object DeviceInfo : Screen("device_info", R.string.tab_device_info, android.R.drawable.ic_menu_info_details)
    object Tools : Screen("tools", R.string.tab_tools, android.R.drawable.ic_menu_manage)
    object Notes : Screen("notes", R.string.tab_notes, android.R.drawable.ic_menu_edit)
    object Tasks : Screen("tasks", R.string.tab_tasks, android.R.drawable.ic_menu_task)
    object Terminal : Screen("terminal", R.string.tab_terminal, android.R.drawable.ic_menu_compass)
    object Settings : Screen("settings", R.string.tab_settings, android.R.drawable.ic_menu_set_as)
}

class MainActivity : ComponentActivity() {
    private val items = listOf(
        Screen.Dashboard,
        Screen.DeviceInfo,
        Screen.Tools,
        Screen.Notes,
        Screen.Tasks,
        Screen.Terminal,
        Screen.Settings
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DroidLabTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
            .fillMaxWidth()
            .wrapContentSize(align = Alignment.Center),
            horizontalArrangement = Arrangement.Center
        ) {
            title()
        }
    )
}

    package com.droidlab.droidlab

    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.activity.enableEdgeToEdge
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.material3.Icon
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.NavigationBar
    import androidx.compose.material3.NavigationBarItem
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Surface
    import androidx.compose.material3.Text
    import androidx.compose.material3.TopAppBar
    import androidx.compose.material3.TopAppBarDefaults
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.res.stringResource
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavDestination.Companion.hierarchy
    import androidx.navigation.NavGraph.Companion.findStartDestination
    import androidx.navigation.compose.NavHost
    import androidx.navigation.compose.composable
    import androidx.navigation.compose.currentBackStackEntryAsState
    import androidx.navigation.compose.rememberNavController
    import com.droidlab.droidlab.ui.theme.DroidLabTheme

    sealed class Screen(val route: String, val resourceId: Int, val iconId: Int) {
        object Dashboard : Screen("dashboard", R.string.tab_dashboard, android.R.drawable.ic_menu_dashboard)
        object DeviceInfo : Screen("device_info", R.string.tab_device_info, android.R.drawable.ic_menu_info_details)
        object Tools : Screen("tools", R.string.tab_tools, android.R.drawable.ic_menu_manage)
        object Notes : Screen("notes", R.string.tab_notes, android.R.drawable.ic_menu_edit)
        object Tasks : Screen("tasks", R.string.tab_tasks, android.R.drawable.ic_menu_task)
        object Terminal : Screen("terminal", R.string.tab_terminal, android.R.drawable.ic_menu_compass)
        object Settings : Screen("settings", R.string.tab_settings, android.R.drawable.ic_menu_set_as)
    }

    class MainActivity : ComponentActivity() {
        private val items = listOf(
            Screen.Dashboard,
            Screen.DeviceInfo,
            Screen.Tools,
            Screen.Notes,
            Screen.Tasks,
            Screen.Terminal,
            Screen.Settings
        )

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                DroidLabTheme {
                    val navController = rememberNavController()
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = { Text(stringResource(R.string.app_name)) }
                            )
                        },
                        bottomBar = {
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
                            startDestination = Screen.Dashboard.route,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable(Screen.Dashboard.route) { DashboardScreen() }
                            composable(Screen.DeviceInfo.route) { DeviceInfoScreen() }
                            composable(Screen.Tools.route) { ToolsScreen() }
                            composable(Screen.Notes.route) { NotesScreen() }
                            composable(Screen.Tasks.route) { TasksScreen() }
                            composable(Screen.Terminal.route) { TerminalScreen() }
                            composable(Screen.Settings.route) { SettingsScreen() }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CenterAlignedTopAppBar(title: @Composable () -> Unit) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(align = Alignment.Center),
                    horizontalArrangement = Arrangement.Center
                ) {
                    title()
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }

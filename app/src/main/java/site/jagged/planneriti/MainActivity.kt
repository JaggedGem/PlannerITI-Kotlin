package site.jagged.planneriti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import site.jagged.planneriti.ui.navigation.AppNavigation
import site.jagged.planneriti.ui.navigation.Screen
import site.jagged.planneriti.ui.theme.PlannerITITheme
import site.jagged.planneriti.ui.theme.Primary
import site.jagged.planneriti.ui.theme.Surface

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlannerITITheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarScreens = listOf(Screen.Schedule, Screen.Grades, Screen.Assignments, Screen.Settings)
    val showBottomBar = bottomBarScreens.any { it.route == currentDestination?.route }

    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Surface) {
                    data class TabItem(val screen: Screen, val icon: androidx.compose.ui.graphics.vector.ImageVector, val labelRes: Int)

                    listOf(
                        TabItem(Screen.Schedule, Icons.Default.CalendarMonth, R.string.tab_schedule),
                        TabItem(Screen.Grades, Icons.Default.School, R.string.tab_grades),
                        TabItem(Screen.Assignments, Icons.Default.Assignment, R.string.tab_assignments),
                        TabItem(Screen.Settings, Icons.Default.Settings, R.string.tab_settings),
                    ).forEach { tab ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == tab.screen.route } == true,
                            onClick = {
                                navController.navigate(tab.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.labelRes)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                indicatorColor = Primary,
                                unselectedIconColor = Color(0xFF8A8A8D),
                                unselectedTextColor = Color(0xFF8A8A8D)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        AppNavigation(navController = navController, modifier = Modifier.padding(padding))
    }
}
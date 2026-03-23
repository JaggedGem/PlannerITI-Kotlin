package site.jagged.planneriti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dagger.hilt.android.AndroidEntryPoint
import site.jagged.planneriti.ui.assignments.AssignmentsScreen
import site.jagged.planneriti.ui.grades.GradesScreen
import site.jagged.planneriti.ui.schedule.ScheduleScreen
import site.jagged.planneriti.ui.settings.SettingsScreen
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
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        bottomBar = {
            NavigationBar(
                containerColor = Surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_schedule)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Primary,
                        unselectedIconColor = Color(0xFF8A8A8D),
                        unselectedTextColor = Color(0xFF8A8A8D)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_grades)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Primary,
                        unselectedIconColor = Color(0xFF8A8A8D),
                        unselectedTextColor = Color(0xFF8A8A8D)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_assignments)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Primary,
                        unselectedIconColor = Color(0xFF8A8A8D),
                        unselectedTextColor = Color(0xFF8A8A8D)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_settings)) },
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
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> ScheduleScreen()
                1 -> GradesScreen()
                2 -> AssignmentsScreen()
                3 -> SettingsScreen()
            }
        }
    }
}
package site.jagged.planneriti.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import site.jagged.planneriti.ui.assignments.AssignmentsScreen
import site.jagged.planneriti.ui.assignments.newassignment.NewAssignmentScreen
import site.jagged.planneriti.ui.grades.GradesScreen
import site.jagged.planneriti.ui.schedule.ScheduleScreen
import site.jagged.planneriti.ui.settings.SettingsScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Schedule.route,
        modifier = modifier
    ) {
        composable(Screen.Schedule.route) { ScheduleScreen() }
        composable(Screen.Grades.route) { GradesScreen() }
        composable(Screen.Assignments.route) {
            AssignmentsScreen(
                onNavigateToNew = { navController.navigate(Screen.NewAssignment.route) },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditAssignment.createRoute(id)) },
                onNavigateToArchive  = { navController.navigate(Screen.Archive.route) }
            )
        }
        composable(Screen.Settings.route) { SettingsScreen() }
        composable(Screen.NewAssignment.route) {
            NewAssignmentScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Archive.route) {
            // placeholder for now
            androidx.compose.foundation.layout.Box {}
        }
        composable(
            route = Screen.EditAssignment.route,
            arguments = listOf(navArgument(Screen.EditAssignment.ARG_ID) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString(Screen.EditAssignment.ARG_ID) ?: return@composable
            // placeholder for now
            androidx.compose.foundation.layout.Box {}
        }
    }
}
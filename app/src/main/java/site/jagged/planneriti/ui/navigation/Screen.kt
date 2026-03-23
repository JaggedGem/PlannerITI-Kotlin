package site.jagged.planneriti.ui.navigation

sealed class Screen(val route: String) {
    object Schedule: Screen("schedule")
    object Grades: Screen("grades")
    object Assignments: Screen("assignments")
    object Settings: Screen("settings")

    // Assignment specific paths
    object NewAssignment: Screen("new_assignment")
    object Archive: Screen("archive")
    object EditAssignment: Screen("edit_assignment/{assignmentId}") {
        fun createRoute(id: String) = "edit_assignment/$id"
        const val ARG_ID = "assignmentId"
    }

    object Auth: Screen("auth")
    object Signup: Screen("signup")
    object ForgotPassword: Screen("forgot_password")
    object PrivacyPolicy: Screen("privacy_policy")

}
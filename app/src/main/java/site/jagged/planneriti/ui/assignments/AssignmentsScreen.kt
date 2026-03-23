package site.jagged.planneriti.ui.assignments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import site.jagged.planneriti.ui.theme.Background

@Composable
fun AssignmentsScreen(
    onNavigateToNew: () -> Unit = {},
    onNavigateToEdit: (String) -> Unit = {},
    onNavigateToArchive: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center
    ) {
        Text("Assignments", color = Color.White)
    }
}
package site.jagged.planneriti.ui.assignments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.composed
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import site.jagged.planneriti.R
import site.jagged.planneriti.ui.assignments.components.AssignmentItemClean
import site.jagged.planneriti.ui.theme.Background
import site.jagged.planneriti.ui.theme.Primary
import site.jagged.planneriti.ui.theme.Surface

@Composable
fun AssignmentsScreen(
    viewModel: AssignmentsViewModel = hiltViewModel(),
    onNavigateToNew: () -> Unit = {},
    onNavigateToEdit: (String) -> Unit = {},
    onNavigateToArchive: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNew,
                containerColor = Primary
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.assignments_title),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onNavigateToArchive) {
                    Icon(
                        imageVector = Icons.Default.Archive,
                        contentDescription = stringResource(R.string.archive_title),
                        tint = Color.White
                    )
                }
            }

            // Tab selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .background(Surface, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                val tabs = listOf(
                    stringResource(R.string.tab_due_date),
                    stringResource(R.string.tab_classes),
                    stringResource(R.string.tab_priority)
                )
                tabs.forEachIndexed { index, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (uiState.selectedTab == index) Primary else Color.Transparent,
                                androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                            )
                            .padding(vertical = 10.dp)
                            .clickableNoRipple { viewModel.selectTab(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (uiState.selectedTab == index) Color.White else Color(0xFF8A8A8D),
                            fontSize = 14.sp,
                            fontWeight = if (uiState.selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                when (uiState.selectedTab) {
                    0 -> DueDateView(uiState = uiState, viewModel = viewModel, onNavigateToEdit = onNavigateToEdit)
                    1 -> ClassesView(uiState = uiState, viewModel = viewModel, onNavigateToEdit = onNavigateToEdit)
                    2 -> PriorityView(uiState = uiState, viewModel = viewModel, onNavigateToEdit = onNavigateToEdit)
                }
            }
        }
    }
}

@Composable
private fun DueDateView(
    uiState: AssignmentsUiState,
    viewModel: AssignmentsViewModel,
    onNavigateToEdit: (String) -> Unit
) {
    if (uiState.activeAssignments.isEmpty()) {
        EmptyState()
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        uiState.groupedByDate.forEach { (date, assignments) ->
            item { DaySectionHeader(date = date, count = assignments.size) }
            items(assignments, key = { it.id }) { assignment ->
                AssignmentItemClean(
                    assignment = assignment,
                    onToggle = { viewModel.toggleAssignment(assignment.id) },
                    onClick = { onNavigateToEdit(assignment.id) }
                )
            }
        }
    }
}

@Composable
private fun ClassesView(
    uiState: AssignmentsUiState,
    viewModel: AssignmentsViewModel,
    onNavigateToEdit: (String) -> Unit
) {
    if (uiState.activeAssignments.isEmpty()) { EmptyState(); return }
    LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
        uiState.groupedByCourse.forEach { (course, assignments) ->
            item {
                Text(
                    text = course,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(assignments, key = { it.id }) { assignment ->
                AssignmentItemClean(
                    assignment = assignment,
                    onToggle = { viewModel.toggleAssignment(assignment.id) },
                    onClick = { onNavigateToEdit(assignment.id) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PriorityView(
    uiState: AssignmentsUiState,
    viewModel: AssignmentsViewModel,
    onNavigateToEdit: (String) -> Unit
) {
    if (uiState.priorityAssignments.isEmpty()) {
        EmptyState(message = "No priority assignments")
        return
    }
    LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(uiState.priorityAssignments, key = { it.id }) { assignment ->
            AssignmentItemClean(
                assignment = assignment,
                onToggle = { viewModel.toggleAssignment(assignment.id) },
                onClick = { onNavigateToEdit(assignment.id) }
            )
        }
    }
}

@Composable
private fun DaySectionHeader(date: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = date, color = Color.White, fontWeight = FontWeight.SemiBold)
        Text(text = "$count", color = Color(0xFF8A8A8D))
    }
}

@Composable
private fun EmptyState(message: String = "No assignments") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color(0xFF8A8A8D))
    }
}

// Helper extension — clickable without ripple effect
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    clickable(
        indication = null,
        interactionSource = interactionSource,
        onClick = onClick
    )
}

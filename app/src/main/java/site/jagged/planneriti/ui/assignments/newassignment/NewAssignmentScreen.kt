package site.jagged.planneriti.ui.assignments.newassignment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import site.jagged.planneriti.domain.model.AssignmentType
import site.jagged.planneriti.ui.theme.Background
import site.jagged.planneriti.ui.theme.Primary
import site.jagged.planneriti.ui.theme.Surface
import site.jagged.planneriti.ui.theme.SurfaceVariant

@Composable
fun NewAssignmentScreen(
    onNavigateBack: () -> Unit,
    viewModel: NewAssignmentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // navigate back when save completes
    LaunchedEffect(state.saveComplete) {
        if (state.saveComplete) onNavigateBack()
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
                }
                Text("New Assignment", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                TextButton(
                    onClick = viewModel::save,
                    enabled = !state.isSaving
                ) {
                    Text("Save", color = Primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::setTitle,
                label = { Text("Title *") },
                isError = state.titleError,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = SurfaceVariant,
                    unfocusedContainerColor = SurfaceVariant,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Color(0xFF3A3A3A),
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = Color(0xFF8A8A8D),
                    errorBorderColor = Color(0xFFFF3B30)
                )
            )

            // Course name
            OutlinedTextField(
                value = state.courseName,
                onValueChange = viewModel::setCourseName,
                label = { Text("Subject") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = SurfaceVariant,
                    unfocusedContainerColor = SurfaceVariant,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Color(0xFF3A3A3A),
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = Color(0xFF8A8A8D)
                )
            )

            // Description
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::setDescription,
                label = { Text("Description (optional)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = SurfaceVariant,
                    unfocusedContainerColor = SurfaceVariant,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Color(0xFF3A3A3A),
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = Color(0xFF8A8A8D)
                )
            )

            // Assignment type
            Text("Type", color = Color(0xFF8A8A8D), fontSize = 14.sp)
            AssignmentTypeSelector(
                selected = state.assignmentType,
                onSelect = viewModel::setType
            )

            // Priority toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Priority", color = Color.White, fontSize = 16.sp)
                Switch(
                    checked = state.isPriority,
                    onCheckedChange = viewModel::setPriority,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Primary)
                )
            }
        }
    }
}

@Composable
private fun AssignmentTypeSelector(
    selected: AssignmentType,
    onSelect: (AssignmentType) -> Unit
) {
    val types = AssignmentType.entries
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        types.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { type ->
                    FilterChip(
                        selected = type == selected,
                        onClick = { onSelect(type) },
                        label = { Text(type.displayKey) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1C1C1E),
                            labelColor = Color(0xFF8A8A8D)
                        )
                    )
                }
            }
        }
    }
}
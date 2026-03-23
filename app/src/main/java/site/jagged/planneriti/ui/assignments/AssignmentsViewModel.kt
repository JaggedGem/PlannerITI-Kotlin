package site.jagged.planneriti.ui.assignments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import site.jagged.planneriti.data.repository.AssignmentRepository
import site.jagged.planneriti.domain.model.Assignment
import site.jagged.planneriti.domain.model.AssignmentType
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AssignmentsViewModel @Inject constructor(
    private val repository: AssignmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssignmentsUiState())
    val uiState: StateFlow<AssignmentsUiState> = _uiState.asStateFlow()

    init {
        loadAssignments()
    }

    private fun loadAssignments() {
        viewModelScope.launch {
            combine(
                repository.activeAssignments,
                repository.overdueAssignments,
                repository.priorityAssignments
            ) { active, overdue, priority ->
                AssignmentsUiState(
                    activeAssignments = active,
                    overdueAssignments = overdue,
                    priorityAssignments = priority,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleAssignment(id: String) {
        viewModelScope.launch { repository.toggle(id) }
    }

    fun deleteAssignment(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    // Test function — adds a fake assignment so you can see the UI
    fun addTestAssignment() {
        viewModelScope.launch {
            repository.add(
                Assignment(
                    id = UUID.randomUUID().toString(),
                    title = "Test Assignment ${(1..100).random()}",
                    description = "This is a test",
                    courseCode = "MATH101",
                    courseName = "Mathematics",
                    dueDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
                    isCompleted = false,
                    isPriority = false,
                    assignmentType = AssignmentType.HOMEWORK
                )
            )
        }
    }
}
package site.jagged.planneriti.ui.assignments.newassignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import site.jagged.planneriti.data.repository.AssignmentRepository
import site.jagged.planneriti.domain.model.Assignment
import site.jagged.planneriti.domain.model.AssignmentType
import java.util.UUID
import javax.inject.Inject

data class NewAssignmentState(
    val title: String = "",
    val description: String = "",
    val courseName: String = "",
    val dueDate: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
    val isPriority: Boolean = false,
    val assignmentType: AssignmentType = AssignmentType.HOMEWORK,
    val isSaving: Boolean = false,
    val saveComplete: Boolean = false,
    val titleError: Boolean = false
)

@HiltViewModel
class NewAssignmentViewModel @Inject constructor(
    private val repository: AssignmentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewAssignmentState())
    val state = _state.asStateFlow()

    fun setTitle(title: String) = _state.update { it.copy(title = title, titleError = false) }
    fun setDescription(desc: String) = _state.update { it.copy(description = desc) }
    fun setCourseName(name: String) = _state.update { it.copy(courseName = name) }
    fun setDueDate(date: Long) = _state.update { it.copy(dueDate = date) }
    fun setPriority(priority: Boolean) = _state.update { it.copy(isPriority = priority) }
    fun setType(type: AssignmentType) = _state.update { it.copy(assignmentType = type) }

    fun save() {
        val state = _state.value
        if (state.title.isBlank()) {
            _state.update { it.copy(titleError = true) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            repository.add(
                Assignment(
                    id = UUID.randomUUID().toString(),
                    title = state.title.trim(),
                    description = state.description.trim(),
                    courseCode = state.courseName.trim(),
                    courseName = state.courseName.trim(),
                    dueDate = state.dueDate,
                    isCompleted = false,
                    isPriority = state.isPriority,
                    assignmentType = state.assignmentType
                )
            )
            _state.update { it.copy(isSaving = false, saveComplete = true) }
        }
    }
}
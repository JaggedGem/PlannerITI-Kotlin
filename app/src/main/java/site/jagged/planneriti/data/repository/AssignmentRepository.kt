package site.jagged.planneriti.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import site.jagged.planneriti.data.local.dao.AssignmentDao
import site.jagged.planneriti.data.local.entity.SubtaskEntity
import site.jagged.planneriti.data.local.mapper.toDomain
import site.jagged.planneriti.data.local.mapper.toEntity
import site.jagged.planneriti.domain.model.Assignment
import site.jagged.planneriti.domain.model.Subtask
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssignmentRepository @Inject constructor(
    private val dao: AssignmentDao
) {
    val activeAssignments: Flow<List<Assignment>> = dao.getActive()
        .map { entities -> entities.map { it.toDomain() } }

    val overdueAssignments: Flow<List<Assignment>> = dao.getOverdue()
        .map { entities -> entities.map { it.toDomain() } }

    val priorityAssignments: Flow<List<Assignment>> = dao.getPriority()
        .map { entities -> entities.map { it.toDomain() } }

    suspend fun getById(id: String): Assignment? = dao.getById(id)?.toDomain()

    suspend fun add(assignment: Assignment) = dao.insert(assignment.toEntity())

    suspend fun update(assignment: Assignment) = dao.update(assignment.toEntity())

    suspend fun delete(id: String) = dao.deleteById(id)

    suspend fun toggle(id: String) = dao.toggleCompletion(id)

    suspend fun addSubtask(assignmentId: String, title: String) {
        dao.insertSubtask(
            SubtaskEntity(
                id = UUID.randomUUID().toString(),
                assignmentId = assignmentId,
                title = title,
                isCompleted = false
            )
        )
    }

    suspend fun deleteSubtask(subtaskId: String) = dao.deleteSubtask(subtaskId)

    suspend fun toggleSubtask(subtaskId: String) = dao.toggleSubtask(subtaskId)
}
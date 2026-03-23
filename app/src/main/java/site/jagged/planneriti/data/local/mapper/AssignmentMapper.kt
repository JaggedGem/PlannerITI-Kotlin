package site.jagged.planneriti.data.local.mapper

import site.jagged.planneriti.data.local.entity.AssignmentEntity
import site.jagged.planneriti.data.local.entity.SubtaskEntity
import site.jagged.planneriti.domain.model.Assignment
import site.jagged.planneriti.domain.model.AssignmentType
import site.jagged.planneriti.domain.model.Subtask

fun AssignmentEntity.toDomain(subtasks: List<SubtaskEntity> = emptyList()): Assignment {
    return Assignment(
        id = id,
        title = title,
        description = description,
        courseCode = courseCode,
        courseName = courseName,
        dueDate = dueDate,
        isCompleted = isCompleted,
        isPriority = isPriority,
        assignmentType = AssignmentType.fromString(assignmentType),
        periodId = periodId,
        subjectId = subjectId,
        isOrphaned = isOrphaned,
        subtasks = subtasks.map { it.toDomain() }
    )
}

fun Assignment.toEntity(): AssignmentEntity {
    return AssignmentEntity(
        id = id,
        title = title,
        description = description,
        courseCode = courseCode,
        courseName = courseName,
        dueDate = dueDate,
        isCompleted = isCompleted,
        isPriority = isPriority,
        assignmentType = assignmentType.displayKey,
        periodId = periodId,
        subjectId = subjectId,
        isOrphaned = isOrphaned
    )
}

fun SubtaskEntity.toDomain(): Subtask = Subtask(
    id = id,
    assignmentId = assignmentId,
    title = title,
    isCompleted = isCompleted
)

fun Subtask.toEntity(): SubtaskEntity = SubtaskEntity(
    id = id,
    assignmentId = assignmentId,
    title = title,
    isCompleted = isCompleted
)
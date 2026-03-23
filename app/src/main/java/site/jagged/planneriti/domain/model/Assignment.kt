package site.jagged.planneriti.domain.model

import android.icu.text.CaseMap

data class Assignment(
    val id: String,
    val title: String,
    val description: String,
    val courseCode: String,
    val courseName: String,
    val dueDate: Long,
    val isCompleted: Boolean,
    val isPriority: Boolean,
    val assignmentType: AssignmentType,
    val periodId: String? = null,
    val subjectId: String? = null,
    val isOrphaned: Boolean = false,
    val subtasks: List<Subtask> = emptyList()
)

data class Subtask(
    val id: String,
    val assignmentId: String,
    val title: String,
    val isCompleted: Boolean
)

enum class AssignmentType(val displayKey: String) {
    HOMEWORK("Homework"),
    TEST("Test"),
    EXAM("Exam"),
    PROJECT("Project"),
    QUIZ("Quiz"),
    LAB("Lab"),
    ESSAY("Essay"),
    PRESENTATION("Presentation"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): AssignmentType {
            return entries.find { it.displayKey == value } ?: OTHER
        }
    }
}
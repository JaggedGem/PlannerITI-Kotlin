package site.jagged.planneriti.ui.assignments

import site.jagged.planneriti.domain.model.Assignment
import site.jagged.planneriti.domain.model.AssignmentType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

data class AssignmentsUiState(
    val activeAssignments: List<Assignment> = emptyList(),
    val overdueAssignments: List<Assignment> = emptyList(),
    val priorityAssignments: List<Assignment> = emptyList(),
    val isLoading: Boolean = true,
    val selectedTab: Int = 0,
    val error: String? = null
) {
    // group active assignments by date — equivalent to your groupAssignmentsByDate()
    val groupedByDate: Map<String, List<Assignment>>
        get() = activeAssignments.groupBy { assignment ->
            val date = Instant.ofEpochMilli(assignment.dueDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            date.toString()
        }

    val groupedByCourse: Map<String, List<Assignment>>
        get() = activeAssignments.groupBy { it.courseName.ifEmpty { it.courseCode } }
}

// Utility extensions
fun Assignment.dueDateFormatted(): String {
    val instant = Instant.ofEpochMilli(dueDate)
    val dateTime = instant.atZone(ZoneId.systemDefault())
    return DateTimeFormatter.ofPattern("h:mm a").format(dateTime)
}

fun Assignment.remainingText(): String {
    val now = System.currentTimeMillis()
    val diff = dueDate - now
    if (diff < 0) return "Overdue"
    val minutes = diff / 60000
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7
    return when {
        minutes < 60 -> "in $minutes min"
        hours < 24 -> "in $hours h"
        days == 1L -> "Due tomorrow"
        days < 7 -> "in $days days"
        else -> "in $weeks weeks"
    }
}

fun AssignmentType.color(): Long = when (this) {
    AssignmentType.HOMEWORK -> 0xFF3478F6
    AssignmentType.TEST -> 0xFFFF9500
    AssignmentType.EXAM -> 0xFFFF3B30
    AssignmentType.PROJECT -> 0xFF5E5CE6
    AssignmentType.QUIZ -> 0xFFFF375F
    AssignmentType.LAB -> 0xFF64D2FF
    AssignmentType.ESSAY -> 0xFF30D158
    AssignmentType.PRESENTATION -> 0xFFFFD60A
    AssignmentType.OTHER -> 0xFF8E8E93
}
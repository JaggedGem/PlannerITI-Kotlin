package site.jagged.planneriti.domain.model

data class Group(
    val id: String,
    val name: String,
    val teacherName: String?
)

data class SchedulePeriod(
    val id: String,
    val startTime: String,
    val endTime: String
)

data class ScheduleItem(
    val period: String,
    val startTime: String,
    val endTime: String,
    val className: String,
    val teacherName: String,
    val roomNumber: String,
    val isEvenWeek: Boolean?,
    val subgroup: String?,
    val isCustom: Boolean = false,
    val color: String? = null,
    val isRecoveryDay: Boolean = false,
    val recoveryReason: String? = null,
    val assignmentCount: Int = 0
)

data class RecoveryDay(
    val date: String,
    val replacedDay: String,
    val reason: String,
    val groupId: String,
    val isActive: Boolean
)

data class UserSettings(
    val language: String = "en",
    val selectedGroupId: String = "",
    val selectedGroupName: String = "P-2422",
    val subgroup: String = "Subgroup 2",
    val scheduleView: String = "day"
)
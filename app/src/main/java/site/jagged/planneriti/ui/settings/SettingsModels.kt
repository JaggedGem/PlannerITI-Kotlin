package site.jagged.planneriti.ui.settings

import java.util.UUID

data class GroupOption(
    val id: String,
    val name: String,
    val teacherName: String?
)

data class CustomPeriodSetting(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val startTime: String,
    val endTime: String,
    val daysOfWeek: List<Int> = emptyList(),
    val colorHex: String = "#2C3DCD",
    val isEnabled: Boolean = true
)

data class NotificationSettings(
    val enabled: Boolean = true,
    val notificationHour: Int = 18,
    val notificationMinute: Int = 0,
    val examReminderDays: Int = 3,
    val testReminderDays: Int = 2,
    val quizReminderDays: Int = 1,
    val projectReminderDays: Int = 4,
    val homeworkReminderDays: Int = 1,
    val otherReminderDays: Int = 1,
    val dailyRemindersForExams: Boolean = true,
    val dailyRemindersForTests: Boolean = true,
    val dailyRemindersForQuizzes: Boolean = true
)

data class UpdateInfo(
    val isAvailable: Boolean,
    val currentVersion: String,
    val latestVersion: String,
    val releaseNotes: String,
    val releaseUrl: String,
    val publishedAt: String
)

data class AccountUiState(
    val email: String? = null,
    val isVerified: Boolean = false,
    val isAuthenticated: Boolean = false
)

data class SettingsUiState(
    val isLoading: Boolean = true,
    val account: AccountUiState = AccountUiState(),
    val language: String = "en",
    val selectedGroupId: String = "",
    val selectedGroupName: String = "P-2422",
    val subgroup: String = "Subgroup 2",
    val scheduleView: String = "day",
    val groups: List<GroupOption> = emptyList(),
    val customPeriods: List<CustomPeriodSetting> = emptyList(),
    val idnp: String? = null,
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val lastScheduleRefreshEpoch: Long? = null,
    val isRefreshingSchedule: Boolean = false,
    val isCheckingForUpdate: Boolean = false,
    val currentVersion: String = "1.0",
    val updateInfo: UpdateInfo? = null,
    val updateMessage: String? = null,
    val error: String? = null
)



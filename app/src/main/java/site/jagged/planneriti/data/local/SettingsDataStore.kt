package site.jagged.planneriti.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import site.jagged.planneriti.domain.model.UserSettings
import site.jagged.planneriti.ui.settings.CustomPeriodSetting
import site.jagged.planneriti.ui.settings.NotificationSettings
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.settingsDataStore by preferencesDataStore(name = "user_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.settingsDataStore
    private val gson = Gson()

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val GROUP_ID = stringPreferencesKey("selected_group_id")
        val GROUP_NAME = stringPreferencesKey("selected_group_name")
        val SUBGROUP = stringPreferencesKey("subgroup")
        val SCHEDULE_VIEW = stringPreferencesKey("schedule_view")
        val IDNP = stringPreferencesKey("idnp")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        val EXAM_REMINDER_DAYS = intPreferencesKey("exam_reminder_days")
        val TEST_REMINDER_DAYS = intPreferencesKey("test_reminder_days")
        val QUIZ_REMINDER_DAYS = intPreferencesKey("quiz_reminder_days")
        val PROJECT_REMINDER_DAYS = intPreferencesKey("project_reminder_days")
        val HOMEWORK_REMINDER_DAYS = intPreferencesKey("homework_reminder_days")
        val OTHER_REMINDER_DAYS = intPreferencesKey("other_reminder_days")
        val DAILY_EXAMS = booleanPreferencesKey("daily_exams")
        val DAILY_TESTS = booleanPreferencesKey("daily_tests")
        val DAILY_QUIZZES = booleanPreferencesKey("daily_quizzes")
        val CUSTOM_PERIODS_JSON = stringPreferencesKey("custom_periods_json")
        val LAST_SCHEDULE_REFRESH = longPreferencesKey("last_schedule_refresh")
        val DISMISSED_UPDATE_VERSION = stringPreferencesKey("dismissed_update_version")
        val DISMISSED_UPDATE_UNTIL = longPreferencesKey("dismissed_update_until")
        val LAST_UPDATE_CHECK = longPreferencesKey("last_update_check")
    }

    val settings: Flow<UserSettings> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            UserSettings(
                language = prefs[Keys.LANGUAGE] ?: "en",
                selectedGroupId = prefs[Keys.GROUP_ID] ?: "",
                selectedGroupName = prefs[Keys.GROUP_NAME] ?: "P-2422",
                subgroup = prefs[Keys.SUBGROUP] ?: "Subgroup 2",
                scheduleView = prefs[Keys.SCHEDULE_VIEW] ?: "day"
            )
        }

    val idnp: Flow<String?> = dataStore.data
        .map { it[Keys.IDNP] }

    val customPeriods: Flow<List<CustomPeriodSetting>> = dataStore.data.map { prefs ->
        val raw = prefs[Keys.CUSTOM_PERIODS_JSON] ?: "[]"
        runCatching {
            val type = object : TypeToken<List<CustomPeriodSetting>>() {}.type
            gson.fromJson<List<CustomPeriodSetting>>(raw, type) ?: emptyList()
        }.getOrDefault(emptyList<CustomPeriodSetting>())
    }

    val notificationSettings: Flow<NotificationSettings> = dataStore.data.map { prefs ->
        NotificationSettings(
            enabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            notificationHour = prefs[Keys.NOTIFICATION_HOUR] ?: 18,
            notificationMinute = prefs[Keys.NOTIFICATION_MINUTE] ?: 0,
            examReminderDays = prefs[Keys.EXAM_REMINDER_DAYS] ?: 3,
            testReminderDays = prefs[Keys.TEST_REMINDER_DAYS] ?: 2,
            quizReminderDays = prefs[Keys.QUIZ_REMINDER_DAYS] ?: 1,
            projectReminderDays = prefs[Keys.PROJECT_REMINDER_DAYS] ?: 4,
            homeworkReminderDays = prefs[Keys.HOMEWORK_REMINDER_DAYS] ?: 1,
            otherReminderDays = prefs[Keys.OTHER_REMINDER_DAYS] ?: 1,
            dailyRemindersForExams = prefs[Keys.DAILY_EXAMS] ?: true,
            dailyRemindersForTests = prefs[Keys.DAILY_TESTS] ?: true,
            dailyRemindersForQuizzes = prefs[Keys.DAILY_QUIZZES] ?: true
        )
    }

    val lastScheduleRefreshEpoch: Flow<Long?> = dataStore.data.map { it[Keys.LAST_SCHEDULE_REFRESH] }

    val dismissedUpdateVersion: Flow<String?> = dataStore.data.map { it[Keys.DISMISSED_UPDATE_VERSION] }
    val dismissedUpdateUntilEpoch: Flow<Long?> = dataStore.data.map { it[Keys.DISMISSED_UPDATE_UNTIL] }
    val lastUpdateCheckEpoch: Flow<Long?> = dataStore.data.map { it[Keys.LAST_UPDATE_CHECK] }

    val language: Flow<String> = dataStore.data
        .map { it[Keys.LANGUAGE] ?: "en" }

    suspend fun setLanguage(language: String) {
        dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    suspend fun setGroup(id: String, name: String) {
        dataStore.edit {
            it[Keys.GROUP_ID] = id
            it[Keys.GROUP_NAME] = name
        }
    }

    suspend fun setSubgroup(subgroup: String) {
        dataStore.edit { it[Keys.SUBGROUP] = subgroup }
    }

    suspend fun setScheduleView(view: String) {
        dataStore.edit { it[Keys.SCHEDULE_VIEW] = view }
    }

    suspend fun saveIdnp(idnp: String) {
        dataStore.edit { it[Keys.IDNP] = idnp }
    }

    suspend fun clearIdnp() {
        dataStore.edit { it.remove(Keys.IDNP) }
    }

    suspend fun setCustomPeriods(periods: List<CustomPeriodSetting>) {
        dataStore.edit { it[Keys.CUSTOM_PERIODS_JSON] = gson.toJson(periods) }
    }

    suspend fun saveNotificationSettings(settings: NotificationSettings) {
        dataStore.edit {
            it[Keys.NOTIFICATIONS_ENABLED] = settings.enabled
            it[Keys.NOTIFICATION_HOUR] = settings.notificationHour
            it[Keys.NOTIFICATION_MINUTE] = settings.notificationMinute
            it[Keys.EXAM_REMINDER_DAYS] = settings.examReminderDays
            it[Keys.TEST_REMINDER_DAYS] = settings.testReminderDays
            it[Keys.QUIZ_REMINDER_DAYS] = settings.quizReminderDays
            it[Keys.PROJECT_REMINDER_DAYS] = settings.projectReminderDays
            it[Keys.HOMEWORK_REMINDER_DAYS] = settings.homeworkReminderDays
            it[Keys.OTHER_REMINDER_DAYS] = settings.otherReminderDays
            it[Keys.DAILY_EXAMS] = settings.dailyRemindersForExams
            it[Keys.DAILY_TESTS] = settings.dailyRemindersForTests
            it[Keys.DAILY_QUIZZES] = settings.dailyRemindersForQuizzes
        }
    }

    suspend fun setLastScheduleRefresh(epochMillis: Long) {
        dataStore.edit { it[Keys.LAST_SCHEDULE_REFRESH] = epochMillis }
    }

    suspend fun clearLastScheduleRefresh() {
        dataStore.edit { it.remove(Keys.LAST_SCHEDULE_REFRESH) }
    }

    suspend fun setUpdateDismissed(version: String, untilEpochMillis: Long) {
        dataStore.edit {
            it[Keys.DISMISSED_UPDATE_VERSION] = version
            it[Keys.DISMISSED_UPDATE_UNTIL] = untilEpochMillis
        }
    }

    suspend fun clearUpdateDismissed() {
        dataStore.edit {
            it.remove(Keys.DISMISSED_UPDATE_VERSION)
            it.remove(Keys.DISMISSED_UPDATE_UNTIL)
        }
    }

    suspend fun setLastUpdateCheck(epochMillis: Long) {
        dataStore.edit { it[Keys.LAST_UPDATE_CHECK] = epochMillis }
    }
}
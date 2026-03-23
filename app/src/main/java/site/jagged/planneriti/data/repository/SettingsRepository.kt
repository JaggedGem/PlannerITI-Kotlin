package site.jagged.planneriti.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import site.jagged.planneriti.data.local.SettingsDataStore
import site.jagged.planneriti.data.remote.api.ScheduleApi
import site.jagged.planneriti.ui.settings.CustomPeriodSetting
import site.jagged.planneriti.ui.settings.GroupOption
import site.jagged.planneriti.ui.settings.NotificationSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val scheduleApi: ScheduleApi
) {
    private var cachedGroups: List<GroupOption> = emptyList()

    val settings = settingsDataStore.settings
    val customPeriods = settingsDataStore.customPeriods
    val idnp = settingsDataStore.idnp
    val notificationSettings = settingsDataStore.notificationSettings
    val lastScheduleRefreshEpoch = settingsDataStore.lastScheduleRefreshEpoch

    suspend fun getGroups(forceRefresh: Boolean = false): List<GroupOption> {
        if (!forceRefresh && cachedGroups.isNotEmpty()) return cachedGroups

        val groups = scheduleApi.getGroups().map {
            GroupOption(
                id = it.id,
                name = it.name,
                teacherName = it.diriginte?.name
            )
        }
        cachedGroups = groups
        return groups
    }

    suspend fun setLanguage(language: String) = settingsDataStore.setLanguage(language)

    suspend fun setSubgroup(subgroup: String) = settingsDataStore.setSubgroup(subgroup)

    suspend fun setScheduleView(view: String) = settingsDataStore.setScheduleView(view)

    suspend fun setGroup(groupId: String, groupName: String) {
        settingsDataStore.setGroup(groupId, groupName)
        settingsDataStore.setLastScheduleRefresh(System.currentTimeMillis())
    }

    suspend fun saveIdnp(idnp: String) = settingsDataStore.saveIdnp(idnp)

    suspend fun clearIdnp() = settingsDataStore.clearIdnp()

    suspend fun setNotificationSettings(settings: NotificationSettings) {
        settingsDataStore.saveNotificationSettings(settings)
    }

    suspend fun addCustomPeriod(period: CustomPeriodSetting) {
        val current = customPeriods.firstOrNull().orEmpty()
        settingsDataStore.setCustomPeriods(current + period)
    }

    suspend fun updateCustomPeriod(period: CustomPeriodSetting) {
        val current = customPeriods.firstOrNull().orEmpty()
        val updated = current.map { if (it.id == period.id) period else it }
        settingsDataStore.setCustomPeriods(updated)
    }

    suspend fun deleteCustomPeriod(id: String) {
        val current = customPeriods.firstOrNull().orEmpty()
        settingsDataStore.setCustomPeriods(current.filterNot { it.id == id })
    }

    suspend fun refreshScheduleMetadata() {
        settingsDataStore.setLastScheduleRefresh(System.currentTimeMillis())
    }

    suspend fun clearScheduleCache() {
        settingsDataStore.clearLastScheduleRefresh()
    }

    suspend fun resetSettingsToDefaults() {
        settingsDataStore.setLanguage("en")
        settingsDataStore.setSubgroup("Subgroup 2")
        settingsDataStore.setScheduleView("day")
        settingsDataStore.setCustomPeriods(emptyList())
        settingsDataStore.saveNotificationSettings(NotificationSettings())
        settingsDataStore.clearLastScheduleRefresh()
    }

    fun lastUpdateCheckEpoch(): Flow<Long?> = settingsDataStore.lastUpdateCheckEpoch
    fun dismissedUpdateVersion(): Flow<String?> = settingsDataStore.dismissedUpdateVersion
    fun dismissedUpdateUntilEpoch(): Flow<Long?> = settingsDataStore.dismissedUpdateUntilEpoch

    suspend fun setUpdateDismissed(version: String, untilEpochMillis: Long) {
        settingsDataStore.setUpdateDismissed(version, untilEpochMillis)
    }

    suspend fun clearUpdateDismissed() = settingsDataStore.clearUpdateDismissed()

    suspend fun setLastUpdateCheck(epochMillis: Long) = settingsDataStore.setLastUpdateCheck(epochMillis)
}


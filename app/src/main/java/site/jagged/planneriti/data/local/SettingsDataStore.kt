package site.jagged.planneriti.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import site.jagged.planneriti.domain.model.UserSettings
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.settingsDataStore by preferencesDataStore(name = "user_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.settingsDataStore

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
}
package site.jagged.planneriti.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import site.jagged.planneriti.data.repository.AuthRepository
import site.jagged.planneriti.data.repository.SettingsRepository
import site.jagged.planneriti.data.repository.UpdateRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val updateRepository: UpdateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val settings = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), site.jagged.planneriti.domain.model.UserSettings())

    init {
        _uiState.update { it.copy(currentVersion = updateRepository.getCurrentVersion()) }
        observeSettings()
        refreshAccount()
        loadGroups()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.settings,
                settingsRepository.customPeriods,
                settingsRepository.idnp,
                settingsRepository.notificationSettings,
                settingsRepository.lastScheduleRefreshEpoch
            ) { settings, periods, idnp, notifications, lastRefresh ->
                settings to periods to idnp to notifications to lastRefresh
            }.collect { chain ->
                val settings = chain.first.first.first.first
                val periods = chain.first.first.first.second
                val idnp = chain.first.first.second
                val notifications = chain.first.second
                val lastRefresh = chain.second

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        language = settings.language,
                        selectedGroupId = settings.selectedGroupId,
                        selectedGroupName = settings.selectedGroupName,
                        subgroup = settings.subgroup,
                        scheduleView = settings.scheduleView,
                        customPeriods = periods,
                        idnp = idnp,
                        notificationSettings = notifications,
                        lastScheduleRefreshEpoch = lastRefresh
                    )
                }
            }
        }
    }

    fun loadGroups(force: Boolean = false) {
        viewModelScope.launch {
            runCatching { settingsRepository.getGroups(force) }
                .onSuccess { groups -> _uiState.update { it.copy(groups = groups, error = null) } }
                .onFailure { _uiState.update { it.copy(error = "Failed to load groups") } }
        }
    }

    fun refreshAccount() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _uiState.update {
                it.copy(
                    account = AccountUiState(
                        email = user?.email,
                        isVerified = user?.isVerified == true,
                        isAuthenticated = user != null
                    )
                )
            }
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }

    fun selectGroup(group: GroupOption) {
        viewModelScope.launch { settingsRepository.setGroup(group.id, group.name) }
    }

    fun setSubgroup(subgroup: String) {
        viewModelScope.launch { settingsRepository.setSubgroup(subgroup) }
    }

    fun setScheduleView(view: String) {
        viewModelScope.launch { settingsRepository.setScheduleView(view) }
    }

    fun saveIdnp(idnp: String) {
        viewModelScope.launch { settingsRepository.saveIdnp(idnp.trim()) }
    }

    fun clearIdnp() {
        viewModelScope.launch { settingsRepository.clearIdnp() }
    }

    fun saveNotificationSettings(settings: NotificationSettings) {
        viewModelScope.launch { settingsRepository.setNotificationSettings(settings) }
    }

    fun addCustomPeriod(period: CustomPeriodSetting) {
        viewModelScope.launch { settingsRepository.addCustomPeriod(period) }
    }

    fun updateCustomPeriod(period: CustomPeriodSetting) {
        viewModelScope.launch { settingsRepository.updateCustomPeriod(period) }
    }

    fun deleteCustomPeriod(id: String) {
        viewModelScope.launch { settingsRepository.deleteCustomPeriod(id) }
    }

    fun refreshSchedule() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshingSchedule = true) }
            runCatching { settingsRepository.refreshScheduleMetadata() }
                .onFailure { _uiState.update { state -> state.copy(error = "Failed to refresh schedule") } }
            _uiState.update { it.copy(isRefreshingSchedule = false) }
        }
    }

    fun clearScheduleCache() {
        viewModelScope.launch { settingsRepository.clearScheduleCache() }
    }

    fun checkForUpdates(manual: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingForUpdate = true, updateMessage = null) }
            val update = if (manual) {
                updateRepository.manualCheckForUpdate()
            } else {
                updateRepository.checkForUpdate(forceCheck = false)
            }

            _uiState.update {
                if (update != null) {
                    it.copy(updateInfo = update, isCheckingForUpdate = false, currentVersion = update.currentVersion)
                } else {
                    it.copy(
                        isCheckingForUpdate = false,
                        updateInfo = null,
                        currentVersion = updateRepository.getCurrentVersion(),
                        updateMessage = "You are already on ${updateRepository.getCurrentVersion()}"
                    )
                }
            }
        }
    }

    fun dismissUpdate(version: String) {
        viewModelScope.launch {
            updateRepository.dismissVersion(version)
            _uiState.update { it.copy(updateInfo = null, updateMessage = "Update reminder postponed") }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.update { it.copy(account = AccountUiState()) }
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            authRepository.deleteAccount(password)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            account = AccountUiState(),
                            updateMessage = "Deletion request submitted. Check your email."
                        )
                    }
                }
                .onFailure {
                    _uiState.update { state -> state.copy(error = it.message ?: "Account deletion failed") }
                }
        }
    }

    fun resetAllSettings() {
        viewModelScope.launch { settingsRepository.resetSettingsToDefaults() }
    }
}
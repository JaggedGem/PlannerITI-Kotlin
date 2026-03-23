package site.jagged.planneriti.ui.grades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import site.jagged.planneriti.data.repository.SettingsRepository
import javax.inject.Inject

data class GradesUiState(
    val idnpInput: String = "",
    val savedIdnpMasked: String = "Not set",
    val isSaving: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

@HiltViewModel
class GradesViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GradesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.idnp.collect { idnp ->
                _uiState.update {
                    it.copy(
                        idnpInput = idnp.orEmpty(),
                        savedIdnpMasked = maskIdnp(idnp),
                        error = null
                    )
                }
            }
        }
    }

    fun onIdnpChanged(value: String) {
        _uiState.update { it.copy(idnpInput = value.filter(Char::isDigit).take(13), error = null, message = null) }
    }

    fun saveIdnp() {
        val value = _uiState.value.idnpInput
        if (value.length != 13) {
            _uiState.update { it.copy(error = "IDNP must contain exactly 13 digits") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, message = null) }
            runCatching { settingsRepository.saveIdnp(value) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            message = "IDNP saved securely",
                            savedIdnpMasked = maskIdnp(value)
                        )
                    }
                }
                .onFailure {
                    _uiState.update { state -> state.copy(isSaving = false, error = "Failed to save IDNP") }
                }
        }
    }

    fun clearIdnp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, message = null) }
            runCatching { settingsRepository.clearIdnp() }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            idnpInput = "",
                            savedIdnpMasked = "Not set",
                            message = "IDNP removed"
                        )
                    }
                }
                .onFailure {
                    _uiState.update { state -> state.copy(isSaving = false, error = "Failed to clear IDNP") }
                }
        }
    }

    private fun maskIdnp(idnp: String?): String {
        if (idnp.isNullOrBlank()) return "Not set"
        if (idnp.length < 8) return "Saved"
        return "${idnp.take(4)}••••••${idnp.takeLast(3)}"
    }
}


package net.adhikary.mrtbuddy.ui.screens.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.changeLang
import net.adhikary.mrtbuddy.repository.SettingsRepository

class MoreScreenViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MoreScreenState())
    val state: StateFlow<MoreScreenState> get() = _state.asStateFlow()

    private val _events = Channel<MoreScreenEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        onAction(MoreScreenAction.OnInit)
    }

    fun onAction(action: MoreScreenAction) {
        when (action) {
            is MoreScreenAction.OnInit -> {
                viewModelScope.launch {
                    try {
                        val autoSaveEnabled = settingsRepository.autoSaveEnabled.value
                        val currentLanguage = settingsRepository.currentLanguage.value
                        _state.value = _state.value.copy(
                            autoSaveEnabled = autoSaveEnabled,
                            currentLanguage = currentLanguage
                        )
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(error = e.message)
                        _events.send(MoreScreenEvent.Error(e.message ?: "Unknown error"))
                    }
                }
            }

            is MoreScreenAction.SetAutoSave -> {
                viewModelScope.launch {
                    try {
                        settingsRepository.setAutoSave(action.enabled)
                        _state.value = _state.value.copy(autoSaveEnabled = action.enabled)
                    } catch (e: Exception) {
                        _events.send(MoreScreenEvent.Error(e.message ?: "Failed to update setting"))
                    }
                }
            }
            is MoreScreenAction.OpenLicenses -> {
                viewModelScope.launch {
                    _events.send(MoreScreenEvent.NavigateToLicenses)
                }
            }
            is MoreScreenAction.SetLanguage -> {
                viewModelScope.launch {
                    try {
                        changeLang(action.language)
                        settingsRepository.setLanguage(action.language)
                        _state.value = _state.value.copy(currentLanguage = action.language)
                    } catch (e: Exception) {
                        _events.send(MoreScreenEvent.Error(e.message ?: "Failed to change language"))
                    }
                }
            }
        }
    }
}

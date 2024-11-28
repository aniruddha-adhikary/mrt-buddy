package net.adhikary.mrtbuddy.ui.screens.stationmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.repository.SettingsRepository

class StationMapViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StationMapScreenState())
    val state: StateFlow<StationMapScreenState> get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.currentLanguage.collect { language ->
                _state.update { it.copy(currentLanguage = language) }
            }
        }
    }
}
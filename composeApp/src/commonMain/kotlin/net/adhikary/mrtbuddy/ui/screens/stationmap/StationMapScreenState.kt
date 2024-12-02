package net.adhikary.mrtbuddy.ui.screens.stationmap

data class StationMapScreenState(
    val isLoading: Boolean = false,
    val currentLanguage: String = "en",
    val error: String? = null
)

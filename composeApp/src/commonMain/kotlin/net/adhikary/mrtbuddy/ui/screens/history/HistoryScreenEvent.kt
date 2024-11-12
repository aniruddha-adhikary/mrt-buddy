package net.adhikary.mrtbuddy.ui.screens.history

sealed interface HistoryScreenEvent {
    data class Error(val error: String) : HistoryScreenEvent
}

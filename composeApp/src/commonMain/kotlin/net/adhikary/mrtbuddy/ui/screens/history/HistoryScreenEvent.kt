package net.adhikary.mrtbuddy.ui.screens.history

sealed interface HistoryScreenEvent {
    data class Error(val error: String) : HistoryScreenEvent
    data class ShowRenameDialog(val cardIdm: String, val currentName: String) : HistoryScreenEvent
    object RenameSuccess : HistoryScreenEvent
    object DeleteSuccess : HistoryScreenEvent
}

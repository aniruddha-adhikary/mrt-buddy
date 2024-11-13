package net.adhikary.mrtbuddy.ui.screens.history

sealed interface HistoryScreenAction {
    object OnInit : HistoryScreenAction
    data class RenameCard(val cardIdm: String, val newName: String) : HistoryScreenAction
    data class DeleteCard(val cardIdm: String) : HistoryScreenAction
    object OnCardAdded : HistoryScreenAction
}

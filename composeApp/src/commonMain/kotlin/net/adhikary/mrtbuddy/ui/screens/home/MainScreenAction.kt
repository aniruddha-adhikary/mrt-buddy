package net.adhikary.mrtbuddy.ui.screens.home

import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.CardState

sealed interface MainScreenAction {
    data object OnInit : MainScreenAction
    data class UpdateCardState(val newState: CardState) : MainScreenAction
    data class UpdateCardReadResult(val cardReadResult: CardReadResult) : MainScreenAction
}
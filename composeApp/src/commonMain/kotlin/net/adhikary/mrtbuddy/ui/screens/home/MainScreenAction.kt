package net.adhikary.mrtbuddy.ui.screens.home

import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction

sealed interface MainScreenAction {
    data object OnInit : MainScreenAction
    data class UpdateCardState(val newState : CardState) : MainScreenAction
    data class UpdateTransactions(val transactions: List<Transaction>) : MainScreenAction
}
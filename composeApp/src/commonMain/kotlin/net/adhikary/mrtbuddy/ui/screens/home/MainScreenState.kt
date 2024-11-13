package net.adhikary.mrtbuddy.ui.screens.home

import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount

data class MainScreenState(
    val isLoading: Boolean = false,
    val cardState: CardState = CardState.WaitingForTap,
    val cardIdm: String? = null,
    val cardName: String? = null,
    val transaction: List<Transaction> = emptyList(),
    val transactionWithAmount: List<TransactionWithAmount> = emptyList(),
    val error: String? = null
)

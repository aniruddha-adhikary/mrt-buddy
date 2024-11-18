package net.adhikary.mrtbuddy.ui.screens.transactionlist

import net.adhikary.mrtbuddy.data.TransactionEntityWithAmount

data class TransactionListState(
    val isLoading: Boolean = false,
    val transactions: List<TransactionEntityWithAmount> = emptyList(),
    val balance: Int? = null, // Added balance property
    val cardName: String? = null, // Added cardName property
    val error: String? = null
)

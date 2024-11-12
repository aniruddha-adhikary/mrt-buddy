package net.adhikary.mrtbuddy.ui.screens.transactionlist

import net.adhikary.mrtbuddy.data.TransactionEntity

data class TransactionListState(
    val isLoading: Boolean = false,
    val transactions: List<TransactionEntity> = emptyList(),
    val error: String? = null
)

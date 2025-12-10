package net.adhikary.mrtbuddy.ui.screens.transactionlist

import net.adhikary.mrtbuddy.data.TransactionEntityWithAmount

/**
 * State for the transaction list screen, enhanced with pagination support
 */
data class TransactionListState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val transactions: List<TransactionEntityWithAmount> = emptyList(),
    val balance: Int? = null,
    val cardName: String? = null,
    val error: String? = null,
    val canLoadMore: Boolean = true,
    val currentOffset: Int = 0
)

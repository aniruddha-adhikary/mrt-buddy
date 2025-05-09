package net.adhikary.mrtbuddy.ui.screens.transactionlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.adhikary.mrtbuddy.repository.TransactionRepository

class TransactionListViewModel(
    private val cardIdm: String,
    private val transactionRepository: TransactionRepository,
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
        private const val KEY_OFFSET = "offset"
        private const val MAX_IN_MEMORY = 100  // Maximum number of transactions to keep in memory
    }

    // Restored from saved state or default to 0
    private var currentOffset = savedStateHandle.get<Int>(KEY_OFFSET) ?: 0

    private val _state = MutableStateFlow(TransactionListState())
    val state: StateFlow<TransactionListState> = _state.asStateFlow()

    // Prevent concurrent load operations
    private val loadMoreLock = Mutex()

    init {
        loadInitialTransactions()
    }

    private fun loadInitialTransactions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Get card info first
                val balance = transactionRepository.getLatestBalanceByCardIdm(cardIdm)
                val cardEntity = transactionRepository.getCardByIdm(cardIdm)

                // Load first batch with larger initial size for smoother experience
                val initialBatchSize = PAGE_SIZE * 2
                val result = transactionRepository.getTransactionsByCardIdmLazy(
                    cardIdm = cardIdm,
                    limit = initialBatchSize,
                    offset = 0
                )

                // Update state with results
                currentOffset = result.nextOffset
                savedStateHandle[KEY_OFFSET] = currentOffset

                _state.update {
                    it.copy(
                        isLoading = false,
                        transactions = result.transactions,
                        balance = balance,
                        cardName = cardEntity?.name,
                        canLoadMore = !result.isEndReached,
                        currentOffset = currentOffset
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error loading transactions"
                    )
                }
            }
        }
    }

    /**
     * Load more transactions when the user scrolls near the end of the list
     */
    fun loadMoreTransactions() {
        // Skip if we can't load more or if we're already loading
        if (!state.value.canLoadMore || state.value.isLoadingMore) return

        viewModelScope.launch {
            // Use mutex to prevent concurrent load operations
            loadMoreLock.withLock {
                _state.update { it.copy(isLoadingMore = true, error = null) }

                try {
                    val result = transactionRepository.getTransactionsByCardIdmLazy(
                        cardIdm = cardIdm,
                        limit = PAGE_SIZE,
                        offset = currentOffset
                    )

                    // Update offset for next load
                    currentOffset = result.nextOffset
                    savedStateHandle[KEY_OFFSET] = currentOffset

                    // Append new transactions to existing list
                    val combinedTransactions = _state.value.transactions + result.transactions

                    // Optionally trim list if it gets too large
                    val trimmedTransactions = if (combinedTransactions.size > MAX_IN_MEMORY) {
                        combinedTransactions.takeLast(MAX_IN_MEMORY)
                    } else {
                        combinedTransactions
                    }

                    _state.update {
                        it.copy(
                            isLoadingMore = false,
                            transactions = trimmedTransactions,
                            canLoadMore = !result.isEndReached,
                            currentOffset = currentOffset
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoadingMore = false,
                            error = e.message ?: "Error loading more transactions"
                        )
                    }
                }
            }
        }
    }

    /**
     * Retry loading after an error
     */
    fun retry() {
        if (state.value.transactions.isEmpty()) {
            loadInitialTransactions()
        } else {
            loadMoreTransactions()
        }
    }

    /**
     * Refresh the transaction list from the beginning
     */
    fun refresh() {
        currentOffset = 0
        savedStateHandle[KEY_OFFSET] = 0
        loadInitialTransactions()
    }
}

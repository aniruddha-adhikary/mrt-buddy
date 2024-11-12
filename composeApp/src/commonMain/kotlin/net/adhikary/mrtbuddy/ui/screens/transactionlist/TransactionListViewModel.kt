package net.adhikary.mrtbuddy.ui.screens.transactionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.repository.TransactionRepository
import net.adhikary.mrtbuddy.data.TransactionEntity

class TransactionListViewModel(
    private val cardIdm: String,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionListState())
    val state: StateFlow<TransactionListState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val transactions = transactionRepository.getTransactionsByCardIdm(cardIdm)
                _state.update { it.copy(isLoading = false, transactions = transactions) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}

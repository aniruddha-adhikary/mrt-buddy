package net.adhikary.mrtbuddy.ui.screens.transactionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.adhikary.mrtbuddy.repository.TransactionRepository

class TransactionListViewModelFactory(
    private val cardIdm: String,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionListViewModel(cardIdm, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

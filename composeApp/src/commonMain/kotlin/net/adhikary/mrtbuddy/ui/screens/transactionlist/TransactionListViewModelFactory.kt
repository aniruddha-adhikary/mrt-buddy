package net.adhikary.mrtbuddy.ui.screens.transactionlist

import net.adhikary.mrtbuddy.repository.TransactionRepository

class TransactionListViewModelFactory(
    private val cardIdm: String,
    private val transactionRepository: TransactionRepository
) {
    fun create(): TransactionListViewModel {
        return TransactionListViewModel(cardIdm, transactionRepository)
    }
}

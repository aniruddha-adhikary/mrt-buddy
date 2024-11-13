package net.adhikary.mrtbuddy.ui.screens.history

import net.adhikary.mrtbuddy.repository.TransactionRepository

class HistoryScreenViewModelFactory(
    private val transactionRepository: TransactionRepository
) {
    fun create(): HistoryScreenViewModel {
        return HistoryScreenViewModel(transactionRepository)
    }
}

package net.adhikary.mrtbuddy.ui.screens.history

import net.adhikary.mrtbuddy.repository.TransactionRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HistoryScreenViewModelFactory : KoinComponent {
    private val transactionRepository: TransactionRepository by inject()
    fun create(): HistoryScreenViewModel {
        return HistoryScreenViewModel(transactionRepository)
    }
}

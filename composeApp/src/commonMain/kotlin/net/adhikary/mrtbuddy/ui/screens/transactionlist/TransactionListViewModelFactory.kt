package net.adhikary.mrtbuddy.ui.screens.transactionlist

import net.adhikary.mrtbuddy.repository.TransactionRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TransactionListViewModelFactory(
    private val cardIdm: String
) : KoinComponent {
    private val transactionRepository: TransactionRepository by inject()
    fun create(): TransactionListViewModel {
        return TransactionListViewModel(cardIdm, transactionRepository)
    }
}

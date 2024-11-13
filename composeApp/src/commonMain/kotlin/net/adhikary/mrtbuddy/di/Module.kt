package net.adhikary.mrtbuddy.di

import net.adhikary.mrtbuddy.database.AppDatabase
import net.adhikary.mrtbuddy.database.DatabaseProvider
import net.adhikary.mrtbuddy.repository.TransactionRepository
import net.adhikary.mrtbuddy.ui.screens.transactionlist.TransactionListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: org.koin.core.module.Module

val appModule = module {
    single { get<AppDatabase>().getCardDao() }
    single { get<AppDatabase>().getScanDao() }
    single { get<AppDatabase>().getTransactionDao() }
    single { TransactionRepository(
        cardDao = get(),
        scanDao = get(),
        transactionDao = get()
    ) }
    viewModel { parameters -> 
        TransactionListViewModel(
            cardIdm = parameters.get(),
            transactionRepository = get()
        )
    }
}


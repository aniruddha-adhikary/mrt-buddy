package net.adhikary.mrtbuddy.di

import com.russhwolf.settings.Settings
import net.adhikary.mrtbuddy.settings.createSettings
import net.adhikary.mrtbuddy.database.AppDatabase
import net.adhikary.mrtbuddy.repository.SettingsRepository
import net.adhikary.mrtbuddy.repository.TransactionRepository
import net.adhikary.mrtbuddy.ui.screens.history.HistoryScreenState
import net.adhikary.mrtbuddy.ui.screens.history.HistoryScreenViewModel
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenState
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenViewModel
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreenViewModel
import net.adhikary.mrtbuddy.ui.screens.transactionlist.TransactionListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: org.koin.core.module.Module

val appModule = module {
    single<Settings> { createSettings() }
    single { SettingsRepository(get()) }
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
    
    viewModel { 
        HistoryScreenViewModel(
            transactionRepository = get()
        )
    }
    
    factory { 
        HistoryScreenState()
    }

    viewModel {
        MoreScreenViewModel(
            settingsRepository = get()
        )
    }

    factory {
        MainScreenState()
    }

    viewModel {
        MainScreenViewModel(
            transactionRepository = get(),
            initialState = get(),
            settingsRepository = get()
        )
    }
}


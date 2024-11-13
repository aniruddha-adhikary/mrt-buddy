package net.adhikary.mrtbuddy.di

import net.adhikary.mrtbuddy.database.AppDatabase
import net.adhikary.mrtbuddy.database.DatabaseProvider
import net.adhikary.mrtbuddy.repository.TransactionRepository
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
}


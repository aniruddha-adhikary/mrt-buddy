package net.adhikary.mrtbuddy.di

import net.adhikary.mrtbuddy.database.DatabaseProvider
import org.koin.dsl.module

actual val platformModule = module {
    single { AssetReader() }
    single { DatabaseProvider().getDatabase() }
}

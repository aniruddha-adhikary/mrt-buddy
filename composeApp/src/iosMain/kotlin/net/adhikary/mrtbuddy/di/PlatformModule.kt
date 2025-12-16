package net.adhikary.mrtbuddy.di

import net.adhikary.mrtbuddy.database.DatabaseProvider
import net.adhikary.mrtbuddy.utils.CsvFileWriter
import net.adhikary.mrtbuddy.utils.FileSharer
import org.koin.dsl.module

actual val platformModule = module {
    single { DatabaseProvider().getDatabase() }
    single { FileSharer() }
    factory { CsvFileWriter() }
}

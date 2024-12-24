package net.adhikary.mrtbuddy.di

import net.adhikary.mrtbuddy.database.DatabaseProvider
import net.adhikary.mrtbuddy.notification.AndroidNotificationManager
import net.adhikary.mrtbuddy.notification.NotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single { DatabaseProvider(androidContext()).getDatabase() }
    single<NotificationManager> { AndroidNotificationManager(androidContext()) }
}

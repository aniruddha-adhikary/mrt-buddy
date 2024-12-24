package net.adhikary.mrtbuddy.di

import net.adhikary.mrtbuddy.database.DatabaseProvider
import net.adhikary.mrtbuddy.notification.IosNotificationManager
import net.adhikary.mrtbuddy.notification.NotificationManager
import org.koin.dsl.module

actual val platformModule = module {
    single { DatabaseProvider().getDatabase() }
    single<NotificationManager> { IosNotificationManager() }
}

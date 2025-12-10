package net.adhikary.mrtbuddy.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual class DatabaseProvider(private val context: Context) {
    actual fun getDatabase(): AppDatabase {
        val dbFile = context.getDatabasePath("mrt_buddy.db")
        return Room.databaseBuilder<AppDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath
        )
            .addMigrations(MIGRATION_2_3)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}

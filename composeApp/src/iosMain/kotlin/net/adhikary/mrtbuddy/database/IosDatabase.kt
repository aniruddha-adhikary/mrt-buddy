package net.adhikary.mrtbuddy.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask

actual class DatabaseProvider {
    actual fun getDatabase(): AppDatabase {
        val dbFilePath = documentDirectory() + "/mrt_buddy.db"
        val builder = Room.databaseBuilder<AppDatabase>(
            name = dbFilePath,
        )
        return builder.setDriver(BundledSQLiteDriver())
            .build()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}


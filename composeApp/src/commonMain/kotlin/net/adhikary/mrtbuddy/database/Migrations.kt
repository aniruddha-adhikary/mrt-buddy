package net.adhikary.mrtbuddy.database

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        // Add optimized index for pagination queries
        connection.execSQL("CREATE INDEX IF NOT EXISTS `idx_cardIdm_order` ON `transactions` (`cardIdm`, `order`)")
        // Add index for date-based queries
        connection.execSQL("CREATE INDEX IF NOT EXISTS `idx_cardIdm_dateTime` ON `transactions` (`cardIdm`, `dateTime`)")
    }
}

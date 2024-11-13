package net.adhikary.mrtbuddy.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import net.adhikary.mrtbuddy.dao.CardDao
import net.adhikary.mrtbuddy.dao.DemoDao
import net.adhikary.mrtbuddy.dao.ScanDao
import net.adhikary.mrtbuddy.dao.TransactionDao
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.data.DemoLocal
import net.adhikary.mrtbuddy.data.ScanEntity
import net.adhikary.mrtbuddy.data.TransactionEntity
import net.adhikary.mrtbuddy.repository.TransactionRepository

expect class DatabaseProvider {
    fun getDatabase(): AppDatabase
}

@Database(
    entities = [DemoLocal::class, CardEntity::class, ScanEntity::class, TransactionEntity::class],
    version = 2
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): DemoDao
    abstract fun getCardDao(): CardDao
    abstract fun getScanDao(): ScanDao
    abstract fun getTransactionDao(): TransactionDao

}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

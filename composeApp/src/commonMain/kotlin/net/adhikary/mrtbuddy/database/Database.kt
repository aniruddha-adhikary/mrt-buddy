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

    // Repository getters
    fun getDemoRepository() = DemoRepository(getDao())
    fun getCardRepository() = CardRepository(getCardDao())
    fun getScanRepository() = ScanRepository(getScanDao())
    fun getTransactionRepository() = TransactionRepository(getTransactionDao())
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

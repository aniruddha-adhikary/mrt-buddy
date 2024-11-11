package net.adhikary.mrtbuddy.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.adhikary.mrtbuddy.dao.DemoDao
import net.adhikary.mrtbuddy.data.DemoLocal
import net.adhikary.mrtbuddy.data.CardEntity

@Database(entities = [DemoLocal::class, CardEntity::class], version = 2) // you must add the entities here
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): DemoDao
    abstract fun getCardDao(): CardDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

package net.adhikary.mrtbuddy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.adhikary.mrtbuddy.model.CardAlias

@Database(entities = [CardAlias::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardAliasDao(): CardAliasDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mrt_buddy_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

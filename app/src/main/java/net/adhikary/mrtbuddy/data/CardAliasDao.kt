package net.adhikary.mrtbuddy.data

import androidx.room.*
import net.adhikary.mrtbuddy.model.CardAlias
import kotlinx.coroutines.flow.Flow

@Dao
interface CardAliasDao {
    @Query("SELECT * FROM card_aliases ORDER BY lastUpdated DESC")
    fun getAllAliases(): Flow<List<CardAlias>>

    @Query("SELECT * FROM card_aliases WHERE cardSerialNumber = :serialNumber LIMIT 1")
    suspend fun getAliasBySerialNumber(serialNumber: String): CardAlias?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAlias(cardAlias: CardAlias)

    @Delete
    suspend fun deleteAlias(cardAlias: CardAlias)

    @Query("UPDATE card_aliases SET lastBalance = :balance, lastUpdated = :timestamp WHERE cardSerialNumber = :serialNumber")
    suspend fun updateBalance(serialNumber: String, balance: Double, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM card_aliases WHERE lastBalance < lowBalanceThreshold AND notificationsEnabled = 1")
    fun getLowBalanceCards(): Flow<List<CardAlias>>
}

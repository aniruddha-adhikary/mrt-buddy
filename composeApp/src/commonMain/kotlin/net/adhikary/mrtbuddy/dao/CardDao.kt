package net.adhikary.mrtbuddy.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.adhikary.mrtbuddy.data.CardEntity

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCard(card: CardEntity)

    @Query("UPDATE cards SET lastScanTime = :scanTime WHERE idm = :cardIdm")
    suspend fun updateLastScanTime(cardIdm: String, scanTime: Long)

    @Query("SELECT * FROM cards WHERE idm = :idm LIMIT 1")
    suspend fun getCardByIdm(idm: String): CardEntity?
    @Query("SELECT * FROM cards")
    suspend fun getAllCards(): List<CardEntity>

    @Query("UPDATE cards SET name = :newName WHERE idm = :cardIdm")
    suspend fun updateCardName(cardIdm: String, newName: String)

    @Query("""
        DELETE FROM cards WHERE idm = :cardIdm;
    """)
    suspend fun deleteCard(cardIdm: String)
}

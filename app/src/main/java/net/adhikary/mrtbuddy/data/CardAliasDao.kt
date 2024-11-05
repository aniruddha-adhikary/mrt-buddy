package net.adhikary.mrtbuddy.data

import androidx.room.*
import net.adhikary.mrtbuddy.model.CardAlias
import kotlinx.coroutines.flow.Flow

@Dao
interface CardAliasDao {
    @Query("SELECT * FROM card_aliases")
    fun getAllAliases(): Flow<List<CardAlias>>

    @Query("SELECT * FROM card_aliases WHERE cardId = :cardId")
    suspend fun getAlias(cardId: String): CardAlias?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlias(cardAlias: CardAlias)

    @Delete
    suspend fun deleteAlias(cardAlias: CardAlias)

    @Query("DELETE FROM card_aliases")
    suspend fun deleteAllAliases()
}

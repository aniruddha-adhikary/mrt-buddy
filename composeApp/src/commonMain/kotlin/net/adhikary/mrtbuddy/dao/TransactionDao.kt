package net.adhikary.mrtbuddy.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.adhikary.mrtbuddy.data.TransactionEntity

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM transactions WHERE cardIdm = :cardIdm ORDER BY `order` DESC LIMIT 1")
    suspend fun getLatestTransactionByCardIdm(cardIdm: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE cardIdm = :cardIdm ORDER BY `order` DESC")
    suspend fun getTransactionsByCardIdm(cardIdm: String): List<TransactionEntity>

    /**
     * Get a paginated list of transactions for a card
     * @param cardIdm The card identifier
     * @param limit Maximum number of transactions to retrieve
     * @param offset The starting position (0-based)
     * @return Paginated list of transactions
     */
    @Query("SELECT * FROM transactions WHERE cardIdm = :cardIdm ORDER BY `order` DESC LIMIT :limit OFFSET :offset")
    suspend fun getTransactionsByCardIdmPaginated(cardIdm: String, limit: Int, offset: Int): List<TransactionEntity>

    /**
     * Get a single transaction at a specific position for a card
     * @param cardIdm The card identifier
     * @param position The position in the ordered list
     * @return The transaction at that position or null if none exists
     */
    @Query("SELECT * FROM transactions WHERE cardIdm = :cardIdm ORDER BY `order` DESC LIMIT 1 OFFSET :position")
    suspend fun getTransactionAtPosition(cardIdm: String, position: Int): TransactionEntity?

    @Query("SELECT MAX(`order`) FROM transactions")
    suspend fun getLastOrder(): Int?

    @Query("DELETE FROM transactions WHERE cardIdm = :cardIdm")
    suspend fun deleteTransactionsByCardIdm(cardIdm: String)
}

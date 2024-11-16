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

    @Query("SELECT MAX(`order`) FROM transactions")
    suspend fun getLastOrder(): Int?

    @Query("DELETE FROM transactions WHERE cardIdm = :cardIdm")
    suspend fun deleteTransactionsByCardIdm(cardIdm: String)
}

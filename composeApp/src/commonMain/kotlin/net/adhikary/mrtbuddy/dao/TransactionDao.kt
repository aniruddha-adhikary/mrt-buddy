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

    @Query("SELECT * FROM transactions WHERE cardIdm = :cardIdm ORDER BY dateTime DESC")
    suspend fun getTransactionsByCardIdm(cardIdm: String): List<TransactionEntity>
}

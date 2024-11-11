package net.adhikary.mrtbuddy.repository

import net.adhikary.mrtbuddy.dao.TransactionDao
import net.adhikary.mrtbuddy.data.TransactionEntity

class TransactionRepository(private val transactionDao: TransactionDao) {
    suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insertTransaction(transaction)
    suspend fun insertTransactions(transactions: List<TransactionEntity>) = transactionDao.insertTransactions(transactions)
    suspend fun getTransactionsByCardIdm(cardIdm: String): List<TransactionEntity> = transactionDao.getTransactionsByCardIdm(cardIdm)
}

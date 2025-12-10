package net.adhikary.mrtbuddy.repository

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import net.adhikary.mrtbuddy.dao.CardDao
import net.adhikary.mrtbuddy.dao.ScanDao
import net.adhikary.mrtbuddy.dao.TransactionDao
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.data.ScanEntity
import net.adhikary.mrtbuddy.data.TransactionEntity
import net.adhikary.mrtbuddy.data.TransactionEntityWithAmount
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.nfc.service.TimestampService

class TransactionRepository(
    private val cardDao: CardDao,
    private val scanDao: ScanDao,
    private val transactionDao: TransactionDao
) {

    suspend fun saveCardReadResult(result: CardReadResult) {
        val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val cardEntity = CardEntity(idm = result.idm, name = null, lastScanTime = currentTime)
        cardDao.insertCard(cardEntity)
        cardDao.updateLastScanTime(result.idm, currentTime)

        val scanEntity = ScanEntity(cardIdm = result.idm)
        val scanId = scanDao.insertScan(scanEntity)

        val newTransactionEntities = result.transactions.map { txn ->
            val dateTime = txn.timestamp
                .toInstant(TimestampService.getDefaultTimezone())
                .toEpochMilliseconds()

            TransactionEntity(
                cardIdm = result.idm,
                scanId = scanId,
                fromStation = txn.fromStation,
                toStation = txn.toStation,
                balance = txn.balance,
                dateTime = dateTime,
                fixedHeader = txn.fixedHeader
            )
        }

        val lastOrder = transactionDao.getLastOrder() ?: 0
        val transactionsToInsert = newTransactionEntities
            .reversed()
            .mapIndexed { index, entity ->
                entity.copy(order = lastOrder + index + 1)
            }

        transactionDao.insertTransactions(transactionsToInsert)
    }

    suspend fun getCardByIdm(idm: String): CardEntity? {
        return cardDao.getCardByIdm(idm)
    }

    suspend fun getAllCards(): List<CardEntity> {
        return cardDao.getAllCards()
    }

    suspend fun getTransactionsByCardIdm(cardIdm: String): List<TransactionEntityWithAmount> {
        val transactions = transactionDao.getTransactionsByCardIdm(cardIdm)

        val sortedTransactions = transactions.sortedByDescending { it.order }
        return sortedTransactions.mapIndexed { index, transaction ->
            val amount = if (index + 1 < sortedTransactions.size) {
                transaction.balance - sortedTransactions[index + 1].balance
            } else {
                null
            }
            TransactionEntityWithAmount(transactionEntity = transaction, amount = amount)
        }.filter { transaction -> transaction.amount != null }
    }

    /**
     * Loads a batch of transactions with pagination support
     * @param cardIdm The card identifier
     * @param limit Maximum number of transactions to retrieve
     * @param offset The starting position (0-based)
     * @return Result containing transactions with amounts calculated, end-of-list status, and next offset
     */
    suspend fun getTransactionsByCardIdmLazy(
        cardIdm: String,
        limit: Int = 20,
        offset: Int = 0
    ): LazyLoadResult {
        // Fetch one extra transaction to help calculate the amount for the last visible transaction
        val fetchLimit = limit + 1
        val transactions = transactionDao.getTransactionsByCardIdmPaginated(cardIdm, fetchLimit, offset)

        // Determine if we've reached the end
        val isEndReached = transactions.size < fetchLimit

        // Process transactions for display (excluding the extra one if present)
        val displayTransactions = if (transactions.size > limit) {
            transactions.take(limit)
        } else {
            transactions
        }

        // Calculate amounts with special handling for batch boundaries
        val transactionsWithAmount = calculateAmounts(displayTransactions, transactions.getOrNull(limit))

        return LazyLoadResult(
            transactions = transactionsWithAmount,
            isEndReached = isEndReached,
            nextOffset = offset + displayTransactions.size
        )
    }

    /**
     * Calculate transaction amounts, using the nextTransaction for the last item if available
     */
    private fun calculateAmounts(
        transactions: List<TransactionEntity>,
        nextTransaction: TransactionEntity?
    ): List<TransactionEntityWithAmount> {
        if (transactions.isEmpty()) return emptyList()

        val result = mutableListOf<TransactionEntityWithAmount>()

        // Process all but the last transaction using adjacent pairs
        for (i in 0 until transactions.size - 1) {
            val current = transactions[i]
            val next = transactions[i + 1]
            val amount = current.balance - next.balance

            result.add(TransactionEntityWithAmount(current, amount))
        }

        // Process the last transaction using the extra transaction if available
        val lastTransaction = transactions.last()
        val lastAmount = if (nextTransaction != null) {
            lastTransaction.balance - nextTransaction.balance
        } else {
            null
        }

        result.add(TransactionEntityWithAmount(lastTransaction, lastAmount))

        return result.filter { it.amount != null }
    }

    suspend fun getLatestBalanceByCardIdm(cardIdm: String): Int? {
        return transactionDao.getLatestTransactionByCardIdm(cardIdm)?.balance
    }

    suspend fun renameCard(cardIdm: String, newName: String) {
        cardDao.updateCardName(cardIdm, newName)
    }

    suspend fun deleteCard(cardIdm: String) {
        cardDao.deleteCard(cardIdm)
        scanDao.deleteScansByCardIdm(cardIdm)
        transactionDao.deleteTransactionsByCardIdm(cardIdm)
    }

    /**
     * Data class to return batched results for lazy loading
     */
    data class LazyLoadResult(
        val transactions: List<TransactionEntityWithAmount>,
        val isEndReached: Boolean,
        val nextOffset: Int
    )
}

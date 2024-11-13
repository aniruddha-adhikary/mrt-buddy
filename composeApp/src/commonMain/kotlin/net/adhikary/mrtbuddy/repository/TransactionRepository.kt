package net.adhikary.mrtbuddy.repository

import io.github.aakira.napier.Napier
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
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount

class TransactionRepository(
    private val cardDao: CardDao,
    private val scanDao: ScanDao,
    private val transactionDao: TransactionDao
) {

    suspend fun saveCardReadResult(result: CardReadResult) {
        val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val cardEntity = CardEntity(idm = result.idm, name = null, lastScanTime = currentTime)
        cardDao.insertCard(cardEntity)

        val scanEntity = ScanEntity(cardIdm = result.idm)
        val scanId = scanDao.insertScan(scanEntity)

        val newTransactionEntities = result.transactions.map { txn ->
            val dateTime = txn.timestamp.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
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

    suspend fun renameCard(cardIdm: String, newName: String) {
        cardDao.updateCardName(cardIdm, newName)
    }

    suspend fun deleteCard(cardIdm: String) {
        cardDao.deleteCard(cardIdm)
        scanDao.deleteScansByCardIdm(cardIdm)
        transactionDao.deleteTransactionsByCardIdm(cardIdm)
    }

}

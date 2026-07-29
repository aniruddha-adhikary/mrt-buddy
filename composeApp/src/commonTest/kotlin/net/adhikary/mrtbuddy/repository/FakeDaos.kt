package net.adhikary.mrtbuddy.repository

import net.adhikary.mrtbuddy.dao.CardDao
import net.adhikary.mrtbuddy.dao.ScanDao
import net.adhikary.mrtbuddy.dao.TransactionDao
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.data.ScanEntity
import net.adhikary.mrtbuddy.data.TransactionEntity

/**
 * In-memory [CardDao] recording inserts, for driving [TransactionRepository] on the JVM.
 * A class (not an interface) so it does not trip the Konsist rule that `Dao` interfaces
 * must reside in `..dao..`.
 */
class FakeCardDao : CardDao {
    val insertedCards = mutableListOf<CardEntity>()
    val updatedScanTimes = mutableListOf<Pair<String, Long>>()

    override suspend fun insertCard(card: CardEntity) {
        insertedCards += card
    }

    override suspend fun updateLastScanTime(
        cardIdm: String,
        scanTime: Long,
    ) {
        updatedScanTimes += cardIdm to scanTime
    }

    override suspend fun getCardByIdm(idm: String): CardEntity? = insertedCards.lastOrNull { it.idm == idm }

    override suspend fun getAllCards(): List<CardEntity> = insertedCards.toList()

    override suspend fun updateCardName(
        cardIdm: String,
        newName: String,
    ) = Unit

    override suspend fun deleteCard(cardIdm: String) = Unit
}

class FakeScanDao : ScanDao {
    val insertedScans = mutableListOf<ScanEntity>()
    private var nextScanId = 1L

    override suspend fun insertScan(scan: ScanEntity): Long {
        insertedScans += scan
        return nextScanId++
    }

    override suspend fun getScansByCardIdm(cardIdm: String): List<ScanEntity> = insertedScans.filter { it.cardIdm == cardIdm }

    override suspend fun deleteScansByCardIdm(cardIdm: String) = Unit
}

class FakeTransactionDao : TransactionDao {
    val insertedTransactions = mutableListOf<TransactionEntity>()

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        insertedTransactions += transaction
    }

    override suspend fun insertTransactions(transactions: List<TransactionEntity>) {
        insertedTransactions += transactions
    }

    override suspend fun getLatestTransactionByCardIdm(cardIdm: String): TransactionEntity? =
        insertedTransactions.lastOrNull { it.cardIdm == cardIdm }

    override suspend fun getTransactionsByCardIdm(cardIdm: String): List<TransactionEntity> =
        insertedTransactions.filter { it.cardIdm == cardIdm }

    override suspend fun getTransactionsByCardIdmPaginated(
        cardIdm: String,
        limit: Int,
        offset: Int,
    ): List<TransactionEntity> = emptyList()

    override suspend fun getTransactionAtPosition(
        cardIdm: String,
        position: Int,
    ): TransactionEntity? = null

    override suspend fun getLastOrder(): Int? = insertedTransactions.size.takeIf { it > 0 }

    override suspend fun getTransactionCountByCardIdm(cardIdm: String): Int = insertedTransactions.count { it.cardIdm == cardIdm }

    override suspend fun deleteTransactionsByCardIdm(cardIdm: String) = Unit
}

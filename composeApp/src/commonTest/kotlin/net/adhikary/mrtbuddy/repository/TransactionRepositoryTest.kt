package net.adhikary.mrtbuddy.repository

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.Transaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionRepositoryTest {
    private fun sampleTransaction() =
        Transaction(
            fixedHeader = "08 52 10 00",
            timestamp = LocalDateTime(2025, 1, 5, 9, 0),
            transactionType = "48 00",
            fromStation = "Uttara North",
            toStation = "Motijheel",
            balance = 340,
            trailing = "00 00",
        )

    private fun repository(
        cardDao: FakeCardDao,
        scanDao: FakeScanDao,
        transactionDao: FakeTransactionDao,
    ) = TransactionRepository(cardDao = cardDao, scanDao = scanDao, transactionDao = transactionDao)

    @Test
    fun blankIdmPersistsNothing() =
        runTest {
            val cardDao = FakeCardDao()
            val scanDao = FakeScanDao()
            val transactionDao = FakeTransactionDao()
            val repository = repository(cardDao, scanDao, transactionDao)

            repository.saveCardReadResult(
                CardReadResult(idm = "", transactions = listOf(sampleTransaction())),
            )

            assertTrue(cardDao.insertedCards.isEmpty(), "no card should be inserted for a blank idm")
            assertTrue(scanDao.insertedScans.isEmpty(), "no scan should be inserted for a blank idm")
            assertTrue(transactionDao.insertedTransactions.isEmpty(), "no transactions for a blank idm")
        }

    @Test
    fun validIdmPersistsCardScanAndTransactions() =
        runTest {
            val cardDao = FakeCardDao()
            val scanDao = FakeScanDao()
            val transactionDao = FakeTransactionDao()
            val repository = repository(cardDao, scanDao, transactionDao)

            repository.saveCardReadResult(
                CardReadResult(idm = "D3 A0 00 00 00 00 00 01", transactions = listOf(sampleTransaction())),
            )

            assertEquals(1, cardDao.insertedCards.size)
            assertEquals("D3 A0 00 00 00 00 00 01", cardDao.insertedCards.first().idm)
            assertEquals(1, scanDao.insertedScans.size)
            assertEquals(1, transactionDao.insertedTransactions.size)
            assertEquals("D3 A0 00 00 00 00 00 01", transactionDao.insertedTransactions.first().cardIdm)
        }
}

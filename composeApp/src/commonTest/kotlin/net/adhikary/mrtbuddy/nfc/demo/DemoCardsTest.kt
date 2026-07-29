package net.adhikary.mrtbuddy.nfc.demo

import kotlinx.coroutines.test.runTest
import net.adhikary.mrtbuddy.nfc.FakeCardTransceiver
import net.adhikary.mrtbuddy.nfc.FelicaReadResult
import net.adhikary.mrtbuddy.nfc.FelicaReader
import net.adhikary.mrtbuddy.nfc.parser.ByteParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DemoCardsTest {
    private val baseYear = DemoCards.BASE_YEAR

    @Test
    fun everyBlockIsSixteenBytes() {
        DemoCards.blocks.forEach { block ->
            assertEquals(16, block.size)
        }
    }

    @Test
    fun idmIsTheRecognizableDemoIdentity() {
        assertEquals("D3 A0 00 00 00 00 00 01", ByteParser.toHexString(DemoCards.IDM))
    }

    @Test
    fun parsesThroughProductionPipelineIntoExpectedTransactions() =
        runTest {
            val transceiver =
                FakeCardTransceiver(
                    idm = DemoCards.IDM,
                    responses = mapOf(0 to FelicaReadResult(0, 0, DemoCards.blocks)),
                )

            val result = FelicaReader(transceiver).readTransactionHistory(baseYear)

            assertEquals("D3 A0 00 00 00 00 00 01", result.idm)
            assertEquals(8, result.transactions.size)

            val latest = result.transactions.first()
            assertEquals("08 52 10 00", latest.fixedHeader)
            assertEquals("Uttara North", latest.fromStation)
            assertEquals("Motijheel", latest.toStation)
            assertEquals(340, latest.balance)
            assertEquals(2026, latest.timestamp.year)
            assertEquals(1, latest.timestamp.monthNumber)
            assertEquals(5, latest.timestamp.dayOfMonth)
            assertEquals(9, latest.timestamp.hour)
        }

    @Test
    fun containsAllRequiredTransactionKinds() =
        runTest {
            val transceiver =
                FakeCardTransceiver(
                    idm = DemoCards.IDM,
                    responses = mapOf(0 to FelicaReadResult(0, 0, DemoCards.blocks)),
                )

            val transactions = FelicaReader(transceiver).readTransactionHistory(baseYear).transactions
            val headers = transactions.map { it.fixedHeader }

            assertTrue(headers.count { it == "08 52 10 00" } >= 4, "expected >= 4 metro commutes")
            assertTrue(headers.contains("08 D2 20 00"), "expected a Hatirjheel bus start")
            assertTrue(headers.contains("42 D6 30 00"), "expected a Hatirjheel bus end")
            assertTrue(headers.contains("1D 60 02 01"), "expected a balance update")
        }

    @Test
    fun allTransactionsArePost2020AndChronologicallyDescending() =
        runTest {
            val transceiver =
                FakeCardTransceiver(
                    idm = DemoCards.IDM,
                    responses = mapOf(0 to FelicaReadResult(0, 0, DemoCards.blocks)),
                )

            val transactions = FelicaReader(transceiver).readTransactionHistory(baseYear).transactions

            transactions.forEach { assertTrue(it.timestamp.year >= 2025) }

            // Newest first: each transaction is not newer than its predecessor.
            transactions.zipWithNext().forEach { (newer, older) ->
                assertTrue(newer.timestamp >= older.timestamp, "timestamps must be newest-first")
            }

            // Chronologically (oldest -> newest) the balance is spent down after the top-up.
            val oldestToNewest = transactions.reversed().map { it.balance }
            assertEquals(1000, oldestToNewest.first(), "oldest event is the top-up")
            assertEquals(340, oldestToNewest.last(), "newest balance is the latest remaining balance")
        }
}

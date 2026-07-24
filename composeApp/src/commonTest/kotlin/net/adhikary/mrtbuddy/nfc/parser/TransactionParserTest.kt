package net.adhikary.mrtbuddy.nfc.parser

import kotlinx.datetime.LocalDateTime
import net.adhikary.mrtbuddy.nfc.FelicaFixtures
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TransactionParserTest {
    private val baseYear = FelicaFixtures.BASE_YEAR

    @Test
    fun parseBlockExtractsAllFields() {
        val block =
            FelicaFixtures.block(
                header = FelicaFixtures.METRO_HEADER,
                timestampValue = FelicaFixtures.encodeTimestamp(yearOffset = 24, month = 7, day = 15, hour = 14),
                fromStation = 90,
                toStation = 10,
                balance = 5000,
                trailing = byteArrayOf(0xAA.toByte(), 0xBB.toByte()),
            )

        val transaction = TransactionParser.parseTransactionBlock(block, baseYear)

        assertEquals("08 52 10 00", transaction.fixedHeader)
        assertEquals(LocalDateTime(2024, 7, 15, 14, 0), transaction.timestamp)
        assertEquals("Uttara North", transaction.fromStation)
        assertEquals("Motijheel", transaction.toStation)
        assertEquals(5000, transaction.balance)
        assertEquals("AA BB", transaction.trailing)
    }

    @Test
    fun parseBlockRejectsWrongSize() {
        assertFailsWith<IllegalArgumentException> {
            TransactionParser.parseTransactionBlock(ByteArray(15), baseYear)
        }
    }

    @Test
    fun parseResponseReturnsAllValidBlocks() {
        val frame = FelicaFixtures.frame(listOf(FelicaFixtures.metroBlock, FelicaFixtures.busStartBlock))

        val transactions = TransactionParser.parseTransactionResponse(frame, baseYear)

        assertEquals(2, transactions.size)
        assertEquals("08 52 10 00", transactions[0].fixedHeader)
        assertEquals("08 D2 20 00", transactions[1].fixedHeader)
    }

    @Test
    fun parseResponseReturnsEmptyForShortFrame() {
        val transactions = TransactionParser.parseTransactionResponse(ByteArray(12), baseYear)
        assertTrue(transactions.isEmpty())
    }

    @Test
    fun parseResponseReturnsEmptyForErrorStatusFlags() {
        val frame = FelicaFixtures.frame(listOf(FelicaFixtures.metroBlock), statusFlag1 = 0x01)

        val transactions = TransactionParser.parseTransactionResponse(frame, baseYear)

        assertTrue(transactions.isEmpty())
    }

    @Test
    fun parseResponseReturnsEmptyForIncompleteBlocks() {
        val fullFrame = FelicaFixtures.frame(listOf(FelicaFixtures.metroBlock))
        val truncated = fullFrame.copyOfRange(0, 20)

        val transactions = TransactionParser.parseTransactionResponse(truncated, baseYear)

        assertTrue(transactions.isEmpty())
    }

    @Test
    fun parseValidTransactionsSkipsMalformedBlocks() {
        val result =
            net.adhikary.mrtbuddy.nfc.FelicaReadResult(
                statusFlag1 = 0,
                statusFlag2 = 0,
                blocks = listOf(ByteArray(15), FelicaFixtures.metroBlock),
            )

        val transactions = TransactionParser.parseValidTransactions(result, baseYear)

        assertEquals(1, transactions.size)
        assertEquals("08 52 10 00", transactions[0].fixedHeader)
    }

    @Test
    fun parseResponseFiltersPre2020Transactions() {
        val frame = FelicaFixtures.frame(listOf(FelicaFixtures.metroBlock, FelicaFixtures.pre2020Block))

        val transactions = TransactionParser.parseTransactionResponse(frame, baseYear)

        assertEquals(1, transactions.size)
        assertEquals(2024, transactions[0].timestamp.year)
    }
}

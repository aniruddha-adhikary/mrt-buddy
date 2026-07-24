package net.adhikary.mrtbuddy.nfc

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FelicaReaderTest {
    private val baseYear = FelicaFixtures.BASE_YEAR

    @Test
    fun readsTransactionsFromBothWindows() =
        runTest {
            val transceiver =
                FakeCardTransceiver(
                    responses =
                        mapOf(
                            0 to FelicaReadResult(0, 0, listOf(FelicaFixtures.metroBlock)),
                            10 to FelicaReadResult(0, 0, listOf(FelicaFixtures.busStartBlock)),
                        ),
                )

            val result = FelicaReader(transceiver).readTransactionHistory(baseYear)

            assertEquals("01 02 03 04 05 06 07 08", result.idm)
            assertEquals(2, result.transactions.size)
            assertEquals("08 52 10 00", result.transactions[0].fixedHeader)
            assertEquals("08 D2 20 00", result.transactions[1].fixedHeader)
        }

    @Test
    fun skipsWindowWithErrorStatusFlags() =
        runTest {
            val transceiver =
                FakeCardTransceiver(
                    responses =
                        mapOf(
                            0 to FelicaReadResult(1, 0, listOf(FelicaFixtures.metroBlock)),
                            10 to FelicaReadResult(0, 0, listOf(FelicaFixtures.busEndBlock)),
                        ),
                )

            val result = FelicaReader(transceiver).readTransactionHistory(baseYear)

            assertEquals(1, result.transactions.size)
            assertEquals("42 D6 30 00", result.transactions[0].fixedHeader)
        }

    @Test
    fun returnsPartialResultsWhenSecondWindowThrows() =
        runTest {
            val transceiver =
                FakeCardTransceiver(
                    responses = mapOf(0 to FelicaReadResult(0, 0, listOf(FelicaFixtures.metroBlock))),
                    throwOnStartBlock = 10,
                )

            val result = FelicaReader(transceiver).readTransactionHistory(baseYear)

            assertEquals(1, result.transactions.size)
            assertEquals("08 52 10 00", result.transactions[0].fixedHeader)
        }

    @Test
    fun returnsEmptyForCardWithNoBlocks() =
        runTest {
            val transceiver = FakeCardTransceiver()

            val result = FelicaReader(transceiver).readTransactionHistory(baseYear)

            assertEquals("01 02 03 04 05 06 07 08", result.idm)
            assertTrue(result.transactions.isEmpty())
        }
}

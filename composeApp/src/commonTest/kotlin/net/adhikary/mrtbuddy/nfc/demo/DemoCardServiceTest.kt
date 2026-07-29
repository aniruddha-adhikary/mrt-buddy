package net.adhikary.mrtbuddy.nfc.demo

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.CardState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DemoCardServiceTest {
    @Test
    fun scanEmitsReadingThenResultThenBalance() =
        runTest(UnconfinedTestDispatcher()) {
            val service = DemoCardService()
            val states = mutableListOf<CardState>()
            val results = mutableListOf<CardReadResult?>()
            backgroundScope.launch { service.cardState.collect { states.add(it) } }
            backgroundScope.launch { service.cardReadResults.collect { results.add(it) } }

            service.scanDemoCard()

            assertEquals(CardState.Reading, states.first())
            val balance = states.last()
            assertTrue(balance is CardState.Balance)
            assertEquals(340, balance.amount)

            val result = results.last()
            assertTrue(result != null)
            assertEquals("D3 A0 00 00 00 00 00 01", result.idm)
            assertEquals(8, result.transactions.size)
        }

    @Test
    fun repeatScansReuseTheSameDemoIdentity() =
        runTest(UnconfinedTestDispatcher()) {
            val service = DemoCardService()
            val results = mutableListOf<CardReadResult?>()
            backgroundScope.launch { service.cardReadResults.collect { results.add(it) } }

            service.scanDemoCard()
            service.scanDemoCard()

            val emitted = results.filterNotNull()
            assertTrue(emitted.size >= 2)
            assertTrue(emitted.all { it.idm == "D3 A0 00 00 00 00 00 01" })
        }
}

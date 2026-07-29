package net.adhikary.mrtbuddy.nfc

import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RecordingCardTransceiverTest {
    @AfterTest
    fun reset() {
        NfcDumpRecorder.resetForTests()
    }

    @Test
    fun passesReadThroughUnchanged() =
        runTest {
            NfcDumpRecorder.enabled = false
            val delegate =
                FakeCardTransceiver(
                    responses = mapOf(0 to FelicaReadResult(0, 0, listOf(FelicaFixtures.metroBlock))),
                )
            val recording = RecordingCardTransceiver(delegate)

            val result = recording.readBlocks(FelicaReader.SERVICE_CODE, 0, FelicaReader.BLOCKS_PER_WINDOW)

            assertEquals(delegate.idm.toList(), recording.idm.toList())
            assertEquals(1, result.blocks.size)
            assertTrue(result.isSuccess)
        }

    @Test
    fun recordsWindowWhenEnabled() =
        runTest {
            NfcDumpRecorder.enabled = true
            NfcDumpRecorder.startSession("android")
            val delegate =
                FakeCardTransceiver(
                    responses = mapOf(0 to FelicaReadResult(0, 0, listOf(FelicaFixtures.metroBlock))),
                )
            val recording = RecordingCardTransceiver(delegate)

            recording.readBlocks(FelicaReader.SERVICE_CODE, 0, FelicaReader.BLOCKS_PER_WINDOW)

            val text = NfcDumpRecorder.lastDumpText()!!
            assertTrue(text.contains("window serviceCode=0x220F start=0 count=10 status=00 00"))
            assertTrue(text.contains("block 00:"))
        }

    @Test
    fun recordsNothingWhenDisabled() =
        runTest {
            NfcDumpRecorder.enabled = false
            NfcDumpRecorder.startSession("android")
            val delegate =
                FakeCardTransceiver(
                    responses = mapOf(0 to FelicaReadResult(0, 0, listOf(FelicaFixtures.metroBlock))),
                )
            val recording = RecordingCardTransceiver(delegate)

            recording.readBlocks(FelicaReader.SERVICE_CODE, 0, FelicaReader.BLOCKS_PER_WINDOW)

            assertNull(NfcDumpRecorder.lastDumpText())
        }
}

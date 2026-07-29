package net.adhikary.mrtbuddy.nfc

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NfcDumpRecorderTest {
    @AfterTest
    fun reset() {
        NfcDumpRecorder.resetForTests()
    }

    private val block0 =
        byteArrayOf(
            0x08, 0x52, 0x10, 0x00, 0x1A, 0x0D, 0x48, 0x00,
            0x5A, 0x00, 0x0A, 0x54, 0x01, 0x00, 0x00, 0x00,
        )
    private val block1 =
        byteArrayOf(
            0x08, 0x52, 0x10, 0x00, 0x1A, 0x0C, 0x90.toByte(), 0x00,
            0x0A, 0x00, 0x5A, 0xB8.toByte(), 0x01, 0x00, 0x00, 0x00,
        )

    @Test
    fun producesExactV1TextWithAnonymizedIdm() {
        NfcDumpRecorder.enabled = true
        NfcDumpRecorder.startSession("android")
        NfcDumpRecorder.record(
            NfcDumpRecorder.Window(
                serviceCode = 0x220F,
                startBlock = 0,
                count = 10,
                statusFlag1 = 0,
                statusFlag2 = 0,
                blocks = listOf(block0, block1),
            ),
        )

        val expected =
            "# MRT Buddy NFC dump v1\n" +
                "platform: android\n" +
                "idm: 00 00 00 00 00 00 00 00 (anonymized)\n" +
                "window serviceCode=0x220F start=0 count=10 status=00 00\n" +
                "block 00: 08 52 10 00 1A 0D 48 00 5A 00 0A 54 01 00 00 00\n" +
                "block 01: 08 52 10 00 1A 0C 90 00 0A 00 5A B8 01 00 00 00"

        assertEquals(expected, NfcDumpRecorder.lastDumpText())
    }

    @Test
    fun formatsNonZeroStatusFlagsAsUppercaseHex() {
        NfcDumpRecorder.enabled = true
        NfcDumpRecorder.startSession("ios")
        NfcDumpRecorder.record(
            NfcDumpRecorder.Window(
                serviceCode = 0x220F,
                startBlock = 10,
                count = 10,
                statusFlag1 = 0xA1,
                statusFlag2 = 0xB2,
                blocks = emptyList(),
            ),
        )

        val text = NfcDumpRecorder.lastDumpText()
        assertTrue(text!!.contains("window serviceCode=0x220F start=10 count=10 status=A1 B2"))
    }

    @Test
    fun recordsNothingWhenDisabled() {
        NfcDumpRecorder.enabled = false
        NfcDumpRecorder.startSession("android")
        NfcDumpRecorder.record(
            NfcDumpRecorder.Window(0x220F, 0, 10, 0, 0, listOf(block0)),
        )

        assertNull(NfcDumpRecorder.lastDumpText())
    }

    @Test
    fun newSessionClearsPreviousWindows() {
        NfcDumpRecorder.enabled = true
        NfcDumpRecorder.startSession("android")
        NfcDumpRecorder.record(NfcDumpRecorder.Window(0x220F, 0, 10, 0, 0, listOf(block0)))

        NfcDumpRecorder.startSession("android")
        NfcDumpRecorder.record(NfcDumpRecorder.Window(0x220F, 10, 10, 0, 0, listOf(block1)))

        val text = NfcDumpRecorder.lastDumpText()!!
        assertTrue(text.contains("start=10"))
        assertTrue(!text.contains("start=0 "))
    }

    @Test
    fun failedSessionPreservesPreviousDump() {
        NfcDumpRecorder.enabled = true
        NfcDumpRecorder.startSession("android")
        NfcDumpRecorder.record(NfcDumpRecorder.Window(0x220F, 0, 10, 0, 0, listOf(block0)))

        // A later read starts but fails before recording any window.
        NfcDumpRecorder.startSession("ios")

        val text = NfcDumpRecorder.lastDumpText()!!
        assertTrue(text.contains("platform: android"))
        assertTrue(text.contains("block 00: 08 52 10 00 1A 0D 48 00 5A 00 0A 54 01 00 00 00"))
    }

    @Test
    fun captureDisabledSessionPreservesPreviousDump() {
        NfcDumpRecorder.enabled = true
        NfcDumpRecorder.startSession("android")
        NfcDumpRecorder.record(NfcDumpRecorder.Window(0x220F, 0, 10, 0, 0, listOf(block0)))

        // Capture is turned off, then a new read happens: nothing should be recorded or wiped.
        NfcDumpRecorder.enabled = false
        NfcDumpRecorder.startSession("ios")
        NfcDumpRecorder.record(NfcDumpRecorder.Window(0x220F, 10, 10, 0, 0, listOf(block1)))

        val text = NfcDumpRecorder.lastDumpText()!!
        assertTrue(text.contains("platform: android"))
        assertTrue(text.contains("start=0 "))
        assertTrue(!text.contains("start=10"))
    }

    @Test
    fun platformStaysWithRecordedSession() {
        NfcDumpRecorder.enabled = true
        NfcDumpRecorder.startSession("android")
        NfcDumpRecorder.record(NfcDumpRecorder.Window(0x220F, 0, 10, 0, 0, listOf(block0)))

        NfcDumpRecorder.startSession("ios")

        assertTrue(NfcDumpRecorder.lastDumpText()!!.contains("platform: android"))
    }

    @Test
    fun successfulNewSessionReplacesOld() {
        NfcDumpRecorder.enabled = true
        NfcDumpRecorder.startSession("android")
        NfcDumpRecorder.record(NfcDumpRecorder.Window(0x220F, 0, 10, 0, 0, listOf(block0)))

        NfcDumpRecorder.startSession("ios")
        NfcDumpRecorder.record(NfcDumpRecorder.Window(0x220F, 10, 10, 0, 0, listOf(block1)))

        val text = NfcDumpRecorder.lastDumpText()!!
        assertTrue(text.contains("platform: ios"))
        assertTrue(text.contains("start=10"))
        assertTrue(!text.contains("platform: android"))
        assertTrue(!text.contains("start=0 "))
    }
}

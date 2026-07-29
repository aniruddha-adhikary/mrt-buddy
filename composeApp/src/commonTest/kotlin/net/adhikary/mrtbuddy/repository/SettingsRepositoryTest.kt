package net.adhikary.mrtbuddy.repository

import com.russhwolf.settings.MapSettings
import net.adhikary.mrtbuddy.nfc.NfcDumpRecorder
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsRepositoryTest {
    @AfterTest
    fun reset() {
        NfcDumpRecorder.enabled = false
    }

    @Test
    fun nfcDumpCaptureDefaultsToFalseAndSyncsRecorder() {
        val repository = SettingsRepository(MapSettings())

        assertFalse(repository.nfcDumpCaptureEnabled.value)
        assertFalse(NfcDumpRecorder.enabled)
    }

    @Test
    fun setNfcDumpCapturePersistsFlowAndRecorder() {
        val settings = MapSettings()
        val repository = SettingsRepository(settings)

        repository.setNfcDumpCapture(true)

        assertTrue(repository.nfcDumpCaptureEnabled.value)
        assertTrue(NfcDumpRecorder.enabled)
        assertTrue(settings.getBoolean("nfc_dump_capture", false))
    }

    @Test
    fun initPushesPersistedValueIntoRecorder() {
        val settings = MapSettings().apply { putBoolean("nfc_dump_capture", true) }

        val repository = SettingsRepository(settings)

        assertTrue(repository.nfcDumpCaptureEnabled.value)
        assertEquals(true, NfcDumpRecorder.enabled)
    }

    @Test
    fun syncRecorderEnablesOnlyInDebugBuilds() {
        val repository = SettingsRepository(MapSettings())

        repository.syncRecorder(flagValue = true, debug = true)
        assertTrue(NfcDumpRecorder.enabled)

        repository.syncRecorder(flagValue = true, debug = false)
        assertFalse(NfcDumpRecorder.enabled)

        repository.syncRecorder(flagValue = false, debug = true)
        assertFalse(NfcDumpRecorder.enabled)
    }
}

package net.adhikary.mrtbuddy.ui.screens.developer

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.adhikary.mrtbuddy.nfc.NfcDumpRecorder
import net.adhikary.mrtbuddy.nfc.demo.DemoCardService
import net.adhikary.mrtbuddy.repository.SettingsRepository
import net.adhikary.mrtbuddy.utils.NfcDumpSharer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeveloperScreenViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    private class RecordingSharer : NfcDumpSharer {
        var shared: String? = null

        override fun share(dumpText: String) {
            shared = dumpText
        }
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        NfcDumpRecorder.enabled = false
        NfcDumpRecorder.startSession("")
    }

    private fun viewModel(sharer: NfcDumpSharer) =
        DeveloperScreenViewModel(
            settingsRepository = SettingsRepository(MapSettings()),
            demoCardService = DemoCardService(),
            nfcDumpSharer = sharer,
        )

    @Test
    fun scanDemoCardEmitsBalanceSnackbar() =
        runTest(dispatcher) {
            val sharer = RecordingSharer()
            val vm = viewModel(sharer)
            val events = mutableListOf<DeveloperScreenEvent>()
            backgroundScope.launch { vm.events.collect { events.add(it) } }

            vm.onAction(DeveloperScreenAction.ScanDemoCard)

            val snackbar = events.filterIsInstance<DeveloperScreenEvent.ShowSnackbar>().first()
            assertEquals("Demo card scanned — ৳340. See the Balance tab.", snackbar.message)
            assertNull(sharer.shared)
        }

    @Test
    fun shareLastDumpWithNoDumpEmitsSnackbar() =
        runTest(dispatcher) {
            NfcDumpRecorder.enabled = false
            NfcDumpRecorder.startSession("")
            val sharer = RecordingSharer()
            val vm = viewModel(sharer)
            val events = mutableListOf<DeveloperScreenEvent>()
            backgroundScope.launch { vm.events.collect { events.add(it) } }

            vm.onAction(DeveloperScreenAction.ShareLastDump)

            val snackbar = events.filterIsInstance<DeveloperScreenEvent.ShowSnackbar>().first()
            assertEquals("No dump captured yet — enable capture and scan a card", snackbar.message)
            assertNull(sharer.shared)
        }
}

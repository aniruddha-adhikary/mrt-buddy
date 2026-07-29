package net.adhikary.mrtbuddy.ui.screens.developer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.nfc.NfcDumpRecorder
import net.adhikary.mrtbuddy.nfc.demo.DemoCardService
import net.adhikary.mrtbuddy.repository.SettingsRepository
import net.adhikary.mrtbuddy.utils.NfcDumpSharer

class DeveloperScreenViewModel(
    private val settingsRepository: SettingsRepository,
    private val demoCardService: DemoCardService,
    private val nfcDumpSharer: NfcDumpSharer,
) : ViewModel() {
    private val _state = MutableStateFlow(DeveloperScreenState())
    val state: StateFlow<DeveloperScreenState> get() = _state.asStateFlow()

    private val _events = Channel<DeveloperScreenEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        onAction(DeveloperScreenAction.OnInit)
    }

    @Suppress("TooGenericExceptionCaught")
    fun onAction(action: DeveloperScreenAction) {
        when (action) {
            is DeveloperScreenAction.OnInit -> {
                _state.value =
                    _state.value.copy(
                        nfcDumpCaptureEnabled = settingsRepository.nfcDumpCaptureEnabled.value,
                    )
            }

            is DeveloperScreenAction.ScanDemoCard -> {
                viewModelScope.launch {
                    val result = demoCardService.scanDemoCard()
                    val balance = result.transactions.firstOrNull()?.balance
                    if (balance != null) {
                        _events.send(
                            DeveloperScreenEvent.ShowSnackbar(
                                "Demo card scanned — ৳$balance. See the Balance tab.",
                            ),
                        )
                    }
                }
            }

            is DeveloperScreenAction.SetNfcDumpCapture -> {
                settingsRepository.setNfcDumpCapture(action.enabled)
                _state.value = _state.value.copy(nfcDumpCaptureEnabled = action.enabled)
            }

            is DeveloperScreenAction.ShareLastDump -> {
                viewModelScope.launch {
                    val dump = NfcDumpRecorder.lastDumpText()
                    if (dump == null) {
                        _events.send(
                            DeveloperScreenEvent.ShowSnackbar(
                                "No dump captured yet — enable capture and scan a card",
                            ),
                        )
                        return@launch
                    }
                    try {
                        nfcDumpSharer.share(dump)
                    } catch (e: Exception) {
                        _events.send(
                            DeveloperScreenEvent.ShowSnackbar(e.message ?: "Failed to share dump"),
                        )
                    }
                }
            }
        }
    }
}

package net.adhikary.mrtbuddy.nfc

import androidx.compose.runtime.Composable
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.CardState
import platform.CoreNFC.NFCFeliCaTagProtocol
import platform.CoreNFC.NFCPollingISO18092
import platform.CoreNFC.NFCTagReaderSession
import platform.CoreNFC.NFCTagReaderSessionDelegateProtocol
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.create
import platform.darwin.NSObject
import platform.darwin.nil
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toNSData(): NSData =
    this.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
    }

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun NSData.toByteArray(): ByteArray {
    val byteArray = ByteArray(this.length.toInt())
    this.bytes?.let { bytesPointer ->
        byteArray.usePinned { pinnedArray ->
            memcpy(pinnedArray.addressOf(0), bytesPointer, this.length)
        }
    }
    return byteArray
}

actual class NFCManager : NSObject(), NFCTagReaderSessionDelegateProtocol {
    private var session: NFCTagReaderSession? = null
    private val scope = CoroutineScope(SupervisorJob())

    private val _cardState = MutableSharedFlow<CardState>(replay = 1)
    actual val cardState: SharedFlow<CardState> = _cardState

    private val _cardReadResults = MutableSharedFlow<CardReadResult?>(replay = 1)
    actual val cardReadResults: SharedFlow<CardReadResult?> = _cardReadResults

    init {
        scope.launch {
            _cardState.emit(CardState.WaitingForTap)
        }
    }

    actual fun isEnabled(): Boolean = NFCTagReaderSession.readingAvailable()

    actual fun isSupported(): Boolean = NFCTagReaderSession.readingAvailable()

    @Composable
    actual fun startScan() {
        if (NFCTagReaderSession.readingAvailable()) {
            session = NFCTagReaderSession(NFCPollingISO18092, this, null)
            session?.alertMessage = "Hold your iPhone near your transit card"
            session?.beginSession()
        }
    }

    actual fun stopScan() {
        session?.invalidateSession()
        session = null
    }

    override fun tagReaderSessionDidBecomeActive(session: NFCTagReaderSession) {
    }

    override fun tagReaderSession(
        session: NFCTagReaderSession,
        didInvalidateWithError: NSError,
    ) {
        this.session = null
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    @Suppress("TooGenericExceptionCaught")
    override fun tagReaderSession(
        session: NFCTagReaderSession,
        didDetectTags: List<*>,
    ) {
        val tag = didDetectTags.firstOrNull() as? NFCFeliCaTagProtocol ?: return

        session.connectToTag(tag) { error ->
            if (error != nil) {
                println("Failed to connect to tag: ${error?.description}")
                return@connectToTag
            }

            scope.launch {
                try {
                    _cardState.emit(CardState.Reading)
                    val result = FelicaReader(FelicaTagTransceiver(tag)).readTransactionHistory()

                    if (result.transactions.isEmpty()) {
                        _cardState.emit(CardState.Error("No transactions found on card"))
                    } else {
                        _cardReadResults.emit(result)
                        val latestBalance = result.transactions.firstOrNull()?.balance
                        latestBalance?.let {
                            _cardState.emit(CardState.Balance(it))
                        } ?: run {
                            _cardState.emit(CardState.Error("Balance not found. You may have moved the card too fast."))
                        }
                    }
                } catch (e: Exception) {
                    _cardState.emit(CardState.Error(e.message ?: "Unknown error occurred"))
                    _cardReadResults.emit(CardReadResult("", emptyList()))
                } finally {
                    session.invalidateSession()
                }
            }
        }
    }
}

@Composable
actual fun getNFCManager(): NFCManager {
    return androidx.compose.runtime.remember {
        NFCManager()
    }
}

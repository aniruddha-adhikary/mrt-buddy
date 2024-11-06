package net.adhikary.mrtbuddy.nfc

import androidx.compose.runtime.Composable
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.parser.ByteParser
import net.adhikary.mrtbuddy.nfc.parser.TransactionParser
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService
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
fun ByteArray.toNSData(): NSData = this.usePinned { pinned ->
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

    private val byteParser = ByteParser()
    private val timestampService = TimestampService()
    private val stationService = StationService()
    private val transactionParser = TransactionParser(byteParser, timestampService, stationService)

    private val _cardState = MutableStateFlow<CardState>(CardState.WaitingForTap)
    actual val cardState: StateFlow<CardState> = _cardState

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    actual val transactions: StateFlow<List<Transaction>> = _transactions

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

    override fun tagReaderSession(session: NFCTagReaderSession, didInvalidateWithError: NSError) {
        this.session = null
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override fun tagReaderSession(session: NFCTagReaderSession, didDetectTags: List<*>) {
        val tag = didDetectTags.firstOrNull() as? NFCFeliCaTagProtocol ?: return

        session.connectToTag(tag) { error ->
            if (error != nil) {
                println("Failed to connect to tag: ${error?.description}")
                return@connectToTag
            }

            scope.launch {
                _cardState.emit(CardState.Reading)
            }

            val serviceCodeList = listOf(byteArrayOf(0x22, 0x0f).reversedArray())

            val blockList = (0 until 10).map { byteArrayOf(0x80.toByte(), it.toByte()) }

            tag.readWithoutEncryptionWithServiceCodeList(
                serviceCodeList = serviceCodeList.map { it.toNSData() },
                blockList = blockList.map { it.toNSData() },
                completionHandler = { statusFlag1, statusFlag2, dataList, error ->
                    if (error != nil) {
                        session.invalidateSessionWithErrorMessage("Card reading failed")

                        scope.launch {
                            _cardState.emit(CardState.Error("Card reading failed"))
                        }

                        return@readWithoutEncryptionWithServiceCodeList
                    }

                    val entries: List<Transaction>? = (dataList)
                        ?.map { (it as NSData).toByteArray() }
                        ?.map { transactionParser.parseTransactionBlock(it) }

                    if (entries == null) {
                        scope.launch {
                            _cardState.emit(CardState.Error("No transactions found on card"))
                        }
                    } else {
                        scope.launch {
                            //_cardState.tryEmit(CardState.Balance(234))
                            _transactions.emit(entries)
                            val latestBalance = entries.firstOrNull()?.balance

                            latestBalance?.let {
                               _cardState.emit(CardState.Balance(it))
                               // session.alertMessage = "Balance: $latestBalance BDT"
                            } ?: run {
                                _cardState.emit(CardState.Error("Balance not found. " +
                                        "You may have moved the card too fast."))
                            }
                        }
                    }
                    session.invalidateSession()
                }
            )
        }
    }
}


@Composable
actual fun getNFCManager(): NFCManager {
    return NFCManager()
}

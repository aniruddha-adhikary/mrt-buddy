// iOS Implementation (NFCManager.ios.kt)
package net.adhikary.mrtbuddy.nfc

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import platform.CoreNFC.*
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.darwin.NSObject
import platform.posix.memcpy

actual class NFCManager : NSObject(), NFCTagReaderSessionDelegateProtocol {
    private var session: NFCTagReaderSession? = null
    private val scope = CoroutineScope(SupervisorJob())
    private val commandGenerator = NfcCommandGenerator()

    private val _cardState = MutableSharedFlow<CardState>()
    private val _transactions = MutableSharedFlow<List<Transaction>>()

    actual val cardState: SharedFlow<CardState> = _cardState
    actual val transactions: SharedFlow<List<Transaction>> = _transactions

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
        println("NFC session became active")
    }

    override fun tagReaderSession(session: NFCTagReaderSession, didInvalidateWithError: NSError) {
        println("NFC session error: ${didInvalidateWithError.description}")
        this.session = null
    }

    override fun tagReaderSession(session: NFCTagReaderSession, didDetectTags: List<*>) {
        val tag = didDetectTags.firstOrNull() as? NFCTagProtocol ?: return
        
        session.connectToTag(tag) { error ->
            if (error != null) {
                println("Failed to connect to tag: ${error.description}")
                return@connectToTag
            }
            
            scope.launch {
                _cardState.emit(CardState.Reading)
                // Here you would implement the actual card reading logic
                // using the commandGenerator and parsing the responses
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val data = this
    val d = memScoped { data }
    return ByteArray(d.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), d.bytes, d.length)
        }
    }
}

@Composable
actual fun getNFCManager(): NFCManager {
    return NFCManager()
}

package net.adhikary.mrtbuddy.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class NFCManager actual constructor() {
    private var nfcAdapter: NfcAdapter? = null
    private val nfcReader = NfcReader()
    private val scope = CoroutineScope(SupervisorJob())

    private val _cardState = MutableSharedFlow<CardState>()
    private val _transactions = MutableSharedFlow<List<Transaction>>()

    actual val cardState: SharedFlow<CardState> = _cardState
    actual val transactions: SharedFlow<List<Transaction>> = _transactions

    private var pendingIntent: PendingIntent? = null
    
    private val nfcStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                NfcAdapter.ACTION_ADAPTER_STATE_CHANGED -> {
                    val state = intent.getIntExtra(
                        NfcAdapter.EXTRA_ADAPTER_STATE,
                        NfcAdapter.STATE_OFF
                    )
                    when (state) {
                        NfcAdapter.STATE_ON -> {
                            scope.launch {
                                _cardState.emit(CardState.WaitingForTap)
                            }
                            setupForegroundDispatch(context as Activity)
                        }
                        NfcAdapter.STATE_OFF -> {
                            scope.launch {
                                _cardState.emit(CardState.NfcDisabled)
                            }
                            disableForegroundDispatch(context as Activity)
                        }
                    }
                }
            }
        }
    }

    private val readerCallback = NfcAdapter.ReaderCallback { tag ->
        scope.launch {
            _cardState.emit(CardState.Reading)
        }
        readFelicaCard(tag)
    }

    actual fun isEnabled(): Boolean = nfcAdapter?.isEnabled == true
    actual fun isSupported(): Boolean = nfcAdapter != null

    @Composable
    actual fun startScan() {
        val context = LocalContext.current as Activity

        DisposableEffect(Unit) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(context)

            // Register NFC state receiver
            context.registerReceiver(
                nfcStateReceiver,
                IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
            )

            // Update initial state
            scope.launch {
                _cardState.emit(
                    when {
                        nfcAdapter == null -> CardState.NoNfcSupport
                        !nfcAdapter!!.isEnabled -> CardState.NfcDisabled
                        else -> CardState.WaitingForTap
                    }
                )
            }

            // Enable reader mode
            if (nfcAdapter?.isEnabled == true) {
                nfcAdapter?.enableReaderMode(
                    context,
                    readerCallback,
                    NfcAdapter.FLAG_READER_NFC_F,
                    null
                )
            }

            onDispose {
                stopScan()
                try {
                    context.unregisterReceiver(nfcStateReceiver)
                } catch (e: IllegalArgumentException) {
                    // Receiver not registered
                }
            }
        }
    }

    actual fun stopScan() {
        nfcAdapter?.disableReaderMode(null)
    }
    
    private fun setupForegroundDispatch(activity: Activity) {
        if (nfcAdapter?.isEnabled == true) {
            val intent = Intent(activity, activity.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            pendingIntent = PendingIntent.getActivity(
                activity, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))
            val techList = arrayOf(arrayOf(NfcF::class.java.name))
            nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, filters, techList)
        }
    }

    private fun disableForegroundDispatch(activity: Activity) {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    fun onNewIntent(intent: Intent) {
        // Only process if NFC is enabled
        if (nfcAdapter?.isEnabled != true) {
            scope.launch {
                _cardState.emit(CardState.NfcDisabled)
            }
            return
        }

        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        tag?.let {
            scope.launch {
                _cardState.emit(CardState.Reading)
            }
            readFelicaCard(it)
        } ?: run {
            scope.launch {
                _cardState.emit(CardState.WaitingForTap)
                _transactions.emit(emptyList())
            }
        }
    }

    private fun readFelicaCard(tag: Tag) {
        val nfcF = NfcF.get(tag)
        try {
            nfcF.connect()
            val transactions = nfcReader.readTransactionHistory(nfcF)
            nfcF.close()

            scope.launch {
                _transactions.emit(transactions)
                val latestBalance = transactions.firstOrNull()?.balance
                latestBalance?.let {
                    _cardState.emit(CardState.Balance(it))
                } ?: run {
                    _cardState.emit(CardState.Error("Balance not found. You moved the card too fast."))
                }
            }
        } catch (e: Exception) {
            scope.launch {
                _cardState.emit(CardState.Error(e.message ?: "Unknown error occurred"))
                _transactions.emit(emptyList())
            }
        }
    }
}

@Composable
actual fun getNFCManager(): NFCManager {
    return NFCManager()
}
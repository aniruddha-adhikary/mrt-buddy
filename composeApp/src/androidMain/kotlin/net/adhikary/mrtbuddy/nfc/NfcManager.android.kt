package net.adhikary.mrtbuddy.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.nfc.tech.NfcF
import android.util.Log
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class NFCManager actual constructor() {
    private var nfcAdapter: NfcAdapter? = null
    private var activity: Activity? = null
    private val nfcReader = NfcReader()
    private val scope = CoroutineScope(SupervisorJob())

    private val _cardState = MutableStateFlow<CardState>(CardState.WaitingForTap)
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())

    actual val cardState: StateFlow<CardState> = _cardState
    actual val transactions: StateFlow<List<Transaction>> = _transactions

    private var pendingIntent: PendingIntent? = null

    private fun checkNfcSupport(context: Context): Boolean {
        // Check if device has NFC hardware support
        // To ensure the device has NFC support so that the bug of NFC turned off showing up even when the device doesn't support NFC
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            scope.launch {
                _cardState.emit(CardState.NoNfcSupport)
                _cardState.emit(CardState.Error("Device does not support NFC"))

            }
            Log.d("NFCManager", "Device does not have NFC hardware")
            return false
        }

        // Try to get NFC adapter
        val nfcManager = context.getSystemService(Context.NFC_SERVICE) as NfcManager
        nfcAdapter = nfcManager.defaultAdapter

        if (nfcAdapter == null) {
            scope.launch {
                _cardState.emit(CardState.NoNfcSupport)
//                _cardState.emit(CardState.Error("Device does not support NFC"))
            }
            Log.d("NFCManager", "NFC adapter is null")
            return false
        }

        return true
    }

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
                            activity?.let {
                                nfcAdapter?.enableReaderMode(
                                    it,
                                    readerCallback,
                                    NfcAdapter.FLAG_READER_NFC_F,
                                    null
                                )
                            }
                        }
                        NfcAdapter.STATE_OFF -> {
                            scope.launch {
                                _cardState.emit(CardState.NfcDisabled)
//                                _cardState.emit(CardState.Error("No nfc card detected"))
                            }
                            activity?.let {
                                nfcAdapter?.disableReaderMode(it)
                            }
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

        DisposableEffect(context) {
            activity = context  // Store the activity
            // Check NFC support first
            if (!checkNfcSupport(context)) {
                return@DisposableEffect onDispose { }
            }

            // Register NFC state receiver only if supported
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

            // Enable reader mode only if supported and enabled
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
        activity?.let {
            nfcAdapter?.disableReaderMode(it)
            activity = null  // Reset the activity reference
        }
    }


    fun onNewIntent(intent: Intent) {
        // First check if device supports NFC
        if (nfcAdapter == null) {
            scope.launch {
                _cardState.emit(CardState.NoNfcSupport)
            }
            return
        }

        // Then check if NFC is enabled
        if (!nfcAdapter!!.isEnabled) {
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
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val nfcManager = NFCManager()
        // Setup NFC manager with context if needed

        onDispose {
            // Cleanup NFC resources if needed
        }
    }
    return NFCManager()
}

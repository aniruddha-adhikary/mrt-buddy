package net.adhikary.mrtbuddy


import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcF
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.NfcReader
import net.adhikary.mrtbuddy.ui.components.MainScreen
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private val cardState = mutableStateOf<CardState>(CardState.WaitingForTap)
    private val transactionsState = mutableStateOf<List<Transaction>>(emptyList())
    private val nfcReader = NfcReader()

    // Broadcast receiver for NFC state changes
    private val nfcStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                NfcAdapter.ACTION_ADAPTER_STATE_CHANGED -> {
                    val state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF)
                    when (state) {
                        NfcAdapter.STATE_ON -> {
                            updateNfcState()
                            setupNfcForegroundDispatch()
                        }
                        NfcAdapter.STATE_OFF -> {
                            cardState.value = CardState.NfcDisabled
                            nfcAdapter?.disableForegroundDispatch(this@MainActivity)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()

        // Initialize NFC adapter and check initial state
        initializeNfcState()

        // Register NFC state change receiver
        registerNfcStateReceiver()

        //set statusbar icons for dark app bar color


        setContent {
            MRTBuddyTheme {
                val currentCardState by remember { cardState }
                val transactions by remember { transactionsState }

                updateStatusBarIcons(isSystemInDarkTheme())

                LaunchedEffect(Unit) {
                    intent?.let {
                        handleNfcIntent(it)
                    }
                }

                MainScreen(currentCardState, transactions)
            }
        }
    }

    private fun updateStatusBarIcons(systemInDarkTheme: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = systemInDarkTheme
        }
    }

    private fun registerNfcStateReceiver() {
        registerReceiver(
            nfcStateReceiver,
            IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
        )
    }

    private fun setupNfcForegroundDispatch() {
        if (nfcAdapter?.isEnabled == true) {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))
            val techList = arrayOf(arrayOf(NfcF::class.java.name))
            nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, techList)
        }
    }

    private fun initializeNfcState() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        updateNfcState()
    }

    override fun onResume() {
        super.onResume()
        // Update NFC state
        updateNfcState()
        // Setup NFC dispatch
        setupNfcForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        if (nfcAdapter != null) {
            nfcAdapter?.disableForegroundDispatch(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the broadcast receiver
        try {
            unregisterReceiver(nfcStateReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver not registered
        }
    }

    private fun updateNfcState() {
        cardState.value = when {
            nfcAdapter == null -> {
                CardState.NoNfcSupport
            }
            !nfcAdapter!!.isEnabled -> {
                CardState.NfcDisabled
            }
            else -> {
                // Only change to WaitingForTap if we're not already in a valid state
                when (cardState.value) {
                    is CardState.Balance,
                    is CardState.Reading -> cardState.value
                    else -> CardState.WaitingForTap
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Only process NFC intent if NFC is enabled
        if (nfcAdapter?.isEnabled == true) {
            cardState.value = CardState.Reading
            handleNfcIntent(intent)
        }
    }

    private fun handleNfcIntent(intent: Intent) {
        // Only process if NFC is enabled
        if (nfcAdapter?.isEnabled != true) {
            cardState.value = CardState.NfcDisabled
            return
        }

        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        tag?.let {
            readFelicaCard(it)
        } ?: run {
            cardState.value = CardState.WaitingForTap
            transactionsState.value = emptyList()
        }
    }

    private fun readFelicaCard(tag: Tag) {
        val nfcF = NfcF.get(tag)
        try {
            nfcF.connect()
            val transactions = nfcReader.readTransactionHistory(nfcF)
            nfcF.close()

            transactionsState.value = transactions
            val latestBalance = transactions.firstOrNull()?.balance
            latestBalance?.let {
                cardState.value = CardState.Balance(it)
            } ?: run {
                cardState.value = CardState.Error("Balance not found. You moved the card too fast.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cardState.value = CardState.Error(e.message ?: "Unknown error occurred")
            transactionsState.value = emptyList()
        }
    }
}
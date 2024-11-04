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
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

    private val nfcStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) {
                handleNfcAdapterStateChange(intent)
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()

        initializeNfcState()
        registerNfcStateReceiver()

        setContent {
            MRTBuddyTheme {
                val currentCardState by remember { cardState }
                val transactions by remember { transactionsState }

                LaunchedEffect(Unit) {
                    intent?.let { handleNfcIntent(it) }
                }

                MainScreen(currentCardState, transactions)
            }
        }
    }

    private fun registerNfcStateReceiver() {
        registerReceiver(nfcStateReceiver, IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED))
    }

    private fun handleNfcAdapterStateChange(intent: Intent) {
        val state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF)
        when (state) {
            NfcAdapter.STATE_ON -> {
                updateNfcState()
                setupNfcForegroundDispatch()
            }
            NfcAdapter.STATE_OFF -> {
                cardState.value = CardState.NfcDisabled
                nfcAdapter?.disableForegroundDispatch(this)
            }
        }
    }

    private fun setupNfcForegroundDispatch() {
        nfcAdapter?.takeIf { it.isEnabled }?.let {
            val pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))
            val techList = arrayOf(arrayOf(NfcF::class.java.name))
            it.enableForegroundDispatch(this, pendingIntent, filters, techList)
        }
    }

    private fun initializeNfcState() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        updateNfcState()
    }

    override fun onResume() {
        super.onResume()
        updateNfcState()
        setupNfcForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(nfcStateReceiver)
        } catch (_: IllegalArgumentException) {
            // Receiver not registered
        }
    }

    private fun updateNfcState() {
        cardState.value = when {
            nfcAdapter == null -> CardState.NoNfcSupport
            !nfcAdapter!!.isEnabled -> CardState.NfcDisabled
            else -> when (cardState.value) {
                is CardState.Balance, is CardState.Reading -> cardState.value
                else -> CardState.WaitingForTap
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (nfcAdapter?.isEnabled == true) {
            cardState.value = CardState.Reading
            handleNfcIntent(intent)
        }
    }

    private fun handleNfcIntent(intent: Intent) {
        if (nfcAdapter?.isEnabled != true) {
            cardState.value = CardState.NfcDisabled
            return
        }

        val tag: Tag? = intent.extras?.getParcelable(NfcAdapter.EXTRA_TAG, Tag::class.java)
            ?: intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

        tag?.let { readFelicaCard(it) }
            ?: run {
                cardState.value = CardState.Error("No MRT Pass / Rapid Pass detected")
                transactionsState.value = emptyList()
            }
    }

    private fun readFelicaCard(tag: Tag) {
        val nfcF = NfcF.get(tag)
        try {
            nfcF.connect()
            val transactions = nfcReader.readTransactionHistory(nfcF)
            transactionsState.value = transactions

            transactions.firstOrNull()?.balance?.let {
                cardState.value = CardState.Balance(it)
            } ?: run {
                cardState.value = CardState.Error("Balance not found. You moved the card too fast.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cardState.value = CardState.Error(e.message ?: "Unknown error occurred")
            transactionsState.value = emptyList()
        } finally {
            nfcF.close()
        }
    }
}

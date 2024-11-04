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
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.model.CardAlias
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.NfcReader
import net.adhikary.mrtbuddy.ui.components.MainScreen
import net.adhikary.mrtbuddy.ui.screens.CardAliasScreen
import net.adhikary.mrtbuddy.ui.screens.FareCalculatorScreenNative
import net.adhikary.mrtbuddy.ui.screens.MetroScheduleScreen
import net.adhikary.mrtbuddy.ui.screens.MonthlyReportsScreen
import net.adhikary.mrtbuddy.ui.screens.StationsMapScreen
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme
import net.adhikary.mrtbuddy.viewmodel.CardAliasViewModel

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private val cardState = mutableStateOf<CardState>(CardState.WaitingForTap)
    private val transactionsState = mutableStateOf<List<Transaction>>(emptyList())
    private val nfcReader = NfcReader()
    private val currentScreen = mutableStateOf("card-reader")
    private val cardAliasViewModel: CardAliasViewModel by viewModels()

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

        setContent {
            MRTBuddyTheme {
                val currentCardState by remember { cardState }
                val transactions by remember { transactionsState }
                val screen by remember { currentScreen }

                LaunchedEffect(Unit) {
                    intent?.let {
                        handleNfcIntent(it)
                    }
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.CreditCard, contentDescription = "Card Reader") },
                                label = { Text("Card Reader") },
                                selected = screen == "card-reader",
                                onClick = { currentScreen.value = "card-reader" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculator") },
                                label = { Text("Calculator") },
                                selected = screen == "calculator",
                                onClick = { currentScreen.value = "calculator" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.History, contentDescription = "Monthly Reports") },
                                label = { Text("Reports") },
                                selected = screen == "reports",
                                onClick = { currentScreen.value = "reports" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Map, contentDescription = "Stations Map") },
                                label = { Text("Map") },
                                selected = screen == "map",
                                onClick = { currentScreen.value = "map" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Person, contentDescription = "Card Aliases") },
                                label = { Text("Aliases") },
                                selected = screen == "aliases",
                                onClick = { currentScreen.value = "aliases" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Schedule, contentDescription = "Schedule") },
                                label = { Text("Schedule") },
                                selected = screen == "schedule",
                                onClick = { currentScreen.value = "schedule" }
                            )
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        when (screen) {
                            "card-reader" -> MainScreen(
                                currentCardState,
                                transactions,
                                onNavigateToCalculator = { currentScreen.value = "calculator" }
                            )
                            "calculator" -> FareCalculatorScreenNative(
                                onNavigateToCardReader = { currentScreen.value = "card-reader" }
                            )
                            "reports" -> MonthlyReportsScreen(transactions = transactions)
                            "map" -> StationsMapScreen()
                            "aliases" -> CardAliasScreen(
                                viewModel = cardAliasViewModel,
                                onNavigateBack = { currentScreen.value = "card-reader" }
                            )
                            "schedule" -> MetroScheduleScreen()
                        }
                    }
                }
            }
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

            // Get card ID from tag
            val cardId = bytesToHex(tag.id)

            // Update card balance in alias system
            latestBalance?.let { balance ->
                lifecycleScope.launch {
                    cardAliasViewModel.updateCardBalance(cardId, balance)

                    // Get card alias if it exists
                    val cardAlias = cardAliasViewModel.getCardBySerialNumber(cardId)
                    cardState.value = CardState.Balance(balance, cardAlias?.alias)
                }
            } ?: run {
                cardState.value = CardState.Error("Balance not found. You moved the card too fast.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cardState.value = CardState.Error(e.message ?: "Unknown error occurred")
            transactionsState.value = emptyList()
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = "0123456789ABCDEF"
        val result = StringBuilder(bytes.size * 2)
        bytes.forEach { byte ->
            val i = byte.toInt()
            result.append(hexChars[i shr 4 and 0x0F])
            result.append(hexChars[i and 0x0F])
        }
        return result.toString()
    }
}

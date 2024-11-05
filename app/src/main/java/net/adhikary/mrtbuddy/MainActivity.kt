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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.NfcReader
import net.adhikary.mrtbuddy.ui.components.MainScreen
import net.adhikary.mrtbuddy.ui.navigation.Screen
import net.adhikary.mrtbuddy.ui.screens.CardAliasScreen
import net.adhikary.mrtbuddy.ui.screens.FareCalculatorScreen
import net.adhikary.mrtbuddy.ui.screens.MetroScheduleScreen
import net.adhikary.mrtbuddy.ui.screens.MonthlyReportsScreen
import net.adhikary.mrtbuddy.ui.screens.StationsMapScreen
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

        setContent {
            MRTBuddyTheme {
                val navController = rememberNavController()
                val currentCardState by remember { cardState }
                val transactions by remember { transactionsState }

                LaunchedEffect(Unit) {
                    intent?.let {
                        handleNfcIntent(it)
                    }
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            Screen.bottomNavItems().forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                    label = { Text(screen.title) },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route
                        ) {
                            composable(Screen.Home.route) {
                                MainScreen(currentCardState, transactions)
                            }
                            composable(Screen.FareCalculator.route) {
                                FareCalculatorScreen()
                            }
                            composable(Screen.CardAlias.route) {
                                CardAliasScreen()
                            }
                            composable(Screen.MetroSchedule.route) {
                                MetroScheduleScreen()
                            }
                            composable(Screen.StationsMap.route) {
                                StationsMapScreen()
                            }
                            composable(Screen.MonthlyReports.route) {
                                MonthlyReportsScreen(transactions)
                            }
                        }
                    }
                }
            }
        }
    } private fun registerNfcStateReceiver() {
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
        println("Starting to read Felica card...")
        val nfcF = NfcF.get(tag)
        try {
            println("Connecting to NFC card...")
            nfcF.connect()
            println("Reading transaction history...")
            val transactions = nfcReader.readTransactionHistory(nfcF)
            println("Successfully read ${transactions.size} transactions")
            nfcF.close()

            // Get card ID from tag
            val cardId = bytesToHexString(tag.id)
            println("Card ID: $cardId")

            // Look up card alias using coroutines
            val cardAliasDao = MrtBuddyApplication.instance.database.cardAliasDao()
            lifecycleScope.launch {
                try {
                    println("Looking up card alias for ID: $cardId")
                    val cardAlias = withContext(Dispatchers.IO) {
                        cardAliasDao.getAlias(cardId)
                    }
                    println("Card alias lookup result: ${cardAlias?.alias ?: "No alias found"}")

                    println("Updating transaction state with ${transactions.size} transactions")
                    transactionsState.value = transactions
                    val latestBalance = transactions.firstOrNull()?.balance
                    println("Latest balance: $latestBalance")

                    latestBalance?.let {
                        println("Updating card state with balance: $it and alias: ${cardAlias?.alias}")
                        cardState.value = CardState.Balance(it, cardAlias?.alias)
                    } ?: run {
                        println("No balance found in transactions")
                        cardState.value = CardState.Error("Balance not found. You moved the card too fast.")
                    }
                } catch (e: Exception) {
                    println("Database error: ${e.message}")
                    e.printStackTrace()
                    cardState.value = CardState.Error("Database error: ${e.message}")
                    transactionsState.value = emptyList()
                }
            }
        } catch (e: Exception) {
            println("Error reading card: ${e.message}")
            e.printStackTrace()
            cardState.value = CardState.Error(e.message ?: "Unknown error occurred")
            transactionsState.value = emptyList()
        }
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[(v ushr 4).toInt()]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[(v and 0x0F).toInt()]
        }
        return String(hexChars)
    }
}

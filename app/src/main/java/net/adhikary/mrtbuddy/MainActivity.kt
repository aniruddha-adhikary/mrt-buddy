package net.adhikary.mrtbuddy


import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcF
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme
import java.io.IOException

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private val balanceState = mutableStateOf("Tap your card to read balance")
    private val transactionsState = mutableStateOf<List<Transaction>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            MRTBuddyTheme {
                val balance by remember { balanceState }
                val transactions by remember { transactionsState }

                LaunchedEffect(Unit) {
                    intent?.let {
                        handleNfcIntent(it)
                    }
                }

                MainScreen(balance, transactions)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Enable NFC foreground dispatch
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))
        val techList = arrayOf(arrayOf(NfcF::class.java.name))
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, techList)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        balanceState.value = "Reading card..."
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent) {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        tag?.let {
            readFelicaCard(it)
        } ?: run {
            balanceState.value = "No MRT Pass / Rapid Pass detected"
            transactionsState.value = emptyList()
        }
    }

    private fun readFelicaCard(tag: Tag) {
        val nfcF = NfcF.get(tag)
        try {
            nfcF.connect()
            val transactions = readTransactionHistory(nfcF)
            nfcF.close()

            transactionsState.value = transactions
            val latestBalance = transactions.firstOrNull()?.balance
            latestBalance?.let {
                balanceState.value = "Latest Balance: $it BDT"
            } ?: run {
                balanceState.value = "Balance not found. You moved the card too fast."
            }
        } catch (e: Exception) {
            e.printStackTrace()
            balanceState.value = "Error reading card: ${e.message}"
            transactionsState.value = emptyList()
        }
    }

    private fun readTransactionHistory(nfcF: NfcF): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val idm = nfcF.tag.id
        val serviceCode = 0x220F
        val serviceCodeList = byteArrayOf(
            (serviceCode and 0xFF).toByte(),
            ((serviceCode shr 8) and 0xFF).toByte()
        )

        val numberOfBlocksToRead =
            10 // Adjust based on how many transaction blocks you want to read

        // Build block list elements
        val blockListElements = ByteArray(numberOfBlocksToRead * 2)
        for (i in 0 until numberOfBlocksToRead) {
            blockListElements[i * 2] = 0x80.toByte() // Two-byte block descriptor
            blockListElements[i * 2 + 1] = i.toByte() // Block number
        }

        // Corrected command length calculation
        val commandLength = 14 + blockListElements.size
        val command = ByteArray(commandLength)
        var idx = 0
        command[idx++] = commandLength.toByte() // Length
        command[idx++] = 0x06.toByte() // Command code
        System.arraycopy(idm, 0, command, idx, idm.size)
        idx += idm.size // idx now at 10
        command[idx++] = 0x01.toByte() // Number of services
        command[idx++] = serviceCodeList[0]
        command[idx++] = serviceCodeList[1]
        command[idx++] = numberOfBlocksToRead.toByte() // Number of blocks
        // idx now at 14
        System.arraycopy(blockListElements, 0, command, idx, blockListElements.size)
        // idx does not need to be incremented further unless required later

        try {
            // Send the command to the card and receive the response
            val response = nfcF.transceive(command)
            // Parse the response
            transactions.addAll(parseTransactionResponse(response))
        } catch (e: IOException) {
            Log.e("NFC", "Error communicating with card", e)
        }

        return transactions
    }

    private fun parseTransactionResponse(response: ByteArray): List<Transaction> {
        val transactions = mutableListOf<Transaction>()

        Log.d("NFC", "Response: ${response.joinToString(" ") { "%02X".format(it) }}")

        // Check minimum response length
        if (response.size < 13) {
            Log.e("NFC", "Response too short")
            return transactions
        }

        val statusFlag1 = response[10]
        val statusFlag2 = response[11]

        if (statusFlag1 != 0x00.toByte() || statusFlag2 != 0x00.toByte()) {
            Log.e("NFC", "Error reading card: Status flags $statusFlag1 $statusFlag2")
            return transactions
        }

        val numBlocks = response[12].toInt() and 0xFF
        val blockData = response.copyOfRange(13, response.size)

        val blockSize = 16 // Each block is 16 bytes
        if (blockData.size < numBlocks * blockSize) {
            Log.e("NFC", "Incomplete block data")
            return transactions
        }

        for (i in 0 until numBlocks) {
            val offset = i * blockSize
            val block = blockData.copyOfRange(offset, offset + blockSize)
            val transaction = parseTransactionBlock(block)
            transactions.add(transaction)
        }

        return transactions
    }

    private fun parseTransactionBlock(block: ByteArray): Transaction {
        if (block.size != 16) {
            throw IllegalArgumentException("Invalid block size")
        }

        // Parse block data based on the provided algorithm

        // Fixed Header (Offsets 0-3)
        val fixedHeader = block.copyOfRange(0, 4)
        val fixedHeaderStr = fixedHeader.joinToString(" ") { "%02X".format(it) }

        // Timestamp (Offsets 4-5)
        val timestampBytes = block.copyOfRange(4, 6)
        val timestampValue = ((timestampBytes[1].toInt() and 0xFF) shl 8) or
                (timestampBytes[0].toInt() and 0xFF)

        // Transaction Type (Offsets 6-7)
        val transactionTypeBytes = block.copyOfRange(6, 8)
        val transactionType = transactionTypeBytes.joinToString(" ") { "%02X".format(it) }

        // From Station (Offset 8)
        val fromStationCode = block[8].toInt() and 0xFF

        // Separator (Offset 9)
        val separator = block[9].toInt() and 0xFF

        // To Station (Offset 10)
        val toStationCode = block[10].toInt() and 0xFF

        // Balance (Offsets 11-13), little-endian format
        val balanceBytes = block.copyOfRange(11, 14)
        val balance = ((balanceBytes[2].toInt() and 0xFF) shl 16) or
                ((balanceBytes[1].toInt() and 0xFF) shl 8) or
                (balanceBytes[0].toInt() and 0xFF)

        // Trailing (Offsets 14-15)
        val trailingBytes = block.copyOfRange(14, 16)
        val trailing = trailingBytes.joinToString(" ") { "%02X".format(it) }

        // Convert timestamp to human-readable date/time if possible
        // Since the timestamp is custom, you might need to implement a specific decoding
        val timestamp = decodeTimestamp(timestampValue)

        // Map station codes to station names if you have a mapping
        val fromStation = getStationName(fromStationCode)
        val toStation = getStationName(toStationCode)

        return Transaction(
            fixedHeader = fixedHeaderStr,
            timestamp = timestamp,
            transactionType = transactionType,
            fromStation = fromStation,
            toStation = toStation,
            balance = balance,
            trailing = trailing
        )
    }

    private fun decodeTimestamp(value: Int): String {
        // Implement the decoding logic for the timestamp
        // Placeholder implementation
        // Assuming the value represents minutes since a specific epoch
        // You may need to adjust this based on actual encoding

        // Example: Convert to date/time (this is speculative)
        val baseTime = System.currentTimeMillis() - (value * 60 * 1000L)
        val date = java.util.Date(baseTime)
        val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
        return format.format(date)
    }

    private fun getStationName(code: Int): String {
        // Map station codes to names
//        val stationMap = mapOf(
//            0x41 to "Station A",
//            0x4B to "Station B",
//            0x1E to "Station C",
//            0x32 to "Station D",
//            0x5A to "Station E",
//            0x0A to "Station F",
//            0x46 to "Station G"
//            // Add other mappings as needed
//        )
        return "Unknown Station ($code)"
    }
}

data class Transaction(
    val fixedHeader: String,
    val timestamp: String,
    val transactionType: String,
    val fromStation: String,
    val toStation: String,
    val balance: Int,
    val trailing: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(balanceText: String, transactions: List<Transaction> = emptyList()) {
    val uriHandler = LocalUriHandler.current
    var showHistory by remember { mutableStateOf(false) }
    val hasTransactions = transactions.isNotEmpty()

    // Calculate amounts by comparing consecutive balances
    val transactionsWithAmounts = remember(transactions) {
        transactions.mapIndexed { index, transaction ->
            val amount = if (index + 1 < transactions.size) {
                // Amount spent is the difference between this balance and the next transaction's balance
                transaction.balance - transactions[index + 1].balance
            } else {
                // For the last transaction, we can't calculate the amount
                null
            }
            TransactionWithAmount(
                transaction = transaction,
                amount = amount
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MRT Buddy") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // NFC Card UI
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = balanceText,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Transaction History Button
                OutlinedButton(
                    onClick = { showHistory = !showHistory },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = hasTransactions
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "History"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (hasTransactions) "View Transaction History" else "No transactions available")
                }

                // Transaction History (if showHistory is true and has transactions)
                AnimatedVisibility(
                    visible = showHistory && hasTransactions,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    TransactionHistoryList(transactionsWithAmounts)
                }
            }

            // Footer
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Built with ❤️ by Ani",
                    modifier = Modifier
                        .clickable { uriHandler.openUri("https://linktr.ee/tuxboy") }
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

data class TransactionWithAmount(
    val transaction: Transaction,
    val amount: Int?
)

@Composable
fun TransactionHistoryList(transactions: List<TransactionWithAmount>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Using LazyColumn for efficient scrolling
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp), // Set maximum height
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions) { transactionWithAmount ->
                    TransactionItem(
                        date = transactionWithAmount.transaction.timestamp,
                        fromStation = transactionWithAmount.transaction.fromStation,
                        toStation = transactionWithAmount.transaction.toStation,
                        balance = "৳ ${transactionWithAmount.transaction.balance}",
                        amount = transactionWithAmount.amount?.let { "৳ $it" } ?: "N/A"
                    )

                    // Add divider between items except for the last one
                    if (transactionWithAmount != transactions.last()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    date: String,
    fromStation: String,
    toStation: String,
    balance: String,
    amount: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
//            Text(
//                text = date,
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
            Text(
                text = "$fromStation → $toStation",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Balance: $balance",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
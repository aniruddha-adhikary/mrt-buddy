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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme
import java.io.IOException

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private val serviceCode = 0x220F
    private val balanceState = mutableStateOf("Tap your card to read balance")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            MRTBuddyTheme {
                // Use the shared state
                val balance by remember { balanceState }

                // Handle NFC intent if activity was launched with one
                LaunchedEffect(Unit) {
                    intent?.let { handleNfcIntent(it) { newBalance ->
                        balanceState.value = newBalance
                    }}
                }

                MainScreen(balance)
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
        // Update the shared state directly
        balanceState.value = "Reading card..."
        handleNfcIntent(intent) { newBalance ->
            balanceState.value = newBalance
        }
    }

    private fun handleNfcIntent(intent: Intent, onBalanceRead: (String) -> Unit) {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        tag?.let {
            readFelicaCard(it, onBalanceRead)
        } ?: run {
            onBalanceRead("No MRT Pass / Rapid Pass detected")
        }
    }

    private fun readFelicaCard(tag: Tag, onBalanceRead: (String) -> Unit) {
        val nfcF = NfcF.get(tag)
        try {
            nfcF.connect()
            // Read the transaction history
            val transactions = readTransactionHistory(nfcF)
            nfcF.close()

            // Get the latest balance if available
            val latestBalance = transactions.firstOrNull()?.balance
            latestBalance?.let {
                onBalanceRead("Latest Balance: $it BDT")
            } ?: run {
                onBalanceRead("Balance not found. You moved the card too fast.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onBalanceRead("Error reading card: $e")
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

        val numberOfBlocksToRead = 10 // Adjust based on how many transaction blocks you want to read

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
fun MainScreen(balanceText: String) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("MRT Buddy") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = balanceText, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

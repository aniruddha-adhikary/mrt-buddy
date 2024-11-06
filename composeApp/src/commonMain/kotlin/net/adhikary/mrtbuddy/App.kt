package net.adhikary.mrtbuddy

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.nfc.getNFCManager
import net.adhikary.mrtbuddy.ui.components.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val nfcManager = getNFCManager()
    // Collect states from NFCManager
    val cardState by nfcManager.cardState.collectAsState(initial = CardState.WaitingForTap)
    val transactions by nfcManager.transactions.collectAsState(initial = emptyList())

    nfcManager.startScan()
    MaterialTheme {
        MainScreen(
            cardState = cardState,
            transactions = transactions,
            onUrlClicked = { url ->
                println("URL clicked: $url")
            }
        )
    }
}

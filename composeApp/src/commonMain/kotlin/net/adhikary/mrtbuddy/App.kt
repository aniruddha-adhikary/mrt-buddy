package net.adhikary.mrtbuddy

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.getNFCManager
import net.adhikary.mrtbuddy.ui.components.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(DelicateCoroutinesApi::class)
@Composable
@Preview
fun App() {
    var isRescanRequested = mutableStateOf(false)
    val scope = rememberCoroutineScope()
    val nfcManager = getNFCManager()
    // Collect states from NFCManager
    val cardState by  nfcManager.cardState.collectAsStateWithLifecycle()
    val transactions by  nfcManager.transactions.collectAsStateWithLifecycle()

    //TODO improve this using view model
    val McardState = remember { mutableStateOf<CardState>(CardState.WaitingForTap) }
    val Mtransactions = remember { mutableStateOf<List<Transaction>>(emptyList()) }

    if(isRescanRequested.value){
        nfcManager.startScan()
        isRescanRequested.value = false
    }

    // not a best practice but ok for now
    scope.launch {
        nfcManager.transactions.collectLatest {
            Mtransactions.value = it
        }
    }
    scope.launch {

        nfcManager.cardState.collectLatest {
            McardState.value = it
        }
    }

    nfcManager.startScan()
    MaterialTheme {
        MainScreen(
            cardState = McardState.value,
            transactions = Mtransactions.value,
            onUrlClicked = { url ->
                println("URL clicked: $url")
            },
            onTapClick = {
             //  nfcManager.startScan()
                if(getPlatform().name != "android"){
                    isRescanRequested.value = true
                }

            }
        )
    }
}

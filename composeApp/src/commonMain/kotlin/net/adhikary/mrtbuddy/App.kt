package net.adhikary.mrtbuddy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.getNFCManager
import net.adhikary.mrtbuddy.platform.openUrl
import net.adhikary.mrtbuddy.ui.components.MainScreen
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var isRescanRequested = mutableStateOf(false)
    val scope = rememberCoroutineScope()
    val nfcManager = getNFCManager()

    val McardState = remember { mutableStateOf<CardState>(CardState.WaitingForTap) }
    val Mtransactions = remember { mutableStateOf<List<Transaction>>(emptyList()) }

    if(isRescanRequested.value){
        nfcManager.startScan()
        isRescanRequested.value = false
    }

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
    
    MRTBuddyTheme {
        MainScreen(
            cardState = McardState.value,
            transactions = Mtransactions.value,
            onUrlClicked = { url ->
                openUrl(url)
            },
            onTapClick = {
                if(getPlatform().name != "android"){
                    isRescanRequested.value = true
                }
            }
        )
    }
}

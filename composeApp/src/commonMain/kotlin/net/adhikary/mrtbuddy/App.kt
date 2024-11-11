package net.adhikary.mrtbuddy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.dao.DemoDao
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.nfc.getNFCManager
import net.adhikary.mrtbuddy.ui.screens.home.MainScreen
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenAction
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenEvent
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenState
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenViewModel
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme
import net.adhikary.mrtbuddy.utils.observeAsActions
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(dao: DemoDao, mainVm: MainScreenViewModel = viewModel { MainScreenViewModel() }) { // TODO need injection
    val scope = rememberCoroutineScope()
    val nfcManager = getNFCManager()


    mainVm.events.observeAsActions {event ->
        when (event) {
            is MainScreenEvent.Error -> {}
            MainScreenEvent.ShowMessage -> {}

        }
    }


    if (RescanManager.isRescanRequested.value) {
        nfcManager.startScan()
        RescanManager.resetRescanRequest()
    }

    scope.launch {
        nfcManager.transactions.collectLatest {
            //   Mtransactions.value = it
            mainVm.onAction(MainScreenAction.UpdateTransactions(it))
        }
    }
    scope.launch {
        nfcManager.cardState.collectLatest {
            // as nfc manager need to call from composable scope
            // so we had to  listen the change on composable scope and update the state of vm
            // McardState.value = it
            mainVm.onAction(MainScreenAction.UpdateCardState(it))
        }
    }


    nfcManager.startScan()

    MRTBuddyTheme {
        var lang by remember { mutableStateOf(Language.English.isoFormat) }
        val state  : MainScreenState by  mainVm.state.collectAsState()

        LocalizedApp(
            language = lang
        ) {
            Scaffold {
                Box(
                    Modifier.systemBarsPadding()
                ) {
                    Column {
                        MainScreen(
                            uiState = state
                        )
                    }
                }
            }


        }


    }
}
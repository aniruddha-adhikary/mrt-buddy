package net.adhikary.mrtbuddy

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    statusBarStyle: @Composable (Boolean) -> Unit,
    dynamicColor: Boolean
) {
    val mainVm = koinViewModel<MainScreenViewModel>()
    val scope = rememberCoroutineScope()
    val nfcManager = getNFCManager()


    var darkModeState by rememberSaveable{
        mutableStateOf(mainVm.state.value.darkModeEnabled)
    }

    mainVm.events.observeAsActions { event ->
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
        nfcManager.cardReadResults.collectLatest { result ->
            result?.let {
                mainVm.onAction(MainScreenAction.UpdateCardReadResult(it))
            }
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
//     MRTBuddyTheme(
//         darkTheme = darkModeState
//     ) {



    MRTBuddyTheme(
        darkTheme =
        if(darkModeState == null) isSystemInDarkTheme()
        else darkModeState!!,
        dynamicColor = dynamicColor
    ) {
        val state: MainScreenState by mainVm.state.collectAsState()
        darkModeState = state.darkModeEnabled
        statusBarStyle(
            if(darkModeState == null) isSystemInDarkTheme()
            else darkModeState!!
        )

        LocalizedApp(
            language = state.currentLanguage
        ) {
            MainScreen()
        }
    }
}

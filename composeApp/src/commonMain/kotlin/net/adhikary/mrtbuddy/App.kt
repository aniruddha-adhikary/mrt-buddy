package net.adhikary.mrtbuddy

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.nfc.getNFCManager
import net.adhikary.mrtbuddy.repository.SettingsRepository
import net.adhikary.mrtbuddy.ui.screens.home.MainScreen
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenAction
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenEvent
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenState
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenViewModel
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme
import net.adhikary.mrtbuddy.utils.observeAsActions
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean
) {
    val mainVm = koinViewModel<MainScreenViewModel>()
    val settingsRepository: SettingsRepository = koinInject()
    val scope = rememberCoroutineScope()
    val nfcManager = getNFCManager()


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

    val isDynamicColor by settingsRepository.isDynamicColorEnabled.collectAsState()

    nfcManager.startScan()

    MRTBuddyTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor && isDynamicColor
    ) {
        val state: MainScreenState by mainVm.state.collectAsState()

        LocalizedApp(
            language = state.currentLanguage
        ) {
            MainScreen()
        }
    }
}

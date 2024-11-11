package net.adhikary.mrtbuddy

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balance
import net.adhikary.mrtbuddy.dao.DemoDao
import net.adhikary.mrtbuddy.ui.navigation.NavHomeDestinationScreens
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.nfc.getNFCManager
import net.adhikary.mrtbuddy.ui.navigation.Navigation
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenAction
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenEvent
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenState
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenViewModel
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme
import net.adhikary.mrtbuddy.utils.observeAsActions
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    dao: DemoDao,
    mainVm: MainScreenViewModel = viewModel { MainScreenViewModel() }
) { // TODO need injection
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

    val navigationItems = remember {
        listOf(
            NavHomeDestinationScreens.HomeScreen,
            NavHomeDestinationScreens.FareScreen,
            NavHomeDestinationScreens.SettingsScreen
        )
    }
    var currentIndex by rememberSaveable { mutableStateOf(0) }
    val navController = rememberNavController()

    MRTBuddyTheme {
        var lang by remember { mutableStateOf(Language.English.isoFormat) }
        val state: MainScreenState by mainVm.state.collectAsState()

        LocalizedApp(
            language = lang
        ) {
            Scaffold(
                modifier = Modifier.systemBarsPadding(),
                bottomBar = {
                    BottomNavigation(
                        backgroundColor = MaterialTheme.colors.surface,
                        contentColor = MaterialTheme.colors.primary
                    ) {
                        navigationItems.forEachIndexed { index, item ->
                            BottomNavigationItem(
                                icon = {
                                    Icon(
                                        imageVector = if (index == currentIndex) item.activeIcon else item.disabledIcon,
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(stringResource(Res.string.balance)) },
                                selected = currentIndex == index,
                                onClick = {
                                    currentIndex = index
                                    navController.navigate(item.route) {
                                        popUpTo(NavHomeDestinationScreens.HomeScreen.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Navigation(
                    state = state,
                    navController = navController
                )
            }
        }
    }
}


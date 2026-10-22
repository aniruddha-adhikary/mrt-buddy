package net.adhikary.mrtbuddy.ui.screens.home

import MoreScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.appName
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.fare
import mrtbuddy.composeapp.generated.resources.historyTab
import mrtbuddy.composeapp.generated.resources.more
import mrtbuddy.composeapp.generated.resources.openSourceLicenses
import mrtbuddy.composeapp.generated.resources.stationMap
import mrtbuddy.composeapp.generated.resources.transactions
import net.adhikary.mrtbuddy.ui.components.AppsIcon
import net.adhikary.mrtbuddy.ui.components.BalanceCard
import net.adhikary.mrtbuddy.ui.components.CalculatorIcon
import net.adhikary.mrtbuddy.ui.components.CardIcon
import net.adhikary.mrtbuddy.ui.components.HistoryIcon
import net.adhikary.mrtbuddy.ui.components.ScreenHeader
import net.adhikary.mrtbuddy.ui.components.TransactionHistoryList
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorScreen
import net.adhikary.mrtbuddy.ui.screens.history.HistoryScreen
import net.adhikary.mrtbuddy.ui.screens.licenses.OpenSourceLicensesScreen
import net.adhikary.mrtbuddy.ui.screens.stationmap.StationMapScreen
import net.adhikary.mrtbuddy.ui.screens.transactionlist.TransactionListScreen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

enum class Screen(val title: StringResource) {
    Home(title = Res.string.balance),
    Calculator(title = Res.string.fare),
    More(title = Res.string.more),
    History(title = Res.string.historyTab),
    TransactionList(title = Res.string.transactions),
    StationMap(title = Res.string.stationMap),
    Licenses(title = Res.string.openSourceLicenses),
}

private class BottomTab(
    val screen: Screen,
    val label: StringResource,
    val icon: @Composable () -> Unit,
)

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val uiState by viewModel.state.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen =
        Screen.valueOf(
            backStackEntry?.destination?.route ?: Screen.Home.name,
        )
    var selectedCardIdm by remember { mutableStateOf<String?>(null) }
    val hasTransactions = uiState.transaction.isNotEmpty()

    val tabs =
        listOf(
            BottomTab(Screen.Calculator, Res.string.fare) { CalculatorIcon() },
            BottomTab(Screen.Home, Res.string.balance) { CardIcon() },
            BottomTab(Screen.History, Res.string.historyTab) { HistoryIcon() },
            BottomTab(Screen.More, Res.string.more) { AppsIcon() },
        )

    Scaffold(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentScreen != Screen.StationMap) {
                NavigationBar(
                    windowInsets = WindowInsets.navigationBars,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 0.dp,
                ) {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            icon = tab.icon,
                            label = { Text(stringResource(tab.label)) },
                            selected =
                                currentScreen == tab.screen ||
                                    (tab.screen == Screen.History && currentScreen == Screen.TransactionList),
                            colors =
                                NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                ),
                            onClick = {
                                navController.navigate(tab.screen.name) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.name,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(route = Screen.Home.name) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ScreenHeader(title = stringResource(Res.string.appName))

                    BalanceCard(
                        cardState = uiState.cardState,
                        cardName = uiState.cardName,
                        cardIdm = uiState.cardIdm,
                    )

                    if (hasTransactions) {
                        TransactionHistoryList(uiState.transactionWithAmount)
                    }
                }
            }

            composable(route = Screen.Calculator.name) {
                FareCalculatorScreen(
                    cardState = uiState.cardState,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            composable(route = Screen.More.name) {
                MoreScreen(
                    onNavigateToStationMap = {
                        navController.navigate(Screen.StationMap.name)
                    },
                    onNavigateToLicenses = {
                        navController.navigate(Screen.Licenses.name)
                    },
                    modifier = Modifier.padding(paddingValues),
                )
            }

            composable(route = Screen.History.name) {
                HistoryScreen(
                    onCardSelected = { cardIdm ->
                        selectedCardIdm = cardIdm
                        navController.navigate(Screen.TransactionList.name)
                    },
                    modifier = Modifier.padding(paddingValues),
                )
            }

            composable(route = Screen.TransactionList.name) {
                selectedCardIdm?.let { cardIdm ->
                    TransactionListScreen(
                        cardIdm = cardIdm,
                        onBack = {
                            navController.navigateUp()
                        },
                        paddingValues = paddingValues,
                    )
                }
            }

            composable(route = Screen.StationMap.name) {
                StationMapScreen(
                    onBack = {
                        navController.navigateUp()
                    },
                )
            }

            composable(route = Screen.Licenses.name) {
                OpenSourceLicensesScreen(
                    onBack = {
                        navController.navigateUp()
                    },
                    paddingValues = paddingValues,
                )
            }
        }
    }
}

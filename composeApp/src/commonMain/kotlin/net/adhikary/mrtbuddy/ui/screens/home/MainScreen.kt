package net.adhikary.mrtbuddy.ui.screens.home

import MoreScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.fare
import mrtbuddy.composeapp.generated.resources.historyTab
import mrtbuddy.composeapp.generated.resources.more
import mrtbuddy.composeapp.generated.resources.openSourceLicenses
import mrtbuddy.composeapp.generated.resources.transactions
import net.adhikary.mrtbuddy.ui.components.AppsIcon
import net.adhikary.mrtbuddy.ui.components.BalanceCard
import net.adhikary.mrtbuddy.ui.components.CalculatorIcon
import net.adhikary.mrtbuddy.ui.components.CardIcon
import net.adhikary.mrtbuddy.ui.components.HistoryIcon
import net.adhikary.mrtbuddy.ui.components.TransactionHistoryList
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorScreen
import net.adhikary.mrtbuddy.ui.screens.history.HistoryScreen
import net.adhikary.mrtbuddy.ui.screens.licenses.OpenSourceLicensesScreen
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
    Licenses(title = Res.string.openSourceLicenses)
}

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.state.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.Home.name
    )
    var selectedCardIdm by remember { mutableStateOf<String?>(null) }
    val hasTransactions = uiState.transaction.isNotEmpty()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    icon = { CalculatorIcon() },
                    label = { Text(stringResource(Res.string.fare)) },
                    selected = currentScreen == Screen.Calculator,
                    onClick = {
                        navController.navigate(Screen.Calculator.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { CardIcon() },
                    label = { Text(stringResource(Res.string.balance)) },
                    selected = currentScreen == Screen.Home,
                    onClick = {
                        navController.navigate(Screen.Home.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { HistoryIcon() },
                    label = { Text(stringResource(Res.string.historyTab)) },
                    selected = currentScreen == Screen.History || currentScreen == Screen.TransactionList,
                    onClick = {
                        navController.navigate(Screen.History.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { AppsIcon() },
                    label = { Text(stringResource(Res.string.more)) },
                    selected = currentScreen == Screen.More,
                    onClick = {
                        navController.navigate(Screen.More.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.name,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = Screen.Home.name) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BalanceCard(
                        cardState = uiState.cardState,
                        cardName = uiState.cardName,
                    )

                    if (hasTransactions) {
                        TransactionHistoryList(uiState.transactionWithAmount)
                    }
                }
            }

            composable(route = Screen.Calculator.name) {
                FareCalculatorScreen(
                    cardState = uiState.cardState,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            composable(route = Screen.More.name) {
                MoreScreen(
                    onNavigateToLicenses = {
                        navController.navigate(Screen.Licenses.name)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }

            composable(route = Screen.History.name) {
                HistoryScreen(
                    onCardSelected = { cardIdm ->
                        selectedCardIdm = cardIdm
                        navController.navigate(Screen.TransactionList.name)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }

            composable(route = Screen.TransactionList.name) {
                selectedCardIdm?.let { cardIdm ->
                    TransactionListScreen(
                        cardIdm = cardIdm,
                        onBack = {
                            navController.navigateUp()
                        },
                        paddingValues = paddingValues
                    )
                }
            }

            composable(route = Screen.Licenses.name) {
                OpenSourceLicensesScreen(
                    onBack = {
                        navController.navigateUp()
                    },
                    paddingValues = paddingValues
                )
            }
        }
    }
}

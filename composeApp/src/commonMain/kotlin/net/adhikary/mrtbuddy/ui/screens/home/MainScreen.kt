package net.adhikary.mrtbuddy.ui.screens.home

import MoreScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            .background(MaterialTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface
            ) {
                BottomNavigationItem(
                    icon = { CalculatorIcon() },
                    label = { Text(stringResource(Res.string.fare)) },
                    selected = currentScreen == Screen.Calculator,
                    onClick = { navController.navigate(Screen.Calculator.name) },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                BottomNavigationItem(
                    icon = { CardIcon() },
                    label = { Text(stringResource(Res.string.balance)) },
                    selected = currentScreen == Screen.Home,
                    onClick = { navController.navigate(Screen.Home.name) },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                BottomNavigationItem(
                    icon = { HistoryIcon() },
                    label = { Text(stringResource(Res.string.historyTab)) },
                    selected = currentScreen == Screen.History || currentScreen == Screen.TransactionList,
                    onClick = { navController.navigate(Screen.History.name) },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                BottomNavigationItem(
                    icon = { AppsIcon() },
                    label = { Text(stringResource(Res.string.more)) },
                    selected = currentScreen == Screen.More,
                    onClick = { navController.navigate(Screen.More.name) },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
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
                    cardState = uiState.cardState
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
                    }
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

package net.adhikary.mrtbuddy.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.fare
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount
import net.adhikary.mrtbuddy.ui.components.BalanceCard
import net.adhikary.mrtbuddy.ui.components.CalculatorIcon
import net.adhikary.mrtbuddy.ui.components.CardIcon
import net.adhikary.mrtbuddy.ui.components.Footer
import net.adhikary.mrtbuddy.ui.components.TransactionHistoryList
import net.adhikary.mrtbuddy.ui.screens.FareCalculatorScreen
import org.jetbrains.compose.resources.stringResource

enum class Screen {
    Home, Calculator
}

@Composable
fun MainScreen(
    uiState : MainScreenState
) {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    val hasTransactions = uiState.transaction.isNotEmpty()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.primary
            ) {
                BottomNavigationItem(
                    icon = { CardIcon() },
                    label = { Text(stringResource(Res.string.balance)) },
                    selected = currentScreen == Screen.Home,
                    onClick = { currentScreen = Screen.Home }
                )
                BottomNavigationItem(
                    icon = {
                        CalculatorIcon()
                    },
                    label = { Text(stringResource(Res.string.fare)) },
                    selected = currentScreen == Screen.Calculator,
                    onClick = { currentScreen = Screen.Calculator }
                )
            }
        }
    ) { paddingValues ->
        when (currentScreen) {
            Screen.Home -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        BalanceCard(cardState = uiState.cardState)

                        if (hasTransactions) {
                            TransactionHistoryList(uiState.transactionWithAmount)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Footer()
                }
            }
            Screen.Calculator -> {
                FareCalculatorScreen(cardState = uiState.cardState)
            }
        }
    }
}

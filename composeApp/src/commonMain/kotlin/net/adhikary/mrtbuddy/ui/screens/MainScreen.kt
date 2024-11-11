package net.adhikary.mrtbuddy.ui.screens

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
import mrtbuddy.composeapp.generated.resources.nav_map
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount
import net.adhikary.mrtbuddy.ui.components.BalanceCard
import net.adhikary.mrtbuddy.ui.components.CalculatorIcon
import net.adhikary.mrtbuddy.ui.components.CardIcon
import net.adhikary.mrtbuddy.ui.components.Footer
import net.adhikary.mrtbuddy.ui.components.MapIcon
import net.adhikary.mrtbuddy.ui.components.TransactionHistoryList
import org.jetbrains.compose.resources.stringResource
import net.adhikary.mrtbuddy.ui.screens.map.MetroMap

enum class Screen {
    Home, Calculator, Map
}

@Composable
fun MainScreen(
    cardState: CardState,
    transactions: List<Transaction> = emptyList(),
) {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    val hasTransactions = transactions.isNotEmpty()

    val transactionsWithAmounts = remember(transactions) {
        transactions.mapIndexed { index, transaction ->
            val amount = if (index + 1 < transactions.size) {
                transaction.balance - transactions[index + 1].balance
            } else {
                null
            }
            TransactionWithAmount(
                transaction = transaction,
                amount = amount
            )
        }
    }

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

                BottomNavigationItem(
                    icon = {
                        MapIcon()
                    },
                    label = { Text(stringResource(Res.string.nav_map)) },
                    selected = currentScreen == Screen.Map,
                    onClick = { currentScreen = Screen.Map }
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
                        BalanceCard(cardState = cardState)

                        if (hasTransactions) {
                            TransactionHistoryList(transactionsWithAmounts)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Footer()
                }
            }
            Screen.Calculator -> {
                FareCalculatorScreen(cardState = cardState)
            }

            Screen.Map -> {
                MetroMap()
            }
        }
    }
}

package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount
import net.adhikary.mrtbuddy.ui.navigation.Screen
import net.adhikary.mrtbuddy.ui.screens.FareCalculatorScreen

@Composable
fun MainScreen(
    cardState: CardState,
    transactions: List<Transaction> = emptyList(),
    onTapClick: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.CardScan) }
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Screen Content
            Box(modifier = Modifier.weight(1f)) {
                when (currentScreen) {
                    Screen.CardScan -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            BalanceCard(
                                cardState = cardState,
                                onTapClick = onTapClick
                            )

                            if (hasTransactions) {
                                TransactionHistoryList(transactionsWithAmounts)
                            }
                        }
                    }
                    Screen.FareCalculator -> {
                        FareCalculatorScreen()
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Navigation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                elevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { currentScreen = Screen.CardScan },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (currentScreen == Screen.CardScan)
                                MaterialTheme.colors.primary
                            else
                                MaterialTheme.colors.surface
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💳",
                                color = if (currentScreen == Screen.CardScan)
                                    MaterialTheme.colors.onPrimary
                                else
                                    MaterialTheme.colors.onSurface
                            )
                            Text(
                                text = "Card Scan",
                                color = if (currentScreen == Screen.CardScan)
                                    MaterialTheme.colors.onPrimary
                                else
                                    MaterialTheme.colors.onSurface
                            )
                        }
                    }
                    Button(
                        onClick = { currentScreen = Screen.FareCalculator },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (currentScreen == Screen.FareCalculator)
                                MaterialTheme.colors.primary
                            else
                                MaterialTheme.colors.surface
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🧮",
                                color = if (currentScreen == Screen.FareCalculator)
                                    MaterialTheme.colors.onPrimary
                                else
                                    MaterialTheme.colors.onSurface
                            )
                            Text(
                                text = "Fare Calculator",
                                color = if (currentScreen == Screen.FareCalculator)
                                    MaterialTheme.colors.onPrimary
                                else
                                    MaterialTheme.colors.onSurface
                            )
                        }
                    }
                }
            }

            Footer()
        }
    }
}

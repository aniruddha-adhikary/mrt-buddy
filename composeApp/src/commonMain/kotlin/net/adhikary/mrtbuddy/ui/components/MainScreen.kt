package net.adhikary.mrtbuddy.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount

@Composable
fun MainScreen(
    cardState: CardState,
    transactions: List<Transaction> = emptyList(),
    onUrlClicked: (String) -> Unit,
    onTapClick: () -> Unit
) {
    var showHistory by remember { mutableStateOf(false) }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                Box(Modifier.fillMaxSize().padding(16.dp)) {

                    if (getPlatform().name != "android") {
                        Text("Rescan", modifier = Modifier.align(
                            Alignment.TopEnd
                        ).clickable {
                            onTapClick()
                        })
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        when (cardState) {
                            is CardState.Balance -> {
                                Text(
                                    text = "Latest Balance",
                                    style = MaterialTheme.typography.h6,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "৳ ${cardState.amount}",
                                    style = MaterialTheme.typography.h4,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colors.onSurface
                                )
                            }

                            CardState.Reading -> {
                                Text(
                                    text = "Reading card...",
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colors.onSurface
                                )
                            }

                            CardState.WaitingForTap -> {
                                Text(
                                    text = "Tap your card behind your phone to read balance",
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colors.onSurface,
                                )
                            }

                            is CardState.Error -> {
                                Text(
                                    text = cardState.message,
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colors.error
                                )
                            }

                            CardState.NoNfcSupport -> {
                                Text(
                                    text = "This device doesn't support NFC",
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colors.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "NFC is required to read your MRT Pass",
                                    style = MaterialTheme.typography.h4,
                                    color = MaterialTheme.colors.error.copy(alpha = 0.7f)
                                )
                            }

                            CardState.NfcDisabled -> {
                                Text(
                                    text = "NFC is turned off",
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colors.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Please enable NFC in your device settings",
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.error.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = { showHistory = !showHistory },
                modifier = Modifier.fillMaxWidth(),
                enabled = hasTransactions
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "History"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (hasTransactions) "View Transaction History" else "No transactions available")
            }

            AnimatedVisibility(
                visible = showHistory && hasTransactions,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                TransactionHistoryList(transactionsWithAmounts)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Built with ❤️ by Ani and friends",
                modifier = Modifier
                    .clickable { onUrlClicked("https://mrtbuddy.com/contributors.html") }
                    .padding(8.dp),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.primary
            )
        }
    }
}

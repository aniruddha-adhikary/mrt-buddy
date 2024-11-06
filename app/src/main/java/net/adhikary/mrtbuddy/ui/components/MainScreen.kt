package net.adhikary.mrtbuddy.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.R
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(cardState: CardState, transactions: List<Transaction> = emptyList()) {
    val uriHandler = LocalUriHandler.current
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MRT Buddy") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(0.95f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        when (cardState) {
                            is CardState.Balance -> {
                                Text(
                                    text = "Latest Balance",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "৳ ${cardState.amount}",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            CardState.Reading -> {
                                Text(
                                    text = "Reading card...",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            CardState.WaitingForTap -> {
                                Column (
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        modifier = Modifier.size(64.dp),
                                        painter = painterResource(R.drawable.ic_fake_nfc),
                                        contentDescription = "NFC",
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap your card behind your phone to read balance",
                                        style = MaterialTheme.typography.headlineMedium,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            is CardState.Error -> {
                                Text(
                                    text = cardState.message,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            CardState.NoNfcSupport -> {
                                Text(
                                    text = "This device doesn't support NFC",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "NFC is required to read your MRT Pass",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                )
                            }
                            CardState.NfcDisabled -> {
                                Text(
                                    text = "NFC is turned off",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Please enable NFC in your device settings",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                )
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
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Built with ❤️ by Ani and friends",
                    modifier = Modifier
                        .clickable { uriHandler.openUri("https://mrtbuddy.com/contributors.html") }
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(
        cardState = CardState.Balance(100),
    )
}
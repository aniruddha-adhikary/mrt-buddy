package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount

@Composable
fun MainScreen(
    cardState: CardState,
    transactions: List<Transaction> = emptyList(),
    onTapClick: () -> Unit
) {
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
            Column(
                modifier = Modifier.weight(1f),
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

            Spacer(modifier = Modifier.height(8.dp))
            
            Footer()
        }
    }
}

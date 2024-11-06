package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.TransactionType
import net.adhikary.mrtbuddy.model.TransactionWithAmount

@Composable
fun TransactionHistoryList(transactions: List<TransactionWithAmount>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions) { transactionWithAmount ->
                    val isCommute = transactionWithAmount.transaction.fixedHeader.startsWith(
                        "08 52 10 00"
                    )
                    TransactionItem(
                        type = if (isCommute) TransactionType.Commute else TransactionType.BalanceUpdate,
                        date = transactionWithAmount.transaction.timestamp,
                        fromStation = transactionWithAmount.transaction.fromStation,
                        toStation = transactionWithAmount.transaction.toStation,
                        balance = "৳ ${transactionWithAmount.transaction.balance}",
                        amount = transactionWithAmount.amount?.let { "৳ $it" } ?: "N/A"
                    )

                    if (transactionWithAmount != transactions.last()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    type: TransactionType,
    date: String,
    fromStation: String,
    toStation: String,
    balance: String,
    amount: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (type == TransactionType.Commute) "$fromStation → $toStation" else "Balance Update",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Balance: $balance",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
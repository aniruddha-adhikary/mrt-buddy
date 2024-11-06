package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import net.adhikary.mrtbuddy.model.TransactionType
import net.adhikary.mrtbuddy.model.TransactionWithAmount
import net.adhikary.mrtbuddy.ui.theme.LightPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.DarkPositiveGreen

@Composable
fun TransactionHistoryList(transactions: List<TransactionWithAmount>) {
    Card(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.SemiBold
            )
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        amount = transactionWithAmount.amount?.let { "৳ $it" } ?: "N/A",
                        amountValue = transactionWithAmount.amount
                    )

                    if (transactionWithAmount != transactions.last()) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
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
    amount: String,
    amountValue: Int?
) {
    val isDarkTheme = isSystemInDarkTheme()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (type == TransactionType.Commute) "$fromStation → $toStation" else "Balance Update",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            val amountColor = when {
                type == TransactionType.BalanceUpdate && (amountValue ?: 0) > 0 -> 
                    if (isDarkTheme) DarkPositiveGreen else LightPositiveGreen
                else -> MaterialTheme.colors.primary
            }
            
            Text(
                text = amount,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

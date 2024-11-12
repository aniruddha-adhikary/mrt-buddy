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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balanceUpdate
import mrtbuddy.composeapp.generated.resources.recentJourneys
import net.adhikary.mrtbuddy.model.TransactionType
import net.adhikary.mrtbuddy.model.TransactionWithAmount
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.theme.DarkNegativeRed
import net.adhikary.mrtbuddy.ui.theme.DarkPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.LightNegativeRed
import net.adhikary.mrtbuddy.ui.theme.LightPositiveGreen
import org.jetbrains.compose.resources.stringResource

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
                text =  stringResource(Res.string.recentJourneys),
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.SemiBold
            )
            Divider(
                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val validTransactions = transactions.filter { it.amount != null }

                items(validTransactions) { transactionWithAmount ->
                    val isCommute = transactionWithAmount.transaction.fixedHeader.startsWith(
                        "08 52 10 00"
                    )
                    TransactionItem(
                        type = if (isCommute) TransactionType.Commute else TransactionType.BalanceUpdate,
                        date = transactionWithAmount.transaction.timestamp,
                        fromStation = transactionWithAmount.transaction.fromStation,
                        toStation = transactionWithAmount.transaction.toStation,
                        balance = "৳ ${transactionWithAmount.transaction.balance}",
                        amount = transactionWithAmount.amount?.let { "৳ ${translateNumber(it)}" } ?: "N/A",
                        amountValue = transactionWithAmount.amount
                    )

                    if (transactionWithAmount != validTransactions.last()) {
                        Divider(
                            modifier = Modifier.padding(top = 12.dp),
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
    date: LocalDateTime,
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
            .padding(top = 4.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (type == TransactionType.Commute)
                    "${StationService.translate(fromStation)} → ${StationService.translate(toStation)}"
                    else stringResource(Res.string.balanceUpdate),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = TimestampService.formatDateTime(date),
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
                type == TransactionType.Commute || (amountValue ?: 0) < 0 ->
                    if (isDarkTheme) DarkNegativeRed else LightNegativeRed
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

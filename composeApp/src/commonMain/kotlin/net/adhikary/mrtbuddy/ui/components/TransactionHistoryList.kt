package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balanceUpdate
import mrtbuddy.composeapp.generated.resources.one_way_arrow
import mrtbuddy.composeapp.generated.resources.payments
import mrtbuddy.composeapp.generated.resources.recentJourneys
import net.adhikary.mrtbuddy.model.TransactionType
import net.adhikary.mrtbuddy.model.TransactionWithAmount
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService
import net.adhikary.mrtbuddy.translateNumber
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

@Composable
fun TransactionHistoryList(
    transactions: List<TransactionWithAmount>,
    modifier: Modifier = Modifier,
) {
    val validTransactions = transactions.filter { it.transaction.timestamp.year >= 2015 }
    if (validTransactions.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(text = stringResource(Res.string.recentJourneys))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                validTransactions.forEachIndexed { index, transactionWithAmount ->
                    // FIXME: Hatirjheel charges 40 and then refunds the change (shows as balance update)
                    TransactionItem(
                        type = TransactionType.fromHeader(transactionWithAmount.transaction.fixedHeader),
                        date = transactionWithAmount.transaction.timestamp,
                        fromStation = transactionWithAmount.transaction.fromStation,
                        toStation = transactionWithAmount.transaction.toStation,
                        balance = "৳ ${translateNumber(transactionWithAmount.transaction.balance)}",
                        amount = formatAmount(transactionWithAmount.amount),
                        amountValue = transactionWithAmount.amount,
                    )
                    if (index != validTransactions.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        )
                    }
                }
            }
        }
    }
}

/** Formats a signed amount as "+৳ 100" / "−৳ 20", or "N/A" when unknown. */
fun formatAmount(amount: Int?): String =
    when {
        amount == null -> "N/A"
        amount > 0 -> "+৳ ${translateNumber(amount)}"
        amount < 0 -> "−৳ ${translateNumber(abs(amount))}"
        else -> "৳ ${translateNumber(0)}"
    }

@Composable
fun TransactionItem(
    type: TransactionType,
    date: LocalDateTime,
    fromStation: String,
    toStation: String,
    balance: String,
    amount: String,
    amountValue: Int?,
) {
    val isTopUp = type == TransactionType.BalanceUpdate
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconBadge(
            painter = painterResource(if (isTopUp) Res.drawable.payments else Res.drawable.one_way_arrow),
            containerColor =
                if (isTopUp) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
            contentColor =
                if (isTopUp) {
                    MaterialTheme.colorScheme.onTertiaryContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                },
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text =
                    if (isTopUp) {
                        stringResource(Res.string.balanceUpdate)
                    } else {
                        "${StationService.translate(fromStation)} → ${StationService.translate(toStation)}"
                    },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = TimestampService.formatDateTime(date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 8.dp),
        ) {
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                color =
                    when {
                        amountValue == null -> MaterialTheme.colorScheme.onSurface
                        amountValue > 0 -> positiveColor()
                        else -> MaterialTheme.colorScheme.onSurface
                    },
            )
            Text(
                text = balance,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

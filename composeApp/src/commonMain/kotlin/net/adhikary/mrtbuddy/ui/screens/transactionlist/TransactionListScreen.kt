package net.adhikary.mrtbuddy.ui.screens.transactionlist

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balanceUpdate
import net.adhikary.mrtbuddy.model.TransactionType
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.theme.DarkNegativeRed
import net.adhikary.mrtbuddy.ui.theme.DarkPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.LightNegativeRed
import net.adhikary.mrtbuddy.ui.theme.LightPositiveGreen
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.adhikary.mrtbuddy.data.TransactionEntity
import net.adhikary.mrtbuddy.repository.TransactionRepository

@Composable
fun TransactionListScreen(
    cardIdm: String,
    transactionRepository: TransactionRepository,
    onBack: () -> Unit
) {
    val viewModel: TransactionListViewModel = viewModel(
        factory = TransactionListViewModelFactory(cardIdm, transactionRepository)
    )

    val uiState = viewModel.state.collectAsState().value

    if (uiState.isLoading) {
        Text("Loading transactions...")
    } else if (uiState.error != null) {
        Text("Error: ${uiState.error}")
    } else {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxSize(),
        ) {
            Text(
                text = "Transactions",
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
                items(uiState.transactions) { transaction ->
                    TransactionItem(transaction)
                    if (transaction != uiState.transactions.last()) {
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
fun TransactionItem(transaction: TransactionEntity) {
    val isDarkTheme = isSystemInDarkTheme()
    val transactionType =
        if (transaction.fromStation.isNotEmpty() && transaction.toStation.isNotEmpty()) {
            TransactionType.Commute
        } else {
            TransactionType.BalanceUpdate
        }

    val amountText = "৳ ${translateNumber(transaction.balance)}"
    val tz = TimeZone.of("Asia/Dhaka")
    val dateTimeFormatted = TimestampService.formatDateTime(
        Instant.fromEpochMilliseconds(transaction.dateTime).toLocalDateTime(tz)
    )

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
                text = if (transactionType == TransactionType.Commute)
                    "${StationService.translate(transaction.fromStation)} → ${
                        StationService.translate(
                            transaction.toStation
                        )
                    }"
                else stringResource(Res.string.balanceUpdate),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateTimeFormatted,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            val amountColor = when (transactionType) {
                TransactionType.BalanceUpdate ->
                    if (isDarkTheme) DarkPositiveGreen else LightPositiveGreen

                TransactionType.Commute ->
                    if (isDarkTheme) DarkNegativeRed else LightNegativeRed
            }

            Text(
                text = amountText,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

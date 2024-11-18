package net.adhikary.mrtbuddy.ui.screens.transactionlist

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.balanceUpdate
import mrtbuddy.composeapp.generated.resources.noTransactionsFound
import mrtbuddy.composeapp.generated.resources.transactions
import mrtbuddy.composeapp.generated.resources.transactionsAppearPrompt
import mrtbuddy.composeapp.generated.resources.unnamedCard
import net.adhikary.mrtbuddy.data.TransactionEntityWithAmount
import net.adhikary.mrtbuddy.model.TransactionType
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.theme.DarkNegativeRed
import net.adhikary.mrtbuddy.ui.theme.DarkPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.LightNegativeRed
import net.adhikary.mrtbuddy.ui.theme.LightPositiveGreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TransactionListScreen(
    cardIdm: String,
    onBack: () -> Unit,
    paddingValues: PaddingValues
) {
    val viewModel: TransactionListViewModel = koinViewModel(
        key = cardIdm,
        parameters = { parametersOf(cardIdm) }
    )

    val uiState = viewModel.state.collectAsState().value

    if (uiState.isLoading) {
//        Text("Loading transactions...")
    } else if (uiState.error != null) {
        Text("Error: ${uiState.error}")
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            TopAppBar(
                title = {
                    val cardName = uiState.cardName?.takeIf { it.isNotBlank() } ?: stringResource(Res.string.unnamedCard)
                    val balanceText = uiState.balance?.let { " (৳ ${translateNumber(it)})" } ?: ""
                    Text("$cardName$balanceText")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp
            )

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                if (uiState.transactions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(Res.string.noTransactionsFound),
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = stringResource(Res.string.transactionsAppearPrompt),
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(horizontal = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
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
    }
}

@Composable
fun TransactionItem(trxEntity: TransactionEntityWithAmount) {
    val transaction = trxEntity.transactionEntity;
    val isDarkTheme = isSystemInDarkTheme()
    val transactionType = if (trxEntity.amount != null && trxEntity.amount > 0) {
        TransactionType.BalanceUpdate
    } else {
        TransactionType.Commute
    }

    val amountText = if (trxEntity.amount != null) {
        "৳ ${translateNumber(trxEntity.amount)}"
    } else {
        "N/A"
    }
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
            val amountColor = when {
                trxEntity.amount == null -> MaterialTheme.colors.onSurface
                trxEntity.amount > 0 -> if (isDarkTheme) DarkPositiveGreen else LightPositiveGreen
                else -> if (isDarkTheme) DarkNegativeRed else LightNegativeRed
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

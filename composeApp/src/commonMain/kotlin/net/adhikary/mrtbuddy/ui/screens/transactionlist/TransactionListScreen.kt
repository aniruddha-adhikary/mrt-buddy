package net.adhikary.mrtbuddy.ui.screens.transactionlist

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.avg
import mrtbuddy.composeapp.generated.resources.balanceUpdate
import mrtbuddy.composeapp.generated.resources.noTransactionsFound
import mrtbuddy.composeapp.generated.resources.recharged
import mrtbuddy.composeapp.generated.resources.spent
import mrtbuddy.composeapp.generated.resources.transactionsAppearPrompt
import mrtbuddy.composeapp.generated.resources.trips
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    modifier: Modifier = Modifier,
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
            modifier = Modifier.fillMaxSize(),
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                windowInsets = WindowInsets.statusBars
            )

            Column {
                if (uiState.transactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .padding(bottom = paddingValues.calculateBottomPadding()),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(Res.string.noTransactionsFound),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = stringResource(Res.string.transactionsAppearPrompt),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(horizontal = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 0.dp,
                            start = 0.dp,
                            end = 0.dp,
                            bottom = 24.dp + paddingValues.calculateBottomPadding()
                        ),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        item {
                            TransactionSummaryCard(transactions = uiState.transactions)
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }
                        items(uiState.transactions) { transaction ->
                            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                TransactionItem(transaction)
                            }
                            if (transaction != uiState.transactions.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
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
    val tz = TimestampService.getDefaultTimezone()
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateTimeFormatted,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            val amountColor = when {
                trxEntity.amount == null -> MaterialTheme.colorScheme.onSurface
                trxEntity.amount > 0 -> if (isDarkTheme) DarkPositiveGreen else LightPositiveGreen
                else -> if (isDarkTheme) DarkNegativeRed else LightNegativeRed
            }

            Text(
                text = amountText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@Composable
private fun TransactionSummaryCard(
    transactions: List<TransactionEntityWithAmount>,
    modifier: Modifier = Modifier
) {
    val totalSpent = transactions
        .filter { it.amount != null && it.amount < 0 }
        .sumOf { it.amount ?: 0 }
        .let { kotlin.math.abs(it) }
    
    val totalRecharged = transactions
        .filter { it.amount != null && it.amount > 0 }
        .sumOf { it.amount ?: 0 }
    
    val averageSpent = if (totalSpent > 0) {
        val commuteCount = transactions.count { it.amount != null && it.amount < 0 }
        if (commuteCount > 0) totalSpent / commuteCount else 0
    } else {
        0
    }
    val isDarkTheme = isSystemInDarkTheme()

    OutlinedCard (
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SummaryItem(
                title = stringResource(Res.string.trips),
                value = transactions.count { it.amount != null && it.amount < 0 }.toString(),
                modifier = Modifier.weight(1f)
            )
            SummaryItem(
                title = stringResource(Res.string.spent),
                value = "৳ ${translateNumber(totalSpent)}",
                modifier = Modifier.weight(1f),
                amountColor = if ((totalRecharged) > 0) {
                    if (isDarkTheme) DarkNegativeRed else LightNegativeRed
                } else MaterialTheme.colorScheme.onSurface
            )
            SummaryItem(
                title = stringResource(Res.string.recharged),
                value = "৳ ${translateNumber(totalRecharged)}",
                modifier = Modifier.weight(1f),
                amountColor = if ((totalRecharged) > 0) {
                    if (isDarkTheme) DarkPositiveGreen else LightPositiveGreen
                } else MaterialTheme.colorScheme.onSurface
            )
            SummaryItem(
                title = stringResource(Res.string.avg),
                value = "৳ ${translateNumber(averageSpent)}",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryItem(
    title: String,
    value: String,
    amountColor : Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = amountColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
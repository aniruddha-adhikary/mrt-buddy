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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balanceUpdate
import mrtbuddy.composeapp.generated.resources.endOfTransactionHistory
import mrtbuddy.composeapp.generated.resources.errorLoadingTransactions
import mrtbuddy.composeapp.generated.resources.noTransactionsFound
import mrtbuddy.composeapp.generated.resources.retry
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

    val state = viewModel.state.collectAsState().value
    val lazyListState = rememberLazyListState()

    // Pull-to-refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { viewModel.refresh() }
    )

    // Monitor scroll position to trigger loading more
    LaunchedEffect(lazyListState, state) {
        val shouldLoadMore by remember {
            derivedStateOf {
                val layoutInfo = lazyListState.layoutInfo
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)

                // Load more when we're 5 items from the bottom and can load more
                lastVisibleItemIndex >= (totalItems - 5) && state.canLoadMore && !state.isLoadingMore
            }
        }

        // When approaching end of list, load more
        if (shouldLoadMore) {
            viewModel.loadMoreTransactions()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Top app bar with card info
        TopAppBar(
            title = {
                val cardName = state.cardName?.takeIf { it.isNotBlank() } ?: stringResource(Res.string.unnamedCard)
                val balanceText = state.balance?.let { " (৳ ${translateNumber(it)})" } ?: ""
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
            windowInsets = WindowInsets.statusBars,
            actions = {
                // Refresh button
                IconButton(onClick = { viewModel.refresh() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        )

        // Main content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pullRefresh(pullRefreshState)
        ) {
            when {
                // Initial loading
                state.isLoading && state.transactions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // Error with empty list
                state.error != null && state.transactions.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.error ?: stringResource(Res.string.errorLoadingTransactions),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { viewModel.retry() }) {
                            Text(stringResource(Res.string.retry))
                        }
                    }
                }

                // Empty transaction list
                state.transactions.isEmpty() -> {
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
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Transaction list
                else -> {
                    TransactionList(
                        state = state,
                        lazyListState = lazyListState,
                        paddingValues = paddingValues,
                        onRetry = { viewModel.retry() }
                    )
                }
            }

            // Pull refresh indicator
            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TransactionList(
    state: TransactionListState,
    lazyListState: LazyListState,
    paddingValues: PaddingValues,
    onRetry: () -> Unit
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 24.dp,
            start = 24.dp,
            end = 24.dp,
            bottom = 24.dp + paddingValues.calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Transaction items with stable keys
        itemsIndexed(
            items = state.transactions,
            key = { _, transaction ->
                "${transaction.transactionEntity.cardIdm}_${transaction.transactionEntity.dateTime}"
            }
        ) { index, transaction ->
            TransactionItem(transaction)
            if (index < state.transactions.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 12.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }
        }

        // Loading indicator at the bottom
        if (state.isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        // Error indicator when loading more failed
        if (state.error != null && state.transactions.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onRetry) {
                        Text(stringResource(Res.string.retry))
                    }
                }
            }
        }

        // End of list indicator
        if (!state.canLoadMore && !state.isLoadingMore && state.transactions.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.endOfTransactionHistory),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
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

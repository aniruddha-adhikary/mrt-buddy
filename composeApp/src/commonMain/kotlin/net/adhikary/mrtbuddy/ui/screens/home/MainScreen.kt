package net.adhikary.mrtbuddy.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.ui.components.BalanceCard
import net.adhikary.mrtbuddy.ui.components.Footer
import net.adhikary.mrtbuddy.ui.components.TransactionHistoryList


@Composable
fun MainScreen(
    uiState: MainScreenState
) {
    val hasTransactions = uiState.transaction.isNotEmpty()
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
            BalanceCard(cardState = uiState.cardState)

            if (hasTransactions) {
                TransactionHistoryList(uiState.transactionWithAmount)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Footer()
    }
}


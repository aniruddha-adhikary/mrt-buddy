package net.adhikary.mrtbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.CardAlias
import net.adhikary.mrtbuddy.viewmodel.CardAliasViewModel

@Composable
fun CardAliasTestScreen(
    viewModel: CardAliasViewModel,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var currentCardSerialNumber by remember { mutableStateOf("") }
    var currentAlias by remember { mutableStateOf("") }
    var currentBalance by remember { mutableStateOf("0.0") }
    var currentThreshold by remember { mutableStateOf("100.0") }

    // Sample card data for testing
    val sampleCards = listOf(
        Triple("0122334455660001", "Wife", 150.0),
        Triple("0122334455660007", "Mom", 80.0),
        Triple("0122334455660008", "Kid", 30.0)
    )

    // Collect cards from database
    val cards by viewModel.allCards.collectAsState(initial = emptyList())
    val lowBalanceCards by viewModel.lowBalanceCards.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        // Add sample cards if database is empty
        if (cards.isEmpty()) {
            sampleCards.forEach { (serial, alias, balance) ->
                viewModel.addCard(
                    CardAlias(
                        cardSerialNumber = serial,
                        alias = alias,
                        lastBalance = balance,
                        lowBalanceThreshold = 50.0
                    )
                )
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Card Alias Test Screen",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Add Card Button
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Add New Card")
        }

        // Simulate Card Tap Button
        Button(
            onClick = {
                // Simulate tapping each sample card
                sampleCards.forEach { (serial, _, balance) ->
                    viewModel.updateCardBalance(serial, balance)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Simulate Card Taps")
        }

        // Cards List
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                Text(
                    text = "All Cards",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(cards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Alias: ${card.alias}")
                        Text("Serial: ${card.cardSerialNumber}")
                        Text("Balance: ${card.lastBalance}")
                        Text("Low Balance Threshold: ${card.lowBalanceThreshold}")
                        if (card.lastBalance != null && card.lowBalanceThreshold != null &&
                            card.lastBalance < card.lowBalanceThreshold) {
                            Text(
                                "⚠️ Low Balance Alert!",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            item {
                if (lowBalanceCards.isNotEmpty()) {
                    Text(
                        text = "Low Balance Cards",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            items(lowBalanceCards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Alias: ${card.alias}")
                        Text("Balance: ${card.lastBalance}")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Card") },
            text = {
                Column {
                    OutlinedTextField(
                        value = currentCardSerialNumber,
                        onValueChange = { currentCardSerialNumber = it },
                        label = { Text("Card Serial Number") }
                    )
                    OutlinedTextField(
                        value = currentAlias,
                        onValueChange = { currentAlias = it },
                        label = { Text("Alias") }
                    )
                    OutlinedTextField(
                        value = currentBalance,
                        onValueChange = { currentBalance = it },
                        label = { Text("Initial Balance") }
                    )
                    OutlinedTextField(
                        value = currentThreshold,
                        onValueChange = { currentThreshold = it },
                        label = { Text("Low Balance Threshold") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addCard(
                        CardAlias(
                            cardSerialNumber = currentCardSerialNumber,
                            alias = currentAlias,
                            lastBalance = currentBalance.toDoubleOrNull() ?: 0.0,
                            lowBalanceThreshold = currentThreshold.toDoubleOrNull()
                        )
                    )
                    showAddDialog = false
                    currentCardSerialNumber = ""
                    currentAlias = ""
                    currentBalance = "0.0"
                    currentThreshold = "100.0"
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

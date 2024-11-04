package net.adhikary.mrtbuddy.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.adhikary.mrtbuddy.model.CardAlias
import net.adhikary.mrtbuddy.viewmodel.CardAliasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardAliasScreen(
    viewModel: CardAliasViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCard by remember { mutableStateOf<CardAlias?>(null) }

    val cards by viewModel.allCards.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Card Aliases") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add Card Alias")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(cards) { card ->
                CardAliasItem(
                    card = card,
                    onEdit = { selectedCard = it },
                    onDelete = { viewModel.deleteCard(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (showAddDialog) {
            AddEditCardDialog(
                card = null,
                onDismiss = { showAddDialog = false },
                onSave = { serialNumber, alias, threshold ->
                    viewModel.addCard(CardAlias(
                        cardSerialNumber = serialNumber,
                        alias = alias,
                        lowBalanceThreshold = threshold,
                        notificationsEnabled = threshold != null
                    ))
                    showAddDialog = false
                }
            )
        }

        selectedCard?.let { card ->
            AddEditCardDialog(
                card = card,
                onDismiss = { selectedCard = null },
                onSave = { _, alias, threshold ->
                    viewModel.updateCard(card.copy(
                        alias = alias,
                        lowBalanceThreshold = threshold,
                        notificationsEnabled = threshold != null
                    ))
                    selectedCard = null
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Implemented by Irfan",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://irfanhasan.vercel.app/"))
                    context.startActivity(intent)
                },
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardAliasItem(
    card: CardAlias,
    onEdit: (CardAlias) -> Unit,
    onDelete: (CardAlias) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = card.alias,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = card.cardSerialNumber,
                    style = MaterialTheme.typography.bodySmall
                )
                card.lastBalance?.let { balance ->
                    Text(
                        text = "Balance: ৳%.2f".format(balance),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row {
                if (card.notificationsEnabled) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications enabled",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                IconButton(onClick = { onEdit(card) }) {
                    Icon(Icons.Default.Edit, "Edit")
                }
                IconButton(onClick = { onDelete(card) }) {
                    Icon(Icons.Default.Delete, "Delete")
                }
            }
        }
    }
}

@Composable
private fun AddEditCardDialog(
    card: CardAlias?,
    onDismiss: () -> Unit,
    onSave: (String, String, Double?) -> Unit
) {
    var serialNumber by remember { mutableStateOf(card?.cardSerialNumber ?: "") }
    var alias by remember { mutableStateOf(card?.alias ?: "") }
    var threshold by remember { mutableStateOf(card?.lowBalanceThreshold?.toString() ?: "") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (card == null) "Add Card Alias" else "Edit Card Alias") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (card == null) {
                    OutlinedTextField(
                        value = serialNumber,
                        onValueChange = { serialNumber = it },
                        label = { Text("Card Serial Number") },
                        isError = showError && serialNumber.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = alias,
                    onValueChange = { alias = it },
                    label = { Text("Alias") },
                    isError = showError && alias.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = threshold,
                    onValueChange = { threshold = it },
                    label = { Text("Low Balance Threshold (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (alias.isBlank() || (card == null && serialNumber.isBlank())) {
                        showError = true
                        return@TextButton
                    }
                    onSave(
                        serialNumber.takeIf { card == null } ?: card.cardSerialNumber,
                        alias,
                        threshold.toDoubleOrNull()
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

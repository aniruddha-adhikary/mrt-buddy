package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(
    uiState: HistoryScreenState,
    onCardSelected: (String) -> Unit,
    viewModel: HistoryScreenViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.onAction(HistoryScreenAction.OnInit)
    }
    
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var cardToRename by remember { mutableStateOf<Pair<String, String?>>("" to null) }
    var cardToDelete by remember { mutableStateOf("") }
    if (showRenameDialog) {
        RenameDialog(
            currentName = cardToRename.second,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName ->
                cardToRename.first?.let { cardIdm ->
                    viewModel.onAction(HistoryScreenAction.RenameCard(cardIdm, newName))
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Card") },
            text = { Text("Are you sure you want to delete this card? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onAction(HistoryScreenAction.DeleteCard(cardToDelete))
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }


    if (uiState.isLoading) {
        // Display a loading indicator
//        Text("Loading...")
    } else if (uiState.error != null) {
        // Display the error message
        Text("Error: ${uiState.error}")
    } else if (uiState.cards.isEmpty()) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No cards found",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Scan a card in the Balance Tab to get started.\n\nCome back here afterwards.",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        // Display the list of cards
        LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
            items(uiState.cards) { card ->
                CardItem(
                    card = card,
                    onCardSelected = { onCardSelected(card.idm) }, // Pass card.idm when selected
                    onRenameClick = {
                        cardToRename = card.idm to card.name
                        showRenameDialog = true
                    },
                    onDeleteClick = {
                        cardToDelete = card.idm
                        showDeleteDialog = true
                    }
                )
            }
        }
    }
}


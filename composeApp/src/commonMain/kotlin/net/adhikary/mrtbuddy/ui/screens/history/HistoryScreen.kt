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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.cancel
import mrtbuddy.composeapp.generated.resources.delete
import mrtbuddy.composeapp.generated.resources.deleteCard
import mrtbuddy.composeapp.generated.resources.deleteCardConfirm
import mrtbuddy.composeapp.generated.resources.noCardsFound
import mrtbuddy.composeapp.generated.resources.scanCardPrompt
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HistoryScreen(
    onCardSelected: (String) -> Unit,
    viewModel: HistoryScreenViewModel = koinViewModel(),
) {
    val uiState = viewModel.state.collectAsState().value
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
            title = { Text(stringResource(Res.string.deleteCard)) },
            text = { Text(stringResource(Res.string.deleteCardConfirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onAction(HistoryScreenAction.DeleteCard(cardToDelete))
                        showDeleteDialog = false
                    }
                ) {
                    Text(stringResource(Res.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(Res.string.cancel))
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
                    text = stringResource(Res.string.noCardsFound),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = stringResource(Res.string.scanCardPrompt),
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
            items(uiState.cards) { cardWithBalance ->
                CardItem(
                    card = cardWithBalance.card,
                    balance = cardWithBalance.balance,
                    onCardSelected = { onCardSelected(cardWithBalance.card.idm) }, // Pass card.idm when selected
                    onRenameClick = {
                        cardToRename = cardWithBalance.card.idm to cardWithBalance.card.name
                        showRenameDialog = true
                    },
                    onDeleteClick = {
                        cardToDelete = cardWithBalance.card.idm
                        showDeleteDialog = true
                    }
                )
            }
        }
    }
    
}

package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
    modifier: Modifier = Modifier,
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
        Box(
            modifier = Modifier.fillMaxSize().then(modifier),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.noCardsFound),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = stringResource(Res.string.scanCardPrompt),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 32.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        // Display the list of cards
        LazyColumn(modifier = Modifier.padding(top = 8.dp).then(modifier)) {
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

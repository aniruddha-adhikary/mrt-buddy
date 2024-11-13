package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var cardToRename by remember { mutableStateOf<Pair<String, String?>>("" to null) }
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


    if (uiState.isLoading) {
        // Display a loading indicator
//        Text("Loading...")
    } else if (uiState.error != null) {
        // Display the error message
        Text("Error: ${uiState.error}")
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
                    }
                )
            }
        }
    }
}


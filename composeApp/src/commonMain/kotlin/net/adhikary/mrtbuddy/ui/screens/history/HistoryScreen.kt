package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.data.CardEntity

@Composable
fun HistoryScreen(
    uiState: HistoryScreenState,
    onCardSelected: (String) -> Unit // Change parameter to accept cardIdm
) {
    Spacer(modifier = Modifier.height(16.dp))

    if (uiState.isLoading) {
        // Display a loading indicator
        Text("Loading...")
    } else if (uiState.error != null) {
        // Display the error message
        Text("Error: ${uiState.error}")
    } else {
        // Display the list of cards
        LazyColumn {
            items(uiState.cards) { card ->
                CardItem(
                    card = card,
                    onCardSelected = { onCardSelected(card.idm) } // Pass card.idm when selected
                )
            }
        }
    }
}


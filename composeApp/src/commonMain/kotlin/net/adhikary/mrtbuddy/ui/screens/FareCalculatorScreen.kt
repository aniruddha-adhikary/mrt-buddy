package net.adhikary.mrtbuddy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.ui.viewmodel.FareCalculatorViewModel
import net.adhikary.mrtbuddy.ui.screens.components.FareCalculatorTopBar
import net.adhikary.mrtbuddy.ui.screens.components.StationSelectionSection
import net.adhikary.mrtbuddy.ui.screens.components.FareDisplayCard

@Composable
fun FareCalculatorScreen(cardState: CardState) {
    val viewModel = remember { FareCalculatorViewModel.getInstance() }

    // Update card state when it changes
    LaunchedEffect(cardState) {
        viewModel.updateCardState(cardState)
    }

    Scaffold(
        topBar = { FareCalculatorTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StationSelectionSection(viewModel)
            FareDisplayCard(viewModel, cardState)
        }
    }
}

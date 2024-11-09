package net.adhikary.mrtbuddy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.ui.screens.components.FareDisplayCard
import net.adhikary.mrtbuddy.ui.screens.components.StationSelectionSection
import net.adhikary.mrtbuddy.ui.viewmodel.FareCalculatorViewModel

@Composable
fun FareCalculatorScreen(cardState: CardState) {
    val viewModel = remember { FareCalculatorViewModel.getInstance() }

    // Update card state when it changes
    LaunchedEffect(cardState) {
        viewModel.updateCardState(cardState)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FareDisplayCard(viewModel, cardState)
        Spacer(modifier = Modifier.height(4.dp))
        StationSelectionSection(viewModel)
    }
}

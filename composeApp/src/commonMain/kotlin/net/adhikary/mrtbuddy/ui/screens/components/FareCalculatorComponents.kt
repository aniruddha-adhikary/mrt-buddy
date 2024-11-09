package net.adhikary.mrtbuddy.ui.screens.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.ui.theme.DarkPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.LightPositiveGreen
import net.adhikary.mrtbuddy.ui.viewmodel.FareCalculatorViewModel


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StationSelectionSection(viewModel: FareCalculatorViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // From Station Dropdown
        ExposedDropdownMenuBox(
            expanded = viewModel.fromExpanded,
            onExpandedChange = { viewModel.toggleFromExpanded() }
        ) {
            TextField(
                value = viewModel.fromStation?.name ?: "Select Origin Station",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.fromExpanded) },
                modifier = Modifier.fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                    focusedIndicatorColor = MaterialTheme.colors.primary.copy(alpha = 0f),
                    unfocusedIndicatorColor = MaterialTheme.colors.primary.copy(alpha = 0f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = viewModel.fromExpanded,
                onDismissRequest = { viewModel.dismissDropdowns() }
            ) {
                viewModel.stations.forEach { station ->
                    DropdownMenuItem(
                        onClick = { viewModel.updateFromStation(station) }
                    ) {
                        Text(text = station.name)
                    }
                }
            }
        }

        // To Station Dropdown
        ExposedDropdownMenuBox(
            expanded = viewModel.toExpanded,
            onExpandedChange = { viewModel.toggleToExpanded() }
        ) {
            TextField(
                value = viewModel.toStation?.name ?: "Select Destination Station",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.toExpanded) },
                modifier = Modifier.fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                    focusedIndicatorColor = MaterialTheme.colors.primary.copy(alpha = 0f),
                    unfocusedIndicatorColor = MaterialTheme.colors.primary.copy(alpha = 0f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = viewModel.toExpanded,
                onDismissRequest = { viewModel.dismissDropdowns() }
            ) {
                viewModel.stations.forEach { station ->
                    DropdownMenuItem(
                        onClick = { viewModel.updateToStation(station) }
                    ) {
                        Text(text = station.name)
                    }
                }
            }
        }
    }
}

@Composable
fun FareDisplayCard(viewModel: FareCalculatorViewModel, cardState: CardState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        if (viewModel.fromStation == null || viewModel.toStation == null) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Select stations",
                        modifier = Modifier.height(48.dp),
                        tint = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select Stations",
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Choose origin and destination\nto calculate fare",
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MRT Pass / Rapid Pass",
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "৳ ${viewModel.discountedFare}",
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.onSurface
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Single Ticket ৳ ${viewModel.calculatedFare}",
                        style = MaterialTheme.typography.caption
                    )
                    Text(
                        text = "Discount ৳ ${viewModel.getSavings()}",
                        style = MaterialTheme.typography.caption,
                        color = if (isSystemInDarkTheme()) DarkPositiveGreen else LightPositiveGreen
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Divider()
                Spacer(modifier = Modifier.height(2.dp))

                Spacer(modifier = Modifier.weight(1f))
                when (cardState) {
                    is CardState.Balance -> {
                        val balance = cardState.amount
                        if (balance >= viewModel.calculatedFare) {
                            Text(
                                text = "Your balance (৳ $balance) is sufficient.",
                                style = MaterialTheme.typography.body2,
                                color = if (isSystemInDarkTheme()) DarkPositiveGreen else LightPositiveGreen,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = "Your balance (৳ $balance) is too low.",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.error.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = "Tap your card to check if you have sufficient balance",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

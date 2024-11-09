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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.TransactionType
import net.adhikary.mrtbuddy.ui.theme.DarkPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.LightPositiveGreen
import net.adhikary.mrtbuddy.ui.viewmodel.FareCalculatorViewModel

@Composable
fun FareCalculatorTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Fare Calculator",
                style = MaterialTheme.typography.h6
            )
        },
        navigationIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Calculator",
                modifier = Modifier.padding(start = 12.dp)
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 4.dp
    )
}

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
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()
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
    if (viewModel.fromStation != null && viewModel.toStation != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(24.dp)
        ) {
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
                        color = MaterialTheme.colors.primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Regular ৳ ${viewModel.calculatedFare}",
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                }
            }
        }
    }
}

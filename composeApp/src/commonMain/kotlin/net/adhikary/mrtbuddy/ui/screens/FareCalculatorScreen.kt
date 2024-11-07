package net.adhikary.mrtbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.ui.viewmodel.FareCalculatorViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FareCalculatorScreen() {
    val viewModel = remember { FareCalculatorViewModel.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Fare Calculator",
            style = MaterialTheme.typography.h5
        )

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

        // Fare Display
        if (viewModel.fromStation != null && viewModel.toStation != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Regular Fare
                    Text(
                        text = "Regular Fare",
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = "${viewModel.calculatedFare} BDT",
                        style = MaterialTheme.typography.h4
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // MRT/Rapid Pass Fare
                    Text(
                        text = "MRT/Rapid Pass Fare",
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = "${viewModel.discountedFare} BDT",
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.primary
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Savings
                    Text(
                        text = "Your Savings",
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = "${viewModel.getSavings()} BDT",
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }
    }
}

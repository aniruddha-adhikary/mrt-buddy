package net.adhikary.mrtbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.model.FareMatrix
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.clickable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FareCalculatorScreen() {
    var fromStation by remember { mutableStateOf("") }
    var toStation by remember { mutableStateOf("") }
    var regularFare by remember { mutableStateOf(0) }
    var passFare by remember { mutableStateOf(0) }
    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    val stationService = StationService()
    val stations = stationService.getAllStations()

    fun updateFares(from: String, to: String) {
        val fare = if (from.isEmpty() || to.isEmpty()) 0 else FareMatrix.getFare(from, to)
        regularFare = fare
        passFare = (fare * 0.9).toInt() // 10% discount
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fare Calculator") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = 56.dp)
        ) {
            // From Station Dropdown
            ExposedDropdownMenuBox(
                expanded = fromExpanded,
                onExpandedChange = { fromExpanded = !fromExpanded }
            ) {
                OutlinedTextField(
                    value = fromStation,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("From Station") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = fromExpanded,
                    onDismissRequest = { fromExpanded = false }
                ) {
                    stations.forEach { station ->
                        DropdownMenuItem(
                            text = { Text(station) },
                            onClick = {
                                fromStation = station
                                fromExpanded = false
                                updateFares(fromStation, toStation)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // To Station Dropdown
            ExposedDropdownMenuBox(
                expanded = toExpanded,
                onExpandedChange = { toExpanded = !toExpanded }
            ) {
                OutlinedTextField(
                    value = toStation,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("To Station") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = toExpanded,
                    onDismissRequest = { toExpanded = false }
                ) {
                    stations.forEach { station ->
                        DropdownMenuItem(
                            text = { Text(station) },
                            onClick = {
                                toStation = station
                                toExpanded = false
                                updateFares(fromStation, toStation)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fare Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Regular Fare: ৳$regularFare",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MRT/Rapid Pass Fare: ৳$passFare",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Attribution
            val uriHandler = LocalUriHandler.current
            Text(
                text = "Implemented by Irfan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { uriHandler.openUri("https://irfanhasan.vercel.app/") }
                    .padding(8.dp)
            )
        }
    }
}



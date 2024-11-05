package net.adhikary.mrtbuddy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.Direction
import net.adhikary.mrtbuddy.model.MetroStations
import net.adhikary.mrtbuddy.model.TimeSlot
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetroScheduleScreen() {
    val uriHandler = LocalUriHandler.current
    var selectedStation by remember { mutableStateOf("") }
    var selectedDirection by remember { mutableStateOf<Direction>(Direction.NORTHBOUND) }
    var showWeekendSchedule by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedTimeRange by remember { mutableStateOf("All") }
    var showElevatorOnly by remember { mutableStateOf(false) }
    var showWheelchairAccessOnly by remember { mutableStateOf(false) }
    var showInterchangeOnly by remember { mutableStateOf(false) }

    val allStations = MetroStations.stations
    val filteredStations = remember(showElevatorOnly, showWheelchairAccessOnly, showInterchangeOnly) {
        allStations.filter { station ->
            (!showElevatorOnly || station.hasElevator) &&
            (!showWheelchairAccessOnly || station.hasWheelchairAccess) &&
            (!showInterchangeOnly || station.isInterchange)
        }
    }
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    // Define time slots for detailed schedule
    val timeSlots = remember {
        listOf(
            TimeSlot(LocalTime.of(8, 0), LocalTime.of(10, 0), 10, true),  // Morning peak
            TimeSlot(LocalTime.of(10, 0), LocalTime.of(16, 0), 15, false), // Off-peak day
            TimeSlot(LocalTime.of(16, 0), LocalTime.of(19, 0), 10, true),  // Evening peak
            TimeSlot(LocalTime.of(19, 0), LocalTime.of(20, 0), 15, false)  // Off-peak evening
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Metro Schedule") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, "Filter")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Station Selection
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedStation.ifEmpty {
                        if (filteredStations.isEmpty()) "No stations match filters" else "Select Station"
                    },
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Select Station") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Select Station") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    enabled = filteredStations.isNotEmpty()
                )
                ExposedDropdownMenu(
                    expanded = expanded && filteredStations.isNotEmpty(),
                    onDismissRequest = { expanded = false }
                ) {
                    filteredStations.forEach { station ->
                        DropdownMenuItem(
                            text = {
                                Text(buildString {
                                    append(station.name)
                                    if (station.isInterchange) append(" (Interchange)")
                                })
                            },
                            onClick = {
                                selectedStation = station.name
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Direction Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Direction:")
                FilterChip(
                    selected = selectedDirection == Direction.NORTHBOUND,
                    onClick = { selectedDirection = Direction.NORTHBOUND },
                    label = { Text("Northbound") }
                )
                FilterChip(
                    selected = selectedDirection == Direction.SOUTHBOUND,
                    onClick = { selectedDirection = Direction.SOUTHBOUND },
                    label = { Text("Southbound") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Schedule Type Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Schedule:")
                FilterChip(
                    selected = !showWeekendSchedule,
                    onClick = { showWeekendSchedule = false },
                    label = { Text("Weekday") }
                )
                FilterChip(
                    selected = showWeekendSchedule,
                    onClick = { showWeekendSchedule = true },
                    label = { Text("Weekend") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Schedule Display
            LazyColumn {
                // Operating Hours
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = if (showWeekendSchedule) "Weekend Operating Hours" else "Weekday Operating Hours",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = if (showWeekendSchedule)
                                    "First Train: 9:00 AM\nLast Train: 7:00 PM"
                                else
                                    "First Train: 8:00 AM\nLast Train: 8:00 PM",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Detailed Time Slots
                items(timeSlots) { slot ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (slot.isPeakHour)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "${if (slot.isPeakHour) "Peak" else "Off-Peak"} Hours: " +
                                    "${slot.startTime.format(timeFormatter)} - ${slot.endTime.format(timeFormatter)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Train Frequency: Every ${slot.frequency} minutes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            if (selectedStation.isNotEmpty()) {
                                val station = allStations.find { it.name == selectedStation }
                                station?.let {
                                    Text(
                                        text = "Station: $selectedStation (Platform ${it.platformNumber})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "Direction: ${selectedDirection.displayName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = buildString {
                                            append("Features: ")
                                            if (it.hasElevator) append("Elevator Available • ")
                                            if (it.hasWheelchairAccess) append("Wheelchair Access • ")
                                            if (it.isInterchange) append("Interchange Station")
                                        }.trimEnd(' ', '•'),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    if (it.estimatedTravelTimeToNext > 0) {
                                        Text(
                                            text = "Est. Travel Time to Next Station: ${it.estimatedTravelTimeToNext} min",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Attribution text
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

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter Schedule") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Time Range Filter
                    Text(
                        text = "Time Range",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = selectedTimeRange == "Morning",
                            onClick = { selectedTimeRange = "Morning" },
                            label = { Text("Morning (6AM-12PM)") }
                        )
                        FilterChip(
                            selected = selectedTimeRange == "Afternoon",
                            onClick = { selectedTimeRange = "Afternoon" },
                            label = { Text("Afternoon (12PM-6PM)") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Accessibility Features
                    Text(
                        text = "Accessibility Features",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = showElevatorOnly,
                            onClick = { showElevatorOnly = !showElevatorOnly },
                            label = { Text("Elevator Available") }
                        )
                        FilterChip(
                            selected = showWheelchairAccessOnly,
                            onClick = { showWheelchairAccessOnly = !showWheelchairAccessOnly },
                            label = { Text("Wheelchair Access") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Interchange Stations
                    Text(
                        text = "Station Type",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = showInterchangeOnly,
                            onClick = { showInterchangeOnly = !showInterchangeOnly },
                            label = { Text("Interchange Stations") }
                        )
                    }
                }
            },
            confirmButton = {
                Row {
                    TextButton(
                        onClick = {
                            showFilterDialog = false
                            // Reset filters
                            if (selectedTimeRange == "All") {
                                showElevatorOnly = false
                                showWheelchairAccessOnly = false
                                showInterchangeOnly = false
                            }
                        }
                    ) {
                        Text("Apply")
                    }
                    TextButton(
                        onClick = {
                            showFilterDialog = false
                            selectedTimeRange = "All"
                            showElevatorOnly = false
                            showWheelchairAccessOnly = false
                            showInterchangeOnly = false
                        }
                    ) {
                        Text("Reset")
                    }
                }
            }
        )
    }
}

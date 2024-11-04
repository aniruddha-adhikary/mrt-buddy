package net.adhikary.mrtbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign

data class Station(val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FareCalculatorScreenNative(onNavigateToCardReader: () -> Unit) {
    var fromStation by remember { mutableStateOf<Station?>(null) }
    var toStation by remember { mutableStateOf<Station?>(null) }
    var showResults by remember { mutableStateOf(false) }

    val stations = listOf(
        Station("Uttara North"),
        Station("Uttara Center"),
        Station("Uttara South"),
        Station("Pallabi"),
        Station("Mirpur-11"),
        Station("Mirpur-10"),
        Station("Kazipara"),
        Station("Shewrapara"),
        Station("Agargaon"),
        Station("Bijoy Sarani"),
        Station("Farmgate"),
        Station("Karwan Bazar"),
        Station("Shahbagh"),
        Station("Dhaka University"),
        Station("Secretariat"),
        Station("Motijheel"),
        Station("Kamalapur")
    )

    val fareMatrix = mapOf(
        "Uttara North" to mapOf(
            "Uttara North" to 20, "Uttara Center" to 20, "Uttara South" to 20,
            "Pallabi" to 30, "Mirpur-11" to 30, "Mirpur-10" to 40,
            "Kazipara" to 40, "Shewrapara" to 40, "Agargaon" to 60,
            "Bijoy Sarani" to 60, "Farmgate" to 70, "Karwan Bazar" to 70,
            "Shahbagh" to 80, "Dhaka University" to 80, "Secretariat" to 100,
            "Motijheel" to 100, "Kamalapur" to 100
        ),
        "Uttara Center" to mapOf(
            "Uttara North" to 20, "Uttara Center" to 20, "Uttara South" to 20,
            "Pallabi" to 30, "Mirpur-11" to 30, "Mirpur-10" to 40,
            "Kazipara" to 40, "Shewrapara" to 40, "Agargaon" to 60,
            "Bijoy Sarani" to 60, "Farmgate" to 70, "Karwan Bazar" to 70,
            "Shahbagh" to 80, "Dhaka University" to 80, "Secretariat" to 100,
            "Motijheel" to 100, "Kamalapur" to 100
        ),
        "Uttara South" to mapOf(
            "Uttara North" to 20, "Uttara Center" to 20, "Uttara South" to 20,
            "Pallabi" to 30, "Mirpur-11" to 30, "Mirpur-10" to 40,
            "Kazipara" to 40, "Shewrapara" to 40, "Agargaon" to 60,
            "Bijoy Sarani" to 60, "Farmgate" to 70, "Karwan Bazar" to 70,
            "Shahbagh" to 80, "Dhaka University" to 80, "Secretariat" to 100,
            "Motijheel" to 100, "Kamalapur" to 100
        ),
        "Pallabi" to mapOf(
            "Uttara North" to 30, "Uttara Center" to 30, "Uttara South" to 30,
            "Pallabi" to 20, "Mirpur-11" to 20, "Mirpur-10" to 20,
            "Kazipara" to 20, "Shewrapara" to 20, "Agargaon" to 40,
            "Bijoy Sarani" to 40, "Farmgate" to 50, "Karwan Bazar" to 50,
            "Shahbagh" to 60, "Dhaka University" to 60, "Secretariat" to 80,
            "Motijheel" to 80, "Kamalapur" to 80
        ),
        "Mirpur-11" to mapOf(
            "Uttara North" to 30, "Uttara Center" to 30, "Uttara South" to 30,
            "Pallabi" to 20, "Mirpur-11" to 20, "Mirpur-10" to 20,
            "Kazipara" to 20, "Shewrapara" to 20, "Agargaon" to 40,
            "Bijoy Sarani" to 40, "Farmgate" to 50, "Karwan Bazar" to 50,
            "Shahbagh" to 60, "Dhaka University" to 60, "Secretariat" to 80,
            "Motijheel" to 80, "Kamalapur" to 80
        ),
        "Mirpur-10" to mapOf(
            "Uttara North" to 40, "Uttara Center" to 40, "Uttara South" to 40,
            "Pallabi" to 20, "Mirpur-11" to 20, "Mirpur-10" to 20,
            "Kazipara" to 20, "Shewrapara" to 20, "Agargaon" to 40,
            "Bijoy Sarani" to 40, "Farmgate" to 50, "Karwan Bazar" to 50,
            "Shahbagh" to 60, "Dhaka University" to 60, "Secretariat" to 80,
            "Motijheel" to 80, "Kamalapur" to 80
        ),
        "Kazipara" to mapOf(
            "Uttara North" to 40, "Uttara Center" to 40, "Uttara South" to 40,
            "Pallabi" to 20, "Mirpur-11" to 20, "Mirpur-10" to 20,
            "Kazipara" to 20, "Shewrapara" to 20, "Agargaon" to 40,
            "Bijoy Sarani" to 40, "Farmgate" to 50, "Karwan Bazar" to 50,
            "Shahbagh" to 60, "Dhaka University" to 60, "Secretariat" to 80,
            "Motijheel" to 80, "Kamalapur" to 80
        ),
        "Shewrapara" to mapOf(
            "Uttara North" to 40, "Uttara Center" to 40, "Uttara South" to 40,
            "Pallabi" to 20, "Mirpur-11" to 20, "Mirpur-10" to 20,
            "Kazipara" to 20, "Shewrapara" to 20, "Agargaon" to 40,
            "Bijoy Sarani" to 40, "Farmgate" to 50, "Karwan Bazar" to 50,
            "Shahbagh" to 60, "Dhaka University" to 60, "Secretariat" to 80,
            "Motijheel" to 80, "Kamalapur" to 80
        ),
        "Agargaon" to mapOf(
            "Uttara North" to 60, "Uttara Center" to 60, "Uttara South" to 60,
            "Pallabi" to 40, "Mirpur-11" to 40, "Mirpur-10" to 40,
            "Kazipara" to 40, "Shewrapara" to 40, "Agargaon" to 20,
            "Bijoy Sarani" to 20, "Farmgate" to 30, "Karwan Bazar" to 30,
            "Shahbagh" to 40, "Dhaka University" to 40, "Secretariat" to 60,
            "Motijheel" to 60, "Kamalapur" to 60
        ),
        "Bijoy Sarani" to mapOf(
            "Uttara North" to 60, "Uttara Center" to 60, "Uttara South" to 60,
            "Pallabi" to 40, "Mirpur-11" to 40, "Mirpur-10" to 40,
            "Kazipara" to 40, "Shewrapara" to 40, "Agargaon" to 20,
            "Bijoy Sarani" to 20, "Farmgate" to 30, "Karwan Bazar" to 30,
            "Shahbagh" to 40, "Dhaka University" to 40, "Secretariat" to 60,
            "Motijheel" to 60, "Kamalapur" to 60
        ),
        "Farmgate" to mapOf(
            "Uttara North" to 70, "Uttara Center" to 70, "Uttara South" to 70,
            "Pallabi" to 50, "Mirpur-11" to 50, "Mirpur-10" to 50,
            "Kazipara" to 50, "Shewrapara" to 50, "Agargaon" to 30,
            "Bijoy Sarani" to 30, "Farmgate" to 20, "Karwan Bazar" to 20,
            "Shahbagh" to 30, "Dhaka University" to 30, "Secretariat" to 50,
            "Motijheel" to 50, "Kamalapur" to 50
        ),
        "Karwan Bazar" to mapOf(
            "Uttara North" to 70, "Uttara Center" to 70, "Uttara South" to 70,
            "Pallabi" to 50, "Mirpur-11" to 50, "Mirpur-10" to 50,
            "Kazipara" to 50, "Shewrapara" to 50, "Agargaon" to 30,
            "Bijoy Sarani" to 30, "Farmgate" to 20, "Karwan Bazar" to 20,
            "Shahbagh" to 30, "Dhaka University" to 30, "Secretariat" to 50,
            "Motijheel" to 50, "Kamalapur" to 50
        ),
        "Shahbagh" to mapOf(
            "Uttara North" to 80, "Uttara Center" to 80, "Uttara South" to 80,
            "Pallabi" to 60, "Mirpur-11" to 60, "Mirpur-10" to 60,
            "Kazipara" to 60, "Shewrapara" to 60, "Agargaon" to 40,
            "Bijoy Sarani" to 40, "Farmgate" to 30, "Karwan Bazar" to 30,
            "Shahbagh" to 20, "Dhaka University" to 20, "Secretariat" to 30,
            "Motijheel" to 30, "Kamalapur" to 30
        ),
        "Dhaka University" to mapOf(
            "Uttara North" to 80, "Uttara Center" to 80, "Uttara South" to 80,
            "Pallabi" to 60, "Mirpur-11" to 60, "Mirpur-10" to 60,
            "Kazipara" to 60, "Shewrapara" to 60, "Agargaon" to 40,
            "Bijoy Sarani" to 40, "Farmgate" to 30, "Karwan Bazar" to 30,
            "Shahbagh" to 20, "Dhaka University" to 20, "Secretariat" to 30,
            "Motijheel" to 30, "Kamalapur" to 30
        ),
        "Secretariat" to mapOf(
            "Uttara North" to 100, "Uttara Center" to 100, "Uttara South" to 100,
            "Pallabi" to 80, "Mirpur-11" to 80, "Mirpur-10" to 80,
            "Kazipara" to 80, "Shewrapara" to 80, "Agargaon" to 60,
            "Bijoy Sarani" to 60, "Farmgate" to 50, "Karwan Bazar" to 50,
            "Shahbagh" to 30, "Dhaka University" to 30, "Secretariat" to 20,
            "Motijheel" to 20, "Kamalapur" to 20
        ),
        "Motijheel" to mapOf(
            "Uttara North" to 100, "Uttara Center" to 100, "Uttara South" to 100,
            "Pallabi" to 80, "Mirpur-11" to 80, "Mirpur-10" to 80,
            "Kazipara" to 80, "Shewrapara" to 80, "Agargaon" to 60,
            "Bijoy Sarani" to 60, "Farmgate" to 50, "Karwan Bazar" to 50,
            "Shahbagh" to 30, "Dhaka University" to 30, "Secretariat" to 20,
            "Motijheel" to 20, "Kamalapur" to 20
        ),
        "Kamalapur" to mapOf(
            "Uttara North" to 100, "Uttara Center" to 100, "Uttara South" to 100,
            "Pallabi" to 80, "Mirpur-11" to 80, "Mirpur-10" to 80,
            "Kazipara" to 80, "Shewrapara" to 80, "Agargaon" to 60,
            "Bijoy Sarani" to 60, "Farmgate" to 50, "Karwan Bazar" to 50,
            "Shahbagh" to 30, "Dhaka University" to 30, "Secretariat" to 20,
            "Motijheel" to 20, "Kamalapur" to 20
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Fare Calculator",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // From Station Dropdown
        var fromExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = fromExpanded,
            onExpandedChange = { fromExpanded = it },
        ) {
            TextField(
                value = fromStation?.name ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text("From Station") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = fromExpanded,
                onDismissRequest = { fromExpanded = false },
            ) {
                stations.forEach { station ->
                    DropdownMenuItem(
                        text = { Text(station.name) },
                        onClick = {
                            fromStation = station
                            fromExpanded = false
                            showResults = fromStation != null && toStation != null
                        }
                    )
                }
            }
        }

        // To Station Dropdown
        var toExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = toExpanded,
            onExpandedChange = { toExpanded = it },
        ) {
            TextField(
                value = toStation?.name ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text("To Station") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = toExpanded,
                onDismissRequest = { toExpanded = false },
            ) {
                stations.forEach { station ->
                    DropdownMenuItem(
                        text = { Text(station.name) },
                        onClick = {
                            toStation = station
                            toExpanded = false
                            showResults = fromStation != null && toStation != null
                        }
                    )
                }
            }
        }

        if (showResults) {
            val fromStationName = fromStation?.name ?: return@Column
            val toStationName = toStation?.name ?: return@Column
            val regularFare = fareMatrix[fromStationName]?.get(toStationName) ?: 0
            val discountedFare = (regularFare * 0.9).toInt()
            val savings = regularFare - discountedFare

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Regular Fare: $regularFare BDT",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "MRT/Rapid Pass Fare: $discountedFare BDT",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "You save: $savings BDT",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

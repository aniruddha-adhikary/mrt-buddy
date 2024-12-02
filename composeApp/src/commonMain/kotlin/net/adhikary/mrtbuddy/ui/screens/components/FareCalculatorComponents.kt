package net.adhikary.mrtbuddy.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balanceAmount
import mrtbuddy.composeapp.generated.resources.chooseOrgDest
import mrtbuddy.composeapp.generated.resources.rescan
import mrtbuddy.composeapp.generated.resources.roundTrips
import mrtbuddy.composeapp.generated.resources.selectDestination
import mrtbuddy.composeapp.generated.resources.selectOrigin
import mrtbuddy.composeapp.generated.resources.selectStations
import mrtbuddy.composeapp.generated.resources.singleTicket
import mrtbuddy.composeapp.generated.resources.tapToCheckSufficientBalance
import mrtbuddy.composeapp.generated.resources.two_way_arrows
import mrtbuddy.composeapp.generated.resources.withMRT
import mrtbuddy.composeapp.generated.resources.yourBalance
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorAction
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorState
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorViewModel
import net.adhikary.mrtbuddy.ui.theme.DarkPositiveGreen
import net.adhikary.mrtbuddy.ui.theme.LightPositiveGreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelectionSection(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // From Station Dropdown
        // From Station Dropdown
        ExposedDropdownMenuBox(
            expanded = uiState.fromExpanded,
            onExpandedChange = { viewModel.onAction(FareCalculatorAction.ToggleFromExpanded) }
        ) {
            TextField(
                value = uiState.fromStation?.let { StationService.translate(it.name) }
                    ?: stringResource(Res.string.selectOrigin),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.fromExpanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    errorContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = uiState.fromExpanded,
                onDismissRequest = { viewModel.onAction(FareCalculatorAction.DismissDropdowns) }
            ) {
                viewModel.stations.forEach { station ->
                    DropdownMenuItem(
                        text = { Text(text = StationService.translate(station.name)) },
                        onClick = { viewModel.onAction(FareCalculatorAction.UpdateFromStation(station)) }
                    )
                }
            }
        }

        // To Station Dropdown
        ExposedDropdownMenuBox(
            expanded = uiState.toExpanded,
            onExpandedChange = { viewModel.onAction(FareCalculatorAction.ToggleToExpanded) }
        ) {
            TextField(
                value = uiState.toStation?.let { StationService.translate(it.name) }
                    ?: stringResource(Res.string.selectDestination),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.toExpanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    errorContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = uiState.toExpanded,
                onDismissRequest = { viewModel.onAction(FareCalculatorAction.DismissDropdowns) }
            ) {
                viewModel.stations.forEach { station ->
                    DropdownMenuItem(
                        text = { Text(text = StationService.translate(station.name)) },
                        onClick = { viewModel.onAction(FareCalculatorAction.UpdateToStation(station)) }
                    )
                }
            }
        }
    }
}

@Composable
fun FareDisplayCard(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        if (viewModel.state.value.fromStation == null || viewModel.state.value.toStation == null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Select stations",
                    modifier = Modifier.height(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.selectStations),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.chooseOrgDest),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            Box(Modifier.fillMaxSize().padding(24.dp)) {
                if (getPlatform().name != "android") {
                    Text(
                        text = stringResource(Res.string.rescan),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { RescanManager.requestRescan() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (getPlatform().name != "android") {
                            Spacer(modifier = Modifier.height(24.dp))
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        Text(
                            text = stringResource(Res.string.withMRT),
                            style = MaterialTheme.typography.titleSmall
                        )
                        if (getPlatform().name == "android") {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Text(
                            text = "৳ ${translateNumber(viewModel.state.value.discountedFare)}",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    when (uiState.cardState) {
                        is CardState.Balance -> {
                            val balance = uiState.cardState.amount
                            if (balance >= uiState.calculatedFare) {
                                val roundTrips = if (uiState.calculatedFare > 0) balance / (uiState.discountedFare * 2) else 0
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${stringResource(Res.string.balanceAmount)} ৳ ${translateNumber(balance)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSystemInDarkTheme()) DarkPositiveGreen else LightPositiveGreen
                                    )
                                    if (roundTrips > 0) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(Res.drawable.two_way_arrows),
                                                contentDescription = "Round trips",
                                                tint = if (isSystemInDarkTheme()) DarkPositiveGreen else LightPositiveGreen
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${translateNumber(roundTrips)} ${stringResource(Res.string.roundTrips)}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (isSystemInDarkTheme()) DarkPositiveGreen else LightPositiveGreen
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "${stringResource(Res.string.yourBalance)} (৳ $balance)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        else -> {
                            Text(
                                text = "${stringResource(Res.string.singleTicket)} ৳ ${
                                    translateNumber(
                                        uiState.calculatedFare
                                    )
                                }",
                                style = MaterialTheme.typography.titleSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(Res.string.tapToCheckSufficientBalance),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
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
}

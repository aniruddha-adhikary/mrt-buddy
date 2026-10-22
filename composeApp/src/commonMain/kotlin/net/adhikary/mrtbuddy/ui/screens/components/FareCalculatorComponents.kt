package net.adhikary.mrtbuddy.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
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
import net.adhikary.mrtbuddy.data.model.Station
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.components.IconBadge
import net.adhikary.mrtbuddy.ui.components.positiveColor
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorAction
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorState
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun StationSelectionSection(
    uiState: FareCalculatorState,
    viewModel: FareCalculatorViewModel,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StationDropdown(
                    label = stringResource(Res.string.selectOrigin),
                    selected = uiState.fromStation,
                    expanded = uiState.fromExpanded,
                    stations = viewModel.stations,
                    isDestination = false,
                    onExpandedChange = { viewModel.onAction(FareCalculatorAction.ToggleFromExpanded) },
                    onDismiss = { viewModel.onAction(FareCalculatorAction.DismissDropdowns) },
                    onSelect = { viewModel.onAction(FareCalculatorAction.UpdateFromStation(it)) },
                )
                StationDropdown(
                    label = stringResource(Res.string.selectDestination),
                    selected = uiState.toStation,
                    expanded = uiState.toExpanded,
                    stations = viewModel.stations,
                    isDestination = true,
                    onExpandedChange = { viewModel.onAction(FareCalculatorAction.ToggleToExpanded) },
                    onDismiss = { viewModel.onAction(FareCalculatorAction.DismissDropdowns) },
                    onSelect = { viewModel.onAction(FareCalculatorAction.UpdateToStation(it)) },
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            FilledTonalIconButton(
                onClick = { viewModel.onAction(FareCalculatorAction.SwapStations) },
                enabled = uiState.fromStation != null || uiState.toStation != null,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.two_way_arrows),
                    contentDescription = "Swap stations",
                    modifier = Modifier.rotate(90f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationDropdown(
    label: String,
    selected: Station?,
    expanded: Boolean,
    stations: List<Station>,
    isDestination: Boolean,
    onExpandedChange: () -> Unit,
    onDismiss: () -> Unit,
    onSelect: (Station) -> Unit,
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange() },
    ) {
        TextField(
            value = selected?.let { StationService.translate(it.name) } ?: "",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            leadingIcon = {
                if (isDestination) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Box(
                        modifier =
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                    )
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier =
                Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
            colors =
                ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            shape = MaterialTheme.shapes.medium,
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
        ) {
            stations.forEach { station ->
                DropdownMenuItem(
                    text = { Text(text = StationService.translate(station.name)) },
                    onClick = { onSelect(station) },
                )
            }
        }
    }
}

@Composable
fun FareDisplayCard(uiState: FareCalculatorState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        if (uiState.fromStation == null || uiState.toStation == null) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconBadge(imageVector = Icons.Default.Info, size = 56.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.selectStations),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(Res.string.chooseOrgDest),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.withMRT),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "৳ ${translateNumber(uiState.discountedFare)}",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Text(
                    text = "${stringResource(Res.string.singleTicket)} ৳ ${translateNumber(uiState.calculatedFare)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(16.dp))
                BalanceStatus(uiState)
            }
        }
    }
}

@Composable
private fun BalanceStatus(uiState: FareCalculatorState) {
    val cardState = uiState.cardState
    val isAndroid = getPlatform().name == "android"

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(
                    when {
                        cardState !is CardState.Balance -> MaterialTheme.colorScheme.surfaceContainerLow
                        cardState.amount >= uiState.calculatedFare -> positiveColor().copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.errorContainer
                    },
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when {
            cardState is CardState.Balance && cardState.amount >= uiState.calculatedFare -> {
                val balance = cardState.amount
                val roundTrips = if (uiState.discountedFare > 0) balance / (uiState.discountedFare * 2) else 0
                Text(
                    text = "${stringResource(Res.string.balanceAmount)} ৳ ${translateNumber(balance)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = positiveColor(),
                )
                if (roundTrips > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.two_way_arrows),
                            contentDescription = null,
                            tint = positiveColor(),
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${translateNumber(roundTrips)} ${stringResource(Res.string.roundTrips)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = positiveColor(),
                        )
                    }
                }
            }
            cardState is CardState.Balance -> {
                Text(
                    text = "${stringResource(Res.string.yourBalance)} ৳ ${translateNumber(cardState.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                )
            }
            else -> {
                Text(
                    text = stringResource(Res.string.tapToCheckSufficientBalance),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (!isAndroid) {
            TextButton(onClick = { RescanManager.requestRescan() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(stringResource(Res.string.rescan))
            }
        }
    }
}

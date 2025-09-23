package net.adhikary.mrtbuddy.ui.screens.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.rotate
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorAction
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorState
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorViewModel
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balanceAmount
import mrtbuddy.composeapp.generated.resources.fromStationLabel
import mrtbuddy.composeapp.generated.resources.journeyInformationDescription
import mrtbuddy.composeapp.generated.resources.journeyInformationLabel
import mrtbuddy.composeapp.generated.resources.rescan
import mrtbuddy.composeapp.generated.resources.roundTrips
import mrtbuddy.composeapp.generated.resources.route
import mrtbuddy.composeapp.generated.resources.selectDestination
import mrtbuddy.composeapp.generated.resources.selectOrigin
import mrtbuddy.composeapp.generated.resources.selectRouteDescription
import mrtbuddy.composeapp.generated.resources.selectRouteText
import mrtbuddy.composeapp.generated.resources.singleTicket
import mrtbuddy.composeapp.generated.resources.tapToCheckSufficientBalance
import mrtbuddy.composeapp.generated.resources.toStationLabel
import mrtbuddy.composeapp.generated.resources.travelTips1
import mrtbuddy.composeapp.generated.resources.travelTips2
import mrtbuddy.composeapp.generated.resources.travelTips3
import mrtbuddy.composeapp.generated.resources.travelTipsDescription
import mrtbuddy.composeapp.generated.resources.travelTipsLabel
import mrtbuddy.composeapp.generated.resources.two_way_arrows
import mrtbuddy.composeapp.generated.resources.withMRT
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelectionSection(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Header row
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Route, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(Res.string.selectRouteText), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = stringResource(Res.string.selectRouteDescription), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }

                // Quick rescan for non-Android platforms
                if (getPlatform().name != "android") {
                    TextButton(onClick = { RescanManager.requestRescan() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(Res.string.rescan), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Station chips row with swap
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StationChip(
                    label = stringResource(Res.string.fromStationLabel),
                    value = uiState.fromStation?.let { StationService.translate(it.name) } ?: stringResource(Res.string.selectOrigin),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    leading = Icons.Default.LocationOn,
                    onClick = { viewModel.onAction(FareCalculatorAction.ToggleFromExpanded) }
                )

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    val rotation by animateFloatAsState(targetValue = if (uiState.fromStation != null && uiState.toStation != null) 180f else 0f, animationSpec = spring())
                    Card(modifier = Modifier.size(48.dp).clickable(enabled = uiState.fromStation != null && uiState.toStation != null) { viewModel.onAction(FareCalculatorAction.SwapStations) },
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.rotate(rotation), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                StationChip(
                    label = stringResource(Res.string.toStationLabel),
                    value = uiState.toStation?.let { StationService.translate(it.name) } ?: stringResource(Res.string.selectDestination),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    leading = Icons.Default.LocationOn,
                    onClick = { viewModel.onAction(FareCalculatorAction.ToggleToExpanded) }
                )
            }

            // Dropdowns (kept visually hidden but available)
            StationDropdownField(
                label = stringResource(Res.string.fromStationLabel),
                value = uiState.fromStation?.let { StationService.translate(it.name) } ?: stringResource(Res.string.selectOrigin),
                expanded = uiState.fromExpanded,
                onExpandedChange = { viewModel.onAction(FareCalculatorAction.ToggleFromExpanded) },
                onDismiss = { viewModel.onAction(FareCalculatorAction.DismissDropdowns) },
                stations = viewModel.stations,
                onStationSelected = { station -> viewModel.onAction(FareCalculatorAction.UpdateFromStation(station)) },
                leadingIcon = Icons.Default.LocationOn,
                iconTint = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f),
            )

            StationDropdownField(
                label = stringResource(Res.string.toStationLabel),
                value = uiState.toStation?.let { StationService.translate(it.name) } ?: stringResource(Res.string.selectDestination),
                expanded = uiState.toExpanded,
                onExpandedChange = { viewModel.onAction(FareCalculatorAction.ToggleToExpanded) },
                onDismiss = { viewModel.onAction(FareCalculatorAction.DismissDropdowns) },
                stations = viewModel.stations,
                onStationSelected = { station -> viewModel.onAction(FareCalculatorAction.UpdateToStation(station)) },
                leadingIcon = Icons.Default.LocationOn,
                iconTint = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.12f),
            )
        }
    }
}

@Composable
fun StationChip(label: String, value: String, color: Color, leading: ImageVector, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = color), modifier = Modifier.clickable { onClick() }) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = leading, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            Column {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDropdownField(
    label: String,
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    stations: List<net.adhikary.mrtbuddy.data.model.Station>,
    onStationSelected: (net.adhikary.mrtbuddy.data.model.Station) -> Unit,
    leadingIcon: ImageVector,
    iconTint: Color,
    containerColor: Color
) {
    // Compact, accessible dropdown using OutlinedTextField as trigger
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            leadingIcon = {
                Card(modifier = Modifier.size(32.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = containerColor)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(imageVector = leadingIcon, contentDescription = null, tint = iconTint)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().clickable { onExpandedChange(!expanded) },
            label = { Text(text = label) },
            textStyle = MaterialTheme.typography.bodyMedium
        )

        if (expanded) {
            // Simple dropdown list
            Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.elevatedCardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    stations.drop(1).forEach { station ->
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStationSelected(station); onDismiss() }
                            .padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = StationService.translate(station.name), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FareDisplayCard(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.elevatedCardElevation(8.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Top row: Service badge + Fare
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.DirectionsTransit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(Res.string.withMRT), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "৳ ${translateNumber(viewModel.state.value.discountedFare)}", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold))
                    Crossfade(targetState = viewModel.state.value.discountedFare != viewModel.state.value.calculatedFare) { showOriginal ->
                        if (showOriginal) {
                            Text(text = "৳ ${translateNumber(viewModel.state.value.calculatedFare)}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)))
                        }
                    }
                }
            }

            // Middle: balance / ticket info
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    when (val cs = uiState.cardState) {
                        is CardState.Balance -> {
                            val balance = cs.amount
                            val enough = balance >= uiState.discountedFare
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = if (enough) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(text = stringResource(Res.string.balanceAmount) + " ৳ ${translateNumber(balance)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (enough) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                    if (!enough) {
                                        Text(text = "Need ৳ ${translateNumber(uiState.discountedFare - balance)} more", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }

                            if (enough && uiState.discountedFare > 0) {
                                val roundTrips = balance / (uiState.discountedFare * 2).coerceAtLeast(1)
                                if (roundTrips > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(painter = painterResource(Res.drawable.two_way_arrows), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Text(text = "${translateNumber(roundTrips)} ${stringResource(Res.string.roundTrips)}", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }

                        is CardState.WaitingForTap, is CardState.Reading -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Rounded.Sell, contentDescription = null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                Text(text = "${stringResource(Res.string.singleTicket)} ৳ ${translateNumber(uiState.calculatedFare)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = stringResource(Res.string.tapToCheckSufficientBalance), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                        }

                        is CardState.Error -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = cs.message, color = MaterialTheme.colorScheme.error)
                            }
                        }

                        else -> {
                            Text(text = stringResource(Res.string.tapToCheckSufficientBalance), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                }
            }

            // Actions: quick CTA buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { /* route preview / directions */ }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(Res.string.route), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }

                TextButton(onClick = { /* open tips */ }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(Res.string.travelTipsLabel), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun TravelInfoCard(uiState: FareCalculatorState) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    }
                }
                Column {
                    Text(text = stringResource(Res.string.journeyInformationLabel), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = stringResource(Res.string.journeyInformationDescription), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
            }

            // Route summary
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(imageVector = Icons.Default.Route, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(Res.string.route), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Text(text = "${uiState.fromStation?.let { StationService.translate(it.name) } ?: "-"} → ${uiState.toStation?.let { StationService.translate(it.name) } ?: "-"}", style = MaterialTheme.typography.bodyLarge)
                    }
                    // small badge with estimated price range
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Single: ৳ ${translateNumber(uiState.calculatedFare)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "MRT/Rapid Pass: ৳ ${translateNumber(uiState.discountedFare)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }
            }

            // Price breakdown and savings
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f))) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(text = stringResource(Res.string.singleTicket), style = MaterialTheme.typography.labelLarge)
                            Text(text = "৳ ${translateNumber(uiState.calculatedFare)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = stringResource(Res.string.withMRT), style = MaterialTheme.typography.labelLarge)
                            Text(text = "৳ ${translateNumber(uiState.discountedFare)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    val saved = (uiState.calculatedFare - uiState.discountedFare).coerceAtLeast(0)
                    if (saved > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Rounded.Percent, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(text = "You save ৳ ${translateNumber(saved)} (${((saved * 100f) / (uiState.calculatedFare.takeIf { it > 0 } ?: 1)).toInt()}%) with MRT/Rapid Pass", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        Text(text = "No additional MRT discount available", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }

                    // subtle CTA
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { /* navigate to route details */ }) {
                            Text(text = stringResource(Res.string.route), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickTipsCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f))) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    }
                }
                Column {
                    Text(text = stringResource(Res.string.travelTipsLabel), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = stringResource(Res.string.travelTipsDescription), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
            }

            // tip items
            TipItem(icon = Icons.Default.AccountBalanceWallet, text = stringResource(Res.string.travelTips1), iconColor = MaterialTheme.colorScheme.primary)
            TipItem(icon = Icons.Default.AccessTime, text = stringResource(Res.string.travelTips2), iconColor = MaterialTheme.colorScheme.secondary)
            TipItem(icon = Icons.Default.Route, text = stringResource(Res.string.travelTips3), iconColor = MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
fun TipItem(icon: ImageVector, text: String, iconColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Card(modifier = Modifier.size(28.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = iconColor.copy(alpha = 0.12f))) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor)
            }
        }
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
    }
}

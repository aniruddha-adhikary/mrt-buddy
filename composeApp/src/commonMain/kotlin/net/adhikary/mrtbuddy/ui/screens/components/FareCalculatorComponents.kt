package net.adhikary.mrtbuddy.ui.screens.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
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
import androidx.compose.foundation.layout.Column

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelectionSection(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
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
                        Icon(imageVector = Icons.Default.Route, contentDescription = stringResource(Res.string.selectRouteText), tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(Res.string.selectRouteText), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = stringResource(Res.string.selectRouteDescription), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                }

                // Quick rescan for non-Android platforms
                if (getPlatform().name != "android") {
                    TextButton(onClick = { RescanManager.requestRescan() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = stringResource(Res.string.rescan), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(Res.string.rescan), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Station chips row with swap
            // Hoist combinedAnchor so we know which chip ("from" or "to") opened the dropdown; null = closed
            val combinedAnchor = remember { mutableStateOf<String?>(null) }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box {
                    StationChip(
                        label = stringResource(Res.string.fromStationLabel),
                        value = uiState.fromStation?.let { StationService.translate(it.name) } ?: stringResource(Res.string.selectOrigin),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        leading = Icons.Default.LocationOn,
                        onClick = { combinedAnchor.value = "from" }
                    )

                    // Anchor the dropdown to the from chip Box (only select origin)
                    if (combinedAnchor.value == "from") {
                        CombinedRouteDropdown(
                            uiState = uiState,
                            stations = viewModel.stations,
                            selecting = "from",
                            onDismiss = { combinedAnchor.value = null },
                            onApply = { selected ->
                                selected?.let { viewModel.onAction(FareCalculatorAction.UpdateFromStation(it)) }
                                combinedAnchor.value = null
                            }
                        )
                    }
                }

                // Swap button
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    val rotation by animateFloatAsState(targetValue = if (uiState.fromStation != null && uiState.toStation != null) 180f else 0f, animationSpec = spring())
                    Card(
                        modifier = Modifier.size(52.dp).clickable(enabled = uiState.fromStation != null && uiState.toStation != null) { viewModel.onAction(FareCalculatorAction.SwapStations) },
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.SwapVert, contentDescription = stringResource(Res.string.selectRouteText), modifier = Modifier.rotate(rotation), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Box {
                    StationChip(
                        label = stringResource(Res.string.toStationLabel),
                        value = uiState.toStation?.let { StationService.translate(it.name) } ?: stringResource(Res.string.selectDestination),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        leading = Icons.Default.LocationOn,
                        onClick = { combinedAnchor.value = "to" }
                    )

                    // Anchor the dropdown to the to chip Box (only select destination)
                    if (combinedAnchor.value == "to") {
                        CombinedRouteDropdown(
                            uiState = uiState,
                            stations = viewModel.stations,
                            selecting = "to",
                            onDismiss = { combinedAnchor.value = null },
                            onApply = { selected ->
                                selected?.let { viewModel.onAction(FareCalculatorAction.UpdateToStation(it)) }
                                combinedAnchor.value = null
                            }
                        )
                    }
                }
            }

            // Short simple steps for all ages
            GettingStartedSteps()
        }
    }
}

@Composable
fun GettingStartedSteps() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.elevatedCardElevation(2.dp)) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "How to use (simple)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "1) Tap 'From' → choose station", style = MaterialTheme.typography.bodyMedium)
                Text(text = "2) Tap 'To' → choose destination", style = MaterialTheme.typography.bodyMedium)
                Text(text = "3) Tap your card on the reader to check balance", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Quick help", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
fun StationChip(label: String, value: String, color: Color, leading: ImageVector, onClick: () ->  Unit) {
    // Ensure a comfortable touch target and animate size changes
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier
            .clickable(onClick = onClick, role = Role.Button, indication = null, interactionSource = remember { MutableInteractionSource() })
            .animateContentSize()
            .sizeIn(minWidth = 140.dp, minHeight = 56.dp)
            .padding(0.dp)
            .semantics { contentDescription = "$label: $value" }
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(imageVector = leading, contentDescription = label, tint = MaterialTheme.colorScheme.onSurface)
            Column {
                Text(text = label, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
                Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombinedRouteDropdown(
    uiState: FareCalculatorState,
    stations: List<net.adhikary.mrtbuddy.data.model.Station>,
    selecting: String, // "from" or "to"
    onDismiss: () -> Unit,
    onApply: (net.adhikary.mrtbuddy.data.model.Station?) -> Unit
) {
    // Use a Dialog (separate window) to avoid PopupLayout / intrinsic measurement interactions.
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.width(320.dp)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = if (selecting == "from") stringResource(Res.string.fromStationLabel) else stringResource(Res.string.toStationLabel), style = MaterialTheme.typography.labelLarge)

                if (stations.isEmpty()) {
                    Text(text = if (selecting == "from") stringResource(Res.string.selectOrigin) else stringResource(Res.string.selectDestination), modifier = Modifier.padding(8.dp))
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                        items(stations) { station ->
                            // Immediately apply when user chooses a station
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onApply(station) }
                                .padding(vertical = 12.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = StationService.translate(station.name), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                                // Highlight current selection based on uiState
                                val selected = if (selecting == "from") uiState.fromStation?.id == station.id else uiState.toStation?.id == station.id
                                if (selected) {
                                    Text(text = "✓", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }

                // Only a cancel button is needed now; selection auto-applies
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text(text = "Cancel") }
                }
            }
        }
    }
}

@Composable
fun FareDisplayCard(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel) {
    // Read vm state once to avoid repetitive access
    val vmState = viewModel.state.value

    Card(modifier = Modifier.fillMaxWidth().animateContentSize(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.elevatedCardElevation(8.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Top row: Service badge + Fare
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.DirectionsTransit, contentDescription = stringResource(Res.string.withMRT), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(Res.string.withMRT), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "৳ ${translateNumber(vmState.discountedFare)}", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold))
                    Crossfade(targetState = vmState.discountedFare != vmState.calculatedFare) { showOriginal ->
                        if (showOriginal) {
                            Text(text = "৳ ${translateNumber(vmState.calculatedFare)}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)))
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
                                Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = stringResource(Res.string.balanceAmount), tint = if (enough) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
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
                                        Icon(painter = painterResource(Res.drawable.two_way_arrows), contentDescription = stringResource(Res.string.roundTrips), tint = MaterialTheme.colorScheme.primary)
                                        Text(text = "${translateNumber(roundTrips)} ${stringResource(Res.string.roundTrips)}", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }

                        is CardState.WaitingForTap -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Rounded.Sell, contentDescription = stringResource(Res.string.singleTicket), modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                Text(text = "${stringResource(Res.string.singleTicket)} ৳ ${translateNumber(uiState.calculatedFare)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = stringResource(Res.string.tapToCheckSufficientBalance), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                        }

                        is CardState.Reading -> {
                            // Show an inline progress indicator so users know reading is in progress
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(6.dp))
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

            // Actions: clear and primary CTA buttons with clear affordance for all ages
            val routeSelected = uiState.fromStation != null && uiState.toStation != null
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                ElevatedButton(onClick = { /* route preview / directions */ }, modifier = Modifier.weight(1f), enabled = routeSelected) {
                    Text(text = stringResource(Res.string.route), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }

                OutlinedButton(onClick = { /* open tips */ }, modifier = Modifier.weight(1f), enabled = true) {
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
                        Icon(imageVector = Icons.Rounded.Info, contentDescription = stringResource(Res.string.journeyInformationLabel), tint = MaterialTheme.colorScheme.secondary)
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
                    Icon(imageVector = Icons.Default.Route, contentDescription = stringResource(Res.string.route), tint = MaterialTheme.colorScheme.primary)
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
                        Icon(imageVector = Icons.Default.AccessTime, contentDescription = stringResource(Res.string.travelTipsLabel), tint = MaterialTheme.colorScheme.tertiary)
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

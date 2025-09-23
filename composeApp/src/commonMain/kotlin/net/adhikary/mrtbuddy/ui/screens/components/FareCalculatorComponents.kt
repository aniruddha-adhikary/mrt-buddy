package net.adhikary.mrtbuddy.ui.screens.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
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
    // Use BoxWithConstraints so we can adapt layout to the available width
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isCompact = maxWidth < 420.dp

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
                .padding( if (isCompact) 12.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 12.dp)) {

                // Header
                ModernHeader(
                    title = stringResource(Res.string.selectRouteText),
                    description = stringResource(Res.string.selectRouteDescription),
                    icon = Icons.Default.Route,
                    isCompact = isCompact,
                    showRescan = getPlatform().name != "android",
                    onRescan = { RescanManager.requestRescan() }
                )

                // Station chips row with swap — show From/To stacked vertically on left and swap on the right
                val combinedAnchor = remember { mutableStateOf<String?>(null) }
                // Swap animation: use a counter so each click increments rotation by 180deg and retriggers animation
                val swapAnimCount = remember { mutableStateOf(0) }
                val swapRotation by animateFloatAsState(
                    // alternate between 0 and 180 degrees so rotation doesn't accumulate indefinitely
                    targetValue = (swapAnimCount.value % 2) * 180f,
                    animationSpec = tween(durationMillis = 420)
                )
                // pulse flag toggles true briefly after each rotation increment to animate a scale pulse
                val swapPulse = remember { mutableStateOf(false) }
                val swapScale by animateFloatAsState(
                    targetValue = if (swapPulse.value) 1.06f else 1f,
                    animationSpec = spring()
                )

                // When the counter changes, trigger a short pulse
                LaunchedEffect(swapAnimCount.value) {
                    if (swapAnimCount.value > 0) {
                        swapPulse.value = true
                        delay(180)
                        swapPulse.value = false
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    // Left column: From and To stacked vertically
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box {
                            StationChip(
                                label = stringResource(Res.string.fromStationLabel),
                                value = uiState.fromStation?.let { StationService.translate(it.name) } ?: stringResource(Res.string.selectOrigin),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                leading = Icons.Default.LocationOn,
                                onClick = { combinedAnchor.value = "from" }
                            )

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

                        Box {
                            StationChip(
                                label = stringResource(Res.string.toStationLabel),
                                value = uiState.toStation?.let { StationService.translate(it.name) } ?: stringResource(Res.string.selectDestination),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                leading = Icons.Default.LocationOn,
                                onClick = { combinedAnchor.value = "to" }
                            )

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

                    // Right side: swap control
                    Card(
                        modifier = Modifier
                            .size(52.dp)
                            .padding(start = 8.dp)
                            .scale(swapScale)
                            .clickable(enabled = uiState.fromStation != null && uiState.toStation != null) {
                                // increment rotation counter and request swap
                                swapAnimCount.value = swapAnimCount.value + 1
                                viewModel.onAction(FareCalculatorAction.SwapStations)
                            },
                         shape = CircleShape,
                         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                     ) {
                         Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                             Icon(
                                 imageVector = Icons.Default.SwapVert,
                                 contentDescription = stringResource(Res.string.selectRouteText),
                                 modifier = Modifier.rotate(swapRotation),
                                 tint = MaterialTheme.colorScheme.onSurfaceVariant
                             )
                         }
                     }
                }

                // Short simple steps for all ages
                GettingStartedSteps()
            }
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
fun StationChip(
    label: String,
    value: String,
    color: Color,
    leading: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier
            .sizeIn(minWidth = 140.dp, minHeight = 56.dp)
            .padding(0.dp)
            .semantics { contentDescription = "$label: $value" }
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = leading, contentDescription = label, tint = MaterialTheme.colorScheme.onSurface)
            Column {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
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
    Dialog(onDismissRequest = onDismiss) {
        BoxWithConstraints {
            val availWidth = maxWidth
            val availHeight = maxHeight
            val isCompactLocal = availWidth < 420.dp
            val listMaxHeightLocal = if (isCompactLocal) (availHeight * 0.6f) else 400.dp

            if (isCompactLocal) {
                // Full-screen modal for small widths
                Card(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = if (selecting == "from") stringResource(Res.string.fromStationLabel) else stringResource(Res.string.toStationLabel),
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1
                            )
                            OutlinedButton(onClick = onDismiss) { Text(text = "Close") }
                        }

                        if (stations.isEmpty()) {
                            Text(text = if (selecting == "from") stringResource(Res.string.selectOrigin) else stringResource(Res.string.selectDestination), modifier = Modifier.padding(8.dp))
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = listMaxHeightLocal)) {
                                items(stations) { station ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onApply(station) }
                                            .padding(vertical = 12.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = StationService.translate(station.name), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                                        val selected = if (selecting == "from") uiState.fromStation?.id == station.id else uiState.toStation?.id == station.id
                                        if (selected) Text(text = "✓", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                // Centered card for larger screens
                val targetWidth = (availWidth * 0.45f).coerceAtLeast(280.dp).coerceAtMost(560.dp)
                Card(modifier = Modifier.widthIn(max = targetWidth)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (selecting == "from") stringResource(Res.string.fromStationLabel) else stringResource(Res.string.toStationLabel),
                            style = MaterialTheme.typography.labelLarge
                        )

                        if (stations.isEmpty()) {
                            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                                Box(modifier = Modifier.padding(12.dp)) {
                                    Text(text = if (selecting == "from") stringResource(Res.string.selectOrigin) else stringResource(Res.string.selectDestination))
                                }
                            }
                        } else {
                            LazyColumn(modifier = Modifier.heightIn(max = listMaxHeightLocal)) {
                                items(stations) { station ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onApply(station) }
                                            .padding(vertical = 12.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = StationService.translate(station.name), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                                        val selected = if (selecting == "from") uiState.fromStation?.id == station.id else uiState.toStation?.id == station.id
                                        if (selected) Text(text = "✓", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(onClick = onDismiss) { Text(text = "Cancel") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FareDisplayCard(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel) {
    // make layout responsive
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isCompact = maxWidth < 420.dp
        val vmState = viewModel.state.value

        Card(modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.elevatedCardElevation(8.dp)) {
            if (isCompact) {
                // Stack content vertically for small screens
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.DirectionsTransit, contentDescription = stringResource(Res.string.withMRT), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = stringResource(Res.string.withMRT), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "৳ ${translateNumber(vmState.discountedFare)}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                            Crossfade(targetState = vmState.discountedFare != vmState.calculatedFare) { showOriginal ->
                                if (showOriginal) {
                                    Text(text = "৳ ${translateNumber(vmState.calculatedFare)}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)))
                                }
                            }
                        }
                    }

                    // balance / ticket info
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
                                }

                                is CardState.WaitingForTap -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Rounded.Sell, contentDescription = stringResource(Res.string.singleTicket), modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                        Text(text = "${stringResource(Res.string.singleTicket)} ৳ ${translateNumber(uiState.calculatedFare)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                        Text(text = stringResource(Res.string.tapToCheckSufficientBalance), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    }
                                }

                                is CardState.Reading -> {
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

                    // Actions stacked
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        ElevatedButton(onClick = { /* route preview / directions */ }, modifier = Modifier.weight(1f), enabled = uiState.fromStation != null && uiState.toStation != null) {
                            Text(text = stringResource(Res.string.route), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        }

                        OutlinedButton(onClick = { /* open tips */ }, modifier = Modifier.weight(1f), enabled = true) {
                            Text(text = stringResource(Res.string.travelTipsLabel), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                // Larger layouts use the original structure
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

                    // Actions
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
    }
}

@Composable
fun TravelInfoCard(uiState: FareCalculatorState) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isCompact = maxWidth < 420.dp

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
            if (isCompact) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Rounded.Info, contentDescription = stringResource(Res.string.journeyInformationLabel), tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        Column {
                            Text(text = stringResource(Res.string.journeyInformationLabel), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(text = stringResource(Res.string.journeyInformationDescription), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }

                    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(imageVector = Icons.Default.Route, contentDescription = stringResource(Res.string.route), tint = MaterialTheme.colorScheme.primary)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = stringResource(Res.string.route), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    Text(text = "${uiState.fromStation?.let { StationService.translate(it.name) } ?: "-"} → ${uiState.toStation?.let { StationService.translate(it.name) } ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }

                    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f))) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val saved = (uiState.calculatedFare - uiState.discountedFare).coerceAtLeast(0)
                            if (saved > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Rounded.Percent, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text(text = "You save ৳ ${translateNumber(saved)} (${((saved * 100f) / (uiState.calculatedFare.takeIf { it > 0 } ?: 1)).toInt()}%) with MRT/Rapid Pass", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                Text(text = "No additional MRT discount available", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            } else {
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
    }
}

@Composable
fun QuickTipsCard() {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isCompact = maxWidth < 420.dp

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
            if (isCompact) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f))) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.AccessTime, contentDescription = stringResource(Res.string.travelTipsLabel), tint = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                        Column {
                            Text(text = stringResource(Res.string.travelTipsLabel), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(text = stringResource(Res.string.travelTipsDescription), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }

                    TipItem(icon = Icons.Default.AccountBalanceWallet, text = stringResource(Res.string.travelTips1), iconColor = MaterialTheme.colorScheme.primary)
                    TipItem(icon = Icons.Default.AccessTime, text = stringResource(Res.string.travelTips2), iconColor = MaterialTheme.colorScheme.secondary)
                    TipItem(icon = Icons.Default.Route, text = stringResource(Res.string.travelTips3), iconColor = MaterialTheme.colorScheme.tertiary)
                }
            } else {
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

@Composable
fun ModernHeader(
    title: String,
    description: String,
    icon: ImageVector = Icons.Default.Route,
    isCompact: Boolean,
    modifier: Modifier = Modifier,
    showRescan: Boolean = false,
    onRescan: () -> Unit = {},
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(vertical = if (isCompact) 6.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val iconSize = if (isCompact) 40.dp else 44.dp
        Card(
            modifier = Modifier.size(iconSize),
            shape = RoundedCornerShape(if (isCompact) 10.dp else 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            val titleStyle = if (isCompact) MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            Text(text = title, style = titleStyle, maxLines = 1)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f), maxLines = 2)
        }

        // trailing content slot: prefer explicit content; fallback to showRescan
        when {
            trailingContent != null -> trailingContent()
            showRescan -> {
                TextButton(onClick = onRescan) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = stringResource(Res.string.rescan), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(Res.string.rescan), color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

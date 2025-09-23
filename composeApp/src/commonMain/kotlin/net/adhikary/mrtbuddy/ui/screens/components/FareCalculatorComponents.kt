package net.adhikary.mrtbuddy.ui.screens.components

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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SwapVert
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.drawBehind
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
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import mrtbuddy.composeapp.generated.resources.fromStationLabel
import mrtbuddy.composeapp.generated.resources.journeyInformationDescription
import mrtbuddy.composeapp.generated.resources.journeyInformationLabel
import mrtbuddy.composeapp.generated.resources.rescan
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

                // Header (ModernHeader, horizontal layout, transparent, no padding)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor =  MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Circular icon avatar on the left
                        Surface(
                            modifier = Modifier.size(if (isCompact) 52.dp else 64.dp),
                            shape = CircleShape,
                            color = Color.Transparent
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Route,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(if (isCompact) 22.dp else 28.dp)
                                )
                            }
                        }

                        // Title + description
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(Res.string.selectRouteText),
                                style = if (isCompact) MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(Res.string.selectRouteDescription),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f),
                                maxLines = 2
                            )
                        }

                        // rescan action if available
                        if (getPlatform().name != "android") {
                            TextButton(onClick = { RescanManager.requestRescan() }) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = stringResource(Res.string.rescan), tint = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = stringResource(Res.string.rescan), color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }

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
                                // Inline dropdown instead of Dialog
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 56.dp), // Position below chip
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.elevatedCardElevation(4.dp)
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                                        Text(text = stringResource(Res.string.fromStationLabel), style = MaterialTheme.typography.labelLarge)
                                        if (viewModel.stations.isEmpty()) {
                                            Text(text = stringResource(Res.string.selectOrigin), modifier = Modifier.padding(8.dp))
                                        } else {
                                            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp)) {
                                                items(viewModel.stations) { station ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable { viewModel.onAction(FareCalculatorAction.UpdateFromStation(station)); combinedAnchor.value = null }
                                                            .padding(vertical = 8.dp, horizontal = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(text = StationService.translate(station.name), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                                                        val selected = uiState.fromStation?.id == station.id
                                                        if (selected) Text(text = "✓", color = MaterialTheme.colorScheme.primary)
                                                    }
                                                }
                                            }
                                        }
                                    }
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

                            if (combinedAnchor.value == "to") {
                                // Inline dropdown instead of Dialog
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 56.dp), // Position below chip
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.elevatedCardElevation(4.dp)
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                                        Text(text = stringResource(Res.string.toStationLabel), style = MaterialTheme.typography.labelLarge)
                                        if (viewModel.stations.isEmpty()) {
                                            Text(text = stringResource(Res.string.selectDestination), modifier = Modifier.padding(8.dp))
                                        } else {
                                            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp)) {
                                                items(viewModel.stations) { station ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable { viewModel.onAction(FareCalculatorAction.UpdateToStation(station)); combinedAnchor.value = null }
                                                            .padding(vertical = 8.dp, horizontal = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(text = StationService.translate(station.name), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                                                        val selected = uiState.toStation?.id == station.id
                                                        if (selected) Text(text = "✓", color = MaterialTheme.colorScheme.primary)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
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

@Composable
fun FareDisplayCard(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val isCompact = maxWidth < 420.dp
        val vmState = viewModel.state.value

        Card(
            modifier = Modifier.fillMaxWidth().animateContentSize(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(8.dp)
        ) {
            when (val cs = uiState.cardState) {
                is CardState.Balance -> {
                    val fare = vmState.discountedFare
                    val original = vmState.calculatedFare
                    val discount = (original - fare).coerceAtLeast(0)

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(if (isCompact) 14.dp else 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = "Fare Invoice", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                if (uiState.fromStation != null || uiState.toStation != null) {
                                    Text(
                                        text = "${uiState.fromStation?.let { StationService.translate(it.name) } ?: "-"} → ${uiState.toStation?.let { StationService.translate(it.name) } ?: "-"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = if (isCompact) "Summary" else "Invoice", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        // Line items
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Base Fare", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "৳ ${translateNumber(original)}", style = MaterialTheme.typography.bodyMedium)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "MRT / Rapid Pass Discount", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "-৳ ${translateNumber(discount)}", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary))
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))

                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Total", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "৳ ${translateNumber(fare)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))
                            }
                        }

                        // Card balance summary
                        val balance = cs.amount
                        val enough = balance >= fare

                        // Professional balance panel: icon + balance + status chip
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "Current balance", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    Text(text = "৳ ${translateNumber(balance)}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = if (enough) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                }
                            }

                            // Status chip
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = if (enough) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = if (enough) "Sufficient" else "Insufficient",
                                    color = if (enough) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        // If not enough, show a short professional message and a Top up CTA
                        if (!enough) {
                            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Insufficient balance to cover this trip. Please top up your card to complete the journey.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

//                        // Actions
//                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//                            ElevatedButton(onClick = { /* route */ }, modifier = Modifier.weight(1f), enabled = uiState.fromStation != null && uiState.toStation != null) {
//                                Text(text = stringResource(Res.string.route), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
//                            }
//                            OutlinedButton(onClick = { /* tips */ }, modifier = Modifier.weight(1f)) {
//                                Text(text = stringResource(Res.string.travelTipsLabel), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
//                            }
//                        }
                        // --- end merged Travel Info section ---
                     }
                 }

                is CardState.Reading -> {
                    // show reading spinner and prompt
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                        Text(text = stringResource(Res.string.tapToCheckSufficientBalance), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                is CardState.Error -> {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = cs.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                else -> {
                    // Single-ticket preview
                    Column(modifier = Modifier.fillMaxWidth().padding(if (isCompact) 14.dp else 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Rounded.Sell, contentDescription = stringResource(Res.string.singleTicket), modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                        Text(text = "${stringResource(Res.string.singleTicket)} ৳ ${translateNumber(vmState.calculatedFare)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = stringResource(Res.string.tapToCheckSufficientBalance), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ElevatedButton(onClick = { /* route preview */ }, modifier = Modifier.weight(1f), enabled = uiState.fromStation != null && uiState.toStation != null) {
                                Text(text = stringResource(Res.string.route), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                            OutlinedButton(onClick = { /* tips */ }, modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(Res.string.travelTipsLabel), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
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

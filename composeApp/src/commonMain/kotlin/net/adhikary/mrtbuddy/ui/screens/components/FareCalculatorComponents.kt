package net.adhikary.mrtbuddy.ui.screens.components
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balanceAmount
import mrtbuddy.composeapp.generated.resources.chooseOrgDest
import mrtbuddy.composeapp.generated.resources.fromStationLabel
import mrtbuddy.composeapp.generated.resources.journeyInformationDescription
import mrtbuddy.composeapp.generated.resources.journeyInformationLabel
import mrtbuddy.composeapp.generated.resources.mrtCardDiscount
import mrtbuddy.composeapp.generated.resources.rescan
import mrtbuddy.composeapp.generated.resources.roundTrips
import mrtbuddy.composeapp.generated.resources.route
import mrtbuddy.composeapp.generated.resources.selectDestination
import mrtbuddy.composeapp.generated.resources.selectOrigin
import mrtbuddy.composeapp.generated.resources.selectRouteDescription
import mrtbuddy.composeapp.generated.resources.selectRouteText
import mrtbuddy.composeapp.generated.resources.selectStations
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
import mrtbuddy.composeapp.generated.resources.yourBalance
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorAction
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorState
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorViewModel
import net.adhikary.mrtbuddy.ui.theme.Transparent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun StationSelectionSection(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Enhanced Header with Progress Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth(), // Removed background modifier
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Card(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Route,
                                contentDescription = "Route",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = stringResource(Res.string.selectRouteText),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(Res.string.selectRouteDescription),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }


            // Enhanced From Station Dropdown
            StationDropdownField(
                label = stringResource(Res.string.fromStationLabel),
                value = uiState.fromStation?.let { StationService.translate(it.name) }
                    ?: stringResource(Res.string.selectOrigin),
                expanded = uiState.fromExpanded,
                onExpandedChange = { viewModel.onAction(FareCalculatorAction.ToggleFromExpanded) },
                onDismiss = { viewModel.onAction(FareCalculatorAction.DismissDropdowns) },
                stations = viewModel.stations,
                onStationSelected = { station ->
                    viewModel.onAction(
                        FareCalculatorAction.UpdateFromStation(
                            station
                        )
                    )
                },
                leadingIcon = Icons.Default.LocationOn,
                iconTint = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                isSelected = uiState.fromStation != null
            )

            // Enhanced Swap Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val rotationAngle by animateFloatAsState(
                    targetValue = if (uiState.fromStation != null && uiState.toStation != null) 180f else 0f,
                    animationSpec = spring()
                )

                Card(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(enabled = uiState.fromStation != null && uiState.toStation != null) {
                            // viewModel.onAction(FareCalculatorAction.SwapStations)
                        },
//                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.fromStation != null && uiState.toStation != null) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = if (uiState.fromStation != null && uiState.toStation != null) 4.dp else 2.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap stations",
                            tint = if (uiState.fromStation != null && uiState.toStation != null) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            },
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(rotationAngle)
                        )
                    }
                }
            }

            // Enhanced To Station Dropdown
            StationDropdownField(
                label = stringResource(Res.string.toStationLabel),
                value = uiState.toStation?.let { StationService.translate(it.name) }
                    ?: stringResource(Res.string.selectDestination),
                expanded = uiState.toExpanded,
                onExpandedChange = { viewModel.onAction(FareCalculatorAction.ToggleToExpanded) },
                onDismiss = { viewModel.onAction(FareCalculatorAction.DismissDropdowns) },
                stations = viewModel.stations,
                onStationSelected = { station ->
                    viewModel.onAction(
                        FareCalculatorAction.UpdateToStation(
                            station
                        )
                    )
                },
                leadingIcon = Icons.Default.LocationOn,
                iconTint = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f),
                isSelected = uiState.toStation != null
            )
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
    containerColor: Color,
    isSelected: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                leadingIcon = {
                    Card(
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = containerColor
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = leadingIcon,
                                contentDescription = label,
                                tint = iconTint,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = iconTint,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = if (isSelected) containerColor.copy(alpha = 0.5f) else Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismiss,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(0.dp)
                    )
                    .padding(vertical = 8.dp)
            ) {
                stations.drop(1).forEach { station ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = StationService.translate(station.name),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                androidx.compose.material3.HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            }
                        },
                        onClick = { onStationSelected(station) },
                        modifier = Modifier.padding(horizontal = 8.dp)
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
            .wrapContentSize(), // Increased height for better content layout
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        if (viewModel.state.value.fromStation == null || viewModel.state.value.toStation == null) {
            // Enhanced empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated icon container
                    Card(
                        modifier = Modifier
                            .size(96.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = Transparent
                                ),

                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsTransit,
                                contentDescription = "Select stations",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.background
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(Res.string.selectStations),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(Res.string.chooseOrgDest),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quick start hint
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        /*Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Tip",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = stringResource(Res.string.fareCalculateSuggestionText),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }*/
                    }
                }
            }
        } else {
            // Enhanced fare display with better visual hierarchy
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Transparent
                    )
            ) {
                // Rescan button (if not Android)
                if (getPlatform().name != "android") {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(20.dp)
                            .clickable { RescanManager.requestRescan() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Rescan",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = stringResource(Res.string.rescan),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp)
                        .background(
                            color = Transparent
                        ),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Enhanced header section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Transparent
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(if (getPlatform().name != "android") 48.dp else 20.dp))

                        // Service type badge
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsTransit,
                                    contentDescription = "MRT",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = stringResource(Res.string.withMRT),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Enhanced fare display
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "৳ ${translateNumber(viewModel.state.value.discountedFare)}",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

//                            if (viewModel.state.value.calculatedFare != viewModel.state.value.discountedFare) {
//                                Spacer(modifier = Modifier.height(4.dp))
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                                ) {
//                                    Text(
//                                        text = "৳ ${translateNumber(viewModel.state.value.calculatedFare)}",
//                                        style = MaterialTheme.typography.bodyLarge.copy(
//                                            fontWeight = FontWeight.Medium
//                                        ),
//                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                                    )
//                                    Card(
//                                        shape = RoundedCornerShape(8.dp),
//                                        colors = CardDefaults.cardColors(
//                                            containerColor = MaterialTheme.colorScheme.secondary.copy(
//                                                alpha = 0.12f
//                                            )
//                                        )
//                                    ) {
//                                       //MRT Discount
//                                    }
//                                }
//                            }
                        }
                    }

                    // Fare details section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(align = Alignment.Center),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    color = Transparent
                                )
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            when (uiState.cardState) {
                                is CardState.Balance -> {
                                    val balance = uiState.cardState.amount
                                    if (balance >= uiState.calculatedFare) {
                                        // Sufficient balance - show positive feedback
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    color = Transparent
                                                ),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Sufficient balance",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp).offset(y = (-2).dp)
                                            )
                                            Text(
                                                text = "  ${stringResource(Res.string.balanceAmount)} ৳ ${
                                                    translateNumber(
                                                        balance
                                                    )
                                                }",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        val roundTrips =
                                            if (uiState.calculatedFare > 0) balance / (uiState.discountedFare * 2) else 0
                                        if (roundTrips > 0) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Card(
                                                shape = RoundedCornerShape(16.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.primary.copy(
                                                        alpha = 0.1f
                                                    )
                                                )
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(16.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        painter = painterResource(Res.drawable.two_way_arrows),
                                                        contentDescription = "Round trips",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Text(
                                                        text = "${translateNumber(roundTrips)} ${
                                                            stringResource(
                                                                Res.string.roundTrips
                                                            )
                                                        }",
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            fontWeight = FontWeight.SemiBold
                                                        ),
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        // Insufficient balance - show warning
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "${stringResource(Res.string.yourBalance)} ৳ ${
                                                        translateNumber(
                                                            balance
                                                        )
                                                    }",
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    color = MaterialTheme.colorScheme.error,
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
//                                                    text = "Need ৳ ${translateNumber(uiState.calculatedFare - balance)} more",
                                                    text = "Need ৳ ${translateNumber(viewModel.state.value.discountedFare - balance)} more",

                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    color = MaterialTheme.colorScheme.error.copy(
                                                        alpha = 0.8f
                                                    ),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }

                                else -> {
                                    // No card detected - show single ticket info
                                    Column(
                                        modifier = Modifier
                                            .background(
                                                color = Transparent
                                            ),
                                        horizontalAlignment = Alignment.CenterHorizontally

                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .background(
                                                    color = Transparent
                                                ),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Sell,
                                                contentDescription = "Single ticket",
                                                tint = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.7f
                                                ),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = "${stringResource(Res.string.singleTicket)} ৳ ${
                                                    translateNumber(
                                                        uiState.calculatedFare
                                                    )
                                                }",
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = stringResource(Res.string.tapToCheckSufficientBalance),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
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
fun TravelInfoCard(uiState: FareCalculatorState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = "Journey Info",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = stringResource(Res.string.journeyInformationLabel),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(Res.string.journeyInformationDescription),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Journey details
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Route information
                InfoRow(
                    icon = Icons.Default.Route,
                    title = stringResource(Res.string.route),
                    value = "${uiState.fromStation?.let { StationService.translate(it.name) }} → ${
                        uiState.toStation?.let { StationService.translate(it.name) }
                    }",
                    iconColor = MaterialTheme.colorScheme.primary
                )

//                // Estimated time (placeholder - replace with actual calculation)
//                InfoRow(
//                    icon = Icons.Default.AccessTime,
//                    title = "Estimated Time",
////                    value = "15-20 minutes",
//                    value = "N/A",
//                    iconColor = MaterialTheme.colorScheme.secondary
//                )
//
//                // Distance (placeholder - replace with actual calculation)
//                InfoRow(
//                    icon = Icons.Default.Timeline,
//                    title = "Distance",
////                    value = "~12 km",
//                    value = "N/A",
//                    iconColor = MaterialTheme.colorScheme.tertiary
//                )

                // Savings with MRT card
                if (uiState.calculatedFare != uiState.discountedFare) {
                    InfoRow(
                        icon = Icons.Rounded.Percent,
                        title = stringResource(Res.string.mrtCardDiscount),
                        value = "৳ ${translateNumber(uiState.calculatedFare - uiState.discountedFare)}",
                        iconColor = MaterialTheme.colorScheme.primary,
                        highlight = true
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    title: String,
    value: String,
    iconColor: Color,
    highlight: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) {
                iconColor.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = iconColor.copy(alpha = 0.15f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (highlight) FontWeight.Bold else FontWeight.SemiBold
                    ),
                    color = if (highlight) iconColor else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun QuickTipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Tips",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = stringResource(Res.string.travelTipsLabel),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(Res.string.travelTipsDescription),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Tips
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TipItem(
                    icon = Icons.Default.AccountBalanceWallet,
                    text = stringResource(Res.string.travelTips1),
                    iconColor = MaterialTheme.colorScheme.primary
                )

                TipItem(
                    icon = Icons.Default.AccessTime,
                    text = stringResource(Res.string.travelTips2),
                    iconColor = MaterialTheme.colorScheme.secondary
                )

                TipItem(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    text = stringResource(Res.string.travelTips3),
                    iconColor = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun TipItem(
    icon: ImageVector,
    text: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.size(24.dp),
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = iconColor.copy(alpha = 0.15f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
    }
}

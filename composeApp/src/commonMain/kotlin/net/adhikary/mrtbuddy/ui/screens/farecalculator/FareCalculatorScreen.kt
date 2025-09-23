package net.adhikary.mrtbuddy.ui.screens.farecalculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.fareCalculatorDescription
import mrtbuddy.composeapp.generated.resources.fareCalculatorText
import mrtbuddy.composeapp.generated.resources.tapToCheckSufficientBalance
import mrtbuddy.composeapp.generated.resources.yourBalance
import mrtbuddy.composeapp.generated.resources.rescan
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.ui.screens.components.FareDisplayCard
import net.adhikary.mrtbuddy.ui.screens.components.StationSelectionSection
import net.adhikary.mrtbuddy.ui.screens.components.TravelInfoCard
import net.adhikary.mrtbuddy.ui.screens.components.QuickTipsCard
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun FareCalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: FareCalculatorViewModel = koinViewModel(),
    cardState: CardState
) {
    val uiState by viewModel.state.collectAsState()
    var headerVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    // Animation specs
    val headerAnimSpec = spring<androidx.compose.ui.unit.IntOffset>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    val contentAnimSpec = tween<androidx.compose.ui.unit.IntOffset>(360, delayMillis = 90)
    val infoAnimSpec = tween<androidx.compose.ui.unit.IntOffset>(360, delayMillis = 200)
    val tipsAnimSpec = tween<androidx.compose.ui.unit.IntOffset>(360, delayMillis = 280)
    val fadeInAnimSpec = tween<Float>(420, delayMillis = 120)
    val scaleInAnimSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

    // Entrance choreography
    LaunchedEffect(Unit) {
        headerVisible = true
        delay(150)
        contentVisible = true
    }

    // Keep the viewModel card state in sync
    LaunchedEffect(cardState) {
        viewModel.onAction(FareCalculatorAction.UpdateCardState(cardState))
    }

    // Initialization
    LaunchedEffect(Unit) {
        viewModel.onAction(FareCalculatorAction.OnInit)
    }

    // Handle events (reserved for future expansion)
    LaunchedEffect(viewModel.events) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is FareCalculatorEvent.Error -> {
                    // Could show a snackbar / dialog here — ViewModel exposes events
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Modern gradient header with compact status chip
            item(key = "modernHeader") {
                AnimatedVisibility(
                    visible = headerVisible,
                    enter = slideInVertically(initialOffsetY = { -it }, animationSpec = headerAnimSpec) + fadeIn()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
                    ) {
                        // gradient surface inside card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                        )
                                    )
                                ),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Card(
                                    modifier = Modifier.size(62.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Calculate,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(Res.string.fareCalculatorText),
                                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(Res.string.fareCalculatorDescription),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                    )
                                }

                                // compact card state indicator / rescan
                                Column(horizontalAlignment = Alignment.End) {
                                    when (val cs = uiState.cardState) {
                                        is CardState.Balance -> {
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.secondary,
                                                contentColor = MaterialTheme.colorScheme.onSecondary
                                            ) {
                                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(text = translateBalanceLabel(cs.amount), color = MaterialTheme.colorScheme.onSecondary)
                                                }
                                            }
                                        }
                                        is CardState.WaitingForTap -> {
                                            Badge(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                                                Text(text = stringResource(Res.string.tapToCheckSufficientBalance), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                            }
                                        }
                                        is CardState.Reading -> {
                                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                                Text(text = "Reading…", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = MaterialTheme.colorScheme.onPrimary)
                                            }
                                        }
                                        is CardState.Error -> {
                                            Badge(containerColor = MaterialTheme.colorScheme.error) {
                                                Text(text = cs.message, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = MaterialTheme.colorScheme.onError)
                                            }
                                        }
                                        else -> {
                                            Badge(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                                                Text(text = stringResource(Res.string.yourBalance), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Rescan button for non-Android platforms
                                    if (net.adhikary.mrtbuddy.getPlatform().name != "android") {
                                        androidx.compose.material3.TextButton(onClick = { net.adhikary.mrtbuddy.managers.RescanManager.requestRescan() }) {
                                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = stringResource(Res.string.rescan), color = MaterialTheme.colorScheme.onPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Station selection
            item(key = "stationSelection") {
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = slideInVertically(initialOffsetY = { it / 2 }, animationSpec = contentAnimSpec) + fadeIn(animationSpec = fadeInAnimSpec)
                ) {
                    StationSelectionSection(uiState, viewModel)
                }
            }

            // Fare display (prominent)
            item(key = "fareDisplay") {
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = scaleIn(initialScale = 0.92f, animationSpec = scaleInAnimSpec) + fadeIn(animationSpec = fadeInAnimSpec)
                ) {
                    FareDisplayCard(uiState, viewModel)
                }
            }

            // Travel info (only when route chosen)
            if (uiState.fromStation != null && uiState.toStation != null) {
                item(key = "travelInfo") {
                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = slideInVertically(initialOffsetY = { it / 3 }, animationSpec = infoAnimSpec) + fadeIn(animationSpec = fadeInAnimSpec)
                    ) {
                        TravelInfoCard(uiState)
                    }
                }
            }

            // Tips
            item(key = "tips") {
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tipsAnimSpec) + fadeIn(animationSpec = fadeInAnimSpec)
                ) {
                    QuickTipsCard()
                }
            }

            item(key = "bottomPad") {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// small helper used in header to show a compact balance label
@Composable
private fun translateBalanceLabel(amount: Int): String {
    return "৳ ${net.adhikary.mrtbuddy.translateNumber(amount)}"
}

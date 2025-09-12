package net.adhikary.mrtbuddy.ui.components
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Contactless
import androidx.compose.material.icons.rounded.WbIncandescent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.deviceCompatibilityWarningText
import mrtbuddy.composeapp.generated.resources.deviceCompatibilityWarningTitle
import mrtbuddy.composeapp.generated.resources.hold
import mrtbuddy.composeapp.generated.resources.keepCardSteady
import mrtbuddy.composeapp.generated.resources.latestBalance
import mrtbuddy.composeapp.generated.resources.lowBalance
import mrtbuddy.composeapp.generated.resources.nfcDisabledText
import mrtbuddy.composeapp.generated.resources.nfcEnableText
import mrtbuddy.composeapp.generated.resources.noNfcSupport
import mrtbuddy.composeapp.generated.resources.quickSetupGuideText
import mrtbuddy.composeapp.generated.resources.quickSetupGuideText1
import mrtbuddy.composeapp.generated.resources.quickSetupGuideText2
import mrtbuddy.composeapp.generated.resources.quickSetupGuideText3
import mrtbuddy.composeapp.generated.resources.readingCard
import mrtbuddy.composeapp.generated.resources.requiredNfc
import mrtbuddy.composeapp.generated.resources.rescan
import mrtbuddy.composeapp.generated.resources.scanFailed
import mrtbuddy.composeapp.generated.resources.tap
import mrtbuddy.composeapp.generated.resources.tapRescanToStart
import mrtbuddy.composeapp.generated.resources.tips
import mrtbuddy.composeapp.generated.resources.tipsText
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.theme.Alert_yellow_D
import net.adhikary.mrtbuddy.ui.theme.Alert_yellow_L
import net.adhikary.mrtbuddy.ui.theme.DarkMRTPass
import net.adhikary.mrtbuddy.ui.theme.DarkRapidPass
import net.adhikary.mrtbuddy.ui.theme.LightMRTPass
import net.adhikary.mrtbuddy.ui.theme.LightRapidPass
import net.adhikary.mrtbuddy.utils.isRapidPassIdm
import org.jetbrains.compose.resources.stringResource

@Composable
fun BalanceCard(
    cardState: CardState,
    cardIdm: String? = null,
    cardName: String? = null,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(400),
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .scale(animatedScale)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp), // Custom shadow
        shape = RoundedCornerShape(28.dp), // More rounded corners
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(Modifier.fillMaxWidth().wrapContentHeight()) {
            // Enhanced gradient header for Balance state with improved design
            if (!cardName.isNullOrBlank() && cardState is CardState.Balance) {
                val isRapidPass = cardIdm?.let { isRapidPassIdm(it) } ?: false
                val isDarkTheme = isSystemInDarkTheme()
                val cardColor = if (isRapidPass) {
                    if (isDarkTheme) DarkRapidPass else LightRapidPass
                } else {
                    if (isDarkTheme) DarkMRTPass else LightMRTPass
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize() // Increased header height
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    cardColor,
                                    cardColor.copy(alpha = 0.9f),
                                    cardColor.copy(alpha = 0.7f)
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Enhanced icon with background
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = cardName,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = if (isRapidPass) "Rapid Pass Card" else "MRT Pass Card",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Enhanced card type indicator with better styling
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = if (isRapidPass) "RP" else "MRT",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Enhanced rescan button for non-Android platforms
            if (getPlatform().name != "android") {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp)
                ) {
                    OutlinedButton(
                        onClick = { RescanManager.requestRescan() },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.rescan),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Main content area with improved spacing
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        start = 28.dp,
                        end = 28.dp,
                        top = if (!cardName.isNullOrBlank() && cardState is CardState.Balance) 100.dp else 28.dp, // Add top padding when header is present
                        bottom = 28.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (cardName.isNullOrBlank() || cardState !is CardState.Balance) {
                    Arrangement.Center
                } else {
                    Arrangement.Top // Change from Bottom to Top to prevent overlap
                }
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(500)) + scaleIn(tween(500)) + slideInVertically(tween(500)),
                    exit = fadeOut(tween(300)) + scaleOut(tween(300)) + slideOutVertically(tween(300))
                ) {
                    when (cardState) {
                        is CardState.Balance -> ModernBalanceContent(
                            amount = cardState.amount,
                            hasHeader = !cardName.isNullOrBlank()
                        )

                        CardState.Reading -> ModernReadingContent()
                        CardState.WaitingForTap -> ModernWaitingContent()
                        is CardState.Error -> ModernErrorContent(message = cardState.message)
                        CardState.NoNfcSupport -> ModernNoNfcSupportContent()
                        CardState.NfcDisabled -> ModernNfcDisabledContent()
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernPulsingEffect(iconSize: Dp) {
    val infiniteTransition = rememberInfiniteTransition()

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val density = LocalDensity.current
    val radiusPx = with(density) { (iconSize.toPx() / 2) * pulseScale }
    val strokeWidth = with(density) { 3.dp.toPx() }
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = Modifier.size(iconSize * 2.5f)
    ) {
        // Outer pulse circle
        drawCircle(
            color = primaryColor.copy(alpha = pulseAlpha * 0.3f),
            radius = radiusPx,
            center = center
        )

        // Inner pulse ring
        drawCircle(
            color = primaryColor.copy(alpha = pulseAlpha),
            radius = radiusPx * 0.7f,
            center = center,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun ModernBalanceContent(
    amount: Int,
    hasHeader: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (!hasHeader) {
            Text(
                text = stringResource(Res.string.latestBalance),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Modern balance display with enhanced status indication
        val (balanceColor, balanceIcon, statusGradient) = when {
            amount <= 20 -> Triple(
                MaterialTheme.colorScheme.error,
                Icons.Default.Warning,
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                    )
                )
            )

            amount <= 70 -> Triple(
                if (isSystemInDarkTheme()) Alert_yellow_D else Alert_yellow_L,
                Icons.Default.Info,
                Brush.radialGradient(
                    colors = listOf(
                        (if (isSystemInDarkTheme()) Alert_yellow_D else Alert_yellow_L).copy(alpha = 0.3f),
                        (if (isSystemInDarkTheme()) Alert_yellow_D else Alert_yellow_L).copy(alpha = 0.1f)
                    )
                )
            )

            else -> Triple(
                MaterialTheme.colorScheme.primary,
                Icons.Default.CheckCircle,
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                    )
                )
            )
        }

        // Enhanced balance container with gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(statusGradient)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = balanceIcon,
                    contentDescription = null,
                    tint = balanceColor,
                    modifier = Modifier.size(36.dp)
                )

                Text(
                    text = "৳ ${translateNumber(amount)}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = balanceColor
                )
            }
        }

        // Enhanced status message with modern styling
        when {
            amount <= 20 -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(Res.string.lowBalance),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            amount <= 70 -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = (if (isSystemInDarkTheme()) Alert_yellow_D else Alert_yellow_L)
                            .copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    /*Text(
                        text = "💡 ${stringResource(Res.string.normalLowBalanceText)}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (isSystemInDarkTheme()) Alert_yellow_D else Alert_yellow_L,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )*/
                }
            }

            else -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    /*Text(
                        text = "✅ ${stringResource(Res.string.normalBalanceText)}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )*/
                }
            }
        }
    }
}

@Composable
private fun ModernReadingContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Modern reading animation with spinning effect
        Box(contentAlignment = Alignment.Center) {
            val infiniteTransition = rememberInfiniteTransition()
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, easing = LinearEasing)
                )
            )
            val primaryColor = MaterialTheme.colorScheme.primary

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Rotating outer ring
                Canvas(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(1f, 1f)
                ) {
                    drawArc(
                        color = primaryColor.copy(alpha = 0.8f),
                        startAngle = rotation,
                        sweepAngle = 120f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.Contactless,
                    contentDescription = "Reading",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.readingCard),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(Res.string.keepCardSteady),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }

        // Progress indicator
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "⚡ Scanning in progress...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ModernWaitingContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (getPlatform().name == "android") {
                ModernPulsingEffect(iconSize = 80.dp)
            }

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Contactless,
                    contentDescription = "Tap Card",
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.tap),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = if (getPlatform().name != "android") {
                    stringResource(Res.string.tapRescanToStart)
                } else {
                    stringResource(Res.string.hold)
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = MaterialTheme.typography.titleMedium.lineHeight
            )
        }
        // Modern instruction card with enhanced design

        Card(
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WbIncandescent,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(Res.string.tips),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = stringResource(Res.string.tipsText),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }

    }
}

@Composable
private fun ModernErrorContent(message: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.scanFailed),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

        if (getPlatform().name != "android") {
            FilledTonalButton(
                onClick = { RescanManager.requestRescan() },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(Res.string.rescan),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun ModernNoNfcSupportContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.NearMe,
                contentDescription = "No NFC",
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📱 ${stringResource(Res.string.noNfcSupport)}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.requiredNfc),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

        // Device compatibility information
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.deviceCompatibilityWarningTitle),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(Res.string.deviceCompatibilityWarningText),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ModernNfcDisabledContent() {
    Column(
        modifier = Modifier
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "NFC Disabled",
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.nfcDisabledText),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                ),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(Res.string.nfcEnableText),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = MaterialTheme.typography.titleMedium.lineHeight
            )
        }

//        // Platform-specific NFC enable functionality
//        if (getPlatform().name == "android") {
//            Button(
//                onClick = {
//                    // For production, you'd want to open NFC settings
//                    // This is a placeholder for the actual Android implementation
//                },
//                modifier = Modifier.fillMaxWidth(0.8f),
//                shape = RoundedCornerShape(16.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Settings,
//                    contentDescription = null,
//                    modifier = Modifier.size(20.dp)
//                )
//                Spacer(modifier = Modifier.width(10.dp))
//                Text(
//                    text = stringResource(Res.string.enableNfc),
//                    style = MaterialTheme.typography.titleMedium.copy(
//                        fontWeight = FontWeight.Bold
//                    )
//                )
//            }
//        }

        // Enhanced help card with step-by-step guide
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement
                    .spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "💡 ${stringResource(Res.string.quickSetupGuideText)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.quickSetupGuideText1),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = stringResource(Res.string.quickSetupGuideText2),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp
                        ), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = stringResource(Res.string.quickSetupGuideText3),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp
                        ), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

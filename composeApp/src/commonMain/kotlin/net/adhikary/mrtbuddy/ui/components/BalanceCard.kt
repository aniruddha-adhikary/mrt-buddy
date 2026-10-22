package net.adhikary.mrtbuddy.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.card
import mrtbuddy.composeapp.generated.resources.hold
import mrtbuddy.composeapp.generated.resources.keepCardSteady
import mrtbuddy.composeapp.generated.resources.latestBalance
import mrtbuddy.composeapp.generated.resources.lowBalance
import mrtbuddy.composeapp.generated.resources.noNfcSupport
import mrtbuddy.composeapp.generated.resources.readingCard
import mrtbuddy.composeapp.generated.resources.requiredNfc
import mrtbuddy.composeapp.generated.resources.rescan
import mrtbuddy.composeapp.generated.resources.tap
import mrtbuddy.composeapp.generated.resources.tapRescanToStart
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.utils.isRapidPassIdm
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val CardHeight = 212.dp

@Composable
fun BalanceCard(
    cardState: CardState,
    cardIdm: String? = null,
    cardName: String? = null,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = cardState,
        transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
        contentKey = { it::class },
        modifier = modifier.fillMaxWidth(),
    ) { state ->
        when (state) {
            is CardState.Balance ->
                TransitCard(
                    amount = state.amount,
                    cardIdm = cardIdm,
                    cardName = cardName,
                )
            else -> StatusCard(state)
        }
    }
}

/** The balance, drawn to look like the physical MRT / Rapid Pass card. */
@Composable
private fun TransitCard(
    amount: Int,
    cardIdm: String?,
    cardName: String?,
) {
    val isRapidPass = cardIdm?.let { isRapidPassIdm(it) } ?: false
    val onCard = Color.White

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(CardHeight)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(cardBrush(isRapidPass)),
    ) {
        // Decorative circles for a bit of depth
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.height * 0.75f,
                center = center.copy(x = size.width * 0.95f, y = size.height * 0.1f),
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = size.height * 0.5f,
                center = center.copy(x = size.width * 0.8f, y = size.height * 1.05f),
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isRapidPass) "Rapid Pass" else "MRT Pass",
                        style = MaterialTheme.typography.labelLarge,
                        color = onCard.copy(alpha = 0.8f),
                    )
                    if (!cardName.isNullOrBlank()) {
                        Text(
                            text = cardName,
                            style = MaterialTheme.typography.titleMedium,
                            color = onCard,
                            maxLines = 1,
                        )
                    }
                }
                if (getPlatform().name != "android") {
                    IconButton(onClick = { RescanManager.requestRescan() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(Res.string.rescan),
                            tint = onCard,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(Res.string.latestBalance),
                style = MaterialTheme.typography.bodyMedium,
                color = onCard.copy(alpha = 0.8f),
            )
            Text(
                text = "৳ ${translateNumber(amount)}",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color =
                    when {
                        amount <= 50 -> Color(0xFFFFB4AB)
                        amount <= 70 -> Color(0xFFFFE082)
                        else -> onCard
                    },
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = cardIdm?.let { "•••• ${it.replace(" ", "").takeLast(4)}" } ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onCard.copy(alpha = 0.8f),
                    modifier = Modifier.weight(1f),
                )
                if (amount <= 20) {
                    LowBalancePill()
                }
            }
        }
    }
}

@Composable
private fun LowBalancePill() {
    Row(
        modifier =
            Modifier
                .padding(start = 12.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.18f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color(0xFFFFE082),
            modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stringResource(Res.string.lowBalance),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            maxLines = 2,
            modifier = Modifier.width(150.dp),
        )
    }
}

/** Any non-balance state: waiting, reading, errors. */
@Composable
private fun StatusCard(cardState: CardState) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(CardHeight),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (cardState) {
                CardState.Reading -> ReadingContent()
                CardState.WaitingForTap -> WaitingContent()
                is CardState.Error -> ErrorContent(message = cardState.message)
                CardState.NoNfcSupport -> NoNfcSupportContent()
                CardState.NfcDisabled -> NfcDisabledContent()
                is CardState.Balance -> Unit
            }
        }
    }
}

@Composable
private fun PulsingCircle(iconSize: Dp) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
    )
    val density = LocalDensity.current
    val initialRadiusPx = with(density) { iconSize.toPx() / 2 }
    val targetRadiusPx = initialRadiusPx * 1.8f

    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = initialRadiusPx,
        targetValue = targetRadiusPx,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
    )

    // Retrieve the color outside the Canvas lambda
    val circleColor = MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)

    Canvas(
        modifier = Modifier.size(iconSize * 1.8f),
    ) {
        drawCircle(
            color = circleColor,
            radius = pulseRadius,
            center = center,
        )
    }
}

@Composable
internal fun StatusTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
internal fun StatusSubtitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 6.dp),
    )
}

@Composable
private fun RescanButton() {
    FilledTonalButton(
        onClick = { RescanManager.requestRescan() },
        modifier = Modifier.padding(top = 16.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(Res.string.rescan))
    }
}

@Composable
private fun ReadingContent() {
    CircularProgressIndicator(
        modifier = Modifier.size(48.dp),
        strokeWidth = 4.dp,
    )
    Spacer(modifier = Modifier.height(16.dp))
    StatusTitle(stringResource(Res.string.readingCard))
    StatusSubtitle(stringResource(Res.string.keepCardSteady))
}

@Composable
private fun WaitingContent() {
    val isAndroid = getPlatform().name == "android"
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp),
    ) {
        if (isAndroid) {
            PulsingCircle(iconSize = 56.dp)
        }
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.card),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    StatusTitle(stringResource(Res.string.tap))
    if (isAndroid) {
        StatusSubtitle(stringResource(Res.string.hold))
    } else {
        StatusSubtitle(stringResource(Res.string.tapRescanToStart))
        RescanButton()
    }
}

@Composable
private fun ErrorContent(message: String) {
    IconBadge(
        imageVector = Icons.Default.Warning,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        size = 56.dp,
    )
    Spacer(modifier = Modifier.height(12.dp))
    StatusTitle("Error")
    StatusSubtitle(message)
    if (getPlatform().name != "android") {
        RescanButton()
    }
}

@Composable
private fun NoNfcSupportContent() {
    IconBadge(
        imageVector = Icons.Default.Info,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        size = 56.dp,
    )
    Spacer(modifier = Modifier.height(12.dp))
    StatusTitle(stringResource(Res.string.noNfcSupport))
    StatusSubtitle(stringResource(Res.string.requiredNfc))
}

@Composable
internal expect fun NfcDisabledContent()

package net.adhikary.mrtbuddy.ui.components

import androidx.compose.material3.Button
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.card
import mrtbuddy.composeapp.generated.resources.enableNfc
import mrtbuddy.composeapp.generated.resources.hold
import mrtbuddy.composeapp.generated.resources.keepCardSteady
import mrtbuddy.composeapp.generated.resources.latestBalance
import mrtbuddy.composeapp.generated.resources.lowBalance
import mrtbuddy.composeapp.generated.resources.nfcDisabled
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
import net.adhikary.mrtbuddy.ui.theme.Alert_yellow_D
import net.adhikary.mrtbuddy.ui.theme.Alert_yellow_L
import net.adhikary.mrtbuddy.ui.theme.DarkMRTPass
import net.adhikary.mrtbuddy.ui.theme.DarkRapidPass
import net.adhikary.mrtbuddy.ui.theme.LightMRTPass
import net.adhikary.mrtbuddy.ui.theme.LightRapidPass
import net.adhikary.mrtbuddy.utils.isRapidPassIdm
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import android.nfc.NfcAdapter
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BalanceCard(
    cardState: CardState,
    cardIdm: String? = null,
    cardName: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(24.dp) // Increased corner radius
    ) {
        Box(Modifier.fillMaxSize()) {
            // Card name at the top with rounded background only in Balance state
            if (!cardName.isNullOrBlank() && cardState is CardState.Balance) {
                val isRapidPass = cardIdm?.let { isRapidPassIdm(it) } ?: false
                val isDarkTheme = isSystemInDarkTheme()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isRapidPass) {
                                if (isDarkTheme) DarkRapidPass else LightRapidPass
                            } else {
                                if (isDarkTheme) DarkMRTPass else LightMRTPass
                            }
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = cardName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            if (getPlatform().name != "android") {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(Res.string.rescan),
                        modifier = Modifier
                            .clickable { RescanManager.requestRescan() },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (!cardName.isNullOrBlank()) MaterialTheme.colorScheme.onPrimary
                               else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (cardState) {
                    is CardState.Balance -> BalanceContent(amount = cardState.amount, cardName = cardName)
                    CardState.Reading -> ReadingContent()
                    CardState.WaitingForTap -> WaitingContent()
                    is CardState.Error -> ErrorContent(message = cardState.message)
                    CardState.NoNfcSupport -> NoNfcSupportContent()
                    CardState.NfcDisabled -> NfcDisabledContent()
                }
            }
        }
    }
}

@Composable
private fun PulsingCircle(iconSize: Dp) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val density = LocalDensity.current
    val initialRadiusPx = with(density) { iconSize.toPx() / 2 }
    val targetRadiusPx = initialRadiusPx * 2

    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = initialRadiusPx,
        targetValue = targetRadiusPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Retrieve the color outside the Canvas lambda
    val circleColor = MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)

    Canvas(
        modifier = Modifier.size(iconSize * 2)
    ) {
        drawCircle(
            color = circleColor,
            radius = pulseRadius,
            center = center
        )
    }
}

@Composable
private fun BalanceContent(amount: Int, cardName: String? = null) {
    Text(
        text = stringResource(Res.string.latestBalance),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "৳ ${translateNumber(amount)}",
        style = MaterialTheme.typography.displaySmall.copy(
            fontWeight = FontWeight.SemiBold
        ),
        color = when {
            amount <= 50 -> MaterialTheme.colorScheme.error
            amount <= 70 -> if (isSystemInDarkTheme()) Alert_yellow_D else Alert_yellow_L
            else -> MaterialTheme.colorScheme.onSurface
        }
    )
    Spacer(modifier = Modifier.height(4.dp))
    if (amount <= 20) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.lowBalance),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReadingContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Reading",
            modifier = Modifier.height(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.readingCard),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.keepCardSteady),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun WaitingContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (getPlatform().name == "android") {
                PulsingCircle(iconSize = 48.dp)
            }
            Icon(
                painter = painterResource(Res.drawable.card),
                contentDescription = "Tap Card",
                modifier = Modifier.height(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.tap),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (getPlatform().name != "android") {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.tapRescanToStart),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        } else {
            Text(
                text = stringResource(Res.string.hold),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Error",
            modifier = Modifier.height(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Error",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        if (getPlatform().name != "android") {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.rescan),
                modifier = Modifier.clickable { RescanManager.requestRescan() },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun NoNfcSupportContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "No NFC",
            modifier = Modifier.height(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.noNfcSupport),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.requiredNfc),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun NfcDisabledContent() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "NFC Disabled",
            modifier = Modifier.height(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "NFC is disabled.",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please enable NFC to continue.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                if (intent.resolveActivity(context.packageManager) != null) {
                    Log.d("NFC", "Opening NFC settings.")
                    context.startActivity(intent)
                } else {
                    Log.d("NFC", "Opening wireless settings.")
                    context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(text = "Enable NFC")
        }
    }
}
@Composable
fun ParentComponent() {
    val context = LocalContext.current
    val nfcAdapter = NfcAdapter.getDefaultAdapter(context)

    if (nfcAdapter == null) {
        // Device doesn't support NFC
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "NFC Not Supported",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "NFC Not Supported",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "This device does not support NFC functionality.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        // Device supports NFC
        NfcDisabledContent()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNfcComponents() {
    MaterialTheme {
        ParentComponent()
    }
}
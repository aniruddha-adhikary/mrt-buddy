package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.model.CardState

@Composable
fun BalanceCard(
    cardState: CardState,
    onTapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp), // Increased corner radius
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Box(Modifier.fillMaxSize().padding(24.dp)) { // Increased padding
            if (getPlatform().name != "android") {
                Text(
                    "Rescan",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable { onTapClick() },
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.primary
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (cardState) {
                    is CardState.Balance -> BalanceContent(amount = cardState.amount)
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
private fun BalanceContent(amount: Int) {
    Text(
        text = "Latest Balance",
        style = MaterialTheme.typography.h6,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "৳ $amount",
        style = MaterialTheme.typography.h4.copy(
            fontWeight = FontWeight.SemiBold
        ),
        color = if (amount < 20) MaterialTheme.colors.error else MaterialTheme.colors.onSurface
    )
    if (amount < 20) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Balance too low for the next trip. Top up needed",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReadingContent() {
    Text(
        text = "Reading card...",
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.onSurface
    )
}

@Composable
private fun WaitingContent() {
    Text(
        text = "Tap your card behind your phone to read balance",
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colors.onSurface,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ErrorContent(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.error,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun NoNfcSupportContent() {
    Text(
        text = "This device doesn't support NFC",
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.error,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "NFC is required to read your MRT Pass",
        style = MaterialTheme.typography.h4,
        color = MaterialTheme.colors.error.copy(alpha = 0.7f),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun NfcDisabledContent() {
    Text(
        text = "NFC is turned off",
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.error,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Please enable NFC in your device settings",
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.colors.error.copy(alpha = 0.7f),
        textAlign = TextAlign.Center
    )
}

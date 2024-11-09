package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.card
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import org.jetbrains.compose.resources.painterResource

@Composable
fun BalanceCard(
    cardState: CardState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(24.dp), // Increased corner radius
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Box(Modifier.fillMaxSize().padding(24.dp)) { // Increased padding
            if (getPlatform().name != "android") {
                Text(
                    "Rescan",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable { RescanManager.requestRescan() },
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
        color = if (amount < 20) MaterialTheme.colors.onSurface else MaterialTheme.colors.onSurface
    )
    if (amount < 20) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Balance too low for the next trip. Top up needed",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface,
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
            tint = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Reading Card",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please keep your card steady",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun WaitingContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(Res.drawable.card),
            contentDescription = "Tap Card",
            modifier = Modifier.height(48.dp),
            tint = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap Your Card",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Hold your card behind your phone\nto read the balance",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
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
            tint = MaterialTheme.colors.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Error",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
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
            tint = MaterialTheme.colors.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "NFC Not Supported",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This device doesn't support NFC, which is required to read your MRT Pass",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun NfcDisabledContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "NFC Disabled",
            modifier = Modifier.height(48.dp),
            tint = MaterialTheme.colors.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "NFC Disabled",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please enable NFC in your device settings to read your MRT Pass",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
    }
}

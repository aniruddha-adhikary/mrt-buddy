package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.error_page
import mrtbuddy.composeapp.generated.resources.nonfc
import mrtbuddy.composeapp.generated.resources.nonfcanother
import mrtbuddy.composeapp.generated.resources.tapnfc
import mrtbuddy.composeapp.generated.resources.wireless
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import org.jetbrains.compose.resources.painterResource

@Composable
fun BalanceCard(
    cardState: CardState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (cardState) {
            is CardState.Balance -> BalanceContent(amount = cardState.amount)
            CardState.Reading -> ReadingContent()
            CardState.WaitingForTap -> WaitingContent()
            is CardState.Error -> ErrorContent(message = cardState.message)
            CardState.NoNfcSupport -> NoNfcSupportContent()
            CardState.NfcDisabled -> NfcDisabledContent()
        }

        if (getPlatform().name != "android") {
            Text(
                "Rescan",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable { RescanManager.requestRescan() }
                    .padding(top = 24.dp, end = 24.dp),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
private fun BalanceContent(
    modifier: Modifier = Modifier,
    amount: Int
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Latest Balance",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "৳ $amount",
                style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.SemiBold // Less bold for iOS
                ),
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
private fun ReadingContent() {
    ContentDesign(
        message = "Reading card...",
        image = painterResource(Res.drawable.wireless)
    )
}

@Composable
private fun WaitingContent() {
    ContentDesign(
        message = "Tap your card to read balance",
        image = painterResource(Res.drawable.tapnfc)
    )
}

@Composable
private fun ErrorContent(message: String) {
    ContentDesign(
        message = message,
        image = painterResource(Res.drawable.error_page)
    )
}

@Composable
private fun NoNfcSupportContent(
) {
    ContentDesign(
        message = "This device doesn't support NFC & it is required to read your MRT Pass",
        image = painterResource(Res.drawable.nonfcanother)
    )
}

@Composable
private fun NfcDisabledContent(
) {
    ContentDesign(
        message = "NFC is turned off, Please enable NFC in your device settings",
        image = painterResource(Res.drawable.nonfc),
    )
}

@Composable
private fun ContentDesign(
    message: String,
    image: Painter,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = image,
            contentDescription = null
        )

        Spacer(Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = message,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

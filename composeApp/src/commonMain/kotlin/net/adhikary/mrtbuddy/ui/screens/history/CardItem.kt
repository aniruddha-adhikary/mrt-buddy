package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toLocalDateTime
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.cardId
import mrtbuddy.composeapp.generated.resources.lastScan
import mrtbuddy.composeapp.generated.resources.unnamedCard
import mrtbuddy.composeapp.generated.resources.visibility
import mrtbuddy.composeapp.generated.resources.visibility_off
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.nfc.service.TimestampService
import net.adhikary.mrtbuddy.ui.theme.DarkRapidPass
import net.adhikary.mrtbuddy.ui.theme.LightRapidPass
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CardItem(
    card: CardEntity,
    onCardSelected: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardSelected() },
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Colored stripe at the top
            val isRapidPass = card.idm.startsWith("01 27")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        if (isRapidPass) {
                            if (MaterialTheme.colors.isLight) LightRapidPass else DarkRapidPass
                        } else {
                            MaterialTheme.colors.primary
                        }
                    ),
                contentAlignment = androidx.compose.ui.Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = card.name ?: stringResource(Res.string.unnamedCard),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Row {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Rename card",
                            tint = MaterialTheme.colors.onPrimary,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(24.dp)
                                .clickable { onRenameClick() }
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete card",
                            tint = MaterialTheme.colors.onPrimary,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(24.dp)
                                .clickable { onDeleteClick() }
                        )
                    }
                }
            }

            // Card details
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column {
                            var isIdVisible by remember { mutableStateOf(false) }
                            Row(
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if (isIdVisible) Res.drawable.visibility else Res.drawable.visibility_off
                                    ),
                                    contentDescription = if (isIdVisible) "Hide card ID" else "Show card ID",
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(20.dp)
                                        .clickable { isIdVisible = !isIdVisible },
                                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                )
                                Column {
                                    Text(
                                        modifier = Modifier.padding(bottom = 2.dp),
                                        text = stringResource(Res.string.cardId),
                                        style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = if (isIdVisible) card.idm else card.idm.replace(
                                            Regex(
                                                "."
                                            ), "*"
                                        ),
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                            }
                            Text(
                                modifier = Modifier.padding(top = 8.dp),
                                text = if (card.lastScanTime != null) {
                                    "${stringResource(Res.string.lastScan)}: ${
                                        TimestampService.formatDateTime(
                                            kotlinx.datetime.Instant.fromEpochMilliseconds(card.lastScanTime)
                                                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                                        )
                                    }"
                                } else {
                                    "${stringResource(Res.string.lastScan)}: Never"
                                },
                                style = MaterialTheme.typography.caption,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View transactions",
                            tint = if (isRapidPass) {
                                if (MaterialTheme.colors.isLight) LightRapidPass else DarkRapidPass
                            } else {
                                MaterialTheme.colors.primary
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

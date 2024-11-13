package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.ui.theme.DarkRapidPass
import net.adhikary.mrtbuddy.ui.theme.LightRapidPass

@Composable
fun CardItem(
    card: CardEntity,
    onCardSelected: () -> Unit,
    onRenameClick: () -> Unit
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
            val isRapidPass = !card.idm.startsWith("01 27")
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
                        text = card.name ?: "Unnamed Card",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Rename card",
                        tint = MaterialTheme.colors.onPrimary,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(24.dp)
                            .clickable { onRenameClick() }
                    )
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
                            Text(
                                modifier = Modifier.padding(bottom = 2.dp),
                                text = "Card ID",
                                style = MaterialTheme.typography.caption,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = card.idm,
                                style = MaterialTheme.typography.body1
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

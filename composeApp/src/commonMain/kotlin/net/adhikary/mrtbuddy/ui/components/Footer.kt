package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

@Composable
fun Footer(
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    return Text(
        text = "Built with ❤️ by Ani and friends",
        modifier = modifier
            .clickable {
                uriHandler.openUri("https://mrtbuddy.com/contributors.html")
            }
            .padding(8.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Footer(
    onUrlClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Built with ❤️ by Ani and friends",
        modifier = modifier
            .clickable { onUrlClicked("https://mrtbuddy.com/contributors.html") }
            .padding(8.dp),
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.colors.primary
    )
}

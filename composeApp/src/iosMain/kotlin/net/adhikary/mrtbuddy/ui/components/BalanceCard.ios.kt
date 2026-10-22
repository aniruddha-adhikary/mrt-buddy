package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.enableNfc
import mrtbuddy.composeapp.generated.resources.nfcDisabled
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun NfcDisabledContent() {
    IconBadge(
        imageVector = Icons.Default.Info,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        size = 56.dp,
    )
    Spacer(modifier = Modifier.height(12.dp))
    StatusTitle(stringResource(Res.string.nfcDisabled))
    StatusSubtitle(stringResource(Res.string.enableNfc))
}

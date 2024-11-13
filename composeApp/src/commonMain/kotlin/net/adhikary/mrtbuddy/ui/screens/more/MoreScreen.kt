import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.Switch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.adhikary.mrtbuddy.repository.SettingsRepository
import org.koin.compose.koinInject
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.aboutHeader
import mrtbuddy.composeapp.generated.resources.help
import mrtbuddy.composeapp.generated.resources.helpAndSupportButton
import mrtbuddy.composeapp.generated.resources.nonAffiliationDisclaimer
import mrtbuddy.composeapp.generated.resources.policy
import mrtbuddy.composeapp.generated.resources.privacyPolicy
import mrtbuddy.composeapp.generated.resources.readOnlyDisclaimer
import mrtbuddy.composeapp.generated.resources.settings
import mrtbuddy.composeapp.generated.resources.autoSaveCardDetails
import mrtbuddy.composeapp.generated.resources.autoSaveCardDetailsDescription
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MoreScreen(
    modifier: Modifier = Modifier,
    settingsRepository: SettingsRepository = koinInject()
) {
    val uriHandler = LocalUriHandler.current;

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .then(modifier),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            SectionHeader(text = stringResource(Res.string.settings))
            val autoSaveEnabled by settingsRepository.autoSaveEnabled.collectAsState()
            RoundedButton(
                text = stringResource(Res.string.autoSaveCardDetails),
                subtitle = stringResource(Res.string.autoSaveCardDetailsDescription),
                onClick = { },
                trailing = {
                    Switch(
                        checked = autoSaveEnabled,
                        onCheckedChange = { settingsRepository.setAutoSave(it) }
                    )
                }
            )

            SectionHeader(text = stringResource(Res.string.aboutHeader))
            RoundedButton(
                text = stringResource(Res.string.privacyPolicy),
                painter = painterResource(Res.drawable.policy),
                onClick = {
                    uriHandler.openUri("https://mrtbuddy.com/privacy-policy")
                }
            )
            RoundedButton(
                text = stringResource(Res.string.helpAndSupportButton),
                painter = painterResource(Res.drawable.help),
                onClick = {
                    uriHandler.openUri("https://mrtbuddy.com/support")
                }
            )
        }

        Column {
            Text(
                text = stringResource(Res.string.nonAffiliationDisclaimer),
                fontSize = 12.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = stringResource(Res.string.readOnlyDisclaimer),
                fontSize = 12.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "Copyright © 2024 Aniruddha Adhikary.",
                fontSize = 12.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RoundedButton(
    text: String,
    subtitle: String? = null,
    painter: Painter? = null,
    iconTint: Color = Color.Gray,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colors.onSurface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = if (subtitle == null) Alignment.CenterVertically else Alignment.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (painter != null) {
                    Icon(
                        painter = painter,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = text,
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onSurface
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = 14.sp,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            trailing?.invoke()
        }
    }
}

// Preview
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF4B0082) // Deep purple background color
        ) {
            MoreScreen()
        }
    }
}

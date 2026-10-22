import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.aboutHeader
import mrtbuddy.composeapp.generated.resources.autoSaveCardDetails
import mrtbuddy.composeapp.generated.resources.autoSaveCardDetailsDescription
import mrtbuddy.composeapp.generated.resources.card
import mrtbuddy.composeapp.generated.resources.contributors
import mrtbuddy.composeapp.generated.resources.dark_mode
import mrtbuddy.composeapp.generated.resources.dark_mode_config_dark
import mrtbuddy.composeapp.generated.resources.dark_mode_config_light
import mrtbuddy.composeapp.generated.resources.dark_mode_config_system_default
import mrtbuddy.composeapp.generated.resources.dark_mode_preference
import mrtbuddy.composeapp.generated.resources.help
import mrtbuddy.composeapp.generated.resources.helpAndSupportButton
import mrtbuddy.composeapp.generated.resources.language
import mrtbuddy.composeapp.generated.resources.license
import mrtbuddy.composeapp.generated.resources.more
import mrtbuddy.composeapp.generated.resources.nonAffiliationDisclaimer
import mrtbuddy.composeapp.generated.resources.openSourceLicenses
import mrtbuddy.composeapp.generated.resources.others
import mrtbuddy.composeapp.generated.resources.policy
import mrtbuddy.composeapp.generated.resources.privacyPolicy
import mrtbuddy.composeapp.generated.resources.readOnlyDisclaimer
import mrtbuddy.composeapp.generated.resources.settings
import mrtbuddy.composeapp.generated.resources.stationMap
import mrtbuddy.composeapp.generated.resources.station_map
import net.adhikary.mrtbuddy.Language
import net.adhikary.mrtbuddy.settings.model.DarkThemeConfig
import net.adhikary.mrtbuddy.ui.components.IconBadge
import net.adhikary.mrtbuddy.ui.components.ScreenHeader
import net.adhikary.mrtbuddy.ui.components.SectionTitle
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreenAction
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreenEvent
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreenViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoreScreen(
    onNavigateToStationMap: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoreScreenViewModel = koinViewModel(),
) {
    val uriHandler = LocalUriHandler.current
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onAction(MoreScreenAction.OnInit)
    }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is MoreScreenEvent.Error -> {
                    // Handle error event (e.g., show a Toast or Snackbar)
                }
                is MoreScreenEvent.NavigateTooStationMap -> {
                    onNavigateToStationMap()
                }
                is MoreScreenEvent.NavigateToLicenses -> {
                    onNavigateToLicenses()
                }
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .then(modifier)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        ScreenHeader(title = stringResource(Res.string.more))

        SectionTitle(text = stringResource(Res.string.settings))
        SettingsGroup {
            SettingsRow(
                text = stringResource(Res.string.autoSaveCardDetails),
                subtitle = stringResource(Res.string.autoSaveCardDetailsDescription),
                painter = painterResource(Res.drawable.card),
                onClick = { viewModel.onAction(MoreScreenAction.SetAutoSave(!uiState.autoSaveEnabled)) },
                trailing = {
                    Switch(
                        checked = uiState.autoSaveEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.onAction(MoreScreenAction.SetAutoSave(enabled))
                        },
                    )
                },
            )
            GroupDivider()
            SettingsRow(
                text = stringResource(Res.string.language),
                painter = painterResource(Res.drawable.language),
                onClick = {
                    if (uiState.currentLanguage == Language.English.isoFormat) {
                        viewModel.onAction(MoreScreenAction.SetLanguage(Language.Bangla.isoFormat))
                    } else {
                        viewModel.onAction(MoreScreenAction.SetLanguage(Language.English.isoFormat))
                    }
                },
                trailing = {
                    TrailingValue(
                        text = if (uiState.currentLanguage == Language.English.isoFormat) "English" else "বাংলা",
                    )
                },
            )
            GroupDivider()
            SettingsRow(
                text = stringResource(Res.string.dark_mode_preference),
                painter = painterResource(Res.drawable.dark_mode),
                onClick = {
                    val nextConfig =
                        when (uiState.darkThemeConfig) {
                            DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfig.DARK
                            DarkThemeConfig.DARK -> DarkThemeConfig.LIGHT
                            DarkThemeConfig.LIGHT -> DarkThemeConfig.FOLLOW_SYSTEM
                        }
                    viewModel.onAction(MoreScreenAction.SetDarkThemeConfig(nextConfig))
                },
                trailing = {
                    TrailingValue(
                        text =
                            when (uiState.darkThemeConfig) {
                                DarkThemeConfig.FOLLOW_SYSTEM -> stringResource(Res.string.dark_mode_config_system_default)
                                DarkThemeConfig.DARK -> stringResource(Res.string.dark_mode_config_dark)
                                DarkThemeConfig.LIGHT -> stringResource(Res.string.dark_mode_config_light)
                            },
                    )
                },
            )
        }

        SectionTitle(text = stringResource(Res.string.others), modifier = Modifier.padding(top = 8.dp))
        SettingsGroup {
            SettingsRow(
                text = stringResource(Res.string.stationMap),
                painter = painterResource(Res.drawable.station_map),
                onClick = { viewModel.onAction(MoreScreenAction.StationMap) },
                trailing = { Chevron() },
            )
        }

        SectionTitle(text = stringResource(Res.string.aboutHeader), modifier = Modifier.padding(top = 8.dp))
        SettingsGroup {
            SettingsRow(
                text = stringResource(Res.string.privacyPolicy),
                painter = painterResource(Res.drawable.policy),
                onClick = { uriHandler.openUri("https://mrtbuddy.com/privacy-policy") },
                trailing = { Chevron() },
            )
            GroupDivider()
            SettingsRow(
                text = stringResource(Res.string.helpAndSupportButton),
                painter = painterResource(Res.drawable.help),
                onClick = { uriHandler.openUri("https://mrtbuddy.com/support") },
                trailing = { Chevron() },
            )
            GroupDivider()
            SettingsRow(
                text = stringResource(Res.string.contributors),
                painter = painterResource(Res.drawable.contributors),
                onClick = { uriHandler.openUri("https://mrtbuddy.com/contributors.html") },
                trailing = { Chevron() },
            )
            GroupDivider()
            SettingsRow(
                text = stringResource(Res.string.openSourceLicenses),
                painter = painterResource(Res.drawable.license),
                onClick = { viewModel.onAction(MoreScreenAction.OpenLicenses) },
                trailing = { Chevron() },
            )
        }

        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp)) {
            FootnoteText(stringResource(Res.string.nonAffiliationDisclaimer))
            FootnoteText(stringResource(Res.string.readOnlyDisclaimer))
            FootnoteText("Copyright © 2025 Aniruddha Adhikary.")
        }
    }
}

@Composable
private fun FootnoteText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 6.dp),
    )
}

@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        content = content,
    )
}

@Composable
private fun GroupDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 68.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
    )
}

@Composable
private fun TrailingValue(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp),
    )
}

@Composable
private fun Chevron() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun SettingsRow(
    text: String,
    subtitle: String? = null,
    painter: Painter? = null,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (painter != null) {
            IconBadge(
                painter = painter,
                size = 36.dp,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        trailing?.invoke()
    }
}

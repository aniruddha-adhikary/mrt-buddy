import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.aboutHeader
import mrtbuddy.composeapp.generated.resources.autoSaveCardDetails
import mrtbuddy.composeapp.generated.resources.autoSaveCardDetailsDescription
import mrtbuddy.composeapp.generated.resources.contributors
import mrtbuddy.composeapp.generated.resources.dark_mode
import mrtbuddy.composeapp.generated.resources.dark_mode_config_dark
import mrtbuddy.composeapp.generated.resources.dark_mode_config_light
import mrtbuddy.composeapp.generated.resources.dark_mode_config_system_default
import mrtbuddy.composeapp.generated.resources.dark_mode_preference
import mrtbuddy.composeapp.generated.resources.developerOptions
import mrtbuddy.composeapp.generated.resources.help
import mrtbuddy.composeapp.generated.resources.helpAndSupportButton
import mrtbuddy.composeapp.generated.resources.language
import mrtbuddy.composeapp.generated.resources.license
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
import net.adhikary.mrtbuddy.isDebug
import net.adhikary.mrtbuddy.settings.model.DarkThemeConfig
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreenAction
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreenEvent
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreenState
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreenViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoreScreen(
    onNavigateToStationMap: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onNavigateToDeveloper: () -> Unit,
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
                .padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Column {
            SettingsSection(uiState = uiState, onAction = viewModel::onAction)
            OthersSection(onAction = viewModel::onAction)
            AboutSection(uriHandler = uriHandler, onAction = viewModel::onAction)

            if (isDebug) {
                RoundedButton(
                    text = stringResource(Res.string.developerOptions),
                    onClick = onNavigateToDeveloper,
                )
            }
        }

        DisclaimerFooter()
    }
}

@Composable
private fun SettingsSection(
    uiState: MoreScreenState,
    onAction: (MoreScreenAction) -> Unit,
) {
    SectionHeader(text = stringResource(Res.string.settings))
    RoundedButton(
        text = stringResource(Res.string.autoSaveCardDetails),
        subtitle = stringResource(Res.string.autoSaveCardDetailsDescription),
        onClick = { onAction(MoreScreenAction.SetAutoSave(!uiState.autoSaveEnabled)) },
        trailing = {
            Switch(
                checked = uiState.autoSaveEnabled,
                onCheckedChange = { onAction(MoreScreenAction.SetAutoSave(it)) },
            )
        },
    )
    LanguageRow(uiState = uiState, onAction = onAction)
    DarkModeRow(uiState = uiState, onAction = onAction)
}

@Composable
private fun LanguageRow(
    uiState: MoreScreenState,
    onAction: (MoreScreenAction) -> Unit,
) {
    val isEnglish = uiState.currentLanguage == Language.English.isoFormat
    RoundedButton(
        text = stringResource(Res.string.language),
        painter = painterResource(Res.drawable.language),
        onClick = {
            val next = if (isEnglish) Language.Bangla.isoFormat else Language.English.isoFormat
            onAction(MoreScreenAction.SetLanguage(next))
        },
        trailing = {
            Text(
                text = if (isEnglish) "English" else "বাংলা",
                modifier = Modifier.padding(end = 8.dp),
            )
        },
    )
}

@Composable
private fun DarkModeRow(
    uiState: MoreScreenState,
    onAction: (MoreScreenAction) -> Unit,
) {
    RoundedButton(
        text = stringResource(Res.string.dark_mode_preference),
        painter = painterResource(Res.drawable.dark_mode),
        onClick = {
            val next =
                when (uiState.darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfig.DARK
                    DarkThemeConfig.DARK -> DarkThemeConfig.LIGHT
                    DarkThemeConfig.LIGHT -> DarkThemeConfig.FOLLOW_SYSTEM
                }
            onAction(MoreScreenAction.SetDarkThemeConfig(next))
        },
        trailing = {
            Text(
                text =
                    when (uiState.darkThemeConfig) {
                        DarkThemeConfig.FOLLOW_SYSTEM -> stringResource(Res.string.dark_mode_config_system_default)
                        DarkThemeConfig.DARK -> stringResource(Res.string.dark_mode_config_dark)
                        DarkThemeConfig.LIGHT -> stringResource(Res.string.dark_mode_config_light)
                    },
                modifier = Modifier.padding(end = 8.dp),
            )
        },
    )
}

@Composable
private fun OthersSection(onAction: (MoreScreenAction) -> Unit) {
    SectionHeader(text = stringResource(Res.string.others))
    RoundedButton(
        text = stringResource(Res.string.stationMap),
        painter = painterResource(Res.drawable.station_map),
        onClick = { onAction(MoreScreenAction.StationMap) },
    )
}

@Composable
private fun AboutSection(
    uriHandler: UriHandler,
    onAction: (MoreScreenAction) -> Unit,
) {
    SectionHeader(text = stringResource(Res.string.aboutHeader))
    RoundedButton(
        text = stringResource(Res.string.privacyPolicy),
        painter = painterResource(Res.drawable.policy),
        onClick = { uriHandler.openUri("https://mrtbuddy.com/privacy-policy") },
    )
    RoundedButton(
        text = stringResource(Res.string.helpAndSupportButton),
        painter = painterResource(Res.drawable.help),
        onClick = { uriHandler.openUri("https://mrtbuddy.com/support") },
    )
    RoundedButton(
        text = stringResource(Res.string.contributors),
        painter = painterResource(Res.drawable.contributors),
        onClick = { uriHandler.openUri("https://mrtbuddy.com/contributors.html") },
    )
    RoundedButton(
        text = stringResource(Res.string.openSourceLicenses),
        painter = painterResource(Res.drawable.license),
        onClick = { onAction(MoreScreenAction.OpenLicenses) },
    )
}

@Composable
private fun DisclaimerFooter() {
    Column {
        Text(
            text = stringResource(Res.string.nonAffiliationDisclaimer),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(vertical = 8.dp),
        )

        Text(
            text = stringResource(Res.string.readOnlyDisclaimer),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(vertical = 8.dp),
        )

        Text(
            text = "Copyright © 2025 Aniruddha Adhikary.",
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp),
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun RoundedButton(
    text: String,
    subtitle: String? = null,
    painter: Painter? = null,
    iconTint: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        onClick = onClick,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                if (painter != null) {
                    Icon(
                        painter = painter,
                        contentDescription = null,
                        tint = iconTint,
                        modifier =
                            Modifier
                                .padding(end = 16.dp)
                                .size(24.dp),
                    )
                }
                Column {
                    Text(
                        text = text,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
            trailing?.invoke()
        }
    }
}

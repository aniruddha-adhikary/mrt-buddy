import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.aboutHeader
import mrtbuddy.composeapp.generated.resources.autoSaveCardDetails
import mrtbuddy.composeapp.generated.resources.autoSaveCardDetailsDescription
import mrtbuddy.composeapp.generated.resources.card
import mrtbuddy.composeapp.generated.resources.contributors
import mrtbuddy.composeapp.generated.resources.contributorsDescription
import mrtbuddy.composeapp.generated.resources.getHelpDescription
import mrtbuddy.composeapp.generated.resources.help
import mrtbuddy.composeapp.generated.resources.helpAndSupportButton
import mrtbuddy.composeapp.generated.resources.language
import mrtbuddy.composeapp.generated.resources.license
import mrtbuddy.composeapp.generated.resources.metroStationMapDescription
import mrtbuddy.composeapp.generated.resources.nonAffiliationDisclaimer
import mrtbuddy.composeapp.generated.resources.openSourceLicenses
import mrtbuddy.composeapp.generated.resources.openSourceLicensesDescription
import mrtbuddy.composeapp.generated.resources.others
import mrtbuddy.composeapp.generated.resources.policy
import mrtbuddy.composeapp.generated.resources.privacyPolicy
import mrtbuddy.composeapp.generated.resources.privacyPolicyDescription
import mrtbuddy.composeapp.generated.resources.readOnlyDisclaimer
import mrtbuddy.composeapp.generated.resources.selectLanguageDescription
import mrtbuddy.composeapp.generated.resources.settings
import mrtbuddy.composeapp.generated.resources.settingsAndMore
import mrtbuddy.composeapp.generated.resources.settingsAndMoreDescription
import mrtbuddy.composeapp.generated.resources.stationMap
import mrtbuddy.composeapp.generated.resources.station_map
import net.adhikary.mrtbuddy.Language
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreenAction
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreenEvent
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreenViewModel
import net.adhikary.mrtbuddy.ui.theme.Transparent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

// Modern redesigned MoreScreen with enhanced UX and fixed compilation issues
@Composable
fun MoreScreen(
    onNavigateToStationMap: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoreScreenViewModel = koinViewModel()
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

    // Modern LazyColumn implementation for better performance
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                )
            ),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Animated Header
        item {
            EnhancedHeaderSection()
        }

        // Settings Section
        item {
            AnimatedSettingsSection(
                autoSaveEnabled = uiState.autoSaveEnabled,
                currentLanguage = uiState.currentLanguage,
                onAutoSaveToggle = { enabled ->
                    viewModel.onAction(MoreScreenAction.SetAutoSave(enabled))
                },
                onLanguageToggle = {
                    if (uiState.currentLanguage == Language.English.isoFormat) {
                        viewModel.onAction(MoreScreenAction.SetLanguage(Language.Bangla.isoFormat))
                    } else {
                        viewModel.onAction(MoreScreenAction.SetLanguage(Language.English.isoFormat))
                    }
                }
            )
        }

        // Tools Section
        item {
            AnimatedToolsSection(
                onStationMapClick = { viewModel.onAction(MoreScreenAction.StationMap) }
            )
        }

        // Support Section
        item {
            AnimatedSupportSection(
                onPrivacyPolicyClick = { uriHandler.openUri("https://mrtbuddy.com/privacy-policy") },
                onHelpClick = { uriHandler.openUri("https://mrtbuddy.com/support") },
                onContributorsClick = { uriHandler.openUri("https://mrtbuddy.com/contributors.html") },
                onLicensesClick = { viewModel.onAction(MoreScreenAction.OpenLicenses) }
            )
        }

        // Enhanced Footer
        item {
            EnhancedFooterSection()
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EnhancedHeaderSection() {
    var isVisible by remember { mutableStateOf(false) }
    var iconScale by remember { mutableStateOf(0.3f) }
    var textAlpha by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        isVisible = true
        iconScale = 1f
        textAlpha = 1f
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "headerAlpha"
    )

    val scale by animateFloatAsState(
        targetValue = iconScale,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 200f
        ),
        label = "iconScale"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = textAlpha,
        animationSpec = tween(durationMillis = 1200, delayMillis = 300),
        label = "textAlpha"
    )

    // Modern glass-morphism style card
    Box(
        modifier = Modifier
            .wrapContentSize()
            .alpha(alpha)
    ) {
        // Background blur effect simulation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                            ),
                            radius = 600f, // reduced from 800f
                            center = androidx.compose.ui.geometry.Offset(0.3f, 0.2f)
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
            ) {
                // Floating decoration elements
                Box(
                    modifier = Modifier
                        .size(70.dp) // reduced from 100.dp
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .align(Alignment.TopEnd)
                        .padding(12.dp) // reduced from 20.dp
                )

                Box(
                    modifier = Modifier
                        .size(40.dp) // reduced from 60.dp
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .align(Alignment.BottomStart)
                        .padding(8.dp) // reduced from 15.dp
                )

                // Main content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp), // reduced from 20.dp
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp) // reduced from 24,40
                ) {
                    // Enhanced icon with multiple layers
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.scale(scale)
                    ) {
                        // Outer ring
                        Box(
                            modifier = Modifier
                                .size(60.dp) // reduced from 90.dp
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.2f),
                                            Color.White.copy(alpha = 0.05f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )

                        // Inner circle with icon
                        Box(
                            modifier = Modifier
                                .size(44.dp) // reduced from 70.dp
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.25f),
                                            Color.White.copy(alpha = 0.1f)
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp) // reduced from 40.dp
                            )
                        }
                    }

                    // Title and subtitle with staggered animation
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp), // reduced from 12.dp
                        modifier = Modifier.alpha(titleAlpha)
                    ) {
                        Text(
                            text = stringResource(Res.string.settingsAndMore),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 26.sp, // reduced from 32.sp
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = stringResource(Res.string.settingsAndMoreDescription),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp, // reduced from 16.sp
                            lineHeight = 18.sp // reduced from 24.sp
                        )


                    }
                }
            }
        }
    }
}

@Composable
private fun ModernFeatureChip(
    icon: String,
    label: String,
    description: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Icon container with glassmorphism effect
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
        }

        // Labels
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.95f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun AnimatedSettingsSection(
    autoSaveEnabled: Boolean,
    currentLanguage: String,
    onAutoSaveToggle: (Boolean) -> Unit,
    onLanguageToggle: () -> Unit
) {
    ModernSectionCard(
        title = stringResource(Res.string.settings),
        icon = null
    ) {
        ModernSettingItem(
            title = stringResource(Res.string.autoSaveCardDetails),
            subtitle = stringResource(Res.string.autoSaveCardDetailsDescription),
            icon = painterResource(Res.drawable.card), // Added card icon for auto-save feature
            trailing = {
                Switch(
                    checked = autoSaveEnabled,
                    onCheckedChange = onAutoSaveToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            },
            onClick = { onAutoSaveToggle(!autoSaveEnabled) }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        ModernSettingItem(
            title = stringResource(Res.string.language),
            subtitle = stringResource(Res.string.selectLanguageDescription),
            icon = painterResource(Res.drawable.language),
            trailing = {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = if (currentLanguage == Language.English.isoFormat) "English" else "বাংলা",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            onClick = onLanguageToggle
        )
    }
}

@Composable
private fun AnimatedToolsSection(
    onStationMapClick: () -> Unit
) {
    ModernSectionCard(
        title = stringResource(Res.string.others),
        icon = null
    ) {
        ModernSettingItem(
            title = stringResource(Res.string.stationMap),
            subtitle = stringResource(Res.string.metroStationMapDescription),
            icon = painterResource(Res.drawable.station_map),
            onClick = onStationMapClick,
            showArrow = true
        )
    }
}

@Composable
private fun AnimatedSupportSection(
    onPrivacyPolicyClick: () -> Unit,
    onHelpClick: () -> Unit,
    onContributorsClick: () -> Unit,
    onLicensesClick: () -> Unit
) {
    ModernSectionCard(
        title = stringResource(Res.string.aboutHeader),
        icon = null
    ) {
        ModernSettingItem(
            title = stringResource(Res.string.privacyPolicy),
            subtitle = stringResource(Res.string.privacyPolicyDescription),
            icon = painterResource(Res.drawable.policy),
            onClick = onPrivacyPolicyClick,
            showArrow = true
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        ModernSettingItem(
            title = stringResource(Res.string.helpAndSupportButton),
            subtitle = stringResource(Res.string.getHelpDescription),
            icon = painterResource(Res.drawable.help),
            onClick = onHelpClick,
            showArrow = true
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        ModernSettingItem(
            title = stringResource(Res.string.contributors),
            subtitle = stringResource(Res.string.contributorsDescription),
            icon = painterResource(Res.drawable.contributors),
            onClick = onContributorsClick,
            showArrow = true
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        ModernSettingItem(
            title = stringResource(Res.string.openSourceLicenses),
            subtitle = stringResource(Res.string.openSourceLicensesDescription),
            icon = painterResource(Res.drawable.license),
            onClick = onLicensesClick,
            showArrow = true
        )
    }
}

@Composable
private fun EnhancedFooterSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.nonAffiliationDisclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Text(
                text = stringResource(Res.string.readOnlyDisclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            Text(
                text = "Copyright © 2024 Aniruddha Adhikary.",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModernSectionCard(
    title: String,
    icon: Painter? = null,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            icon?.let {
                Icon(
                    painter = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernSettingItem(
    title: String,
    subtitle: String? = null,
    icon: Painter? = null,
    trailing: @Composable (() -> Unit)? = null,
    showArrow: Boolean = false,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "itemScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "itemBackground"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Enhanced Icon with gradient background
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else {
                // Placeholder for items without icons to maintain alignment
                Spacer(modifier = Modifier.width(44.dp))
            }

            // Enhanced Content with better typography
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }

            // Trailing content
            trailing?.invoke()

            // Enhanced Arrow with animation
            if (showArrow) {
                val arrowRotation by animateFloatAsState(
                    targetValue = if (isPressed) 5f else 0f,
                    animationSpec = tween(durationMillis = 200),
                    label = "arrowRotation"
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "›",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.rotate(arrowRotation)
                    )
                }
            }
        }
    }
}

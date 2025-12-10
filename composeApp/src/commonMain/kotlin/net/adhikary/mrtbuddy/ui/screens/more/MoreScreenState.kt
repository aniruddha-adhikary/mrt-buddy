package net.adhikary.mrtbuddy.ui.screens.more

import net.adhikary.mrtbuddy.settings.model.DarkThemeConfig

data class MoreScreenState(
    val isLoading: Boolean = false,
    val autoSaveEnabled: Boolean = false,
    val currentLanguage: String = "en",
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val error: String? = null
)

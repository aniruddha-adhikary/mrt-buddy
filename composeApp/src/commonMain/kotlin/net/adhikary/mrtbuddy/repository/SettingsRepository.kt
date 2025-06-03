package net.adhikary.mrtbuddy.repository

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.adhikary.mrtbuddy.Language
import net.adhikary.mrtbuddy.settings.model.DarkThemeConfig

class SettingsRepository(private val settings: Settings) {
    private val _autoSaveEnabled = MutableStateFlow(settings.getBoolean(AUTO_SAVE_KEY, true))
    val autoSaveEnabled: StateFlow<Boolean> = _autoSaveEnabled.asStateFlow()

    private val _currentLanguage =
        MutableStateFlow(settings.getString(LANGUAGE_KEY, Language.English.isoFormat))
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()


    private val _darkThemeConfig = MutableStateFlow(
        DarkThemeConfig.valueOf(
            settings.getString(
                DARK_THEME_CONFIG_KEY,
                DarkThemeConfig.FOLLOW_SYSTEM.name
            )
        )
    )
    val darkThemeConfig: StateFlow<DarkThemeConfig> = _darkThemeConfig.asStateFlow()

    fun setAutoSave(enabled: Boolean) {
        settings.putBoolean(AUTO_SAVE_KEY, enabled)
        _autoSaveEnabled.value = enabled
    }

    fun setLanguage(language: String) {
        settings.putString(LANGUAGE_KEY, language)
        _currentLanguage.value = language
    }


    fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        settings.putString(DARK_THEME_CONFIG_KEY, darkThemeConfig.name)
        _darkThemeConfig.value = darkThemeConfig
    }

    companion object {
        private const val AUTO_SAVE_KEY = "auto_save_enabled"
        private const val LANGUAGE_KEY = "app_language"
        private const val DARK_THEME_CONFIG_KEY = "dark_theme_config"
    }
}

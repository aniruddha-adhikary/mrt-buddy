package net.adhikary.mrtbuddy.repository

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.adhikary.mrtbuddy.Language
import net.adhikary.mrtbuddy.isDebug
import net.adhikary.mrtbuddy.nfc.NfcDumpRecorder
import net.adhikary.mrtbuddy.settings.model.DarkThemeConfig

class SettingsRepository(private val settings: Settings) {
    private val _autoSaveEnabled = MutableStateFlow(settings.getBoolean(AUTO_SAVE_KEY, true))
    val autoSaveEnabled: StateFlow<Boolean> = _autoSaveEnabled.asStateFlow()

    private val _nfcDumpCaptureEnabled = MutableStateFlow(settings.getBoolean(NFC_DUMP_CAPTURE_KEY, false))
    val nfcDumpCaptureEnabled: StateFlow<Boolean> = _nfcDumpCaptureEnabled.asStateFlow()

    init {
        syncRecorder(_nfcDumpCaptureEnabled.value, isDebug)
    }

    private val _currentLanguage =
        MutableStateFlow(settings.getString(LANGUAGE_KEY, Language.English.isoFormat))
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _darkThemeConfig =
        MutableStateFlow(
            settings.getString(
                DARK_THEME_CONFIG_KEY,
                DarkThemeConfig.FOLLOW_SYSTEM.name,
            ).let { stored ->
                DarkThemeConfig.entries.firstOrNull { it.name == stored }
                    ?: DarkThemeConfig.FOLLOW_SYSTEM
            },
        )
    val darkThemeConfig: StateFlow<DarkThemeConfig> = _darkThemeConfig.asStateFlow()

    fun setAutoSave(enabled: Boolean) {
        settings.putBoolean(AUTO_SAVE_KEY, enabled)
        _autoSaveEnabled.value = enabled
    }

    fun setNfcDumpCapture(enabled: Boolean) {
        settings.putBoolean(NFC_DUMP_CAPTURE_KEY, enabled)
        _nfcDumpCaptureEnabled.value = enabled
        syncRecorder(enabled, isDebug)
    }

    /**
     * Recorder capture is a debug-only diagnostic: it stays off in release builds regardless of
     * the persisted flag. Extracted so the debug guard can be exercised on the JVM with both
     * flag values ([isDebug] itself is a build-fixed val).
     */
    internal fun syncRecorder(
        flagValue: Boolean,
        debug: Boolean,
    ) {
        NfcDumpRecorder.enabled = debug && flagValue
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
        private const val NFC_DUMP_CAPTURE_KEY = "nfc_dump_capture"
        private const val LANGUAGE_KEY = "app_language"
        private const val DARK_THEME_CONFIG_KEY = "dark_theme_config"
    }
}

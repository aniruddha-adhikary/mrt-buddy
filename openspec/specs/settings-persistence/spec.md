# settings-persistence Specification

## Purpose
Manages user preferences and application settings using the Multiplatform Settings library. This capability provides reactive state flows for settings values, persists preferences across app sessions, and supports settings for auto-save, language, and dark theme configuration.

## Requirements

### Requirement: Settings Repository
The system SHALL provide a centralized repository for all user settings.

#### Scenario: Initialize settings repository
- **GIVEN** the app is launched
- **WHEN** SettingsRepository is initialized
- **THEN** it SHALL load all saved settings from platform storage
- **AND** expose settings as StateFlows for reactive UI updates

#### Scenario: Platform-agnostic storage
- **GIVEN** the app runs on Android or iOS
- **WHEN** settings are saved or loaded
- **THEN** the Multiplatform Settings library SHALL handle platform-specific storage
- **AND** settings SHALL be stored in SharedPreferences (Android) or NSUserDefaults (iOS)

### Requirement: Auto-Save Setting
The system SHALL allow users to enable or disable automatic card saving.

#### Scenario: Get auto-save state
- **WHEN** `autoSaveEnabled` StateFlow is observed
- **THEN** it SHALL emit the current auto-save preference
- **AND** default to `true` if not previously set

#### Scenario: Set auto-save enabled
- **GIVEN** the user toggles auto-save on
- **WHEN** `setAutoSave(true)` is called
- **THEN** the setting SHALL be persisted with key "auto_save_enabled"
- **AND** the StateFlow SHALL emit `true`

#### Scenario: Set auto-save disabled
- **GIVEN** the user toggles auto-save off
- **WHEN** `setAutoSave(false)` is called
- **THEN** the setting SHALL be persisted with key "auto_save_enabled"
- **AND** the StateFlow SHALL emit `false`

### Requirement: Language Setting
The system SHALL persist the user's language preference.

#### Scenario: Get current language
- **WHEN** `currentLanguage` StateFlow is observed
- **THEN** it SHALL emit the current language ISO code
- **AND** default to "en" (English) if not previously set

#### Scenario: Set language preference
- **GIVEN** the user selects a language
- **WHEN** `setLanguage(isoCode)` is called
- **THEN** the setting SHALL be persisted with key "app_language"
- **AND** the StateFlow SHALL emit the new language code

#### Scenario: Supported language codes
- **GIVEN** the language setting
- **THEN** valid values SHALL be "en" (English) or "bn" (Bengali)

### Requirement: Dark Theme Configuration
The system SHALL persist the user's theme preference.

#### Scenario: Get dark theme config
- **WHEN** `darkThemeConfig` StateFlow is observed
- **THEN** it SHALL emit the current DarkThemeConfig value
- **AND** default to `FOLLOW_SYSTEM` if not previously set

#### Scenario: Set dark theme config
- **GIVEN** the user selects a theme option
- **WHEN** `setDarkThemeConfig(config)` is called
- **THEN** the setting SHALL be persisted with key "dark_theme_config"
- **AND** the StateFlow SHALL emit the new config

#### Scenario: Theme config options
- **GIVEN** the dark theme setting
- **THEN** valid values SHALL be:
  - `FOLLOW_SYSTEM`: Match device theme
  - `LIGHT`: Always light theme
  - `DARK`: Always dark theme

### Requirement: Reactive State Updates
The system SHALL provide reactive updates when settings change.

#### Scenario: Immediate UI updates
- **GIVEN** a Composable observing a settings StateFlow
- **WHEN** the setting value changes
- **THEN** the StateFlow SHALL emit the new value immediately
- **AND** the observing Composable SHALL recompose

#### Scenario: StateFlow replay
- **GIVEN** a new observer subscribes to a settings StateFlow
- **WHEN** the subscription starts
- **THEN** the current value SHALL be immediately emitted (replay = 1 behavior via MutableStateFlow)

### Requirement: Settings Screen Integration
The system SHALL integrate with the More/Settings screen for user configuration.

#### Scenario: Display current settings
- **GIVEN** the user navigates to the More screen
- **WHEN** the screen loads
- **THEN** all current setting values SHALL be displayed
- **AND** toggles/selectors SHALL reflect current state

#### Scenario: Modify settings via UI
- **GIVEN** the settings screen
- **WHEN** the user changes a setting
- **THEN** the change SHALL be immediately persisted
- **AND** the UI SHALL update to reflect the new value

### Requirement: Settings Keys
The system SHALL use consistent keys for all settings.

#### Scenario: Settings key constants
- **GIVEN** the SettingsRepository
- **THEN** the following keys SHALL be used:
  - `auto_save_enabled`: Boolean for auto-save preference
  - `app_language`: String for language ISO code
  - `dark_theme_config`: String for theme enum name

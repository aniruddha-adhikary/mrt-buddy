# dark-theme Specification

## Purpose
Provides theme configuration and dark mode support for the MRT Buddy application. This capability allows users to choose between light theme, dark theme, or system-following theme, and applies appropriate Material 3 color schemes throughout the app.

## Requirements

### Requirement: Theme Configuration Options
The system SHALL provide three theme configuration options.

#### Scenario: Follow system theme
- **GIVEN** `DarkThemeConfig.FOLLOW_SYSTEM` is selected
- **WHEN** the theme is applied
- **THEN** the app SHALL use light theme if device is in light mode
- **AND** use dark theme if device is in dark mode

#### Scenario: Light theme forced
- **GIVEN** `DarkThemeConfig.LIGHT` is selected
- **WHEN** the theme is applied
- **THEN** the app SHALL always use light theme
- **AND** ignore device theme setting

#### Scenario: Dark theme forced
- **GIVEN** `DarkThemeConfig.DARK` is selected
- **WHEN** the theme is applied
- **THEN** the app SHALL always use dark theme
- **AND** ignore device theme setting

### Requirement: Material 3 Color Schemes
The system SHALL define separate color schemes for light and dark themes.

#### Scenario: Light color scheme
- **GIVEN** light theme is active
- **WHEN** Material colors are accessed
- **THEN** the light color palette SHALL be used
- **AND** provide appropriate contrast ratios

#### Scenario: Dark color scheme
- **GIVEN** dark theme is active
- **WHEN** Material colors are accessed
- **THEN** the dark color palette SHALL be used
- **AND** background colors SHALL be darker
- **AND** text colors SHALL be lighter

### Requirement: Theme-Aware Custom Colors
The system SHALL provide theme-aware custom colors for specific UI elements.

#### Scenario: Balance warning colors
- **GIVEN** a low balance amount
- **WHEN** displayed in light theme
- **THEN** `Alert_yellow_L` SHALL be used for warning (51-70 balance)
- **AND** error color SHALL be used for critical (≤50 balance)

#### Scenario: Balance warning colors dark
- **GIVEN** a low balance amount
- **WHEN** displayed in dark theme
- **THEN** `Alert_yellow_D` SHALL be used for warning
- **AND** error color SHALL be appropriate for dark theme

#### Scenario: Transaction amount colors
- **GIVEN** transaction amounts displayed
- **WHEN** in light theme
- **THEN** `LightPositiveGreen` SHALL be used for positive amounts
- **AND** `LightNegativeRed` SHALL be used for negative amounts

#### Scenario: Transaction amount colors dark
- **GIVEN** transaction amounts displayed
- **WHEN** in dark theme
- **THEN** `DarkPositiveGreen` SHALL be used for positive amounts
- **AND** `DarkNegativeRed` SHALL be used for negative amounts

### Requirement: Card Type Theme Colors
The system SHALL provide distinct colors for different card types.

#### Scenario: MRT Pass colors light
- **GIVEN** an MRT Pass card in light theme
- **WHEN** the card name banner is displayed
- **THEN** `LightMRTPass` color SHALL be used for background

#### Scenario: MRT Pass colors dark
- **GIVEN** an MRT Pass card in dark theme
- **WHEN** the card name banner is displayed
- **THEN** `DarkMRTPass` color SHALL be used for background

#### Scenario: Rapid Pass colors light
- **GIVEN** a Rapid Pass card in light theme
- **WHEN** the card name banner is displayed
- **THEN** `LightRapidPass` color SHALL be used for background

#### Scenario: Rapid Pass colors dark
- **GIVEN** a Rapid Pass card in dark theme
- **WHEN** the card name banner is displayed
- **THEN** `DarkRapidPass` color SHALL be used for background

### Requirement: Theme Persistence
The system SHALL persist the user's theme preference.

#### Scenario: Save theme preference
- **GIVEN** the user selects a theme option
- **WHEN** the selection is confirmed
- **THEN** the DarkThemeConfig SHALL be saved to settings
- **AND** the key "dark_theme_config" SHALL store the enum name

#### Scenario: Load theme preference
- **GIVEN** the app is launched
- **WHEN** theme settings are loaded
- **THEN** the saved theme preference SHALL be applied
- **AND** default to FOLLOW_SYSTEM if not previously set

### Requirement: Theme Selection UI
The system SHALL provide UI for theme selection in the More screen.

#### Scenario: Display theme options
- **GIVEN** the user is on the More screen
- **WHEN** theme settings are shown
- **THEN** all three options SHALL be displayed:
  - Follow System
  - Light
  - Dark

#### Scenario: Current selection indicator
- **GIVEN** the theme selection UI
- **WHEN** displayed
- **THEN** the currently selected option SHALL be visually indicated

### Requirement: Dynamic Theme Application
The system SHALL apply theme changes dynamically without restart.

#### Scenario: Immediate theme change
- **GIVEN** the user changes theme preference
- **WHEN** the new theme is selected
- **THEN** the app SHALL immediately recompose with new theme
- **AND** no restart SHALL be required

#### Scenario: System theme change detection
- **GIVEN** FOLLOW_SYSTEM is selected
- **WHEN** the device theme changes
- **THEN** the app SHALL detect the change
- **AND** update to match the new system theme

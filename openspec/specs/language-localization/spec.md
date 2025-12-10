# language-localization Specification

## Purpose
Provides multi-language support for the MRT Buddy application, enabling users to switch between English and Bengali (Bangla). This capability handles UI string translation, number formatting in Bengali digits, station name localization, and date/time formatting according to the selected language.

## Requirements

### Requirement: Language Selection
The system SHALL allow users to select their preferred language.

#### Scenario: Available languages
- **GIVEN** the language selection UI
- **WHEN** the user views language options
- **THEN** English (en) and Bengali (bn) SHALL be available

#### Scenario: Change language
- **GIVEN** the user is on the More/Settings screen
- **WHEN** the user selects a different language
- **THEN** the app language SHALL change immediately
- **AND** the preference SHALL be persisted for future sessions

#### Scenario: Default language
- **GIVEN** the app is launched for the first time
- **WHEN** no language preference is stored
- **THEN** the default language SHALL be English (en)

### Requirement: UI String Translation
The system SHALL translate all UI strings based on selected language.

#### Scenario: English UI strings
- **GIVEN** English is the selected language
- **WHEN** UI elements are rendered
- **THEN** all strings SHALL be displayed in English
- **AND** strings SHALL be loaded from the English resource file

#### Scenario: Bengali UI strings
- **GIVEN** Bengali is the selected language
- **WHEN** UI elements are rendered
- **THEN** all strings SHALL be displayed in Bengali
- **AND** strings SHALL be loaded from the Bengali resource file

### Requirement: Number Translation
The system SHALL translate numeric values to Bengali digits when Bengali is selected.

#### Scenario: Translate balance amount
- **GIVEN** Bengali is the selected language
- **AND** a balance of 150 Taka
- **WHEN** the balance is displayed
- **THEN** it SHALL show "৳ ১৫০" (Bengali digits)

#### Scenario: Translate transaction amounts
- **GIVEN** Bengali is the selected language
- **WHEN** transaction amounts are displayed
- **THEN** all numeric values SHALL use Bengali digits (০১২৩৪৫৬৭৮৯)

#### Scenario: English numbers
- **GIVEN** English is the selected language
- **WHEN** numeric values are displayed
- **THEN** standard Arabic numerals (0123456789) SHALL be used

### Requirement: Station Name Localization
The system SHALL display station names in the selected language.

#### Scenario: English station names
- **GIVEN** English is the selected language
- **WHEN** station names are displayed
- **THEN** stations SHALL show English names (e.g., "Uttara North", "Motijheel")

#### Scenario: Bengali station names
- **GIVEN** Bengali is the selected language
- **WHEN** station names are displayed
- **THEN** stations SHALL show Bengali names (e.g., "উত্তরা উত্তর", "মতিঝিল")

#### Scenario: Translate station via StationService
- **GIVEN** a station name string
- **WHEN** `StationService.translate(stationName)` is called
- **THEN** the system SHALL return the localized string resource
- **OR** return empty string if no translation exists

### Requirement: Date and Time Localization
The system SHALL format dates and times according to the selected language.

#### Scenario: Month name localization
- **GIVEN** a date to display
- **WHEN** `TimestampService.getMonth(monthNumber)` is called
- **THEN** the month name SHALL be returned in the current language
- **AND** Bengali months: জানু, ফেব্রু, মার্চ, এপ্রিল, মে, জুন, জুলাই, আগস্ট, সেপ্টে, অক্টো, নভে, ডিসে

#### Scenario: AM/PM localization
- **GIVEN** a time to display
- **WHEN** AM/PM is formatted
- **THEN** Bengali SHALL show "এএম" / "পিএম"
- **AND** English SHALL show "AM" / "PM"

#### Scenario: Full timestamp formatting
- **GIVEN** a LocalDateTime
- **WHEN** `TimestampService.formatDateTime()` is called
- **THEN** the format SHALL be "DD Mon YYYY, HH:MM AM/PM"
- **AND** all components SHALL be localized to current language

### Requirement: Language Persistence
The system SHALL persist the language preference across app sessions.

#### Scenario: Save language preference
- **GIVEN** the user selects a language
- **WHEN** the selection is confirmed
- **THEN** the language ISO code SHALL be saved to settings
- **AND** the key "app_language" SHALL store the value

#### Scenario: Load language preference
- **GIVEN** the app is launched
- **WHEN** settings are loaded
- **THEN** the saved language preference SHALL be applied
- **AND** UI SHALL render in the saved language

### Requirement: Compose Locale Integration
The system SHALL integrate with Compose's locale system for resource loading.

#### Scenario: Locale-aware string resources
- **GIVEN** a Composable function
- **WHEN** `stringResource(Res.string.key)` is called
- **THEN** the correct localized string SHALL be returned based on current locale

#### Scenario: Locale state flow
- **GIVEN** the current language as a StateFlow
- **WHEN** the language changes
- **THEN** all observing Composables SHALL recompose with new language

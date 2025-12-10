# station-mapping Specification

## Purpose
Maps numeric station codes from FeliCa card data to human-readable station names. This capability provides the translation layer between raw card data (which uses numeric codes) and display names, supporting localization for both English and Bengali station names.

## Requirements

### Requirement: Station Code to Name Mapping
The system SHALL map numeric station codes to station names.

#### Scenario: Known station codes
- **GIVEN** the station code map
- **THEN** the following mappings SHALL exist:
  - 10 → Motijheel
  - 20 → Bangladesh Secretariat
  - 25 → Dhaka University
  - 30 → Shahbagh
  - 35 → Karwan Bazar
  - 40 → Farmgate
  - 45 → Bijoy Sarani
  - 50 → Agargaon
  - 55 → Shewrapara
  - 60 → Kazipara
  - 65 → Mirpur 10
  - 70 → Mirpur 11
  - 75 → Pallabi
  - 80 → Uttara South
  - 85 → Uttara Center
  - 90 → Uttara North

#### Scenario: Get station name for known code
- **GIVEN** a known station code (e.g., 50)
- **WHEN** `getStationName(code)` is called
- **THEN** the system SHALL return the corresponding name (e.g., "Agargaon")

#### Scenario: Get station name for unknown code
- **GIVEN** an unknown station code (e.g., 99)
- **WHEN** `getStationName(code)` is called
- **THEN** the system SHALL return "Unknown Station (99)"

### Requirement: Station Name Translation
The system SHALL translate station names to the current app language.

#### Scenario: Translate to English
- **GIVEN** English is the selected language
- **AND** a station name "Uttara North"
- **WHEN** `StationService.translate(stationName)` is called
- **THEN** the system SHALL return the English string resource

#### Scenario: Translate to Bengali
- **GIVEN** Bengali is the selected language
- **AND** a station name "Uttara North"
- **WHEN** `StationService.translate(stationName)` is called
- **THEN** the system SHALL return "উত্তরা উত্তর"

#### Scenario: All station translations
- **GIVEN** the translation function
- **THEN** translations SHALL exist for all 17 stations:
  - Kamalapur, Motijheel, Bangladesh Secretariat, Dhaka University
  - Shahbagh, Karwan Bazar, Farmgate, Bijoy Sarani
  - Agargaon, Shewrapara, Kazipara, Mirpur 10, Mirpur 11
  - Pallabi, Uttara South, Uttara Center, Uttara North

#### Scenario: Handle station name variants
- **GIVEN** station names with hyphens vs spaces
- **WHEN** translating "Mirpur-10" or "Mirpur 10"
- **THEN** both variants SHALL map to the same translation

#### Scenario: Unknown station translation
- **GIVEN** a station name not in the translation map
- **WHEN** `translate(stationName)` is called
- **THEN** the system SHALL return an empty string

### Requirement: Station Service Composable Integration
The system SHALL provide Composable-compatible translation functions.

#### Scenario: Translation in Composable context
- **GIVEN** a Composable function displaying station names
- **WHEN** `StationService.translate(name)` is called
- **THEN** the function SHALL use `stringResource()` for localization
- **AND** return the localized string

### Requirement: Station Data Consistency
The system SHALL maintain consistency between parsing and fare calculation station data.

#### Scenario: Station code range
- **GIVEN** the station code map
- **THEN** codes SHALL range from 10 to 90 (in increments of 5 or 10)

#### Scenario: Station count
- **GIVEN** the Dhaka MRT Line 6
- **THEN** exactly 17 stations SHALL be defined
- **AND** station IDs in FareCalculator (0-16) SHALL correspond to station codes

### Requirement: Station Map Display
The system SHALL support displaying the MRT station map.

#### Scenario: Station map screen
- **GIVEN** the user navigates to Station Map
- **WHEN** the screen loads
- **THEN** a visual map of all stations SHALL be displayed
- **AND** the map SHALL show the linear arrangement of stations

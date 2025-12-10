## MODIFIED Requirements

### Requirement: Station Code to Name Mapping
The system SHALL map numeric station codes to station names.

#### Scenario: Known Dhaka Metro station codes
- **GIVEN** the station code map
- **THEN** the following Dhaka Metro mappings SHALL exist:
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

#### Scenario: Known Hatirjheel Bus station codes
- **GIVEN** the station code map
- **THEN** the following Hatirjheel Bus mappings SHALL exist:
  - 13 → Mohanagar (HJ)
  - 16 → Rampura (HJ)
  - 17 → Badda (HJ)
  - 19 → Police Plaza (HJ)
  - 28 → FDC (HJ)

#### Scenario: Get station name for known code
- **GIVEN** a known station code (e.g., 50)
- **WHEN** `getStationName(code)` is called
- **THEN** the system SHALL return the corresponding name (e.g., "Agargaon")

#### Scenario: Get station name for unknown code
- **GIVEN** an unknown station code (e.g., 99)
- **WHEN** `getStationName(code)` is called
- **THEN** the system SHALL return "Unknown (99)"

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
- **THEN** translations SHALL exist for all 17 Dhaka Metro stations:
  - Kamalapur, Motijheel, Bangladesh Secretariat, Dhaka University
  - Shahbagh, Karwan Bazar, Farmgate, Bijoy Sarani
  - Agargaon, Shewrapara, Kazipara, Mirpur 10, Mirpur 11
  - Pallabi, Uttara South, Uttara Center, Uttara North

#### Scenario: Handle station name variants
- **GIVEN** station names with hyphens vs spaces
- **WHEN** translating "Mirpur-10" or "Mirpur 10"
- **THEN** both variants SHALL map to the same translation

#### Scenario: Unknown station translation fallback
- **GIVEN** a station name not in the translation map
- **WHEN** `translate(stationName)` is called
- **THEN** the system SHALL return the original station name unchanged

## ADDED Requirements

### Requirement: Demo Card Scan (Debug Builds)
In debug builds the system SHALL provide a developer action that simulates a complete card scan by driving the production read orchestration over fixture card data, feeding results through the same state and result flows as a physical scan. The action and its UI SHALL NOT exist in release builds.

#### Scenario: Demo scan renders and persists like a real scan
- **GIVEN** a debug build with auto-save enabled
- **WHEN** the user triggers "Scan demo card" from the Developer section
- **THEN** the balance screen SHALL show the demo card's latest balance and transactions
- **AND** the scan SHALL be persisted identically to a physical card read

#### Scenario: Recognizable, stable demo identity
- **WHEN** the demo scan runs multiple times
- **THEN** every scan SHALL use the same fixed demo IDm so it updates a single, identifiable demo card

#### Scenario: Demo scan gives immediate feedback
- **WHEN** the user triggers the demo scan
- **THEN** a confirmation (including the scanned balance) SHALL be shown on the current screen without requiring navigation

#### Scenario: Developer tools are unobtrusive
- **GIVEN** a debug build
- **WHEN** the More screen renders
- **THEN** developer tools SHALL appear only as a single low-prominence entry that navigates to a dedicated developer screen
- **AND** the demo scan, capture toggle, and dump sharing SHALL live on that screen, not on More

#### Scenario: Hidden in release builds
- **GIVEN** a release build
- **WHEN** the More screen renders
- **THEN** no developer entry, screen, or demo scan action SHALL be present

### Requirement: Raw Read Capture (Debug Builds)
In debug builds the system SHALL optionally record each block-read window of a card read (service code, block range, status flags, raw block bytes) and let the developer share the last recorded read as a fixture-ready text dump. The card IDm SHALL be anonymized in recorded dumps.

#### Scenario: Capture disabled by default
- **GIVEN** a fresh install
- **WHEN** a card is read
- **THEN** nothing SHALL be recorded until the developer enables the capture toggle

#### Scenario: Capture and share a read
- **GIVEN** the capture toggle is enabled
- **WHEN** a card read completes and the developer taps "Share last dump"
- **THEN** the system SHALL produce a text dump listing each window's service code, block range, status flags, and block bytes in hex
- **AND** the dump's IDm field SHALL be zeroed

#### Scenario: New read replaces previous dump
- **WHEN** a new card read starts with capture enabled
- **THEN** the recorder SHALL discard the previous session's windows

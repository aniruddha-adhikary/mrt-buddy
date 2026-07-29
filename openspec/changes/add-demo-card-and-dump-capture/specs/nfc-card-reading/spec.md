## MODIFIED Requirements

### Requirement: Card Read Results Emission
The system SHALL emit CardReadResult objects containing card data on both platforms, including reads that yield no transactions.

#### Scenario: Successful card read result
- **GIVEN** a card has been successfully read
- **WHEN** the read completes
- **THEN** the system SHALL emit a `CardReadResult` containing:
  - `idm`: The card's 8-byte identifier as hex string
  - `transactions`: List of parsed Transaction objects

#### Scenario: Completed read with no transactions is still emitted
- **GIVEN** a card read completes but yields zero valid transactions
- **WHEN** the read finishes on either platform
- **THEN** the system SHALL emit a `CardReadResult` with the card's real IDm and an empty transactions list
- **AND** then emit the appropriate error card state

#### Scenario: Failed card read result
- **GIVEN** a card read has failed
- **WHEN** an error occurs
- **THEN** the system SHALL emit a `CardReadResult` with empty IDM and empty transactions list

### Requirement: FeliCa Card Reading (iOS)
The system SHALL read FeliCa NFC-F cards on iOS using CoreNFC, routing block data through the shared read orchestration.

#### Scenario: Request NFC scan session
- **GIVEN** an iOS device with NFC capability
- **WHEN** the user initiates a scan (via Rescan button)
- **THEN** the system SHALL present the CoreNFC scanning UI
- **AND** wait for a FeliCa card to be detected

#### Scenario: Read card data on iOS
- **GIVEN** a FeliCa card is detected by CoreNFC
- **WHEN** the card is read
- **THEN** the system SHALL read transaction blocks using FeliCa polling
- **AND** extract the card IDM and transactions via the shared reader

#### Scenario: iOS honors FeliCa status flags and validity filter
- **GIVEN** a block read completes on iOS
- **WHEN** status flags are non-zero or a parsed transaction predates January 1, 2020
- **THEN** those blocks or transactions SHALL be excluded, identically to Android

#### Scenario: Tag connect failure is surfaced
- **GIVEN** CoreNFC fails to connect to a detected tag
- **WHEN** the connect callback returns an error
- **THEN** the system SHALL emit `CardState.Error`
- **AND** invalidate the session with an error message

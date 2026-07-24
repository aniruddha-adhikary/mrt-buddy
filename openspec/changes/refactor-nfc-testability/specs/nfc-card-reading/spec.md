## ADDED Requirements

### Requirement: Shared Read Orchestration
The system SHALL orchestrate FeliCa transaction-history reads through a shared, platform-independent reader operating on a `CardTransceiver` abstraction (card IDm + block reads returning status flags and raw 16-byte blocks), so the full read-parse-filter pipeline is executable without NFC hardware.

#### Scenario: Both block windows read successfully
- **WHEN** the shared reader reads blocks 0-9 and 10-19 through a transceiver
- **AND** both reads return zero status flags
- **THEN** all returned blocks SHALL be parsed into transactions, validity-filtered, and emitted as a single `CardReadResult` with the card IDm

#### Scenario: One window reports FeliCa error status
- **WHEN** a block read returns non-zero status flags
- **THEN** that window's blocks SHALL be discarded
- **AND** transactions from the other window SHALL still be returned

#### Scenario: I/O failure mid-read returns partial results
- **GIVEN** the first block window was read successfully
- **WHEN** the transceiver fails with an I/O error on the second window
- **THEN** the reader SHALL return the transactions already collected

#### Scenario: Malformed blocks are skipped
- **WHEN** a read window returns a block that is not exactly 16 bytes
- **THEN** that block SHALL be skipped without aborting the read
- **AND** remaining well-formed blocks SHALL still be parsed

#### Scenario: Pipeline testable with a fake transceiver
- **WHEN** a test supplies a fake `CardTransceiver` returning fixture bytes
- **THEN** the identical production reader code path SHALL produce the resulting `CardReadResult` on JVM with no platform NFC APIs involved

## MODIFIED Requirements

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

# nfc-card-reading Specification

## Purpose
Provides platform-specific NFC card reading capabilities for Dhaka MRT FeliCa transit cards. This capability handles the low-level communication with NFC hardware on Android (using android.nfc) and iOS (using CoreNFC), reading card data blocks, and emitting card read results to the application.

## Requirements

### Requirement: NFC Hardware Detection
The system SHALL detect NFC hardware availability and status on the device.

#### Scenario: Device with NFC support enabled
- **GIVEN** the device has NFC hardware
- **AND** NFC is enabled in device settings
- **WHEN** `isSupported()` and `isEnabled()` are called
- **THEN** both SHALL return `true`

#### Scenario: Device with NFC support disabled
- **GIVEN** the device has NFC hardware
- **AND** NFC is disabled in device settings
- **WHEN** `isSupported()` is called
- **THEN** it SHALL return `true`
- **AND** `isEnabled()` SHALL return `false`

#### Scenario: Device without NFC hardware
- **GIVEN** the device does not have NFC hardware
- **WHEN** `isSupported()` is called
- **THEN** it SHALL return `false`
- **AND** the card state SHALL be `CardState.NoNfcSupport`

### Requirement: Card State Flow
The system SHALL expose a SharedFlow of CardState for UI observation.

#### Scenario: Initial state on app launch
- **GIVEN** the app is launched
- **WHEN** the NFCManager initializes
- **THEN** the card state SHALL be `CardState.WaitingForTap` (if NFC enabled)
- **OR** `CardState.NfcDisabled` (if NFC disabled)
- **OR** `CardState.NoNfcSupport` (if no NFC hardware)

#### Scenario: State transition on card detection
- **GIVEN** the current state is `CardState.WaitingForTap`
- **WHEN** a FeliCa card is detected
- **THEN** the state SHALL transition to `CardState.Reading`

#### Scenario: State transition on successful read
- **GIVEN** the current state is `CardState.Reading`
- **WHEN** the card data is successfully parsed
- **THEN** the state SHALL transition to `CardState.Balance(amount)`
- **AND** the balance SHALL be extracted from the first transaction

#### Scenario: State transition on read error
- **GIVEN** the current state is `CardState.Reading`
- **WHEN** an error occurs during card reading
- **THEN** the state SHALL transition to `CardState.Error(message)`

### Requirement: FeliCa Card Reading (Android)
The system SHALL read FeliCa NFC-F cards on Android using Reader Mode.

#### Scenario: Enable NFC reader mode
- **GIVEN** an Android activity with NFC enabled
- **WHEN** `startScan()` is called
- **THEN** the system SHALL enable NFC reader mode with `FLAG_READER_NFC_F`
- **AND** register a broadcast receiver for NFC state changes

#### Scenario: Read transaction history blocks
- **GIVEN** a FeliCa card is connected via NfcF
- **WHEN** the card is read
- **THEN** the system SHALL read up to 19 transaction blocks
- **AND** extract the card IDM (8-byte identifier)
- **AND** parse each block into Transaction objects

#### Scenario: Handle card removed too quickly
- **GIVEN** a card read is in progress
- **WHEN** the card is removed before reading completes
- **THEN** the system SHALL emit `CardState.Error` with message about card moved too fast

### Requirement: FeliCa Card Reading (iOS)
The system SHALL read FeliCa NFC-F cards on iOS using CoreNFC.

#### Scenario: Request NFC scan session
- **GIVEN** an iOS device with NFC capability
- **WHEN** the user initiates a scan (via Rescan button)
- **THEN** the system SHALL present the CoreNFC scanning UI
- **AND** wait for a FeliCa card to be detected

#### Scenario: Read card data on iOS
- **GIVEN** a FeliCa card is detected by CoreNFC
- **WHEN** the card is read
- **THEN** the system SHALL read transaction blocks using FeliCa polling
- **AND** extract the card IDM and transactions

### Requirement: Card Read Results Emission
The system SHALL emit CardReadResult objects containing card data.

#### Scenario: Successful card read result
- **GIVEN** a card has been successfully read
- **WHEN** the read completes
- **THEN** the system SHALL emit a `CardReadResult` containing:
  - `idm`: The card's 8-byte identifier as hex string
  - `transactions`: List of parsed Transaction objects

#### Scenario: Failed card read result
- **GIVEN** a card read has failed
- **WHEN** an error occurs
- **THEN** the system SHALL emit a `CardReadResult` with empty IDM and empty transactions list

### Requirement: NFC State Change Handling (Android)
The system SHALL respond to NFC adapter state changes on Android.

#### Scenario: NFC turned off during scan
- **GIVEN** NFC reader mode is active
- **WHEN** the user disables NFC in device settings
- **THEN** the system SHALL transition to `CardState.NfcDisabled`
- **AND** disable reader mode

#### Scenario: NFC turned on after being disabled
- **GIVEN** the current state is `CardState.NfcDisabled`
- **WHEN** the user enables NFC in device settings
- **THEN** the system SHALL transition to `CardState.WaitingForTap` (preserving Balance if present)
- **AND** re-enable reader mode

### Requirement: Resource Cleanup
The system SHALL properly clean up NFC resources when scanning stops.

#### Scenario: Stop scan cleanup
- **GIVEN** NFC reader mode is active
- **WHEN** `stopScan()` is called
- **THEN** the system SHALL disable reader mode
- **AND** unregister any broadcast receivers
- **AND** release activity references

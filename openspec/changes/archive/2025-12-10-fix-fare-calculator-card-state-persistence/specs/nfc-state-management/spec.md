# nfc-state-management Specification

## Purpose
Define the lifecycle and persistence behavior of NFC card states in the MRT Buddy app, ensuring that successfully read card information remains available to the user until explicitly replaced or invalidated.

## ADDED Requirements

### Requirement: Card State Persistence Across Scan Initialization
The NFC manager SHALL preserve the `CardState.Balance` state when `startScan()` is invoked, preventing unnecessary resets of successfully read card information.

#### Scenario: Balance state preserved during scan reinitialization
- **GIVEN** a card has been successfully read and the current state is `CardState.Balance(amount)`
- **WHEN** the `startScan()` method is invoked (due to recomposition or other triggers)
- **THEN** the system SHALL NOT reset the state to `WaitingForTap`
- **AND** the balance information SHALL remain accessible to the UI

#### Scenario: Non-balance states reset to waiting
- **GIVEN** the current state is `WaitingForTap`, `Error`, `NfcDisabled`, `NoNfcSupport`, or `Reading`
- **WHEN** the `startScan()` method is invoked
- **THEN** the system SHALL transition to `WaitingForTap` (if NFC is enabled and supported)
- **OR** SHALL maintain the appropriate error state (if NFC is disabled or unsupported)

#### Scenario: Balance state updated on new card read
- **GIVEN** the current state is `CardState.Balance(oldAmount)`
- **WHEN** a new card is tapped and successfully read with balance `newAmount`
- **THEN** the system SHALL emit `CardState.Balance(newAmount)`
- **AND** the UI SHALL display the new balance information

#### Scenario: Balance state cleared on read error
- **GIVEN** the current state is `CardState.Balance(amount)`
- **WHEN** a card read operation fails with an error
- **THEN** the system SHALL transition to `CardState.Error(message)`
- **AND** the previous balance information SHALL no longer be displayed

### Requirement: Cross-Platform State Consistency
The NFC manager SHALL implement identical state preservation logic on both Android and iOS platforms.

#### Scenario: Android balance persistence
- **GIVEN** an Android device with NFC enabled
- **AND** a card has been read with `CardState.Balance(amount)`
- **WHEN** the Fare Calculator screen is navigated away from and returned to
- **THEN** the balance information SHALL persist and remain visible

#### Scenario: iOS balance persistence
- **GIVEN** an iOS device with NFC capability
- **AND** a card has been read with `CardState.Balance(amount)`
- **WHEN** the Fare Calculator screen is navigated away from and returned to
- **THEN** the balance information SHALL persist and remain visible
- **AND** the behavior SHALL match the Android implementation

### Requirement: State Lifecycle Transparency
The system SHALL maintain clear and predictable state transitions for NFC card reading operations.

#### Scenario: Initial app launch state
- **GIVEN** the app is launched for the first time or after being terminated
- **WHEN** the NFC manager initializes via `startScan()`
- **THEN** the system SHALL display `WaitingForTap` (if NFC enabled) or appropriate error state
- **AND** no balance information SHALL be shown until a card is read

#### Scenario: State visible on fare calculator
- **GIVEN** the user navigates to the Fare Calculator screen
- **AND** the current state is `CardState.Balance(amount)`
- **WHEN** the user has selected origin and destination stations
- **THEN** the UI SHALL display the balance amount, calculated fare, and number of round trips possible
- **AND** the balance information SHALL NOT disappear unexpectedly

#### Scenario: Explicit state reset conditions
- **GIVEN** the current state is `CardState.Balance(amount)`
- **WHEN** one of the following occurs:
  - NFC is disabled by the user
  - A card read error occurs
  - A new card is successfully read
- **THEN** the system SHALL transition to the appropriate new state
- **AND** the old balance information SHALL be replaced or cleared

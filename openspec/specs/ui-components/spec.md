# ui-components Specification

## Purpose
Provides reusable UI components for displaying card state, balance information, and transaction history across the MRT Buddy application. These components handle platform-specific behaviors (Android vs iOS), support dark/light themes, and integrate with the localization system for multi-language display.

## Requirements

### Requirement: Balance Card Display
The system SHALL display a card component that shows different content based on the current CardState.

#### Scenario: Balance state display
- **GIVEN** the card state is `CardState.Balance(amount)`
- **WHEN** the BalanceCard component is rendered
- **THEN** the system SHALL display the balance amount in Taka (৳) format
- **AND** the amount SHALL be colored red if balance ≤ 50
- **AND** the amount SHALL be colored yellow/amber if balance is between 51-70
- **AND** a "Low Balance" warning SHALL appear if balance ≤ 20

#### Scenario: Card name display with theming
- **GIVEN** the card has a custom name and the state is `CardState.Balance`
- **AND** the card IDM indicates either MRT Pass or Rapid Pass
- **WHEN** the BalanceCard component is rendered
- **THEN** the card name SHALL be displayed at the top with appropriate theme colors
- **AND** Rapid Pass cards SHALL use distinct colors from MRT Pass cards

#### Scenario: Waiting for tap state
- **GIVEN** the card state is `CardState.WaitingForTap`
- **WHEN** the BalanceCard component is rendered on Android
- **THEN** the system SHALL display a pulsing animation around the card icon
- **AND** instruct the user to "Tap your card on back of phone"

#### Scenario: iOS waiting state
- **GIVEN** the card state is `CardState.WaitingForTap`
- **WHEN** the BalanceCard component is rendered on iOS
- **THEN** the system SHALL display a "Rescan" button
- **AND** instruct the user to tap Rescan to start scanning

#### Scenario: Reading state display
- **GIVEN** the card state is `CardState.Reading`
- **WHEN** the BalanceCard component is rendered
- **THEN** the system SHALL display "Reading card..." message
- **AND** instruct the user to "Keep card steady"

#### Scenario: Error state display
- **GIVEN** the card state is `CardState.Error(message)`
- **WHEN** the BalanceCard component is rendered
- **THEN** the system SHALL display the error message
- **AND** on iOS, a "Rescan" link SHALL be shown

#### Scenario: No NFC support state
- **GIVEN** the card state is `CardState.NoNfcSupport`
- **WHEN** the BalanceCard component is rendered
- **THEN** the system SHALL display "NFC Not Supported" message
- **AND** explain that an NFC-enabled device is required

### Requirement: Platform-Specific NFC Disabled Content
The system SHALL display platform-appropriate UI when NFC is disabled on the device.

#### Scenario: Android NFC disabled
- **WHEN** NFC is disabled on an Android device
- **THEN** the system displays an error icon, "NFC is disabled" message, explanatory text, and an "Enable NFC" button
- **AND** tapping the button opens the device's NFC settings screen

#### Scenario: iOS stub implementation
- **GIVEN** the NFC disabled state is not applicable on iOS
- **WHEN** the `NfcDisabledContent` composable is called on iOS (for compilation compatibility)
- **THEN** the system renders nothing (empty composable)

### Requirement: Transaction History List Display
The system SHALL display a list of recent transactions with calculated amounts.

#### Scenario: Transaction list rendering
- **GIVEN** a list of transactions with calculated amounts
- **WHEN** the TransactionHistoryList component is rendered
- **THEN** each transaction SHALL display the route (from → to stations) or "Balance Update"
- **AND** the formatted timestamp SHALL be shown
- **AND** the transaction amount SHALL be color-coded (green for positive, red for negative)

#### Scenario: Station name translation
- **GIVEN** transactions with station codes
- **WHEN** the TransactionHistoryList displays station names
- **THEN** station names SHALL be translated to the current app language

#### Scenario: Filter invalid transactions
- **GIVEN** transactions that have timestamps before year 2015
- **WHEN** the TransactionHistoryList filters transactions
- **THEN** those transactions SHALL NOT be displayed

### Requirement: iOS Rescan Functionality
The system SHALL provide iOS-specific rescan capability since iOS requires explicit user action to start NFC scanning.

#### Scenario: Rescan button on iOS balance card
- **GIVEN** the app is running on iOS
- **WHEN** the BalanceCard is displayed
- **THEN** a "Rescan" button SHALL be visible in the top-right corner
- **AND** tapping it SHALL trigger RescanManager.requestRescan()

#### Scenario: Android excludes rescan button
- **GIVEN** the app is running on Android
- **WHEN** the BalanceCard is displayed
- **THEN** no "Rescan" button SHALL be shown (Android auto-detects cards)

## ADDED Requirements

### Requirement: Transaction Type Detection from Header
The system SHALL determine transaction type based on the fixed header bytes.

#### Scenario: Detect Dhaka Metro commute
- **GIVEN** a transaction with fixedHeader "08 52 10 00"
- **WHEN** `TransactionType.fromHeader()` is called
- **THEN** the system SHALL return `CommuteDhakaMetro`

#### Scenario: Detect Hatirjheel Bus start
- **GIVEN** a transaction with fixedHeader "08 D2 20 00"
- **WHEN** `TransactionType.fromHeader()` is called
- **THEN** the system SHALL return `CommuteHatirjheelBusStart`

#### Scenario: Detect Hatirjheel Bus end
- **GIVEN** a transaction with fixedHeader "42 D6 30 00"
- **WHEN** `TransactionType.fromHeader()` is called
- **THEN** the system SHALL return `CommuteHatirjheelBusEnd`

#### Scenario: Detect MRT balance update
- **GIVEN** a transaction with fixedHeader "1D 60 02 01"
- **WHEN** `TransactionType.fromHeader()` is called
- **THEN** the system SHALL return `BalanceUpdate`

#### Scenario: Detect Rapid balance update
- **GIVEN** a transaction with fixedHeader "42 60 02 00"
- **WHEN** `TransactionType.fromHeader()` is called
- **THEN** the system SHALL return `BalanceUpdate`

#### Scenario: Unknown header defaults to unknown commute
- **GIVEN** a transaction with an unrecognized fixedHeader
- **WHEN** `TransactionType.fromHeader()` is called
- **THEN** the system SHALL return `CommuteUnknown`

### Requirement: Granular Transaction Type Variants
The system SHALL support multiple transaction type variants to distinguish transit systems.

#### Scenario: Transaction type enumeration
- **GIVEN** the TransactionType sealed class
- **THEN** the following variants SHALL exist:
  - `CommuteDhakaMetro` - Dhaka Metro rail trips
  - `CommuteHatirjheelBusStart` - Hatirjheel Bus entry
  - `CommuteHatirjheelBusEnd` - Hatirjheel Bus exit
  - `CommuteUnknown` - Unknown commute type
  - `BalanceUpdate` - Card top-up transactions

# fare-calculation Specification

## Purpose
Calculates transit fares between Dhaka MRT stations using a predefined fare matrix. This capability provides the fare lookup functionality for the Fare Calculator screen, supports bidirectional lookups, and calculates the number of possible round trips based on current card balance.

## Requirements

### Requirement: Fare Matrix Lookup
The system SHALL calculate fares between any two stations using the fare matrix.

#### Scenario: Same station (no travel)
- **GIVEN** the origin and destination are the same station
- **WHEN** `calculateFare()` is called
- **THEN** the system SHALL return 0

#### Scenario: Adjacent stations
- **GIVEN** origin and destination are adjacent stations (e.g., Uttara North to Uttara Center)
- **WHEN** `calculateFare()` is called
- **THEN** the system SHALL return 20 Taka (minimum fare)

#### Scenario: End-to-end journey
- **GIVEN** origin is Uttara North and destination is Kamalapur (or vice versa)
- **WHEN** `calculateFare()` is called
- **THEN** the system SHALL return 100 Taka (maximum fare)

#### Scenario: Bidirectional lookup
- **GIVEN** a fare lookup from station A to station B
- **WHEN** `calculateFare()` is called
- **THEN** the system SHALL return the same fare as B to A
- **AND** the matrix lookup SHALL try both directions

### Requirement: Station List Management
The system SHALL provide access to all MRT line stations.

#### Scenario: Get all stations
- **WHEN** `getAllStations()` is called
- **THEN** the system SHALL return a list of 17 stations
- **AND** stations SHALL be ordered from Uttara North (ID 0) to Kamalapur (ID 16)

#### Scenario: Get station by name
- **GIVEN** a station name
- **WHEN** `getStation(name)` is called
- **THEN** the system SHALL return the Station object with matching name
- **OR** return null if not found

#### Scenario: Get station by ID
- **GIVEN** a station ID (0-16)
- **WHEN** `getStation(id)` is called
- **THEN** the system SHALL return the Station object at that index
- **OR** return null if ID is out of range

### Requirement: Fare Structure Data
The system SHALL maintain the complete fare matrix for all station pairs.

#### Scenario: Fare matrix structure
- **GIVEN** the fare matrix
- **THEN** it SHALL contain fares for all 17 stations
- **AND** fares SHALL range from 20 Taka (minimum) to 100 Taka (maximum)
- **AND** the matrix SHALL be symmetric (A→B = B→A)

#### Scenario: Station data structure
- **GIVEN** a Station object
- **THEN** it SHALL contain:
  - `name`: The station name (e.g., "Uttara North")
  - `id`: The station index (0-16)

### Requirement: Round Trip Calculation
The system SHALL calculate how many round trips are possible with current balance.

#### Scenario: Calculate possible round trips
- **GIVEN** a card balance and a selected route fare
- **WHEN** round trips are calculated
- **THEN** the system SHALL divide balance by (fare × 2)
- **AND** return the floor of that division

#### Scenario: Insufficient balance for round trip
- **GIVEN** a card balance less than twice the fare
- **WHEN** round trips are calculated
- **THEN** the system SHALL return 0

### Requirement: MRT Pass Discount (Future)
The system SHALL support fare discounts for MRT Pass cards when discount calculation is enabled.

#### Scenario: MRT Pass 10% discount
- **GIVEN** a card identified as MRT Pass (not Rapid Pass)
- **WHEN** fare is calculated with discount applied
- **THEN** the fare SHALL be reduced by 10%
- **AND** the discounted fare SHALL be rounded appropriately

### Requirement: Fare Calculator UI Integration
The system SHALL integrate with the Fare Calculator screen for interactive fare lookup.

#### Scenario: Station selection dropdowns
- **GIVEN** the Fare Calculator screen
- **WHEN** the user opens origin or destination dropdown
- **THEN** all 17 stations SHALL be listed for selection
- **AND** station names SHALL be displayed in current language

#### Scenario: Display calculated fare
- **GIVEN** origin and destination stations are selected
- **WHEN** the fare is calculated
- **THEN** the fare SHALL be displayed in Taka (৳) format
- **AND** if card balance is available, possible round trips SHALL be shown

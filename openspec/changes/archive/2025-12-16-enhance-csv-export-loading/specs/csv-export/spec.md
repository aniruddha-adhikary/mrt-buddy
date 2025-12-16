## MODIFIED Requirements

### Requirement: Export UI Integration
The system SHALL provide an export button in the transaction list screen with clear loading feedback.

#### Scenario: Display export button
- **GIVEN** the TransactionListScreen is displayed
- **WHEN** there are transactions loaded
- **THEN** a share icon button SHALL appear in the top app bar

#### Scenario: Disable export when empty
- **GIVEN** the TransactionListScreen with no transactions
- **WHEN** the screen is displayed
- **THEN** the export button SHALL be disabled

#### Scenario: Show loading overlay during export
- **GIVEN** the user taps the export button
- **WHEN** export is in progress
- **THEN** a semi-transparent overlay SHALL cover the screen
- **AND** a centered progress indicator with "Exporting..." text SHALL be displayed
- **AND** the export button SHALL be disabled until export completes

#### Scenario: Export all transactions
- **GIVEN** a card with transactions (some loaded via pagination)
- **WHEN** the user taps export
- **THEN** the system SHALL fetch ALL transactions for the card
- **AND** generate CSV with the complete history
- **AND** open the native share sheet

#### Scenario: Display export error
- **GIVEN** export fails with an error
- **WHEN** the error occurs
- **THEN** an error dialog SHALL be displayed with the error message
- **AND** a dismiss button SHALL allow the user to close the dialog

## ADDED Requirements

### Requirement: Export Background Processing
The system SHALL perform CPU-bound export operations on a background thread.

#### Scenario: CSV generation on background dispatcher
- **GIVEN** the user initiates an export
- **WHEN** transactions are fetched and CSV is generated
- **THEN** the database fetch and CSV generation SHALL run on Dispatchers.Default
- **AND** the UI thread SHALL remain responsive

#### Scenario: File sharing on main thread
- **GIVEN** CSV content has been generated
- **WHEN** the file is ready to share
- **THEN** the platform share dialog SHALL be invoked on the main thread

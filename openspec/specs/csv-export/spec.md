# csv-export Specification

## Purpose
TBD - created by archiving change add-csv-export. Update Purpose after archive.
## Requirements
### Requirement: CSV Export Generation
The system SHALL generate CSV files from transaction data with all relevant fields.

#### Scenario: Generate CSV with transactions
- **GIVEN** a list of transactions for a card
- **WHEN** `CsvExportService.generateCsv()` is called
- **THEN** the system SHALL return a CSV string with header row
- **AND** columns SHALL be: Date, Time, From Station, To Station, Amount, Balance, Card ID, Transaction Type
- **AND** each transaction SHALL be formatted as one row

#### Scenario: Format date and time columns
- **GIVEN** a transaction with epoch milliseconds timestamp
- **WHEN** the CSV is generated
- **THEN** Date SHALL be formatted as YYYY-MM-DD
- **AND** Time SHALL be formatted as HH:MM (24-hour)

#### Scenario: Use English station names
- **GIVEN** a transaction with station names
- **WHEN** the CSV is generated
- **THEN** station names SHALL use the English values stored in the database
- **AND** Hatirjheel bus stations SHALL appear as stored (e.g., "Mohanagar (HJ)")

#### Scenario: Format transaction type
- **GIVEN** a transaction with a fixedHeader
- **WHEN** the CSV is generated
- **THEN** Transaction Type SHALL be the enum name (e.g., "CommuteDhakaMetro", "BalanceUpdate", "CommuteHatirjheelBusStart")

#### Scenario: Handle CSV special characters
- **GIVEN** a value containing commas, quotes, or newlines
- **WHEN** the CSV is generated
- **THEN** the value SHALL be enclosed in double quotes
- **AND** internal double quotes SHALL be escaped as two double quotes

### Requirement: CSV Filename Generation
The system SHALL generate descriptive filenames for exported CSV files.

#### Scenario: Generate filename with card name
- **GIVEN** a card with name "My Card"
- **WHEN** `CsvExportService.generateFilename()` is called
- **THEN** the filename SHALL be "MRT_My-Card_YYYY-MM-DD.csv"
- **AND** spaces in card name SHALL be replaced with hyphens

#### Scenario: Generate filename for unnamed card
- **GIVEN** a card with no name (null or blank)
- **WHEN** `CsvExportService.generateFilename()` is called
- **THEN** the filename SHALL be "MRT_Card_YYYY-MM-DD.csv"

#### Scenario: Sanitize filename characters
- **GIVEN** a card name with invalid filename characters
- **WHEN** `CsvExportService.generateFilename()` is called
- **THEN** invalid characters SHALL be removed or replaced

### Requirement: Platform File Sharing
The system SHALL share CSV files using native platform sharing mechanisms.

#### Scenario: Share on Android
- **GIVEN** CSV content to share
- **WHEN** `FileSharer.share()` is called on Android
- **THEN** the system SHALL write content to a temporary file in cache directory
- **AND** create a content URI via FileProvider
- **AND** launch Intent.ACTION_SEND with the share chooser

#### Scenario: Share on iOS
- **GIVEN** CSV content to share
- **WHEN** `FileSharer.share()` is called on iOS
- **THEN** the system SHALL write content to a temporary file
- **AND** present UIActivityViewController with the file URL

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

### Requirement: Export Data Retrieval
The system SHALL fetch all transactions for export, bypassing pagination.

#### Scenario: Fetch all transactions for export
- **GIVEN** a card IDM
- **WHEN** `TransactionRepository.getAllTransactionsForExport()` is called
- **THEN** the system SHALL return all transactions for that card
- **AND** transactions SHALL include calculated amounts
- **AND** transactions SHALL be sorted by order (descending - newest first)

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


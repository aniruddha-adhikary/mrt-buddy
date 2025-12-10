# database-persistence Specification

## Purpose
Provides multiplatform SQLite database persistence using Room for storing cards, scans, and transactions. This capability handles database initialization, schema management, and platform-specific database construction for both Android and iOS.

## Requirements

### Requirement: Database Initialization
The system SHALL initialize the Room database on app startup.

#### Scenario: Android database initialization
- **GIVEN** the app is running on Android
- **WHEN** the database is requested
- **THEN** the system SHALL create the database using `Room.databaseBuilder`
- **AND** the database file SHALL be stored in the app's private directory
- **AND** the database name SHALL be "mrt_buddy_database"

#### Scenario: iOS database initialization
- **GIVEN** the app is running on iOS
- **WHEN** the database is requested
- **THEN** the system SHALL create the database using the iOS Room builder
- **AND** the database file SHALL be stored in the app's documents directory

#### Scenario: Database singleton
- **GIVEN** multiple requests for the database instance
- **WHEN** the database is accessed
- **THEN** the same database instance SHALL be returned (singleton pattern)

### Requirement: Database Schema
The system SHALL define the database schema with three entities.

#### Scenario: Cards table schema
- **GIVEN** the cards table
- **THEN** it SHALL have columns:
  - `idm` (String, Primary Key): Card identifier
  - `name` (String, nullable): Custom card name
  - `lastScanTime` (Long, nullable): Last scan timestamp

#### Scenario: Scans table schema
- **GIVEN** the scans table
- **THEN** it SHALL have columns:
  - `scanId` (Long, Primary Key, Auto-generate): Scan identifier
  - `cardIdm` (String): Foreign key to cards table

#### Scenario: Transactions table schema
- **GIVEN** the transactions table
- **THEN** it SHALL have:
  - Composite primary key: cardIdm, fromStation, toStation, balance, dateTime, fixedHeader
  - `scanId` (Long): Foreign key to scans table with CASCADE delete
  - `fromStation` (String): Origin station name
  - `toStation` (String): Destination station name
  - `balance` (Int): Balance after transaction
  - `dateTime` (Long): Transaction timestamp
  - `fixedHeader` (String): Transaction header data
  - `order` (Int): Ordering index

### Requirement: Data Access Objects (DAOs)
The system SHALL provide DAOs for each entity.

#### Scenario: CardDao operations
- **GIVEN** the CardDao
- **THEN** it SHALL support:
  - `insertCard(card)`: Insert or replace card
  - `getCardByIdm(idm)`: Get card by identifier
  - `getAllCards()`: Get all cards ordered by lastScanTime DESC
  - `updateCardName(idm, name)`: Update card name
  - `updateLastScanTime(idm, time)`: Update last scan time
  - `deleteCard(idm)`: Delete card by identifier

#### Scenario: ScanDao operations
- **GIVEN** the ScanDao
- **THEN** it SHALL support:
  - `insertScan(scan)`: Insert scan and return generated ID
  - `deleteScansByCardIdm(cardIdm)`: Delete all scans for a card

#### Scenario: TransactionDao operations
- **GIVEN** the TransactionDao
- **THEN** it SHALL support:
  - `insertTransactions(transactions)`: Bulk insert transactions
  - `getTransactionsByCardIdm(cardIdm)`: Get all transactions for card
  - `getTransactionsByCardIdmPaginated(cardIdm, limit, offset)`: Paginated query
  - `getLatestTransactionByCardIdm(cardIdm)`: Get most recent transaction
  - `getLastOrder()`: Get highest order value
  - `deleteTransactionsByCardIdm(cardIdm)`: Delete all transactions for card

### Requirement: Database Indexes
The system SHALL maintain indexes for query performance.

#### Scenario: Transaction table indexes
- **GIVEN** the transactions table
- **THEN** the following indexes SHALL exist:
  - Index on `scanId` for foreign key lookups
  - Index on `order` for sorting
  - Composite index on `cardIdm, order` for pagination queries
  - Composite index on `cardIdm, dateTime` for date-based queries

### Requirement: Foreign Key Constraints
The system SHALL enforce referential integrity via foreign keys.

#### Scenario: Transaction to scan relationship
- **GIVEN** a transaction entity
- **WHEN** the referenced scan is deleted
- **THEN** the transaction SHALL be automatically deleted (CASCADE)

#### Scenario: Cascade delete on card removal
- **GIVEN** a card with associated scans and transactions
- **WHEN** the card is deleted
- **THEN** scans SHALL be deleted via repository logic
- **AND** transactions SHALL cascade delete via foreign key

### Requirement: Database Version Management
The system SHALL manage database schema versions.

#### Scenario: Current database version
- **GIVEN** the AppDatabase definition
- **THEN** the version SHALL be 2

#### Scenario: Schema export
- **GIVEN** the database configuration
- **THEN** schema SHALL be exported to `$projectDir/schemas` directory
- **AND** this enables migration validation

### Requirement: Dependency Injection Integration
The system SHALL integrate with Koin for database access.

#### Scenario: Provide database instance
- **GIVEN** the Koin module configuration
- **WHEN** database is requested
- **THEN** the platform-specific database builder SHALL be invoked
- **AND** the singleton instance SHALL be provided

#### Scenario: Provide DAOs
- **GIVEN** the Koin module configuration
- **WHEN** DAOs are requested
- **THEN** they SHALL be obtained from the database instance
- **AND** CardDao, ScanDao, and TransactionDao SHALL be available

# transaction-history Specification

## Purpose
Manages the storage, retrieval, and display of transit transaction history. This capability handles persisting transactions to the Room database, calculating transaction amounts (fare spent or balance added), and providing paginated lazy loading for efficient display of large transaction histories.

## Requirements

### Requirement: Transaction Persistence
The system SHALL persist transaction data from card reads to the local database.

#### Scenario: Save card read result
- **GIVEN** a CardReadResult with IDM and transactions
- **WHEN** `saveCardReadResult()` is called
- **THEN** the system SHALL create or update the card entity with the IDM
- **AND** update the card's lastScanTime to the current timestamp
- **AND** create a new scan entity linked to the card
- **AND** insert all transactions linked to the scan

#### Scenario: Transaction ordering
- **GIVEN** transactions are being saved
- **WHEN** they are inserted into the database
- **THEN** each transaction SHALL be assigned an incrementing `order` value
- **AND** transactions SHALL be reversed so oldest transactions have lower order values

#### Scenario: Timestamp conversion
- **GIVEN** a transaction with a LocalDateTime timestamp
- **WHEN** the transaction is saved
- **THEN** the timestamp SHALL be converted to epoch milliseconds using Asia/Dhaka timezone

### Requirement: Transaction Amount Calculation
The system SHALL calculate the amount spent or added for each transaction.

#### Scenario: Calculate transaction amounts
- **GIVEN** a list of transactions sorted by order (descending)
- **WHEN** amounts are calculated
- **THEN** each transaction's amount SHALL be: current.balance - previous.balance
- **AND** transactions without a calculable amount (first in history) SHALL be filtered out

#### Scenario: Positive amount indicates balance top-up
- **GIVEN** a transaction where the amount is positive
- **WHEN** displayed in the UI
- **THEN** it SHALL be identified as a "Balance Update" transaction

#### Scenario: Negative amount indicates fare deduction
- **GIVEN** a transaction where the amount is negative
- **WHEN** displayed in the UI
- **THEN** it SHALL be identified as a "Commute" transaction

### Requirement: Lazy Loading with Pagination
The system SHALL support paginated loading of transactions for efficient memory usage.

#### Scenario: Load first batch of transactions
- **GIVEN** a card with many transactions
- **WHEN** `getTransactionsByCardIdmLazy()` is called with offset=0
- **THEN** the system SHALL fetch `limit + 1` transactions (default limit=20)
- **AND** return up to `limit` transactions with calculated amounts
- **AND** use the extra transaction to calculate the last visible item's amount

#### Scenario: Load subsequent batches
- **GIVEN** transactions have been loaded with nextOffset > 0
- **WHEN** `getTransactionsByCardIdmLazy()` is called with the nextOffset
- **THEN** the system SHALL fetch the next batch starting from that offset
- **AND** return the correct transactions with amounts

#### Scenario: Detect end of list
- **GIVEN** a paginated query
- **WHEN** fewer transactions are returned than requested (fetchLimit)
- **THEN** `isEndReached` SHALL be `true`
- **AND** no more pagination requests are needed

#### Scenario: Return lazy load result
- **GIVEN** a lazy load operation completes
- **WHEN** results are returned
- **THEN** the result SHALL contain:
  - `transactions`: List of TransactionEntityWithAmount
  - `isEndReached`: Boolean indicating if all transactions loaded
  - `nextOffset`: The offset for the next batch

### Requirement: Transaction Retrieval by Card
The system SHALL retrieve all transactions for a specific card.

#### Scenario: Get transactions by card IDM
- **GIVEN** a card IDM
- **WHEN** `getTransactionsByCardIdm()` is called
- **THEN** the system SHALL return all transactions for that card
- **AND** transactions SHALL be sorted by order (descending - newest first)
- **AND** amounts SHALL be calculated for each transaction

#### Scenario: Get latest balance
- **GIVEN** a card IDM with transactions
- **WHEN** `getLatestBalanceByCardIdm()` is called
- **THEN** the system SHALL return the balance from the most recent transaction
- **OR** return null if no transactions exist

### Requirement: Transaction Display Formatting
The system SHALL format transactions for display with localized content.

#### Scenario: Format transaction timestamp
- **GIVEN** a transaction with a timestamp
- **WHEN** displayed in the UI
- **THEN** the timestamp SHALL be formatted as "DD Mon YYYY, HH:MM AM/PM"
- **AND** month names SHALL be localized (English or Bengali)
- **AND** numbers SHALL be translated to Bengali digits if Bengali language is selected

#### Scenario: Display transaction route
- **GIVEN** a commute transaction with from and to stations
- **WHEN** displayed in the UI
- **THEN** the route SHALL be shown as "FromStation → ToStation"
- **AND** station names SHALL be translated to current language

### Requirement: Transaction Entity Schema
The system SHALL store transactions with proper indexing for efficient queries.

#### Scenario: Transaction composite primary key
- **GIVEN** a transaction to be stored
- **WHEN** it is inserted
- **THEN** the primary key SHALL be composite of: cardIdm, fromStation, toStation, balance, dateTime, fixedHeader

#### Scenario: Transaction indexes
- **GIVEN** the transaction table
- **WHEN** queries are executed
- **THEN** indexes SHALL exist for:
  - scanId (for cascade deletes)
  - order (for sorting)
  - cardIdm + order (for paginated queries)
  - cardIdm + dateTime (for date-based queries)

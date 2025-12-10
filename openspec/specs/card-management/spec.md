# card-management Specification

## Purpose
Manages the lifecycle of saved transit cards including storage, naming, retrieval, and deletion. This capability allows users to save multiple cards, assign custom names for identification, view their card history, and remove cards they no longer need.

## Requirements

### Requirement: Card Storage
The system SHALL persist card metadata to the local database.

#### Scenario: Save new card
- **GIVEN** a card read result with a new IDM
- **WHEN** the card is saved
- **THEN** the system SHALL create a CardEntity with:
  - `idm`: The card's unique identifier (primary key)
  - `name`: null (initially unnamed)
  - `lastScanTime`: Current timestamp in epoch milliseconds

#### Scenario: Update existing card scan time
- **GIVEN** a card with an IDM that already exists in the database
- **WHEN** the card is scanned again
- **THEN** the system SHALL update the `lastScanTime` to the current timestamp
- **AND** preserve the existing card name

### Requirement: Card Naming
The system SHALL allow users to assign custom names to their cards.

#### Scenario: Rename card
- **GIVEN** a saved card with an IDM
- **WHEN** `renameCard(cardIdm, newName)` is called
- **THEN** the system SHALL update the card's name in the database
- **AND** the new name SHALL be displayed in the UI

#### Scenario: Display card name
- **GIVEN** a card with a custom name
- **WHEN** the card is displayed in the balance card or history
- **THEN** the custom name SHALL be shown prominently

#### Scenario: Card without name
- **GIVEN** a card without a custom name (name is null)
- **WHEN** the card is displayed
- **THEN** the card IDM or a default identifier MAY be shown

### Requirement: Card Retrieval
The system SHALL provide methods to retrieve saved cards.

#### Scenario: Get card by IDM
- **GIVEN** a card IDM
- **WHEN** `getCardByIdm(idm)` is called
- **THEN** the system SHALL return the CardEntity if found
- **OR** return null if not found

#### Scenario: Get all cards
- **WHEN** `getAllCards()` is called
- **THEN** the system SHALL return all saved CardEntity objects
- **AND** cards SHALL be ordered by lastScanTime (most recent first)

### Requirement: Card Deletion
The system SHALL allow users to delete saved cards and their associated data.

#### Scenario: Delete card with cascade
- **GIVEN** a saved card with associated scans and transactions
- **WHEN** `deleteCard(cardIdm)` is called
- **THEN** the system SHALL delete the card entity
- **AND** delete all associated scan entities
- **AND** delete all associated transaction entities

#### Scenario: Confirm card deletion
- **GIVEN** the user initiates card deletion
- **WHEN** the delete action is triggered
- **THEN** the system SHOULD prompt for confirmation before deletion

### Requirement: Card Type Identification
The system SHALL identify the type of transit card based on its IDM.

#### Scenario: Identify Rapid Pass card
- **GIVEN** a card IDM
- **WHEN** `isRapidPassIdm(idm)` is called
- **THEN** the system SHALL return true if the IDM pattern indicates a Rapid Pass
- **AND** return false otherwise

#### Scenario: Display card type styling
- **GIVEN** a card displayed in the UI
- **WHEN** the card type is determined
- **THEN** Rapid Pass cards SHALL use distinct theme colors
- **AND** MRT Pass cards SHALL use different theme colors

### Requirement: Auto-Save Functionality
The system SHALL support optional automatic saving of card reads.

#### Scenario: Auto-save enabled
- **GIVEN** auto-save is enabled in settings
- **WHEN** a card is successfully read
- **THEN** the system SHALL automatically save the card and transactions

#### Scenario: Auto-save disabled
- **GIVEN** auto-save is disabled in settings
- **WHEN** a card is successfully read
- **THEN** the system SHALL NOT automatically save the card
- **AND** the user MAY manually save if desired

### Requirement: Card History Display
The system SHALL display a list of all saved cards in the History screen.

#### Scenario: Display card list
- **GIVEN** the user navigates to the History screen
- **WHEN** the screen loads
- **THEN** all saved cards SHALL be displayed
- **AND** each card SHALL show its name (or IDM) and last scan time

#### Scenario: Select card for details
- **GIVEN** a list of saved cards
- **WHEN** the user taps on a card
- **THEN** the system SHALL navigate to the Transaction List screen
- **AND** display all transactions for that card

#### Scenario: Card management actions
- **GIVEN** a card in the history list
- **WHEN** the user long-presses or accesses the menu
- **THEN** options to rename or delete the card SHALL be available

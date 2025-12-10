# app-navigation Specification

## Purpose
Manages screen navigation and routing throughout the MRT Buddy application using Navigation Compose. This capability provides bottom navigation between main screens, handles deep navigation to detail screens, and maintains navigation state across configuration changes.

## Requirements

### Requirement: Screen Definitions
The system SHALL define all navigable screens as an enum.

#### Scenario: Main screens
- **GIVEN** the Screen enum
- **THEN** the following screens SHALL be defined:
  - `Home`: Balance display and recent transactions
  - `Calculator`: Fare calculation between stations
  - `History`: List of saved cards
  - `More`: Settings and additional options

#### Scenario: Detail screens
- **GIVEN** the Screen enum
- **THEN** the following detail screens SHALL be defined:
  - `TransactionList`: Transaction history for a specific card
  - `StationMap`: Visual MRT network map
  - `Licenses`: Open source license attributions

#### Scenario: Screen titles
- **GIVEN** each Screen enum value
- **THEN** it SHALL have an associated `title` property
- **AND** titles SHALL be string resource references for localization

### Requirement: Bottom Navigation Bar
The system SHALL display a bottom navigation bar for main screen switching.

#### Scenario: Navigation bar items
- **GIVEN** the bottom navigation bar
- **THEN** it SHALL display 4 items:
  - Calculator icon with "Fare" label
  - Card icon with "Balance" label
  - History icon with "History" label
  - Apps icon with "More" label

#### Scenario: Selected item highlighting
- **GIVEN** the user is on a specific main screen
- **WHEN** the navigation bar is displayed
- **THEN** the corresponding item SHALL be visually selected

#### Scenario: Hide navigation bar on detail screens
- **GIVEN** the user navigates to StationMap
- **WHEN** the screen is displayed
- **THEN** the bottom navigation bar SHALL be hidden

### Requirement: Navigation Actions
The system SHALL handle navigation between screens.

#### Scenario: Navigate to main screen
- **GIVEN** the user taps a bottom navigation item
- **WHEN** the navigation action executes
- **THEN** the system SHALL navigate to that screen
- **AND** use `popUpTo(startDestination)` with `saveState = true`
- **AND** use `launchSingleTop = true` and `restoreState = true`

#### Scenario: Navigate to transaction list
- **GIVEN** the user taps a card in History
- **WHEN** the navigation action executes
- **THEN** the system SHALL store the selected card IDM
- **AND** navigate to TransactionList screen

#### Scenario: Navigate back from detail screen
- **GIVEN** the user is on a detail screen (TransactionList, StationMap, Licenses)
- **WHEN** the user triggers back navigation
- **THEN** the system SHALL call `navController.navigateUp()`

### Requirement: Navigation State Management
The system SHALL preserve navigation state across configuration changes.

#### Scenario: Save navigation state
- **GIVEN** the user has navigated between screens
- **WHEN** navigation state is saved
- **THEN** scroll positions and screen state SHALL be preserved

#### Scenario: Restore navigation state
- **GIVEN** saved navigation state exists
- **WHEN** the user returns to a previously visited screen
- **THEN** the previous state SHALL be restored

#### Scenario: Remember selected card
- **GIVEN** a card was selected in History
- **WHEN** navigating to TransactionList
- **THEN** the selected card IDM SHALL be stored in a remember state

### Requirement: NavHost Configuration
The system SHALL configure the NavHost with all screen routes.

#### Scenario: Start destination
- **GIVEN** the NavHost configuration
- **THEN** the start destination SHALL be `Screen.Home.name`

#### Scenario: Route definitions
- **GIVEN** the NavHost
- **THEN** composable routes SHALL be defined for all Screen enum values
- **AND** each route SHALL render the appropriate screen Composable

### Requirement: Cross-Screen Navigation
The system SHALL support navigation from More screen to sub-screens.

#### Scenario: Navigate to Station Map
- **GIVEN** the user is on More screen
- **WHEN** the user taps "Station Map"
- **THEN** the system SHALL navigate to StationMap screen

#### Scenario: Navigate to Licenses
- **GIVEN** the user is on More screen
- **WHEN** the user taps "Open Source Licenses"
- **THEN** the system SHALL navigate to Licenses screen

### Requirement: History Tab Selection State
The system SHALL highlight History tab for both History and TransactionList screens.

#### Scenario: History tab selection
- **GIVEN** the user is on History or TransactionList screen
- **WHEN** the navigation bar is displayed
- **THEN** the History tab SHALL be shown as selected

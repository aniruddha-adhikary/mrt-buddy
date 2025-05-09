# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MRT Buddy is a Kotlin Multiplatform (KMP) app built with Jetpack Compose for Android and iOS that allows users to:
- Check the balance of Dhaka MRT transit cards (FeliCa cards) using NFC
- View transaction history (up to 19 transactions)
- Calculate fares between stations
- Store card information locally without requiring internet connectivity
- Support for multiple languages (English and Bengali)

## Build System and Project Structure

- Built using Kotlin Multiplatform (KMP) with Gradle (Kotlin DSL)
- Compose Multiplatform for UI across platforms
- Uses Koin for dependency injection
- Uses Room database for local storage
- NFC functionality for reading transit cards (platform-specific implementations)

## Development Environment Setup

To work with this codebase, make sure you have:
- Android Studio or IntelliJ IDEA with Kotlin Multiplatform support
- JDK 11+
- Android SDK with latest build tools 
- Xcode (for iOS development)

## Common Development Commands

### Build and Run

For Android:
```bash
./gradlew :composeApp:assembleDebug
```

For running on an Android device:
```bash
./gradlew :composeApp:installDebug
```

For iOS (requires macOS):
```bash
./gradlew :composeApp:iosDeployIPhone # for physical device
./gradlew :composeApp:iosDeployIPhoneSimulator # for simulator
```

### Testing

Run tests with:
```bash
./gradlew :composeApp:testDebugUnitTest # For Android
./gradlew :composeApp:allTests # For all platforms
```

### Build Release Version

```bash
./gradlew :composeApp:assembleRelease
```

Note: For signing release builds, you need to set environment variables:
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

### Generate License Report

```bash
./gradlew generateLicenseReport
```

## Code Structure and Architecture

### Overall Architecture

The app follows an MVI (Model-View-Intent) pattern for UI state management:
- **Model**: Represented by state classes (e.g., `MainScreenState`)
- **View**: Compose UI components (Screens and composables)
- **Intent**: User actions represented by Action classes (e.g., `MainScreenAction`)

Each screen typically has:
- A View (`ScreenName.kt`) - UI components
- A ViewModel (`ScreenNameViewModel.kt`) - Business logic
- A State class (`ScreenNameState.kt`) - UI state
- An Action class (`ScreenNameAction.kt`) - User actions
- An Event class (`ScreenNameEvent.kt`) - Events from ViewModel to UI

### UI Layer

The UI is implemented using Compose Multiplatform with the following structure:

#### Navigation

- `Screen.kt` - Sealed class defining all screen routes
- Navigation is managed in `MainScreen.kt` with a `NavHost` and composables
- Bottom navigation bar with main tabs: Calculator, Balance, History, More

#### Main Screens

1. **Home Screen** (`MainScreen.kt`):
   - Entry point for the app
   - Shows balance card and recent transactions
   - Bottom navigation controller for the app

2. **Fare Calculator** (`FareCalculatorScreen.kt`):
   - Calculates fare between two stations
   - Uses dropdown selectors for origin and destination

3. **History Screen** (`HistoryScreen.kt`):
   - Shows list of previously scanned cards
   - Allows renaming and selecting cards to view transactions

4. **Transaction List** (`TransactionListScreen.kt`):
   - Shows detailed transactions for a selected card

5. **More Screen** (`MoreScreen.kt`):
   - Additional options and settings
   - Language selection
   - Links to licenses and station map

6. **Station Map Screen** (`StationMapScreen.kt`):
   - Shows the MRT network map

#### Common UI Components

- `BalanceCard.kt` - Shows the current balance and card state
- `TransactionHistoryList.kt` - Reusable component for transaction lists
- `Footer.kt` - Common footer component
- `Icons.kt` - Custom icons used throughout the app

### Data Layer

#### Database

Room database with SQLite storage for both platforms:

- `AppDatabase.kt` - Database configuration
- Platform-specific implementations:
  - `AndroidDatabase.kt`
  - `IosDatabase.kt`

#### Data Access Objects (DAOs)

- `CardDao.kt` - Operations for card entities
- `ScanDao.kt` - Operations for scan entities
- `TransactionDao.kt` - Operations for transaction entities
- `Dao.kt` - Common DAO interface

#### Models and Entities

- `Transaction.kt` - Data model for transactions
- `CardEntity.kt` - Room entity for cards
- `ScanEntity.kt` - Room entity for scan events
- `TransactionEntity.kt` - Room entity for transactions

#### Repositories

- `TransactionRepository.kt` - Business logic for transaction operations
- `SettingsRepository.kt` - User settings management

### NFC Implementation

The app uses platform-specific NFC implementations that share a common interface:

#### Common Interface

- `NFCManager.kt` - Expect class with common NFC operations
- `NfcCommandGenerator.kt` - Generates FeliCa card commands

#### Platform-Specific Implementations

- **Android**: `NfcManager.android.kt` and `NfcReader.kt`
- **iOS**: `NFCManager.ios.kt` - Uses CoreNFC for card reading

#### Card Parsing

- `TransactionParser.kt` - Parses raw card data into transaction objects
- `ByteParser.kt` - Low-level binary parsing utilities
- `StationService.kt` - Maps station codes to station names
- `TimestampService.kt` - Converts binary timestamps to DateTime objects

### Dependency Injection

Uses Koin for dependency injection:

- `Module.kt` - Main module with common dependencies
- Platform-specific modules:
  - `PlatformModule.kt` (Android)
  - `PlatformModule.kt` (iOS)

### Settings and Localization

- `Settings.kt` - Settings management with platform-specific implementations
- `Language.kt` - Language selection utilities
- `Localization.kt` - Localization utilities

### Platform Entry Points

- **Android**: `MainActivity.kt` and `MrtApp.kt` (Application class)
- **iOS**: `MainViewController.kt` (UI view controller)

## Important Patterns

1. **Platform-Specific Implementations**:
   - Common interfaces with `expect`/`actual` pattern
   - Platform-specific implementations under `androidMain` and `iosMain`

2. **State Management**:
   - Immutable state objects
   - StateFlow for reactive UI updates
   - Actions for handling user input

3. **Database Access**:
   - Repository pattern abstracting data access
   - Room DAOs for database operations

4. **Dependency Injection**:
   - Koin for service location
   - ViewModels injected with `koinViewModel()`

## Contribution Guidelines

When contributing to this project:

1. Discuss changes in Issues or Discussions before implementation
2. Keep pull requests focused on a single feature or bug fix
3. Avoid unnecessary code formatting changes
4. Maintain code style consistency with the project
5. Add tests for new functionality
6. Ensure compatibility with both Android and iOS platforms

Refer to the [contribution guidelines](/docs/contributions.md) for more details.
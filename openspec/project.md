# Project Context

## Purpose
MRT Buddy is a mobile app for Dhaka MRT (Mass Rapid Transit) transit card users. It allows users to:
- Check the balance of Dhaka MRT transit cards (FeliCa NFC cards) without internet
- View transaction history (up to 19 transactions)
- Calculate fares between stations
- Store card information locally for offline access
- Support multiple languages (English and Bengali)

The app works offline by reading NFC transit cards directly on the device.

## Tech Stack
- **Language**: Kotlin 2.0.21
- **Framework**: Kotlin Multiplatform (KMP) with Compose Multiplatform 1.7.0
- **Platforms**: Android (minSdk 24, targetSdk 35) and iOS
- **UI**: Jetpack Compose / Compose Multiplatform with Material 3
- **Database**: Room 2.7.0 with SQLite (multiplatform)
- **Dependency Injection**: Koin 4.0.0
- **Navigation**: Navigation Compose 2.8.0
- **Settings**: Multiplatform Settings 1.2.0
- **Async**: Kotlinx Coroutines 1.9.0
- **Date/Time**: Kotlinx Datetime 0.6.0
- **Logging**: Napier 2.7.1
- **Build**: Gradle with Kotlin DSL, KSP for annotation processing

## Project Conventions

### Code Style
- Kotlin idioms and conventions
- Compose Multiplatform patterns for UI
- `expect`/`actual` pattern for platform-specific implementations
- Package structure: `net.adhikary.mrtbuddy`
- Screens organized by feature under `ui/screens/`
- Shared components under `ui/components/`

### Architecture Patterns
- **MVI (Model-View-Intent)** for UI state management:
  - Each screen has: `Screen.kt`, `ViewModel.kt`, `State.kt`, `Action.kt`, `Event.kt`
  - Immutable state objects with `StateFlow` for reactive UI updates
  - Actions represent user intents, Events flow from ViewModel to UI
- **Repository Pattern** for data access abstraction
- **DAOs** for Room database operations
- **Service Objects** for parsing and domain logic (e.g., `StationService`, `TimestampService`)

### Testing Strategy
- Unit tests via `kotlin-test` in `commonTest`
- Platform-specific tests as needed
- Run with: `./gradlew :composeApp:testDebugUnitTest` (Android) or `./gradlew :composeApp:allTests`

### Git Workflow
- Main branch: `master`
- Commit message prefixes: `feat:`, `fix:`, `chore:`, `docs:`
- PRs should be focused on a single feature or bug fix
- Discuss changes in Issues or Discussions before implementation
- Avoid unnecessary reformatting in PRs

## Domain Context
- **FeliCa Cards**: NFC transit cards used by Dhaka MRT, read via platform NFC APIs
- **Transaction Parsing**: Binary data from cards parsed to extract balance, timestamp, and station info
- **Station Codes**: Numeric codes mapped to station names via `StationService`
- **Fare Structure**: Defined fare matrix between stations for fare calculation
- **Card IDm**: Unique identifier for each FeliCa card (8 bytes)

## Important Constraints
- **Offline-First**: Core functionality must work without internet
- **NFC Required**: Device must have NFC capability to scan cards
- **Platform NFC APIs**: Android uses `android.nfc`, iOS uses CoreNFC
- **Card Format**: Specific to Dhaka MRT FeliCa card format
- **Transaction Limit**: Cards store up to 19 transactions

## External Dependencies
- **NFC Hardware**: Physical NFC reader on the device
- **No Backend Services**: App is fully self-contained, no external APIs
- **Room Schema**: Database migrations managed via Room schema directory (`$projectDir/schemas`)

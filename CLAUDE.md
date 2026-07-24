<!-- OPENSPEC:START -->
# OpenSpec Instructions

These instructions are for AI assistants working in this project.

Always open `@/openspec/AGENTS.md` when the request:
- Mentions planning or proposals (words like proposal, spec, change, plan)
- Introduces new capabilities, breaking changes, architecture shifts, or big performance/security work
- Sounds ambiguous and you need the authoritative spec before coding

Use `@/openspec/AGENTS.md` to learn:
- How to create and apply change proposals
- Spec format and conventions
- Project structure and guidelines

Keep this managed block so 'openspec update' can refresh the instructions.

<!-- OPENSPEC:END -->

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

For iOS (requires macOS; the old `iosDeployIPhone*` gradle tasks no longer exist):
```bash
# Physical device: signed build, then install + launch via devicectl
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug \
  -destination 'id=<DEVICE_UDID>' -derivedDataPath build/ios-device -allowProvisioningUpdates build
xcrun devicectl device install app --device <DEVICE_UDID> "build/ios-device/Build/Products/Debug-iphoneos/MRT Buddy.app"
xcrun devicectl device process launch --device <DEVICE_UDID> net.adhikary.mrtbuddy
# Find UDIDs with: xcrun xctrace list devices
```

### Debugging the running app (mobile-mcp + adb/devicectl)

The `mobile` MCP server (mobile-mcp, configured project-locally in `~/.claude.json`, needs the phone paired via wireless debugging) lets Claude drive a connected Android device directly — prefer it over raw adb for interaction:

- `mobile_list_available_devices` → get the device id, pass it to every other call
- `mobile_launch_app` / `mobile_terminate_app` with package `net.adhikary.mrtbuddy`
- `mobile_list_elements_on_screen` → Compose view hierarchy with tap coordinates; then `mobile_click_on_screen_at_coordinates`
- `mobile_take_screenshot` / `mobile_save_screenshot` to verify UI states visually
- `mobile_get_crash` / `mobile_list_crashes` after a suspected crash

Fallbacks when mobile-mcp isn't enough:
- `adb` is NOT on PATH; use `~/Library/Android/sdk/platform-tools/adb`. Live logs: `adb logcat --pid=$(adb shell pidof -s net.adhikary.mrtbuddy) -v time` (run it bare/streaming — never pipe through `tail`). Note: the NFC read success path logs nothing; verify reads via the UI (screenshot), not logcat.
- Physical iPhone: mobile-mcp doesn't drive it; build + install + launch with the `xcodebuild`/`devicectl` commands above. NFC scanning can't be automated on either platform — a human must tap the card.

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

### Android Verification (cheapest first)

```bash
# 1. Kotlin compile only. Catches source errors without packaging.
./gradlew :composeApp:compileDebugKotlinAndroid

# 2. Android Lint (built-in AGP linter).
./gradlew :composeApp:lintDebug

# 3. Full debug APK.
./gradlew :composeApp:assembleDebug
```

### Code Quality (ktlint + detekt + Konsist)

```bash
# Style check + auto-fix
./gradlew ktlintCheck
./gradlew ktlintFormat

# Static analysis
./gradlew detekt

# Architecture tests (Konsist)
./gradlew :composeApp:testDebugUnitTest --tests "*ArchitectureTest*"
```

Baselines are checked in and used to accept legacy violations:
- `composeApp/config/ktlint/baseline.xml`
- `composeApp/detekt-baseline.xml`

Regenerate when consciously accepting new violations: `./gradlew ktlintGenerateBaseline detektBaseline`.
Detekt config lives in `config/detekt/detekt.yml`. Ktlint rule overrides live in `.editorconfig`.

### iOS Verification (from Android/CI or headless)

Three levels of iOS build validation, cheapest first:

```bash
# 1. Kotlin/Native compile only (~30s warm). Catches KMP source errors.
./gradlew :composeApp:compileKotlinIosSimulatorArm64

# 2. Framework link (~1m). Produces the .framework Xcode consumes.
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# 3. Full Xcode app build. End-to-end integration check.
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 16 Pro' \
  build CODE_SIGNING_ALLOWED=NO
```

Run #1 after any commonMain/iosMain edit. Run #2 before pushing KMP changes. Run #3 before releases.

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

#### Screens

Each screen lives under `composeApp/src/commonMain/kotlin/net/adhikary/mrtbuddy/ui/screens/` and follows the ScreenName / ScreenNameViewModel / State / Action / Event pattern above. Main tabs: Calculator, Balance, History, More. Additional screens include TransactionList and StationMap.

Common UI components (BalanceCard, TransactionHistoryList, Footer, Icons) live under `ui/components/`.

### Data Layer

Room database with SQLite storage for both platforms. Platform-specific database builders under `androidMain`/`iosMain` use `expect`/`actual`. Entities (`CardEntity`, `ScanEntity`, `TransactionEntity`) and DAOs (`CardDao`, `ScanDao`, `TransactionDao`) live under `data/` in commonMain. Business logic is abstracted through repositories (`TransactionRepository`, `SettingsRepository`).

### NFC Implementation

Platform code handles only session lifecycle and raw block I/O; all orchestration and parsing is shared:

#### Shared (commonMain, `nfc/`)

- `NFCManager.kt` - Expect class with common NFC operations (session lifecycle, state flows)
- `CardTransceiver.kt` - Seam interface: card IDm + `readBlocks()` returning status flags and raw 16-byte blocks
- `FelicaReader.kt` - Shared read orchestration (two 10-block windows, status-flag checks, validity filter, partial results on I/O error)
- `FelicaFrameDecoder.kt` - Pure decoder for raw FeliCa response frames
- `NfcCommandGenerator.kt` - Generates FeliCa card commands

#### Platform adapters

- **Android**: `NfcManager.android.kt` (session) and `NfcFTransceiver.kt` (wraps `NfcF.transceive`)
- **iOS**: `NFCManager.ios.kt` (session) and `FelicaTagTransceiver.kt` (wraps CoreNFC `readWithoutEncryption`)

#### Card Parsing

- `TransactionParser.kt` - Parses raw card data into transaction objects
- `ByteParser.kt` - Low-level binary parsing utilities
- `StationService.kt` - Maps station codes to station names
- `TimestampService.kt` - Converts binary timestamps to DateTime objects (pass explicit `baseYear` in tests for determinism)

#### Testing FeliCa features without hardware

The whole read-parse pipeline runs on JVM: build card data with `FelicaFixtures` (commonTest, `nfc/`) and drive `FelicaReader` with `FakeCardTransceiver` — this exercises the identical production code path. Run with `./gradlew :composeApp:testDebugUnitTest`. The `felica` skill (`.claude/skills/felica/`) documents the full byte layouts. Never hand-roll fixture byte arrays in tests; extend `FelicaFixtures`.

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

## Gotchas

- **AGP 9.0+ KMP workaround**: `gradle.properties` sets `android.builtInKotlin=false` and `android.newDsl=false` because AGP 9.0 dropped compatibility between `com.android.application` and the Kotlin Multiplatform plugin. Removing these flags will break the build. Proper fix (not yet done): migrate to `com.android.kotlin.multiplatform.library`, but that's a library plugin and the app module needs a different migration path.
- **compileSdk 37 warning**: `android.suppressUnsupportedCompileSdk=37` silences the "not tested" warning from AGP for SDK 37. Remove once AGP officially lists 37 as supported.
- **Version bumps**: When releasing, update BOTH `composeApp/build.gradle.kts` (`versionCode`/`versionName`) AND `iosApp/iosApp/Info.plist` (`CFBundleVersion`/`CFBundleShortVersionString`). They are not linked.
- **JogAmp repository**: `settings.gradle.kts` declares `https://jogamp.org/deployment/maven` (scoped to `org.jogamp.*`) because `compose-webview-multiplatform` transitively needs JOGL artifacts that are absent from Maven Central; without it `:composeApp:lintDebug` fails to resolve `debugLintChecksClasspath`.

## Contribution Guidelines

When contributing to this project:

1. Discuss changes in Issues or Discussions before implementation
2. Keep pull requests focused on a single feature or bug fix
3. Avoid unnecessary code formatting changes
4. Maintain code style consistency with the project
5. Add tests for new functionality
6. Ensure compatibility with both Android and iOS platforms

Refer to the [contribution guidelines](/docs/contributions.md) for more details.
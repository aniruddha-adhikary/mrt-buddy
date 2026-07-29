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

This file gives guidance to Claude Code (claude.ai/code) for work in this repository.

## Project Overview

MRT Buddy is a Kotlin Multiplatform (KMP) app built with Compose Multiplatform for Android and iOS. It lets users:
- Check the balance of Dhaka MRT transit cards (FeliCa cards) with NFC
- View transaction history (up to 19 transactions)
- Calculate fares between stations
- Store card information locally, with no internet connection
- Use the app in English and Bengali

Domain vocabulary is canonical in `CONTEXT.md` — use its terms. For example: "Transaction", not "journey". The Fixed Header is the type discriminator. The `Transaction.transactionType` field is a misnomer, and a rename is planned.

## Build System and Project Structure

- Kotlin Multiplatform (KMP) with Gradle (Kotlin DSL)
- Compose Multiplatform for UI on both platforms
- Koin for dependency injection
- Room database for local storage
- NFC card reading with platform-specific adapters

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

The `mobile` MCP server (mobile-mcp) lets Claude drive a connected Android device directly. It is configured project-locally in `~/.claude.json` and needs the phone paired via wireless debugging. Prefer it over raw adb for interaction:

- `mobile_list_available_devices` → get the device id, then pass the id to every other call
- `mobile_launch_app` / `mobile_terminate_app` with package `net.adhikary.mrtbuddy`
- `mobile_list_elements_on_screen` → Compose view hierarchy with tap coordinates, then `mobile_click_on_screen_at_coordinates`
- `mobile_take_screenshot` / `mobile_save_screenshot` to verify UI states visually
- `mobile_get_crash` / `mobile_list_crashes` after a suspected crash

Fallbacks when mobile-mcp is not enough:
- `adb` is NOT on PATH. Use `~/Library/Android/sdk/platform-tools/adb`. Live logs: `adb logcat --pid=$(adb shell pidof -s net.adhikary.mrtbuddy) -v time`. Run it bare and streaming — never pipe it through `tail`. Note: the NFC read success path logs nothing. Verify reads in the UI (screenshot), not in logcat.
- Physical iPhone: mobile-mcp does not drive it. Build, install, and launch with the `xcodebuild`/`devicectl` commands above. NFC scanning cannot be automated on either platform — a human must tap the card.

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

Note: To sign release builds, set these environment variables:
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

Baselines are checked in. They accept legacy violations only:
- `composeApp/config/ktlint/baseline.xml`
- `composeApp/detekt-baseline.xml`

Regenerate a baseline only when you consciously accept new violations: `./gradlew ktlintGenerateBaseline detektBaseline`.
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

Run #1 after any commonMain/iosMain edit. Run #2 before you push KMP changes. Run #3 before releases.

## Code Structure and Architecture

### Overall Architecture

The app follows an MVI (Model-View-Intent) pattern for UI state management:
- **Model**: State classes (for example `MainScreenState`)
- **View**: Compose UI components (screens and composables)
- **Intent**: User actions as Action classes (for example `MainScreenAction`)

Each screen typically has:
- A View (`ScreenName.kt`) - UI components
- A ViewModel (`ScreenNameViewModel.kt`) - Business logic
- A State class (`ScreenNameState.kt`) - UI state
- An Action class (`ScreenNameAction.kt`) - User actions
- An Event class (`ScreenNameEvent.kt`) - Events from ViewModel to UI

### UI Layer

The UI uses Compose Multiplatform with this structure:

#### Navigation

- `Screen.kt` - Sealed class that defines all screen routes
- `MainScreen.kt` manages navigation with a `NavHost` and composables
- Bottom navigation bar with main tabs: Calculator, Balance, History, More

#### Screens

Each screen lives under `composeApp/src/commonMain/kotlin/net/adhikary/mrtbuddy/ui/screens/` and follows the ScreenName / ScreenNameViewModel / State / Action / Event pattern above. Main tabs: Calculator, Balance, History, More. Additional screens include TransactionList, StationMap, and Developer (debug builds only).

Common UI components (BalanceCard, TransactionHistoryList, Footer, Icons) live under `ui/components/`.

### Data Layer

Room database with SQLite storage on both platforms. Platform-specific database builders under `androidMain`/`iosMain` use `expect`/`actual`. Entities (`CardEntity`, `ScanEntity`, `TransactionEntity`) and DAOs (`CardDao`, `ScanDao`, `TransactionDao`) live under `data/` in commonMain. Repositories (`TransactionRepository`, `SettingsRepository`) abstract the business logic.

### NFC Implementation

Platform code handles only session lifecycle and raw block I/O. All orchestration and parsing is shared:

#### Shared (commonMain, `nfc/`)

- `NFCManager.kt` - Expect class with common NFC operations (session lifecycle, state flows)
- `CardTransceiver.kt` - Seam interface: card IDm + `readBlocks()` that returns status flags and raw 16-byte blocks
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
- `TimestampService.kt` - Converts binary timestamps to DateTime objects (pass an explicit `baseYear` in tests for determinism)

#### Testing FeliCa features without hardware

The whole read-parse pipeline runs on the JVM. Build card data with `FelicaFixtures` (commonTest, `nfc/`) and drive `FelicaReader` with `FakeCardTransceiver`. This exercises the identical production code path. Run with `./gradlew :composeApp:testDebugUnitTest`. The `felica` skill (`.claude/skills/felica/`) documents the full byte layouts. Never hand-roll fixture byte arrays in tests. Extend `FelicaFixtures` instead.

In-app: debug builds have More → Developer options → "Scan demo card" (fixed IDm `D3 A0 00 00 00 00 00 01`). It drives the production pipeline end-to-end, including Room persistence.

### Dependency Injection

Koin provides dependency injection:

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
   - Common interfaces with the `expect`/`actual` pattern
   - Platform-specific implementations under `androidMain` and `iosMain`

2. **State Management**:
   - Immutable state objects
   - StateFlow for reactive UI updates
   - Actions for user input

3. **Database Access**:
   - Repository pattern that abstracts data access
   - Room DAOs for database operations

4. **Dependency Injection**:
   - Koin for service location
   - ViewModels injected with `koinViewModel()`

## Gotchas

- **AGP 9.0+ KMP workaround**: `gradle.properties` sets `android.builtInKotlin=false` and `android.newDsl=false` because AGP 9.0 dropped compatibility between `com.android.application` and the Kotlin Multiplatform plugin. If you remove these flags, the build breaks. The proper fix (not yet done) is a migration to `com.android.kotlin.multiplatform.library`. That is a library plugin, so the app module needs a different migration path.
- **compileSdk 37 warning**: `android.suppressUnsupportedCompileSdk=37` silences the "not tested" warning from AGP for SDK 37. Remove it after AGP officially lists 37 as supported.
- **Version bumps**: For a release, update BOTH `composeApp/build.gradle.kts` (`versionCode`/`versionName`) AND `iosApp/iosApp/Info.plist` (`CFBundleVersion`/`CFBundleShortVersionString`). They are not linked.
- **JogAmp repository**: `settings.gradle.kts` declares `https://jogamp.org/deployment/maven` (scoped to `org.jogamp.*`) because `compose-webview-multiplatform` transitively needs JOGL artifacts that Maven Central does not have. Without it, `:composeApp:lintDebug` fails to resolve `debugLintChecksClasspath`.
- **Wireless adb drops**: MIUI turns Wireless debugging off aggressively. If `adb devices` is empty, ask the user to toggle it on again on the phone (the pairing survives). Rediscover with `adb mdns services`.
- **KSP version warning**: builds print "ksp-… is too old for kotlin-…" many times. This is known noise until a KSP bump. It is not a failure. Do not chase it mid-task.
- **OpenSpec sequencing**: archive a shipped change before you draft a new change that MODIFIES the same requirements. Deltas are written against current spec text.

## Contribution Guidelines

When contributing to this project:

1. Discuss changes in Issues or Discussions before implementation
2. Keep pull requests focused on a single feature or bug fix
3. Avoid unnecessary code formatting changes
4. Maintain code style consistency with the project
5. Add tests for new functionality
6. Ensure compatibility with both Android and iOS platforms

Refer to the [contribution guidelines](/docs/contributions.md) for more details.

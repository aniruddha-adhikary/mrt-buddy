# Design: demo card scan + dump capture

## Context
Post `refactor-nfc-testability`, the read pipeline is JVM-testable, but app-level verification (balance screen, persistence, History) still needs hardware, and fixture dumps must be hand-built. `NFCManager` is created by a `getNFCManager()` composable (not Koin), `App.kt:40-52` collects its two SharedFlows into `MainScreenAction`s, and persistence happens automatically in `MainScreenViewModel` when auto-save is on. `isDebug` exists as `expect val` on both platforms. `CsvFileWriter` (Koin factory) is a generic streaming writer with `share(mimeType)`. kotlinx-serialization is NOT in the project.

## Goals / Non-Goals
- Goals: scan a demo card end-to-end (UI + Room) with no hardware on any platform incl. emulators/simulators; capture real reads as fixture-ready dumps; close the two iOS emission gaps.
- Non-Goals: injecting/refactoring `NFCManager` itself; adding kotlinx-serialization; release-build visibility; automating physical NFC taps.

## Decisions

### Demo feed beside NFCManager, not inside it
`DemoCardService` is a plain commonMain Koin `single` exposing its own `cardState`/`cardReadResults` SharedFlows and a `scanDemoCard()` that runs `FelicaReader(DemoCardTransceiver())` (production path, fixed `baseYear`) and emits `Reading` → result → `Balance`. `App.kt` adds two `LaunchedEffect` collectors gated by `isDebug`, dispatching the same `MainScreenAction`s. Rejected: interface-extracting/Koin-injecting `NFCManager` — far larger surface (expect/actual churn, `@Composable startScan`) for zero additional user value here; can still be done later.

### Demo data is production-code bytes with a recognizable IDm
`DemoCards` (commonMain, `nfc/demo/`) holds raw 16-byte blocks (metro commutes, Hatirjheel bus start/end, balance update, all post-2020) and a constant IDm `D3 A0 00 00 00 00 00 01` so repeated demo scans update one identifiable "demo" card that the user can delete. Blocks are built by a tiny local builder — commonTest's `FelicaFixtures` stays test-only; duplication is ~30 lines and keeps test code out of the app.

### Capture via wrapper + shared mutable recorder object
`RecordingCardTransceiver(delegate)` copies each `readBlocks` window into `NfcDumpRecorder` (commonMain `object`: `enabled` flag, last-session window list, IDm anonymized to zeros at record time — the real IDm never enters the buffer). Platform `NFCManager`s wrap their transceiver unconditionally; the wrapper no-ops when disabled. Rationale for a plain object over DI: both platform `NFCManager`s live outside Koin; threading a singleton into them would force the bigger refactor we just rejected. `SettingsRepository` persists the toggle (`nfc_dump_capture`, default false) and pushes it into `NfcDumpRecorder.enabled` on init and on change.

### Dump format: line-based text, fixture-ready
No serialization dependency. Header (`# MRT Buddy NFC dump v1`, platform, anonymized idm) then per window: `window serviceCode=0x220F start=0 count=10 status=00 00` followed by `block NN: <32 hex chars spaced>` lines. Shared through `CsvFileWriter.createFile/appendLine/close/share("text/plain")` from `MoreScreenViewModel` — same flow CSV export uses.

### iOS alignment
`NFCManager.ios.kt`: emit `CardReadResult(idm, transactions)` unconditionally after a completed read (empty included) before the state emission, matching `NfcManager.android.kt`; on `connectToTag` error emit `CardState.Error` and `invalidateSessionWithErrorMessage`. Behavior change is iOS-only and intentional.

### Developer UI is a dedicated screen, not a More-screen section (rev 2, maintainer feedback)
The three developer rows move to a `DeveloperScreen` with its own route in `Screen.kt`/the `MainScreen` NavHost (same pattern as StationMap), reached via one plain row at the very bottom of More (below About, `isDebug`-gated). Demo scan shows a snackbar with the scanned balance on the Developer screen; "Share last dump" with no dump shows a snackbar instead of silently no-oping. Rejected: tap-version-7-times unlock — needless hidden state when the whole surface only exists in debug builds.

## Risks / Trade-offs
- Demo scans write to the real Room DB → mitigated by the fixed, recognizable demo IDm and debug-only gating; exercising real persistence is partly the point.
- `NfcDumpRecorder` is global mutable state → confined to debug diagnostics, single writer (read coroutine), read by share action after session end.
- New strings added to base `strings.xml` only (English); Bengali falls back to default for dev-only UI.
- Demo UI on iOS debug binaries: `isDebug` uses `Platform.isDebugBinary` — verified actual exists (`Platform.ios.kt:13`).

## Migration Plan
Purely additive + two iOS emission fixes; straight revert if needed. No schema changes.

## Open Questions
- None blocking.

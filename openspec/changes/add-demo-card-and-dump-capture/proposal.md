# Change: Add debug demo-card scan, raw dump capture, and iOS emission alignment

## Why
Verifying card-reading features still requires a physical card for anything UI-facing, and building real-card test fixtures requires hand-transcribing bytes. Additionally, two platform asymmetries survive from before the seam refactor: iOS never emits `cardReadResults` for an empty read (the scan is not persisted) and a tag connect failure is silent (`println` only, session left open).

## What Changes
- **Demo card scan (debug builds only)**: a "Developer" section on the More screen with a "Scan demo card" action. It drives the production `FelicaReader` over a fixture-backed `CardTransceiver` (`DemoCards` block bytes in commonMain) and feeds the resulting `CardReadResult` + `CardState` into the exact same `MainScreenAction` path as a real NFC read — rendering the balance screen and persisting via auto-save like a real scan. Invisible in release builds (`isDebug` gate).
- **Raw read capture (debug builds only)**: a persisted "Capture NFC dumps" toggle plus "Share last dump". When enabled, a recording wrapper around the platform transceiver captures each read window (service code, block range, status flags, raw block hex) in memory; the share action writes a fixture-ready text dump via the existing `CsvFileWriter` share flow. The card IDm is anonymized in dumps by default.
- **iOS alignment fixes**: always emit `CardReadResult` (real IDm, possibly empty transactions) like Android does, so empty reads are persisted; surface tag connect failures as `CardState.Error` and invalidate the session with an error message.

## Impact
- Affected specs: new capability `nfc-diagnostics`; `nfc-card-reading` (Card Read Results Emission, FeliCa Card Reading (iOS))
- Affected code:
  - commonMain: new `nfc/demo/DemoCards.kt`, `nfc/demo/DemoCardService.kt`, `nfc/RecordingCardTransceiver.kt`, `nfc/NfcDumpRecorder.kt`; `App.kt` (debug-gated collectors), `di/Module.kt` (one `single`), `repository/SettingsRepository.kt` (one boolean), `ui/screens/more/*` (Developer section), compose string resources
  - androidMain/iosMain: `NfcManager.android.kt` / `NFCManager.ios.kt` wrap their transceiver in the recorder; iOS emission fixes
  - commonTest: tests for demo data validity, recorder behavior, dump formatting
- No release-build behavior change except the two intentional iOS fixes.

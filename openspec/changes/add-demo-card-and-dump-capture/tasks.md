# Tasks: add-demo-card-and-dump-capture

## 1. Demo card data + service (commonMain, TDD)
- [x] 1.1 `DemoCards`: constant IDm `D3 A0 00 00 00 00 00 01` + raw 16-byte blocks (≥4 metro commutes, Hatirjheel bus start+end, balance update; all post-2020; descending timestamps; realistic balances). Test: every block is 16 bytes and parses via `FelicaReader` + fake transceiver with fixed `baseYear` into the expected transactions (types, stations, latest balance first)
- [x] 1.2 `DemoCardService` (Koin `single` in `appModule`): own `cardState`/`cardReadResults` SharedFlows (replay 1), `suspend scanDemoCard()` emitting Reading → CardReadResult → Balance via `FelicaReader(DemoCardTransceiver)`. Test with `runTest`: emission order, result contents, repeat scans
- [x] 1.3 `App.kt`: two `isDebug`-gated `LaunchedEffect` collectors dispatching `UpdateCardReadResult`/`UpdateCardState` (mirror existing blocks at App.kt:40-52)

## 2. Developer section UI (More screen)
- [x] 2.1 String resources (base `strings.xml` only): developer section title, scan demo card, capture dumps title+description, share last dump
- [x] 2.2 `MoreScreen`: `isDebug`-gated `SectionHeader` + rows following existing `RoundedButton`/auto-save-toggle patterns; wire actions through `MoreScreenAction`/`MoreScreenViewModel`/`MoreScreenState` (add `nfcDumpCaptureEnabled` field)
- [x] 2.3 "Scan demo card" invokes `DemoCardService.scanDemoCard()` (viewModel scope) — user lands on Balance tab seeing demo data

## 3. Dump capture (TDD for commonMain parts)
- [x] 3.1 `NfcDumpRecorder` (object): `enabled`, `startSession(platform)`, `record(window)`, `lastDumpText(): String?` producing the v1 text format with zeroed IDm. Tests: format exactness, anonymization, disabled → no records, new session clears old
- [x] 3.2 `RecordingCardTransceiver(delegate)`: passthrough + record-when-enabled. Tests with `FakeCardTransceiver`
- [x] 3.3 `SettingsRepository`: `nfcDumpCaptureEnabled` StateFlow + setter (key `nfc_dump_capture`, default false) following `autoSaveEnabled` pattern; init + setter push into `NfcDumpRecorder.enabled`
- [x] 3.4 Wrap platform transceivers: `NfcManager.android.kt` and `NFCManager.ios.kt` construct `FelicaReader(RecordingCardTransceiver(...))` and call `startSession`
- [x] 3.5 "Share last dump" in `MoreScreenViewModel` via `CsvFileWriter` (`text/plain`); disabled/no-op message when no dump exists

## 4. iOS emission alignment
- [x] 4.1 Always emit `CardReadResult(idm, transactions)` after a completed read (empty included), before state emission — matches Android
- [x] 4.2 `connectToTag` failure: emit `CardState.Error` + `invalidateSessionWithErrorMessage` (no bare `println`/open session)

## 5. Verification
- [x] 5.1 Ladder: `testDebugUnitTest` (58 tests), `compileDebugKotlinAndroid`, `lintDebug`, `compileKotlinIosSimulatorArm64`, `linkDebugFrameworkIosSimulatorArm64`, `ktlintCheck`, `detekt` — all green, run independently of the implementing agent
- [x] 5.2 kmp-platform-parity audit: no expect/actual issues, no platform types in commonMain, Koin resolvable both platforms; four findings (empty-read message drift, missing iOS connect-failure sentinel, recorder enabled without isDebug guard, dead toggle-row onClick) — all fixed and re-verified (58 tests)
- [x] 5.3 Live check via mobile-mcp on the Xiaomi (debug build), 2026-07-29: Developer section renders (Scan demo card / Capture NFC dumps / Share last dump); demo scan shows ৳340 with metro + Hatirjheel journeys on the Balance tab (screenshot captured); demo card persisted and visible in History as a new card alongside the user's real cards
- [x] 5.4 Release-build spot check (code-review level): demo/capture UI and collectors all inside single `isDebug` gates (App.kt, MoreScreen.kt); actions have no other entry points; recorder now hard-gated by `isDebug` in SettingsRepository — confirmed by parity audit
- [ ] 5.5 Maintainer (physical card, later): enable capture, scan real card, share dump, confirm IDm anonymized

## 6. Developer UI rework (rev 2, maintainer feedback)
- [x] 6.1 New `DeveloperScreen` + route in `Screen.kt` and the `MainScreen` NavHost (StationMap pattern): hosts the three developer rows, top bar with back navigation, `SnackbarHost`
- [x] 6.2 More screen: remove the Developer section; add one plain `isDebug`-gated "Developer options" row at the very bottom (below About section) navigating to the new screen; new string key
- [x] 6.3 Demo scan feedback: after `scanDemoCard()` completes, show snackbar "Demo card scanned — ৳<balance>. See the Balance tab." on DeveloperScreen (event via the screen's MVI Event flow)
- [x] 6.4 "Share last dump" with no dump: snackbar "No dump captured yet — enable capture and scan a card" instead of silent no-op
- [x] 6.5 Re-run ladder (tests, both compiles, ktlint, detekt) — green
## 7. PR #145 review findings (augmentcode bot — both verified real)
- [x] 7.1 `NfcDumpRecorder`: defer clearing to the first `record()` of a new session so capture-off scans and failed reads no longer wipe the last dump; platform label must stay with the recorded session (TDD)
- [x] 7.2 Guard persistence against blank IDm: `TransactionRepository.saveCardReadResult` returns early when `result.idm` is blank (pre-existing Android exposure via failure sentinel `CardReadResult("", ...)`; widened by iOS alignment). TDD with fake DAOs
- [x] 7.3 Re-run ladder green

- [x] 6.6 Live re-check via mobile-mcp on the Xiaomi, 2026-07-29: More shows only the single bottom row; DeveloperScreen opens with title flush under the status bar (double-inset regression found live and fixed); share-without-dump snackbar visible above the tab bar (hidden-snackbar bug found live, root-caused to missing outer paddingValues, fixed); demo-scan snackbar shows "Demo card scanned — ৳340"; Balance header reads "Recent Transactions"; StationMap bottom edge clear of the system bar

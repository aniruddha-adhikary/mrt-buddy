# Tasks: refactor-nfc-testability

## 1. Deterministic timestamps (additive, no behavior change)
- [x] 1.1 Add `baseYear` parameter to `TimestampService.decodeTimestamp` with `currentBaseYear()` default; expose `currentBaseYear()`
- [x] 1.2 Thread optional `baseYear` through `TransactionParser.parseTransactionBlock` and `parseTransactionResponse` with defaults
- [x] 1.3 Add `TimestampServiceTest` in commonTest: bit-field extraction, base-year math, invalid month/day clamping — all with fixed `baseYear`

## 2. Synthetic fixtures + parser safety net (pre-refactor)
- [x] 2.1 Create `FelicaFixtures` in commonTest: block builder (header, timestamp, stations, balance) and frame builder (LEN/code/IDm header, status flags, blocks); named fixtures for metro commute, Hatirjheel bus start/end, MRT + Rapid balance updates, unknown header, unknown station, pre-2020 block
- [x] 2.2 Add `ByteParserTest`: int16/int24 LE, int24 BE, byte extraction, hex string round-trip
- [x] 2.3 Add `NfcCommandGeneratorTest`: command layout (LEN byte, cmd 0x06, IDm placement, service code 0x220F little-endian, block list encoding) for both read windows
- [x] 2.4 Add `TransactionParserTest`: block field extraction, 16-byte size guard, frame parsing (short frame, error status flags, incomplete blocks, valid multi-block), pre-2020 filtering
- [x] 2.5 Add `TransactionTypeTest`: `fromHeader()` mapping for all five known headers + unknown → `CommuteUnknown` (closes verification gap of add-hatirjheel-bus-support tasks 4.3)
- [x] 2.6 Add `StationServiceTest`: known metro + Hatirjheel codes, unknown code text format (closes add-hatirjheel-bus-support task 4.4)
- [x] 2.7 Run `./gradlew :composeApp:testDebugUnitTest` — all green before touching production structure

## 3. Shared seam + reader (commonMain)
- [x] 3.1 Add `CardTransceiver` interface (`idm`, `readBlocks`) and `FelicaReadResult` (status flags, blocks)
- [x] 3.2 Extract pure frame decoding (frame → `FelicaReadResult`) into commonMain, reusing it from `parseTransactionResponse`
- [x] 3.3 Add `FelicaReader` orchestrating both block windows with status-flag checks, parsing, validity filter, partial-result-on-error semantics
- [x] 3.4 Add `FelicaReaderTest` with `FakeCardTransceiver`: happy path (both windows), error status flags in one window, I/O exception after first window → partial results, empty card
- [x] 3.5 Run `./gradlew :composeApp:testDebugUnitTest`

## 4. Android adapter
- [x] 4.1 Add `NfcFTransceiver` (androidMain) wrapping `NfcF`: builds command via `NfcCommandGenerator`, `transceive()`, decodes frame via shared function
- [x] 4.2 Rewire `NfcManager.android.kt` `readFelicaCard` through `FelicaReader`; delete `NfcReader.kt`
- [x] 4.3 Run `./gradlew :composeApp:compileDebugKotlinAndroid && ./gradlew :composeApp:testDebugUnitTest && ./gradlew :composeApp:lintDebug` (compile + tests green; `lintDebug` blocked by a pre-existing environmental failure resolving `org.jogamp.jogl` in `debugLintChecksClasspath`, unrelated to this change)

## 5. iOS adapter
- [x] 5.1 Add `FelicaTagTransceiver` (iosMain) wrapping `NFCFeliCaTagProtocol.readWithoutEncryptionWithServiceCodeList` via `suspendCancellableCoroutine`
- [x] 5.2 Rewire `NFCManager.ios.kt` tag-detection delegate through `FelicaReader` (status flags + validity filter now honored); keep session invalidation messages
- [x] 5.3 Run `./gradlew :composeApp:compileKotlinIosSimulatorArm64 && ./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64`

## 6. Verification & docs
- [x] 6.1 Full ladder: `ktlintCheck`, `detekt`, `:composeApp:testDebugUnitTest`, `:composeApp:lintDebug`, `:composeApp:assembleDebug` (lintDebug required adding the JogAmp Maven repo to `settings.gradle.kts` — pre-existing resolution failure for `compose-webview-multiplatform`'s transitive JOGL deps)
- [x] 6.2 kmp-platform-parity audit: no orphaned expects, no missing actuals, no signature mismatches. Fixed the one real finding (malformed non-16-byte block would crash the iOS read coroutine and hang the NFC sheet): `parseValidTransactions` now skips malformed blocks (test-first), iOS delegate gained an outer catch + `finally { invalidateSession() }` mirroring Android, and `NfcFTransceiver` moved blocking `transceive` onto `Dispatchers.IO`. Pre-existing asymmetries left for follow-up: iOS empty-result path doesn't emit `cardReadResults`; iOS connect failure is silent
- [x] 6.3 Update CLAUDE.md: stale "ArchitectureTest blocked" note removed; NFC architecture section rewritten (seam + adapters); "Testing FeliCa features without hardware" section added; JogAmp repo gotcha documented
- [x] 6.4 On-device verification by maintainer (physical card): Android read unchanged, iOS read still works and now filters invalid entries
  - [x] Android: verified 2026-07-24 on Xiaomi 24117RN76O over wireless adb — real card read through the new `FelicaReader` path; balance and transaction history correct, no exceptions in logcat
  - [x] iOS: verified 2026-07-24 on Ani's iPhone (signed Debug build installed via devicectl) — card read works through the shared reader; maintainer confirmed

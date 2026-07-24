# Change: Refactor NFC layer for hardware-free testability

## Why
The FeliCa read pipeline is currently untestable without a physical card: read orchestration is duplicated per platform inside `NFCManager`, Android-only `NfcReader` depends on the concrete `android.nfc.tech.NfcF` class, and `TimestampService` derives the century base year from the wall clock. The duplication has already produced real behavioral drift — iOS ignores FeliCa status flags and skips the pre-2020 transaction validity filter that Android applies.

## What Changes
- Introduce a `CardTransceiver` seam in commonMain: a small interface exposing the card IDm and a `readBlocks(serviceCode, startBlock, count)` operation returning status flags + raw 16-byte blocks.
- Add a shared `FelicaReader` in commonMain that orchestrates the two 10-block reads, checks status flags, parses blocks via `TransactionParser`, and applies the validity filter — one code path for both platforms.
- Android: replace `NfcReader` with an `NfcFTransceiver` adapter wrapping `NfcF.transceive()`; frame decoding (status flags at bytes 10–11, block extraction from byte 13) becomes a pure commonMain function.
- iOS: add a `FelicaTagTransceiver` adapter wrapping `readWithoutEncryptionWithServiceCodeList` (same CoreNFC API as today — no radio-level change); `NFCManager.ios.kt` routes through the shared `FelicaReader`, gaining status-flag checking and validity filtering (behavior alignment with Android).
- Inject the timestamp base year: `TimestampService.decodeTimestamp(value, baseYear = currentBaseYear())`, threaded through `TransactionParser` with defaults preserving current behavior.
- Add commonTest coverage: synthetic FeliCa fixtures (frames + blocks for metro/bus/balance-update/unknown headers, unknown stations, pre-2020 entries) exercising parsers and `FelicaReader` via a `FakeCardTransceiver`. Also covers the unverified detection scenarios (unknown header → `CommuteUnknown`, unknown station text) from the in-flight `add-hatirjheel-bus-support` change.

## Impact
- Affected specs: `nfc-card-reading`, `card-data-parsing`
- Affected code:
  - `composeApp/src/commonMain/kotlin/net/adhikary/mrtbuddy/nfc/` — new `CardTransceiver.kt`, `FelicaReader.kt`; `parser/TransactionParser.kt`, `service/TimestampService.kt` modified
  - `composeApp/src/androidMain/kotlin/net/adhikary/mrtbuddy/nfc/` — `NfcReader.kt` removed, `NfcFTransceiver.kt` added, `NfcManager.android.kt` slimmed
  - `composeApp/src/iosMain/kotlin/net/adhikary/mrtbuddy/nfc/` — `FelicaTagTransceiver.kt` added, `NFCManager.ios.kt` slimmed
  - `composeApp/src/commonTest/` — new fixtures and test suites
- **Behavior change (intentional, iOS only)**: iOS reads now honor FeliCa status flags, filter pre-2020 transactions, and return partial results when the second block read fails (matching Android), instead of showing unfiltered blocks or discarding everything.
- Deferred to a follow-up change: demo/fixture card mode behind Koin-injected `NFCManager` interface, and an in-app raw-dump capture toggle for building real-card fixtures.

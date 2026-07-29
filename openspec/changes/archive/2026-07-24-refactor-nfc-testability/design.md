# Design: NFC testability refactor

## Context
`NFCManager` (expect/actual) currently conflates platform session lifecycle, byte I/O, and parsing/state emission. Android parses full FeliCa response frames (`TransactionParser.parseTransactionResponse`, status flags at bytes 10–11, blocks from byte 13); iOS receives per-block `NSData` from CoreNFC's `readWithoutEncryptionWithServiceCodeList` and inlines `parseTransactionBlock` calls in the session delegate, skipping status-flag checks and the pre-2020 validity filter. No behavioral tests exist; nothing in the pipeline can run on JVM without hardware.

## Goals / Non-Goals
- Goals: one shared read-orchestration path; JVM-testable end-to-end from fake byte responses to `CardReadResult`; deterministic timestamp decoding; zero radio-level protocol changes on either platform.
- Non-Goals: demo/fixture card mode in the app UI; Koin injection of `NFCManager`; in-app dump capture; changing which blocks/services are read; iOS `sendFeliCaCommand` migration.

## Decisions

### Seam level: block read, not raw transceive
`CardTransceiver` exposes `suspend fun readBlocks(serviceCode: Int, startBlock: Int, count: Int): FelicaReadResult` (status flags + `List<ByteArray>` of 16-byte blocks) plus `val idm: ByteArray`.

- Alternative considered: raw `transceive(command: ByteArray): ByteArray` frame-level seam. Rejected because iOS would have to either migrate to `sendFeliCaCommand` (a radio-level behavior change we cannot verify without a physical card in this change) or absurdly parse command packets inside the adapter to call `readWithoutEncryption`. The block-level seam lets each platform keep its exact current CoreNFC/NfcF API usage.
- Android's frame decoding (LEN/response-code/IDm header skip, status flags, block splitting) moves to a pure commonMain function used by the Android adapter, so it stays unit-testable with recorded raw frames.

### Shared orchestration with partial-result semantics
`FelicaReader.readTransactionHistory()` performs the two 10-block reads (blocks 0–9, 10–19), skips windows whose status flags are non-zero, parses and validity-filters blocks, and returns what it collected. On transceiver I/O failure it returns transactions gathered so far — this matches Android's current `IOException` handling (a card yanked mid-read still shows the first window) and is adopted on iOS as intentional alignment.

### Deterministic timestamps via parameter injection
`TimestampService.decodeTimestamp(value: Int, baseYear: Int = currentBaseYear())`; `currentBaseYear()` keeps the existing `Clock.System` derivation as the default. `TransactionParser.parseTransactionBlock`/`parseTransactionResponse` accept an optional `baseYear` and default it, so production call sites are unchanged and tests pass a fixed year. Rejected alternative: injecting a `Clock` object — heavier, and the only consumed value is the century.

### Fixtures are synthetic in this change
Fixture builders in commonTest construct frames/blocks from the documented layout (header bytes 0–3, int24BE timestamp at 4, stations at 8/10, int24LE balance at 11) including all known `fixedHeader` values from `Transaction.kt` (metro, Hatirjheel bus start/end, MRT/Rapid balance updates). Real anonymized dumps get a capture path in a follow-up change; the fixture format (full frames + block lists) is chosen so captured dumps can drop in later.

## Risks / Trade-offs
- iOS behavior changes (status flags honored, pre-2020 filtered, partial results) cannot be hardware-verified in this change → mitigations: identical CoreNFC calls as today; on-device verification task assigned to maintainer before release.
- `parseTransactionResponse` signature gains an optional parameter → source-compatible; no external callers beyond the reader and tests.
- Kotlin/Native callback-to-coroutine bridging in the iOS adapter (`suspendCancellableCoroutine` around `readWithoutEncryptionWithServiceCodeList`) → verified via `compileKotlinIosSimulatorArm64` + `linkDebugFrameworkIosSimulatorArm64`.

## Migration Plan
Pure additive steps first (base-year param, fixtures, parser tests), then the seam + shared reader with tests, then platform adapters one at a time, each gated on the verification ladder. Rollback is a straight git revert; no schema or persisted-data changes.

## Open Questions
- None blocking. Follow-up change will decide how demo mode surfaces in UI and how captured dumps are anonymized/stored.

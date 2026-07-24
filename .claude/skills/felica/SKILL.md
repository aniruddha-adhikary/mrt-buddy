---
name: felica
description: FeliCa/MRT Pass card protocol knowledge for this app — command and response byte layouts, transaction block anatomy, known headers, and how to test card-reading features without physical hardware (fixtures + FakeCardTransceiver). Load before any task touching nfc/, parsers, stations, or card data.
---

# FeliCa card reading in MRT Buddy

Dhaka MRT Pass / Rapid Pass cards are Sony FeliCa (NFC-F). The app reads the transaction-history service and parses 16-byte blocks. All parsing is pure Kotlin in commonMain — no hardware needed to test it.

## Read command (built by `NfcCommandGenerator`, commonMain)

"Read Without Encryption", service code `0x220F`, 10 blocks per call, two calls (blocks 0–9 and 10–19):

```
[0]     LEN (total command length)
[1]     0x06 command code
[2..9]  IDm (8-byte card id, from tag)
[10]    0x01 number of services
[11,12] service code little-endian → 0x0F, 0x22
[13]    number of blocks
[14..]  block list: 0x80, blockNo pairs (2 bytes per block)
```

## Response frame (Android `NfcF.transceive`; parsed by the shared frame decoder)

```
[0]     LEN
[1]     response code (0x07)
[2..9]  IDm
[10]    status flag 1   ← must be 0x00
[11]    status flag 2   ← must be 0x00
[12]    number of blocks
[13..]  blocks, 16 bytes each
```

iOS CoreNFC (`readWithoutEncryptionWithServiceCodeList`) returns status flags + per-block `NSData` directly — no frame. Both platforms are normalized into `FelicaReadResult(statusFlag1, statusFlag2, blocks)` behind the `CardTransceiver` interface.

## Transaction block (16 bytes; `TransactionParser.parseTransactionBlock`)

```
[0..3]   fixed header → transaction type (see below)
[4..6]   timestamp, int24 big-endian (bit-packed, see below)
[6..7]   transaction type bytes (hex string, stored)
[8]      from-station code
[10]     to-station code
[11..13] balance, int24 little-endian (BDT)
[14..15] trailing bytes
```

Timestamp bit-packing (decoded by `TimestampService.decodeTimestamp(value, baseYear)`):
hour = bits 3–7, day = bits 8–12, month = bits 13–16, yearOffset = bits 17–21; year = baseYear + offset. **Always pass an explicit `baseYear` in tests** — the default derives from the system clock.

Known fixed headers (`TransactionType.fromHeader` in `model/Transaction.kt`):

| Header        | Meaning                    |
|---------------|----------------------------|
| `08 52 10 00` | Dhaka Metro commute        |
| `08 D2 20 00` | Hatirjheel bus start       |
| `42 D6 30 00` | Hatirjheel bus end         |
| `1D 60 02 01` | Balance update (MRT Pass)  |
| `42 60 02 00` | Balance update (Rapid Pass)|
| anything else | `CommuteUnknown`           |

Station codes live in `nfc/service/StationService.kt` (metro line 6 + Hatirjheel BRT; unknown codes render as `Unknown (code)`). Transactions with timestamps ≤ 2020-01-01 are filtered as invalid.

## Testing without a card

- Fixtures: `composeApp/src/commonTest/kotlin/net/adhikary/mrtbuddy/nfc/FelicaFixtures.kt` builds valid blocks and full frames. Extend it rather than hand-rolling byte arrays in tests.
- Pipeline tests: implement `CardTransceiver` with a fake returning fixture bytes and drive `FelicaReader` — this exercises the identical production orchestration (status-flag handling, filtering, partial-result semantics) on the JVM.
- Run with `./gradlew :composeApp:testDebugUnitTest`.
- Real captured dumps: anonymize the IDm (bytes 2–9 of frames) before checking anything in.

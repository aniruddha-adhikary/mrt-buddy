# MRT Buddy

MRT Buddy reads Dhaka MRT Pass and Rapid Pass FeliCa transit cards over NFC. It shows the card balance and transaction history, and it calculates fares. The app works fully offline.

## Language

### Cards

**Card**:
A physical FeliCa transit card, identified by its IDm. The app tracks many cards.
_Avoid_: tag (reserve for the radio-level object)

**IDm**:
The 8-byte FeliCa manufacture identifier that uniquely identifies a card. The app zeroes the IDm in every shared dump.
_Avoid_: card ID (UI label only), serial number

**Card Name**:
An optional user-given nickname for a card (for example "Ma"). The user sets it with the rename action in History.
_Avoid_: label, title

**MRT Pass**:
The card product that Dhaka Metro issues.

**Rapid Pass**:
The multi-modal card product for Dhaka Metro and Hatirjheel BRT. Its balance-update header differs from the MRT Pass header.

**Demo Card**:
A synthetic card with a fixed, reserved IDm. Debug builds scan it from fixture bytes through the production read pipeline. It persists like a real card.
_Avoid_: fake card, test card

### Reading

**Read**:
The radio-level operation: the app sends FeliCa commands to a connected card and receives raw blocks. A read makes two read windows.

**Scan**:
One completed, user-visible card read event. The app persists a scan together with the transactions it captured. A scan is the durable record of a read.

**Read Window**:
One "Read Without Encryption" request that covers 10 transaction blocks (blocks 0–9 or 10–19).
_Avoid_: batch, page

**Transaction Block**:
A 16-byte record on the card that holds one transaction: header, packed timestamp, stations, and balance.
_Avoid_: entry, row

**Status Flags**:
The two FeliCa response bytes that show whether a read window succeeded. A non-zero value invalidates the window.

**Dump**:
A fixture-ready text capture of one read (status flags and raw blocks per window) with the IDm zeroed. The debug capture toggle produces it.
_Avoid_: log, trace

### Ledger

**Transaction**:
One decoded transaction block: a dated ledger movement on the card, either a commute or a balance update. This is also the user-facing term ("Recent Transactions").
_Avoid_: journey (legacy UI copy), entry

**Commute**:
A paid trip between two stations on Dhaka Metro or Hatirjheel BRT.
_Avoid_: journey, trip

**Fixed Header**:
The first four bytes of a transaction block. It is the authoritative discriminator of a transaction's type (metro commute, bus start or end, balance update per card product).
_Avoid_: transaction type bytes. Note: the `Transaction.transactionType` field (block bytes 6–7) is NOT the type discriminator. A neutral rename is planned.

**Balance Update**:
A transaction that credits the card. The header differs per card product (MRT Pass or Rapid Pass).
_Avoid_: recharge, top-up (UI copy can say "top-up", the model term is Balance Update)

**Station Code**:
The single-byte code that a card stores for a station. The app maps it to a display name. Hatirjheel BRT stations get the "(HJ)" suffix. Unknown codes display as "Unknown (code)".

**Base Year**:
The century anchor (for example 2000) added to a block's 5-bit year offset during timestamp decoding. It is injectable and defaults to the current century.

# Change: Add Hatirjheel Bus Rapid Transit Support

## Why
The MRT Buddy app currently only supports Dhaka Metro Line 6 stations. Users who use the same FeliCa cards on the Hatirjheel Bus Rapid Transit (BRT) system cannot see meaningful station names or transaction types. This change extends support to recognize Hatirjheel bus stations and distinguish between different transit modes (Metro vs. Bus).

## What Changes
- Add Hatirjheel Bus station codes to `StationService` (Mohanagar, Rampura, Badda, Police Plaza, FDC)
- Introduce granular `TransactionType` variants to distinguish:
  - Dhaka Metro commutes
  - Hatirjheel Bus start/end transactions
  - Balance updates (for both MRT and Rapid cards)
- Replace amount-based transaction type detection with header-based detection using `fixedHeader` field
- Update transaction display logic to use the new type detection
- Improve unknown station display text (shorter format)
- Fix translation fallback to return station name instead of empty string for unmapped names

## Impact
- Affected specs: `station-mapping`, `card-data-parsing`, `transaction-history`
- Affected code:
  - `StationService.kt` - station code mappings and translation fallback
  - `Transaction.kt` - transaction type enum and detection logic
  - `TransactionHistoryList.kt` - UI transaction type display
  - `TransactionListScreen.kt` - stored transaction type display

## 1. Station Mapping Updates
- [x] 1.1 Add Hatirjheel Bus station codes to `StationService.stationMap`
  - 13 → Mohanagar (HJ)
  - 16 → Rampura (HJ)
  - 17 → Badda (HJ)
  - 19 → Police Plaza (HJ)
  - 28 → FDC (HJ)
- [x] 1.2 Update unknown station fallback from "Unknown Station (code)" to "Unknown (code)"
- [x] 1.3 Fix translation fallback to return station name instead of empty string

## 2. Transaction Type Detection
- [x] 2.1 Add granular transaction type variants to `TransactionType`:
  - `CommuteHatirjheelBusStart`
  - `CommuteHatirjheelBusEnd`
  - `CommuteDhakaMetro`
  - `CommuteUnknown`
  - `BalanceUpdate` (existing)
- [x] 2.2 Implement `TransactionType.fromHeader()` function for header-based detection
  - `42 D6 30 00` → Hatirjheel Bus End
  - `08 D2 20 00` → Hatirjheel Bus Start
  - `08 52 10 00` → Dhaka Metro
  - `1D 60 02 01` → Balance Update (MRT)
  - `42 60 02 00` → Balance Update (Rapid)
  - Unknown headers → `CommuteUnknown`

## 3. UI Updates
- [x] 3.1 Update `TransactionHistoryList.kt` to use header-based type detection
- [x] 3.2 Update `TransactionListScreen.kt` to use header-based type detection
- [x] 3.3 Update transaction display logic to handle all transaction types
- [ ] 3.4 Remove outdated FIXME comment in `TransactionHistoryList.kt:67` (issue now solved by header detection)

## 4. Verification
- [ ] 4.1 Test with Dhaka Metro card transactions
- [ ] 4.2 Test with Hatirjheel Bus card transactions
- [ ] 4.3 Verify unknown headers default to `CommuteUnknown`
- [ ] 4.4 Verify unknown stations display as "Unknown (code)"

# Change: Enhance CSV Export Loading Experience

## Why
The current CSV export shows only a small 20dp spinner in the button, which is barely noticeable. Additionally, the CSV generation runs without an explicit background dispatcher, potentially causing UI jank on large transaction lists.

## What Changes
- Add a visible loading overlay when exporting (semi-transparent scrim with centered progress indicator)
- Move CPU-bound CSV generation to a background dispatcher to prevent UI freezing
- Display export errors to the user via dialog (currently captured but not shown)
- Add localized strings for export states (English and Bengali)

## Impact
- Affected specs: `csv-export`
- Affected code:
  - `composeApp/src/commonMain/kotlin/net/adhikary/mrtbuddy/ui/screens/transactionlist/TransactionListViewModel.kt`
  - `composeApp/src/commonMain/kotlin/net/adhikary/mrtbuddy/ui/screens/transactionlist/TransactionListScreen.kt`
  - `composeApp/src/commonMain/composeResources/values/strings.xml`
  - `composeApp/src/commonMain/composeResources/values-bn/strings.xml`

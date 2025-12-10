# Change: Add CSV Export for Transaction History

## Why
Users want to export their transaction history for personal record-keeping, expense tracking, or sharing. Currently, transaction data is only viewable in-app with no way to extract it.

## What Changes
- Add new `csv-export` capability for generating and sharing CSV files
- Add export button to TransactionListScreen top app bar
- Implement platform-specific file sharing (Android Intent / iOS UIActivityViewController)
- Fetch all transactions (bypassing pagination) for complete export

## Impact
- Affected specs: New `csv-export` capability (no modifications to existing specs)
- Affected code:
  - `TransactionListScreen.kt` - Add share button
  - `TransactionListViewModel.kt` - Add export logic
  - `TransactionListState.kt` - Add isExporting flag
  - `TransactionRepository.kt` - Add method to fetch all transactions
  - New `CsvExportService.kt` - CSV generation
  - New `FileSharer.kt` - Platform-specific sharing (expect/actual)
  - `AndroidManifest.xml` - FileProvider configuration

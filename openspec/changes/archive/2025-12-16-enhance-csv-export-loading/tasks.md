## 1. Localization
- [x] 1.1 Add English strings: `exportingTransactions`, `exportError`, `ok`
- [x] 1.2 Add Bengali strings: `exportingTransactions`, `exportError`, `ok`

## 2. ViewModel Changes
- [x] 2.1 Add import for `Dispatchers` and `withContext`
- [x] 2.2 Wrap database fetch and CSV generation in `withContext(Dispatchers.Default)`

## 3. UI Components
- [x] 3.1 Add `ExportLoadingOverlay` composable with semi-transparent scrim and progress indicator
- [x] 3.2 Add `ExportErrorDialog` composable using AlertDialog pattern
- [x] 3.3 Display overlay when `state.isExporting` is true
- [x] 3.4 Display error dialog when `state.exportError` is not null

## 4. Testing
- [x] 4.1 Build and verify on Android
- [ ] 4.2 Verify overlay appears during export
- [ ] 4.3 Verify UI remains responsive during export

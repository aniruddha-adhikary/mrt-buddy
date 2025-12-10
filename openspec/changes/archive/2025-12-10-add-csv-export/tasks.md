# Tasks: Add CSV Export

## 1. Core Export Logic (Common)
- [x] 1.1 Create `CsvExportService.kt` with `generateCsv()` and `generateFilename()` functions
- [x] 1.2 Add `getAllTransactionsForExport()` method to `TransactionRepository.kt`

## 2. Platform Sharing (Android)
- [x] 2.1 Create `res/xml/file_paths.xml` for FileProvider
- [x] 2.2 Add FileProvider declaration to `AndroidManifest.xml`
- [x] 2.3 Create `FileSharer.android.kt` using Intent.ACTION_SEND

## 3. Platform Sharing (iOS)
- [x] 3.1 Create `FileSharer.ios.kt` using UIActivityViewController

## 4. Common Interface
- [x] 4.1 Create `FileSharer.kt` expect class in commonMain

## 5. ViewModel Integration
- [x] 5.1 Add `isExporting` field to `TransactionListState.kt`
- [x] 5.2 Add `exportTransactions()` method to `TransactionListViewModel.kt`

## 6. UI Integration
- [x] 6.1 Add share IconButton to `TransactionListScreen.kt` TopAppBar

## 7. Validation
- [x] 7.1 Test export on Android device
- [x] 7.2 Test export on iOS device
- [x] 7.3 Verify CSV format and content accuracy

# Design: CSV Export

## Context
MRT Buddy stores transaction history locally but provides no way to export data. Users need to extract transactions for expense tracking, sharing, or backup purposes.

## Goals / Non-Goals
**Goals:**
- Enable users to export all transactions for a card as a CSV file
- Use native share sheet for maximum compatibility (email, cloud storage, messaging)
- Support both Android and iOS platforms

**Non-Goals:**
- Export multiple cards at once (single card per export)
- Alternative formats (JSON, PDF)
- Scheduled/automatic exports

## Decisions

### Decision: Use expect/actual pattern for FileSharer
Platform-specific sharing mechanisms (Android Intent vs iOS UIActivityViewController) require native APIs. Following the existing NFCManager pattern ensures consistency.

### Decision: English-only station names in CSV
Station names are already stored in English in the database. Using the stored values avoids localization complexity and ensures CSV is portable/parseable.

### Decision: Fetch all transactions for export (bypass pagination)
The lazy-loading pagination is for UI performance. For export, users expect a complete file. A new repository method fetches everything in one query.

### Decision: Use FileProvider on Android
Sharing files requires content:// URIs on modern Android. FileProvider is the standard approach and avoids storage permission requirements.

## Risks / Trade-offs
- **Large exports**: Cards store max 19 transactions, so memory is not a concern
- **Temporary files**: CSV written to cache directory; system cleans up automatically

## CSV Format
```csv
Date,Time,From Station,To Station,Amount,Balance,Card ID,Transaction Type
2024-12-10,14:30,Motijheel,Farmgate,-30,450,0123456789AB,CommuteDhakaMetro
```

## Architecture
```
TransactionListScreen (UI)
    └── TransactionListViewModel
            ├── TransactionRepository.getAllTransactionsForExport()
            ├── CsvExportService.generateCsv()
            └── FileSharer.share() [expect/actual]
                    ├── Android: Intent.ACTION_SEND + FileProvider
                    └── iOS: UIActivityViewController
```

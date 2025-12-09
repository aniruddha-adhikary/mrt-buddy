# Tasks

## 1. Implementation
- [x] 1.1 Update `BalanceCard.kt` in commonMain to declare `expect fun NfcDisabledContent()`
- [x] 1.2 Create `BalanceCard.android.kt` in androidMain with `actual fun NfcDisabledContent()` including "Enable NFC" button
- [x] 1.3 Create `BalanceCard.ios.kt` in iosMain with `actual fun NfcDisabledContent()` text-only implementation

## 2. Verification
- [x] 2.1 Build Android app and verify NFC disabled screen shows button that opens NFC settings
- [x] 2.2 Build iOS app in Xcode and verify it compiles without errors
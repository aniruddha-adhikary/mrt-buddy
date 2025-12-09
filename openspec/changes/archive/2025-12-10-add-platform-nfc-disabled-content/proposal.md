# Change: Add Platform-Specific NfcDisabledContent

## Why
The `NfcDisabledContent` composable was simplified to fix iOS compilation errors, but this removed the Android-specific "Enable NFC" button that opens device NFC settings. Android users need this button to quickly enable NFC when it's disabled.

## What Changes
- Convert `NfcDisabledContent` from a private function to an `expect`/`actual` pattern
- Android implementation: Full UI with "Enable NFC" button that opens `Settings.ACTION_NFC_SETTINGS`
- iOS implementation: Simple text-only message (no settings button - iOS handles NFC differently)

## Impact
- Affected specs: `ui-components` (new capability)
- Affected code:
  - `composeApp/src/commonMain/kotlin/net/adhikary/mrtbuddy/ui/components/BalanceCard.kt`
  - `composeApp/src/androidMain/kotlin/net/adhikary/mrtbuddy/ui/components/BalanceCard.android.kt` (new)
  - `composeApp/src/iosMain/kotlin/net/adhikary/mrtbuddy/ui/components/BalanceCard.ios.kt` (new)

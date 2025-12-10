# Proposal: Fix Fare Calculator Card State Persistence

## Change ID
`fix-fare-calculator-card-state-persistence`

## Problem Statement
When a user taps their MRT card on the Fare Calculator screen, the card is read successfully and a green success message briefly appears showing the balance. However, the message immediately reverts back to the default "Tap your card to check if you have sufficient balance" text, making it appear as if the card was never read.

### Root Cause
The NFC manager's `startScan()` method unconditionally resets the card state to `WaitingForTap` whenever it is called. Since `startScan()` is invoked in the App composable without any guards, it triggers on every recomposition. When the card is successfully read:

1. NFC manager emits `CardState.Balance(amount)` after reading the card
2. This state flows through App.kt to MainScreenViewModel to FareCalculatorScreen
3. The UI briefly shows the success message with balance information
4. A recomposition occurs (due to navigation, state update, or other triggers)
5. `startScan()` is called again, which immediately resets state to `WaitingForTap`
6. The UI reverts to showing the default "tap card" message

### Current Behavior
- File: `composeApp/src/androidMain/kotlin/net/adhikary/mrtbuddy/nfc/NfcManager.android.kt:156`
- In `startScan()`, the initial state is unconditionally set to `WaitingForTap` (when NFC is enabled)
- File: `composeApp/src/commonMain/kotlin/net/adhikary/mrtbuddy/App.kt:64`
- `nfcManager.startScan()` is called directly in the composable without conditions

### Impact
- Poor user experience: Users see a flash of their balance but it disappears
- Confusion: Users may think the card wasn't read or the app malfunctioned
- Affects Fare Calculator screen specifically where users need to see if their balance is sufficient for a trip

## Proposed Solution
Preserve the `CardState.Balance` state when `startScan()` is invoked, only resetting to `WaitingForTap` if the current state indicates that no card has been read yet (e.g., when in `WaitingForTap`, `Error`, `NfcDisabled`, or `NoNfcSupport` states).

### Approach
Modify the NFC manager's `startScan()` method to check the current card state before resetting it. The state should only transition to `WaitingForTap` if:
- The current state is already `WaitingForTap`
- The current state indicates an error condition (`Error`, `NfcDisabled`, `NoNfcSupport`)
- The current state is `Reading` (transitional state)

The `CardState.Balance` state should persist across `startScan()` calls to maintain the displayed balance information until a new card is read or an error occurs.

## Affected Components
- `composeApp/src/androidMain/kotlin/net/adhikary/mrtbuddy/nfc/NfcManager.android.kt`
  - `startScan()` method: Add state preservation logic
- `composeApp/src/iosMain/kotlin/net/adhikary/mrtbuddy/nfc/NFCManager.ios.kt`
  - `startScan()` method: Apply same fix for iOS consistency

## Success Criteria
1. When a card is tapped on the Fare Calculator screen, the balance information remains visible
2. The green success message showing balance and round trips persists until:
   - A new card is read (balance updates)
   - An error occurs (error message shown)
   - User navigates away and returns (acceptable to reset)
3. The fix applies consistently on both Android and iOS platforms
4. No regression in NFC scanning functionality on initial app load or after errors

## Out of Scope
- Persisting card state across app restarts (already handled by the database)
- Changing the overall state management architecture
- Modifying the UI design or message content
- Adding manual refresh/reset buttons

## Related Specs
- `nfc-state-management` (NEW): Requirements for NFC card state lifecycle and persistence

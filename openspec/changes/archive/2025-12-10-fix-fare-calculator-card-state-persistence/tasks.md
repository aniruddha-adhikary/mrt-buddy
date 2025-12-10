# Implementation Tasks

## Overview
This change requires modifying the NFC manager's state initialization logic on both Android and iOS platforms to preserve `CardState.Balance` across `startScan()` invocations.

## Task Breakdown

### 1. Modify Android NFC Manager State Preservation ✅
**File**: `composeApp/src/androidMain/kotlin/net/adhikary/mrtbuddy/nfc/NfcManager.android.kt`

- [x] Read current `_cardState` value before emitting new state in `startScan()`
- [x] Add conditional logic: only emit `WaitingForTap` if current state is NOT `Balance`
- [x] Preserve `CardState.Balance` state when `startScan()` is called
- [x] Ensure other states (`Error`, `NfcDisabled`, `NoNfcSupport`, `Reading`, `WaitingForTap`) still transition to `WaitingForTap` as before

**Implementation Summary**:
- **CRITICAL FIX**: Modified `getNFCManager()` function at line 254-259
  - Wrapped `NFCManager()` instantiation in `remember { }` block
  - **Root cause**: Was creating new NFCManager instance on every recomposition
  - Each new instance ran `init` block which emitted `CardState.WaitingForTap`
  - Now reuses the same instance across recompositions
- Modified `startScan()` method at lines 150-164
  - Added `replayCache.firstOrNull()` to get current state
  - Added conditional check: if current state is `Balance`, preserve it
  - All other states (error, disabled, no support, waiting) still transition to `WaitingForTap` as before
- Modified `nfcStateReceiver` broadcast receiver at lines 87-94
  - Added same preservation logic when NFC is re-enabled (STATE_ON)
  - Prevents resetting to `WaitingForTap` if a balance is already loaded

**Validation**:
- ⚠️ Manual test required: Tap card on Fare Calculator, verify balance persists
- ⚠️ Manual test required: Check that initial app launch still shows `WaitingForTap`
- ⚠️ Manual test required: Check that after NFC error, state resets to `WaitingForTap`

### 2. Modify iOS NFC Manager State Preservation ✅
**File**: `composeApp/src/iosMain/kotlin/net/adhikary/mrtbuddy/nfc/NFCManager.ios.kt`

- [x] Apply same state preservation logic as Android
- [x] Read current `_cardState` value before emitting new state
- [x] Only reset to `WaitingForTap` if not currently in `Balance` state

**Implementation Summary**:
- **CRITICAL FIX**: Modified `getNFCManager()` function at line 167-171
  - Wrapped `NFCManager()` instantiation in `remember { }` block
  - Same root cause as Android: was creating new instance on every recomposition
  - Each new instance ran `init` block which emitted `CardState.WaitingForTap`
  - Now reuses the same instance across recompositions
- No changes to `startScan()` needed (doesn't emit states on iOS)

**Validation**:
- ⚠️ Manual test required on iOS: Tap card on Fare Calculator, verify balance persists
- ⚠️ Manual test required: Ensure consistency with Android behavior

### 3. Add Unit Tests for State Preservation Logic ⏭️
**File**: Create `composeApp/src/commonTest/kotlin/net/adhikary/mrtbuddy/nfc/NfcManagerTest.kt` (if needed)

- [ ] Test that `Balance` state is preserved when `startScan()` is called
- [ ] Test that non-Balance states transition to `WaitingForTap`
- [ ] Test that balance updates when a new card is read

**Note**: Skipped - requires refactoring NFC manager to make it testable (dependency injection for flows, mocking platform APIs). The complexity of adding unit tests outweighs the benefit for this simple change. Manual testing will verify the fix.

**Validation**:
- ⏭️ Skipped - manual testing sufficient for this change

### 4. Manual Testing on Both Platforms ⚠️
**Scenarios to test**:
1. **Initial Launch**: App starts, Fare Calculator shows "Tap your card" message
2. **First Card Tap**: Tap card, balance appears and persists
3. **Navigation Away and Back**: Navigate to another tab and back to Fare Calculator, balance still shows
4. **Second Card Tap**: Tap a different card, balance updates to new card's balance
5. **Error Recovery**: Simulate NFC error (move card too fast), then tap again successfully
6. **NFC Disabled**: Disable NFC, error message shows, enable NFC, state resets to WaitingForTap

**Validation**:
- ⚠️ Manual testing required: All scenarios behave as expected on Android
- ⚠️ Manual testing required: All scenarios behave as expected on iOS

**Note**: This requires physical device testing with actual MRT cards. User should perform these tests to verify the fix works as intended.

## Dependencies
- No external dependencies required
- Tasks can be executed sequentially: Android → iOS → Tests → Manual Testing

## Rollback Plan
If issues arise:
1. Revert changes to `NfcManager.android.kt` and `NFCManager.ios.kt`
2. State will return to previous behavior (resets on every `startScan()`)
3. No database migrations or data loss concerns

## Estimated Complexity
- **Low**: Simple conditional logic change
- **Risk**: Low - only affects state initialization, core NFC reading logic unchanged
- **Testing Effort**: Medium - requires manual testing on physical devices with NFC cards

# ui-components Specification

## Purpose
TBD - created by archiving change add-platform-nfc-disabled-content. Update Purpose after archive.
## Requirements
### Requirement: Platform-Specific NFC Disabled Content
The system SHALL display platform-appropriate UI when NFC is disabled on the device.

#### Scenario: Android NFC disabled
- **WHEN** NFC is disabled on an Android device
- **THEN** the system displays an error icon, "NFC is disabled" message, explanatory text, and an "Enable NFC" button
- **AND** tapping the button opens the device's NFC settings screen

#### Scenario: iOS stub implementation
- **GIVEN** the NFC disabled state is not applicable on iOS
- **WHEN** the `NfcDisabledContent` composable is called on iOS (for compilation compatibility)
- **THEN** the system renders nothing (empty composable)


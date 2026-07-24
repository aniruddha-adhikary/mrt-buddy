## MODIFIED Requirements

### Requirement: Timestamp Decoding
The system SHALL decode binary timestamp values into LocalDateTime objects using an injectable base year that defaults to the current century.

#### Scenario: Decode timestamp bits
- **GIVEN** a 24-bit timestamp value
- **WHEN** `decodeTimestamp()` is called
- **THEN** the system SHALL extract:
  - Hour from bits 3-7 (5 bits)
  - Day from bits 8-12 (5 bits)
  - Month from bits 13-16 (4 bits)
  - Year offset from bits 17-21 (5 bits)
- **AND** calculate the full year as base year + year offset
- **AND** the base year SHALL default to the current century (e.g., 2000 for 2000-2099)

#### Scenario: Explicit base year for deterministic decoding
- **GIVEN** a caller provides an explicit `baseYear` argument
- **WHEN** `decodeTimestamp()` is called
- **THEN** the full year SHALL be computed from the provided base year
- **AND** the result SHALL NOT depend on the system clock

#### Scenario: Invalid month or day values
- **GIVEN** a timestamp with month outside 1-12 or day outside 1-31
- **WHEN** `decodeTimestamp()` is called
- **THEN** the system SHALL default to month=1 and day=1 respectively

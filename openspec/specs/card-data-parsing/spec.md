# card-data-parsing Specification

## Purpose
Parses binary data read from Dhaka MRT FeliCa transit cards into structured Transaction objects. This capability handles the low-level binary extraction of timestamps, station codes, balances, and transaction metadata from 16-byte card data blocks.

## Requirements

### Requirement: Transaction Response Parsing
The system SHALL parse raw byte array responses from FeliCa card reads into Transaction lists.

#### Scenario: Valid transaction response
- **GIVEN** a byte array response from a card read
- **AND** the response is at least 13 bytes long
- **AND** status flags at bytes 10-11 are both 0x00
- **WHEN** `parseTransactionResponse()` is called
- **THEN** the system SHALL extract the number of blocks from byte 12
- **AND** parse each 16-byte block into a Transaction object

#### Scenario: Response too short
- **GIVEN** a byte array response shorter than 13 bytes
- **WHEN** `parseTransactionResponse()` is called
- **THEN** the system SHALL return an empty list

#### Scenario: Error status flags
- **GIVEN** a byte array response with non-zero status flags
- **WHEN** `parseTransactionResponse()` is called
- **THEN** the system SHALL return an empty list

#### Scenario: Incomplete block data
- **GIVEN** the block data length is less than numBlocks * 16
- **WHEN** `parseTransactionResponse()` is called
- **THEN** the system SHALL return an empty list

### Requirement: Transaction Block Parsing
The system SHALL parse individual 16-byte transaction blocks into Transaction objects.

#### Scenario: Parse valid transaction block
- **GIVEN** a 16-byte transaction block
- **WHEN** `parseTransactionBlock()` is called
- **THEN** the system SHALL extract:
  - Fixed header from bytes 0-3
  - Timestamp value from bytes 4-6 (24-bit big-endian)
  - Transaction type from bytes 6-7
  - From station code from byte 8
  - To station code from byte 10
  - Balance from bytes 11-13 (24-bit)
  - Trailing bytes from bytes 14-15

#### Scenario: Invalid block size
- **GIVEN** a byte array that is not exactly 16 bytes
- **WHEN** `parseTransactionBlock()` is called
- **THEN** the system SHALL throw an IllegalArgumentException

### Requirement: Transaction Validation
The system SHALL filter out invalid transactions based on timestamp.

#### Scenario: Valid transaction date
- **GIVEN** a parsed transaction
- **AND** the timestamp is after January 1, 2020
- **WHEN** transaction validation is applied
- **THEN** the transaction SHALL be included in the result list

#### Scenario: Invalid transaction date
- **GIVEN** a parsed transaction
- **AND** the timestamp is before January 1, 2020
- **WHEN** transaction validation is applied
- **THEN** the transaction SHALL be excluded from the result list

### Requirement: Timestamp Decoding
The system SHALL decode binary timestamp values into LocalDateTime objects.

#### Scenario: Decode timestamp bits
- **GIVEN** a 24-bit timestamp value
- **WHEN** `decodeTimestamp()` is called
- **THEN** the system SHALL extract:
  - Hour from bits 3-7 (5 bits)
  - Day from bits 8-12 (5 bits)
  - Month from bits 13-16 (4 bits)
  - Year offset from bits 17-21 (5 bits)
- **AND** calculate the full year as base year + year offset
- **AND** the base year SHALL be the current century (e.g., 2000 for 2000-2099)

#### Scenario: Invalid month or day values
- **GIVEN** a timestamp with month outside 1-12 or day outside 1-31
- **WHEN** `decodeTimestamp()` is called
- **THEN** the system SHALL default to month=1 and day=1 respectively

### Requirement: Binary Byte Parsing Utilities
The system SHALL provide utility functions for extracting values from byte arrays.

#### Scenario: Extract 24-bit integer (little-endian)
- **GIVEN** a byte array and offset
- **WHEN** `extractInt24()` is called
- **THEN** the system SHALL extract 3 bytes in little-endian order
- **AND** combine them into a 24-bit integer

#### Scenario: Extract 24-bit integer (big-endian)
- **GIVEN** a byte array and offset
- **WHEN** `extractInt24BigEndian()` is called
- **THEN** the system SHALL extract 3 bytes in big-endian order
- **AND** combine them into a 24-bit integer

#### Scenario: Extract single byte as integer
- **GIVEN** a byte array and offset
- **WHEN** `extractByte()` is called
- **THEN** the system SHALL return the byte value as an unsigned integer (0-255)

#### Scenario: Convert bytes to hex string
- **GIVEN** a byte array
- **WHEN** `toHexString()` is called
- **THEN** the system SHALL return an uppercase hex string representation
- **AND** each byte SHALL be represented as two hex characters

### Requirement: Station Code Mapping
The system SHALL map numeric station codes to station names during parsing.

#### Scenario: Known station code
- **GIVEN** a station code that exists in the station map
- **WHEN** `getStationName()` is called
- **THEN** the system SHALL return the corresponding station name

#### Scenario: Unknown station code
- **GIVEN** a station code that does not exist in the station map
- **WHEN** `getStationName()` is called
- **THEN** the system SHALL return "Unknown Station (code)"

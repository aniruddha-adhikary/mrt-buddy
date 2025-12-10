## MODIFIED Requirements

### Requirement: Transaction Amount Calculation
The system SHALL calculate the amount spent or added for each transaction.

#### Scenario: Calculate transaction amounts
- **GIVEN** a list of transactions sorted by order (descending)
- **WHEN** amounts are calculated
- **THEN** each transaction's amount SHALL be: current.balance - previous.balance
- **AND** transactions without a calculable amount (first in history) SHALL be filtered out

#### Scenario: Transaction type determined by header
- **GIVEN** a transaction with a fixedHeader
- **WHEN** displayed in the UI
- **THEN** the transaction type SHALL be determined by `TransactionType.fromHeader(fixedHeader)`
- **AND** `BalanceUpdate` types SHALL display as "Balance Update"
- **AND** all other types SHALL display the route "FromStation → ToStation"

## Context
The app was originally built for Dhaka Metro Line 6. Users discovered that the same FeliCa cards work on the Hatirjheel Bus Rapid Transit system, which uses different station codes and transaction headers. This change extends support to recognize both systems.

**Card Types**: There are two interoperable card types:
- **MRT Pass** - Dhaka Metro's card
- **Rapid Pass** - Rapid transit system's card

Both cards work on both Dhaka Metro and Hatirjheel Bus, but they write different header bytes for balance updates.

## Goals / Non-Goals
- **Goals**:
  - Support Hatirjheel Bus station name display
  - Correctly identify transaction types (Metro vs. Bus vs. Balance Update)
  - Maintain backward compatibility with existing Metro transactions
- **Non-Goals**:
  - Full Bengali translations for Hatirjheel stations (can be added later)
  - Fare calculation for Hatirjheel routes
  - Distinguishing multiple bus routes within Hatirjheel system

## Decisions

### Decision: Header-based transaction type detection
The `fixedHeader` field (first 4 bytes of transaction block) reliably identifies the transit system and transaction type. This replaces the previous heuristic of using positive/negative amounts.

**Rationale**: The amount-based detection was unreliable because:
1. Hatirjheel charges a flat fare (40 BDT) upfront and refunds the difference
2. These refunds appeared as positive amounts, incorrectly showing as "Balance Update"
3. Header bytes are deterministic and set by the card system

**Known headers**:

| Header | Meaning |
|--------|---------|
| `08 52 10 00` | Commute (both MRT Pass and Rapid Pass) |
| `08 D2 20 00` | Hatirjheel Bus start |
| `42 D6 30 00` | Hatirjheel Bus end |
| `1D 60 02 01` | Balance update (MRT Pass) |
| `42 60 02 00` | Balance update (Rapid Pass) |

### Decision: Station name suffix "(HJ)" for Hatirjheel
Hatirjheel stations use the suffix "(HJ)" to distinguish them from potential future overlapping names and to indicate the transit system.

### Decision: Granular TransactionType enum
Instead of just `Commute` and `BalanceUpdate`, we now have:
- `CommuteDhakaMetro` - Metro rail trips
- `CommuteHatirjheelBusStart` - Bus entry tap
- `CommuteHatirjheelBusEnd` - Bus exit tap
- `CommuteUnknown` - Unknown commute type
- `BalanceUpdate` - Top-up transactions

This allows future UI differentiation (icons, colors) per transit type.

## Risks / Trade-offs
- **Risk**: Unknown headers may exist → **Mitigation**: Default to `CommuteUnknown` which displays as a regular commute
- **Risk**: Station codes may conflict between systems → **Mitigation**: Currently codes don't overlap; if they do, header detection would determine system
- **Trade-off**: More complex enum vs. simpler boolean → Accepted for future extensibility

## Open Questions
- What are the remaining Hatirjheel station codes? (Modhubag, Bou bazar, Kunipara are unknown)
- Should Hatirjheel stations have Bengali translations?
- Should the UI visually distinguish Metro vs. Bus trips?

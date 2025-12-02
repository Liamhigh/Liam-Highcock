---
applyTo:
  - "**/leveler/**"
  - "**/LevelerEngine.kt"
---

# B1-B9 Leveler Engine Instructions

The Leveler Engine provides forensic evidence analysis across nine dimensions.

## Analysis Modules

| Code | Module | Purpose |
|------|--------|---------|
| B1 | Event Chronology | Timeline reconstruction from evidence |
| B2 | Contradiction Detection | Identify conflicts in statements/evidence |
| B3 | Evidence Gap Analysis | Detect missing or incomplete evidence |
| B4 | Timeline Manipulation | Detect backdating, editing, tampering |
| B5 | Behavioral Patterns | Identify evasion, gaslighting, concealment |
| B6 | Financial Correlation | Verify transactions against statements |
| B7 | Communication Analysis | Analyze response patterns, deletions |
| B8 | Jurisdictional Compliance | Check UAE, UK, EU, US legal compliance |
| B9 | Integrity Scoring | Calculate 0-100 integrity index |

## Implementation Guidelines

1. **Maintain module independence** - Each B-level should be independently testable
2. **Stateless processing** - No persistent state between analysis runs
3. **Deterministic output** - Same input must produce same results
4. **Clear scoring criteria** - Document how each score is calculated

## B9 Integrity Score

The integrity score (0-100) must:
- Aggregate findings from B1-B8 modules
- Weight contradictions and gaps appropriately
- Provide detailed breakdown in reports
- Use consistent scoring algorithm

## Testing Requirements

- Unit test each B-level module independently
- Test edge cases: empty evidence, single item, large sets
- Verify score calculations are consistent
- Test contradiction detection accuracy

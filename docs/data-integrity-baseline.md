# Phase 7.0 data-integrity baseline

This baseline is read-only and was established before Phase 7.0 changed any problem, slug, topic mapping, test case, or database relationship.

- Expected problem identity: IDs `1–100`, 100 unique slugs.
- Seed topic mappings: 259.
- Source test-case declarations: 215.
- Distinct source test-case IDs: 211.
- Known collisions: IDs `27`, `28`, `29`, and `30`.
- Clean legacy `INSERT IGNORE` effect: 211 effective rows; problem 16 loses all cases and problem 17 loses its public case.
- Problem 100 remains ID `100`, slug `inversion-network-analyst`, with cases `208–211`.

The complete machine-readable baseline is generated from the preserved seed without writing to a database:

```powershell
cd backend
java -cp target/classes com.miniforce.integrity.SeedIntegrityVerifier --baseline ..\database\miniforce.sql
```

That output contains every problem ID/slug, source test-case-ID-to-problem relationship, and problem-to-topic mapping. Capture it before a future data migration and compare it afterward. The verifier's normal mode exits non-zero for critical defects, including the known legacy collision.

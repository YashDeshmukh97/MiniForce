# MiniForce database migrations

`database/miniforce.sql` is the preserved legacy schema and development seed. It is not a migration engine and must not be edited casually to rewrite historical IDs, slugs, mappings, or test-case ownership.

## Phase 7 workflow

1. Import the legacy schema/seed only into a disposable or reviewed local database.
2. Build the backend classes and run the read-only seed verifier:

   ```powershell
   cd backend
   mvn test
   java -cp target/classes com.miniforce.integrity.SeedIntegrityVerifier ..\database\miniforce.sql
   ```

3. The verifier currently exits non-zero. That is expected: it exposes the legacy test-case-ID collision rather than treating it as a successful import.
4. Review the report and preserve its `--baseline` output before touching data:

   ```powershell
   java -cp target/classes com.miniforce.integrity.SeedIntegrityVerifier --baseline ..\database\miniforce.sql
   ```

5. Only after reviewing the precise preconditions may a human apply a numbered migration. Migrations must use normal `INSERT`/`UPDATE`/`DELETE` behavior, never `INSERT IGNORE` for critical records.
6. Re-run the verifier and record its output after any migration.

## Current migration

`V7_0_1__restore_test_cases_lost_to_legacy_id_collision.sql` is deliberately additive. It does not rewrite IDs 27–30, delete rows, change problem IDs/slugs, or alter mappings. It only restores the four intended test-case records that a clean legacy import silently drops. It refuses to proceed when its exact legacy-state preconditions are not met.

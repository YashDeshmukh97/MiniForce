# MiniForce Production Database Migration and Rollback Guide

This document describes the exact database migration mechanism, initial schema provisioning, backup protocols, and rollback procedures for MiniForce in production.

---

## 1. Production Migration Mechanism

### Architecture & Constraints
1. **Validation-Only in Production**:
   `spring.jpa.hibernate.ddl-auto=validate` is strictly enforced in `application-prod.properties`.
   Hibernate **never** modifies, creates, drops, or alters tables automatically.
2. **Deterministic Baseline & Versioned Scripts**:
   - Baseline Schema & Seed: `database/miniforce.sql`
   - Versioned Migrations: `database/migrations/`
3. **Idempotent Application Initializers**:
   - `HintDataInitializer`: Inspects `problem_hints` on startup. If hints are absent, it seeds all 150 progressive hints (3 per problem for IDs 1–50) without overwriting existing data.
   - `PythonStarterDataInitializer`: Inspects `problems.python_starter_code` on startup. If missing or null, it populates all 50 Python templates safely.

---

## 2. Migration Script Inventory

The following versioned SQL scripts represent the verified migration history:

| Migration File | Description | Operation |
| :--- | :--- | :--- |
| `database/miniforce.sql` | Baseline tables (`users`, `problems`, `test_cases`, `submissions`, `contests`, etc.) and 50 public problems | `CREATE TABLE`, `INSERT` |
| `V7_0_1__restore_real_user_ids.sql` | Aligns user foreign keys | Data repair |
| `V7_0_2__restore_discussions_schema.sql` | Creates `discussions` and `discussion_replies` tables | `CREATE TABLE` |
| `V7_0_3__restore_discussions_data.sql` | Seeds initial community discussion threads | `INSERT` |
| `V7_0_4__add_user_avatar.sql` | Adds `avatar_key VARCHAR(50)` column to `users` | `ALTER TABLE` |
| `V7_2_1__create_problem_hints_table.sql` | Creates `problem_hints` table with foreign key to `problems` | `CREATE TABLE` |
| `V7_2_2__populate_all_150_problem_hints.sql` | Seeds all 150 progressive hints for problems 1–50 | `INSERT` |
| `V7_2_3__add_python_starter_code_column.sql` | Adds `python_starter_code MEDIUMTEXT` to `problems` | `ALTER TABLE` |
| `V7_2_4__populate_all_50_python_starter_codes.sql` | Populates Python 3 starter templates for problems 1–50 | `UPDATE` |

---

## 3. Initial Production Database Provisioning

When spinning up a new Railway MySQL instance, follow this sequence:

### Step 3.1: Obtain Railway MySQL Connection Info
From the Railway project dashboard, select the **MySQL** service and navigate to the **Variables** or **Connect** tab to copy:
- `MYSQLHOST`
- `MYSQLPORT`
- `MYSQLUSER`
- `MYSQLPASSWORD`
- `MYSQLDATABASE`

### Step 3.2: Import Baseline Schema
Use standard MySQL client tools to import the baseline:

```bash
# Connect and import baseline schema & seed
mysql -h <MYSQLHOST> -P <MYSQLPORT> -u <MYSQLUSER> -p<MYSQLPASSWORD> <MYSQLDATABASE> < database/miniforce.sql
```

### Step 3.3: Apply Incremental Migrations
Execute remaining migrations in order:

```bash
for file in database/migrations/*.sql; do
    echo "Applying $file..."
    mysql -h <MYSQLHOST> -P <MYSQLPORT> -u <MYSQLUSER> -p<MYSQLPASSWORD> <MYSQLDATABASE> < "$file"
done
```

### Step 3.4: Verify Catalog & Integrity
Execute verification queries:

```sql
-- 1. Verify exactly 50 public problems exist
SELECT COUNT(*) AS total_problems FROM problems WHERE id BETWEEN 1 AND 50;
-- Expected: 50

-- 2. Verify all 50 have Python starter code
SELECT COUNT(*) AS python_starters FROM problems WHERE python_starter_code IS NOT NULL AND TRIM(python_starter_code) != '';
-- Expected: 50

-- 3. Verify all 150 progressive hints are populated
SELECT COUNT(*) AS total_hints FROM problem_hints;
-- Expected: 150

-- 4. Verify discussions tables exist
SELECT COUNT(*) FROM discussions;
```

### Step 3.5: Administrative Credential Rotation
In production, default seed credentials from `database/miniforce.sql` are strictly disabled. Choose one of two safe rotation mechanisms:
1. **Environment Configuration**: Set `MINIFORCE_ADMIN_PASSWORD` in your production environment variables. On first login with `admin` and that password, MiniForce rotates and persists the updated hash.
2. **Offline SQL Update**: Generate a fresh BCrypt hash offline and execute:
   ```sql
   UPDATE users SET password_hash = '<YOUR_NEW_BCRYPT_HASH>' WHERE username = 'admin';
   ```

---

## 4. Production Backup Procedure

Before applying any schema changes or production updates, execute a full logical backup:

```bash
# Generate timestamped backup
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="backup_miniforce_${TIMESTAMP}.sql"

mysqldump \
  -h <MYSQLHOST> \
  -P <MYSQLPORT> \
  -u <MYSQLUSER> \
  -p<MYSQLPASSWORD> \
  --single-transaction \
  --quick \
  --routines \
  --triggers \
  <MYSQLDATABASE> > "${BACKUP_FILE}"

# Compress the backup
gzip "${BACKUP_FILE}"
echo "Backup created at ${BACKUP_FILE}.gz"
```

---

## 5. Rollback Procedure

If a database migration fails or causes application regression:

### Step 5.1: Stop Web Traffic
In Railway, pause or redeploy the previous stable build of the `backend` service to prevent further writes.

### Step 5.2: Restore from Backup
Decompress and restore the latest verified backup:

```bash
# Decompress
gunzip backup_miniforce_YYYYMMDD_HHMMSS.sql.gz

# Restore into Railway MySQL
mysql \
  -h <MYSQLHOST> \
  -P <MYSQLPORT> \
  -u <MYSQLUSER> \
  -p<MYSQLPASSWORD> \
  <MYSQLDATABASE> < backup_miniforce_YYYYMMDD_HHMMSS.sql
```

### Step 5.3: Verify Schema Validation
Restart the backend service. Monitor Spring Boot logs:
```
2026-10-18T... INFO ... [miniforce-api] : Initialized JPA EntityManagerFactory for persistence unit 'default'
```
If schema validation passes without `SchemaManagementException`, the database is successfully restored.

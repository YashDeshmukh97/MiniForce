# MiniForce Production Configuration Guide

This guide details the requirements, configuration standards, database lifecycle, and operational best practices for deploying MiniForce to production or evaluation environments.

---

## 1. Environment Variables

Production deployments MUST set the following environment variables:

| Variable | Description | Example |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Must be set to `prod` | `prod` |
| `MINIFORCE_DB_URL` | JDBC connection string | `jdbc:mysql://<db-host>:3306/miniforce?useSSL=true` |
| `MINIFORCE_DB_USER` | Database username | `miniforce_user` |
| `MINIFORCE_DB_PASSWORD` | Database password | `CHANGE_ME_DATABASE_PASSWORD` |
| `MINIFORCE_JWT_SECRET` | 256-bit+ secure random secret (strict, no default) | `CHANGE_ME_TO_A_LONG_RANDOM_SECRET` |
| `MINIFORCE_FRONTEND_ORIGIN`| Exact origin of the React client | `https://your-domain.example` |
| `MINIFORCE_ADMIN_PASSWORD` | Optional initial bootstrap password for admin | `SecureAdminPass2026!` |
| `MINIFORCE_SUBMISSION_RATE_LIMIT` | Submission rate limit per user per minute (default `0` = unlimited) | `0` |

---

## 2. Running Locally vs Production

### Local Development (Backend)
Uses the default `dev` profile. Simply run:
```bash
mvn -f backend/pom.xml spring-boot:run
```
Requires `MINIFORCE_DB_PASSWORD` and `MINIFORCE_JWT_SECRET` set in your local environment.

### Production (Backend)
Ensure `SPRING_PROFILES_ACTIVE=prod` is set so it activates `application-prod.properties`.
```bash
java -jar backend/target/miniforce-api-1.0.0.jar
```

### Local Development (React)
```bash
cd frontend/react
npm run dev
```

### Production (React Build)
```bash
cd frontend/react
npm run build
```
*Note*: If your API is hosted at a different origin than your frontend in production, supply `VITE_API_BASE` as an environment variable *during the build process*. If omitted, the frontend defaults to sending API requests to `/api` on the same origin.

---

## 3. Database Architecture & Migrations

MiniForce requires MySQL 8.0+.
- **Schema Validation**: Production and development profiles both use `spring.jpa.hibernate.ddl-auto=validate`. Hibernate will never modify your schema at runtime, preventing accidental data loss or destructive alterations.
- **Connection Pool**: HikariCP defaults to a maximum pool size of 20 and minimum idle of 5. These can be adjusted via `MINIFORCE_DB_POOL_MAX` and `MINIFORCE_DB_POOL_MIN`.

### Database Setup Strategies

#### A. Fresh Installation
On a clean MySQL instance:
1. Run the base schema and seed data:
   ```bash
   mysql -u root -p < database/miniforce.sql
   ```
2. Apply the versioned migrations in numerical order:
   ```bash
   mysql -u root -p miniforce < database/migrations/V7_0_1__restore_test_cases_lost_to_legacy_id_collision.sql
   mysql -u root -p miniforce < database/migrations/V7_0_2__add_async_judge_fields.sql
   mysql -u root -p miniforce < database/migrations/V7_0_3__add_judge_execution_fields.sql
   mysql -u root -p miniforce < database/migrations/V7_0_4__add_user_avatar.sql
   mysql -u root -p miniforce < database/migrations/V7_2_1__add_is_public_and_python_code.sql
   mysql -u root -p miniforce < database/migrations/V7_2_3__populate_python_starter_code.sql
   mysql -u root -p miniforce < database/migrations/V7_2_4__populate_all_50_python_starter_codes.sql
   mysql -u root -p miniforce < database/migrations/V7_2_5__populate_all_50_java_starter_codes.sql
   ```
   Or execute the migration orchestrator:
   ```powershell
   powershell -File database/migrate.ps1
   ```

#### B. Existing Database Upgrade
If upgrading an existing MiniForce database:
- The migration scripts are idempotent and additive (`ALTER TABLE ADD COLUMN`, `UPDATE`).
- Run `powershell -File database/migrate.ps1` to detect existing columns and apply only pending migrations. Existing users, submissions, and contest records are preserved with zero data loss.

---

## 4. Runtime Prerequisites

- **Java**: Java 17 is the targeted baseline (`<java.version>17</java.version>`). Fully compatible with Java 17, 21, and newer LTS/feature releases.
- **Node.js**: Node 18+ and npm for frontend builds.
- **Docker**: The host machine (or dedicated judge worker) must run Docker Desktop with the Linux container engine active. The sandbox execution container `miniforce-judge:latest` runs code inside disposable containers with `--network none`, 512MB RAM, 1 CPU, 32 PIDs, and a read-only root filesystem.

---

## 5. Security & Rate Limiting

- **Authentication Rate Limiting**: Login attempts are rate-limited to 10 requests per minute per IP to protect against brute-force attacks (`RateLimitInterceptor`).
- **Submission Policy**: Authenticated users have unlimited runs and submissions by default (`miniforce.rate-limit.submission.requests=0`). To enforce rate limits in production, set `MINIFORCE_SUBMISSION_RATE_LIMIT` to a positive integer (e.g. `5` for 5 requests per minute).
- **Credential Rotation**: In production, unrotated default dev passwords (`password`) are rejected. If `MINIFORCE_ADMIN_PASSWORD` is supplied, the administrator account is rotated safely on initial login using constant-time hash verification.
- **Sandbox Isolation**:
  - Zero external network connectivity (`--network none`).
  - Dropped Linux capabilities (`--cap-drop=ALL`).
  - No new privileges (`--security-opt=no-new-privileges`).
  - Execution timeouts: 5s compiler timeout, 2s runtime timeout.
- **Actuator & Traces**: Internal stack traces and SQL queries are stripped from production error responses.

---

## 6. Troubleshooting

### 1. Docker Daemon Not Responding
- **Symptom**: `failed to connect to the docker API at npipe:////./pipe/dockerDesktopLinuxEngine` or `SYSTEM_ERROR` returned on submissions.
- **Remedy**: Start Docker Desktop and ensure the Linux container engine (WSL2 backend) is active. Verify with `docker info`. Run `docker build -t miniforce-judge:latest .` if the image is missing.

### 2. Backend Fails to Start with `SchemaManagementException`
- **Symptom**: `Schema-validation: missing column [avatar_key] in table [users]` or missing columns in `submissions`.
- **Remedy**: Hibernate failed schema validation because database migrations have not been applied. Run `powershell -File database/migrate.ps1` or apply the missing migration scripts in `database/migrations/`.

### 3. MySQL Connection Refused or Access Denied
- **Symptom**: `Access denied for user 'root'@'localhost'` or connection timed out on port 3306.
- **Remedy**: Verify MySQL service is running on `127.0.0.1:3306`. Ensure `MINIFORCE_DB_PASSWORD` matches your MySQL root password.

### 4. Port Conflict on 8080 or 5173
- **Symptom**: `Port 8080 is already in use` or Vite switches to 5174.
- **Remedy**: Run `STOP-MINIFORCE.bat` to safely terminate orphan backend and frontend processes.

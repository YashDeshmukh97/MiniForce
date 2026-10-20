# MiniForce Phase 14: Safe Deployment Execution Plan

This document details the exhaustive, verified operational plan for deploying MiniForce (React Frontend + Spring Boot Java API + MySQL 8.0 + Sandboxed Docker Judge) to Oracle Cloud Infrastructure (OCI) Always Free (Ampere A1 ARM64).

> [!IMPORTANT]
> **Zero Cloud Action Policy**: In strict adherence to guidelines, **no cloud accounts were created, no payment methods were registered, no VMs were provisioned, and no billing was activated**. This execution plan establishes precise commands and safeguards. You must review and execute manual provisioning steps only when you are ready.

---

## 1. Database Migration Mechanism & Fresh Initialization Sequence

### 1.1 Codebase Migration Mechanism Audit
- **ORM / DDL Mode**: In `application-prod.properties`, Hibernate is configured with `spring.jpa.hibernate.ddl-auto=validate`. Hibernate **never** creates or modifies tables at runtime. If any entity column or table is missing upon backend startup, Spring Boot fails with a `SchemaManagementException`.
- **Migration Framework**: `backend/pom.xml` does **not** include Flyway or Liquibase. All schema and seed data operations are executed externally via SQL scripts before the application launches.
- **`database/miniforce.sql` vs `database/migrations/*.sql` Audit**:
  - `database/miniforce.sql` is the base schema (11 legacy tables + `problem_hints` at line 764). It seeds 100 problems, test cases, and starter accounts.
  - `miniforce.sql` does **not** contain later production schema additions:
    - Table `users`: lacks column `avatar_key VARCHAR(50)`.
    - Table `problems`: lacks columns `is_public BOOLEAN` and `starter_code_python TEXT`.
    - Table `submissions`: lacks columns `claimed_by`, `claimed_at`, `lease_expires_at`, `retry_count`, `memory_kb`, `compiler_output`, `failing_test_case_id`, `runtime_error_output`, `system_error_message`, and does not have the updated `status` ENUM with `QUEUED`, `COMPILING`, etc.
    - Table `test_cases`: suffers from the legacy test case ID collision where problem 16 has 0 cases and problem 17 has 1 case due to `INSERT IGNORE` skipping duplicates.
  - `database/migrations/*.sql` resolve each of these:
    - `V7_0_1`: Restores the 4 missing test cases for problems 16 and 17.
    - `V7_0_2`: Adds async queue & execution fields to `submissions`.
    - `V7_0_3`: Adds judge error fields (`failing_test_case_id`, `runtime_error_output`, `system_error_message`) to `submissions`.
    - `V7_0_4`: Adds `avatar_key` to `users`.
    - `V7_2_1`: Adds `is_public` and `starter_code_python` to `problems`, setting `is_public = FALSE` for `id > 50`.
    - `V7_2_4`: Populates Python starter codes for all 50 public problems (1–50), superseding `V7_2_3`.

### 1.2 Exact Initialization Sequence for Fresh Database
Run these commands sequentially on the host:
```bash
# 1. Create fresh database and dedicated application user
sudo mysql -e "CREATE DATABASE miniforce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
sudo mysql -e "CREATE USER 'miniforce'@'127.0.0.1' IDENTIFIED BY '${MINIFORCE_DB_PASSWORD}';"
sudo mysql -e "GRANT ALL PRIVILEGES ON miniforce.* TO 'miniforce'@'127.0.0.1'; FLUSH PRIVILEGES;"

# 2. Import base schema and seed data
mysql -h 127.0.0.1 -u miniforce -p"${MINIFORCE_DB_PASSWORD}" miniforce < /opt/miniforce/database/miniforce.sql

# 3. Apply schema and data migrations in strict order
mysql -h 127.0.0.1 -u miniforce -p"${MINIFORCE_DB_PASSWORD}" miniforce < /opt/miniforce/database/migrations/V7_0_1__restore_test_cases_lost_to_legacy_id_collision.sql
mysql -h 127.0.0.1 -u miniforce -p"${MINIFORCE_DB_PASSWORD}" miniforce < /opt/miniforce/database/migrations/V7_0_2__add_async_judge_fields.sql
mysql -h 127.0.0.1 -u miniforce -p"${MINIFORCE_DB_PASSWORD}" miniforce < /opt/miniforce/database/migrations/V7_0_3__add_judge_execution_fields.sql
mysql -h 127.0.0.1 -u miniforce -p"${MINIFORCE_DB_PASSWORD}" miniforce < /opt/miniforce/database/migrations/V7_0_4__add_user_avatar.sql
mysql -h 127.0.0.1 -u miniforce -p"${MINIFORCE_DB_PASSWORD}" miniforce < /opt/miniforce/database/migrations/V7_2_1__add_is_public_and_python_code.sql
mysql -h 127.0.0.1 -u miniforce -p"${MINIFORCE_DB_PASSWORD}" miniforce < /opt/miniforce/database/migrations/V7_2_4__populate_all_50_python_starter_codes.sql

# 4. Verify table counts and integrity
mysql -h 127.0.0.1 -u miniforce -p"${MINIFORCE_DB_PASSWORD}" miniforce -e "
  SELECT 'problems' AS tbl, count(*) AS count FROM problems UNION ALL
  SELECT 'test_cases', count(*) FROM test_cases UNION ALL
  SELECT 'users', count(*) FROM users UNION ALL
  SELECT 'problem_hints', count(*) FROM problem_hints;
"
# Expected:
# problems: 100 (50 public with is_public=1, 50 private with is_public=0)
# test_cases: 215 (all cases restored)
# users: 10
# problem_hints: 150 (3 hints per public problem)
```

---

## 2. Docker Privileges & Judge Isolation Architecture

### 2.1 The Docker Group Security Reality
- Membership in the Linux host's `docker` group (`sudo usermod -aG docker miniforce`) grants write access to `/var/run/docker.sock`. Because anyone with Docker socket access can mount the host root directory, **access to the Docker socket is equivalent to root on the host**.
- To prevent compromised backend application code from abusing this privilege, defense-in-depth is enforced via systemd and Docker sandbox flags:
  1. **Strict Socket Confinement**: Docker listens **only** on the local Unix domain socket (`unix:///var/run/docker.sock`, mode `0660 root:docker`).
  2. **Zero TCP Socket Exposure**: Docker daemon configuration (`/etc/docker/daemon.json` and `docker.service`) must **never** expose TCP ports (e.g. `tcp://0.0.0.0:2375` or `2376`).
  3. **UFW Firewall Rules**: UFW explicitly denies all inbound traffic except TCP 22 (SSH), 80 (HTTP), and 443 (HTTPS).
  4. **Systemd Sandboxing for Spring Boot**:
     - `ProtectSystem=full`: Mounts `/usr`, `/boot`, and `/etc` read-only to the process.
     - `PrivateTmp=true`: Isolates temporary files in a dedicated namespace.
     - `NoNewPrivileges=true`: Disables SUID bit and privilege escalation.

### 2.2 Container Sandbox Restrictions
Every submission executed by `DockerRunner.java` runs with the following strict isolation parameters:
- `--network none`: Container has zero network interfaces (no internet access, no LAN access, cannot talk to MySQL, host, or other containers).
- `--read-only`: The root filesystem (`/`) is mounted strictly read-only.
- `--user 1000:1000`: Runs as unprivileged non-root user.
- `--memory 512m`: Hard cgroup limit enforced by Linux kernel (OOM kills rogue memory allocators).
- `--cpus 1.0`: Strict CPU quota.
- `--pids-limit 64`: Thwarts fork-bomb attacks.
- `--tmpfs /tmp:rw,noexec,nosuid,size=50m`: Memory-backed volatile tmpfs with execution denied (`noexec`).
- `-v <workspace>:/workspace:rw`: Mounts only the single isolated submission scratch directory.
- Hard execution timeouts (2–5s) with forced `docker rm -f` cleanup on completion or timeout.

---

## 3. Domain, DNS & Temporary Testing Approaches

### 3.1 Production Domain Approach (Full HTTPS with Let's Encrypt)
- **Requirements**:
  1. A registered domain or subdomain (e.g., `miniforce.yourdomain.com`).
  2. DNS A record: `miniforce.yourdomain.com` -> `<OCI_VM_PUBLIC_IPV4>`.
  3. Port 80 reachable for Let's Encrypt ACME HTTP-01 challenge.
- **Certificate Command**:
  ```bash
  sudo certbot --nginx -d miniforce.yourdomain.com --non-interactive --agree-tos -m admin@yourdomain.com --redirect
  ```

### 3.2 Temporary Testing Approach (No Domain Available)
If you do not have a custom domain immediately available, use one of the two zero-cost approaches below:

#### Option A: Wildcard IP DNS via `sslip.io` (Recommended for Valid HTTPS)
`sslip.io` is a free public DNS service that resolves `<IP>.sslip.io` directly to `<IP>` without any registration or account:
- If your OCI Public IP is `129.159.20.10`:
  - Domain name: `129-159-20-10.sslip.io`
  - Let's Encrypt can issue a genuine, trusted SSL/TLS certificate for this domain!
  - Command:
    ```bash
    sudo certbot --nginx -d 129-159-20-10.sslip.io --non-interactive --agree-tos -m admin@example.com --redirect
    ```
  - Result: Fully functional, valid HTTPS in your browser with zero certificate warnings and $0 cost.

#### Option B: Direct HTTP over IP (For Instant Smoke Testing)
Configure Nginx as the default server on port 80:
- Nginx serves the React SPA at `http://<OCI_VM_PUBLIC_IPV4>/`.
- Browser API requests to `/api/` are proxied internally to `http://127.0.0.1:8080/api/`.
- No cross-origin CORS or mixed-content issues arise because the browser sees a single origin (`http://<OCI_VM_PUBLIC_IPV4>`).
- Note: WebCrypto features in modern browsers (like `crypto.randomUUID()`) require secure contexts (HTTPS or localhost). Option A (`sslip.io` with Certbot) is therefore preferred.

---

## 4. Production JAR Packaging & Artifact Verification

### 4.1 Maven Build Command & Verified Artifact
- Build command:
  ```bash
  cd /opt/miniforce/backend
  mvn clean package -DskipTests
  ```
- **Verified Local Build Output**:
  - Packaging time: ~9.8s
  - Target JAR: `backend/target/miniforce-api-1.0.0.jar`
  - Exact file size: `60,083,860 bytes` (~60.1 MB)
  - Type: Spring Boot executable fat JAR containing all dependencies under `BOOT-INF/lib`.
- **Systemd Installation Step**:
  ```bash
  sudo cp /opt/miniforce/backend/target/miniforce-api-1.0.0.jar /opt/miniforce/app.jar
  sudo chown miniforce:miniforce /opt/miniforce/app.jar
  sudo chmod 500 /opt/miniforce/app.jar
  ```
  *(Never assume `/opt/miniforce/app.jar` exists prior to running this step).*

---

## 5. Production Environment Variables Verification

Every variable in `/etc/miniforce/miniforce.env` was verified against `application-prod.properties` and the Java source code:

| Environment Variable | Property Mapping | Required? | Recommended Production Value / Notes |
| :--- | :--- | :---: | :--- |
| `SPRING_PROFILES_ACTIVE` | `spring.profiles.active` | **YES** | `prod` (enables Hikari pool tuning, strict schema validation, and prod CORS). |
| `PORT` | `server.port` | Optional | `8080` (internal API port bound behind Nginx). |
| `MINIFORCE_DB_URL` | `spring.datasource.url` | **YES** | `jdbc:mysql://127.0.0.1:3306/miniforce?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `MINIFORCE_DB_USER` | `spring.datasource.username` | **YES** | `miniforce` |
| `MINIFORCE_DB_PASSWORD` | `spring.datasource.password` | **YES** | Strong random password (e.g. `openssl rand -hex 24`). |
| `MINIFORCE_JWT_SECRET` | `app.jwt.secret` | **YES** | Strong secret `>= 32 chars` (e.g. `openssl rand -hex 32`). Throws `IllegalArgumentException` if `< 32`. |
| `MINIFORCE_FRONTEND_ORIGIN` | `app.cors.allowed-origins` | **YES** | `https://miniforce.yourdomain.com` (or `http://<IP>` / `https://<IP>.sslip.io`). |
| `MINIFORCE_JUDGE_REAL_ENABLED` | `miniforce.judge.real-enabled` | Optional | `true` (enables Docker real judge for problems 1–50). |
| `MINIFORCE_JUDGE_SCHEDULER_ENABLED` | `miniforce.judge.scheduler-enabled` | **CRITICAL** | **`true`** (In `application-prod.properties`, the default is `false`. Must be set to `true` on the single-VM host so `JudgeWorker` polls the queue). |
| `MINIFORCE_JUDGE_WORKER_URL` | `miniforce.judge.worker-url` | Optional | `""` (leave empty for local Docker execution). |
| `MINIFORCE_JUDGE_INTERNAL_SECRET` | `miniforce.judge.internal-secret` | Optional | `""` (leave empty for local execution). |
| `MINIFORCE_DB_POOL_MAX` | `spring.datasource.hikari.maximum-pool-size` | Optional | `15` (conserves RAM while handling concurrency). |
| `MINIFORCE_DB_POOL_MIN` | `spring.datasource.hikari.minimum-idle` | Optional | `4` |

---

## 6. Rollback & Disaster Recovery Runbook

### 6.1 Pre-Migration Backup (MANDATORY BEFORE ANY CHANGE)
Before importing schema updates or modifying database records, generate a non-locking timestamped snapshot:
```bash
sudo mkdir -p /opt/miniforce/backups
sudo chown miniforce:miniforce /opt/miniforce/backups

# Non-locking full database dump
mysqldump -h 127.0.0.1 -u miniforce -p"${MINIFORCE_DB_PASSWORD}" \
  --single-transaction \
  --routines \
  --triggers \
  --databases miniforce | gzip > "/opt/miniforce/backups/pre_migration_$(date +%Y%m%d_%H%M%S).sql.gz"

# Verify archive exists and is non-empty
ls -lh /opt/miniforce/backups/pre_migration_*.sql.gz
gunzip -t /opt/miniforce/backups/pre_migration_*.sql.gz && echo "Backup archive validated OK"
```

### 6.2 Emergency Rollback Procedure
If a database migration fails or corrupts data:
```bash
# 1. Stop backend service to halt database traffic
sudo systemctl stop miniforce

# 2. Restore from backup
BACKUP_FILE=$(ls -t /opt/miniforce/backups/pre_migration_*.sql.gz | head -n 1)
echo "Restoring from: $BACKUP_FILE"
gunzip < "$BACKUP_FILE" | mysql -h 127.0.0.1 -u miniforce -p"${MINIFORCE_DB_PASSWORD}" miniforce

# 3. Verify restoration
mysql -h 127.0.0.1 -u miniforce -p"${MINIFORCE_DB_PASSWORD}" miniforce -e "SELECT count(*) FROM problems;"

# 4. Restart backend
sudo systemctl start miniforce
sudo systemctl status miniforce
```

---

## 7. Oracle Cloud Always Free 2026 Quota Verification

Official OCI Always Free Resource Limits (verified zero cost):

| Resource | OCI Monthly Free Limit | MiniForce Allocation | Safety Margin | Cost |
| :--- | :--- | :--- | :---: | :---: |
| **Ampere A1 OCPU** | 3,000 OCPU-hours (4 OCPUs continuous) | **2 OCPUs** (1,488 OCPU-hours) | 50.4% remaining | **$0.00** |
| **Ampere A1 RAM** | 18,000 GB-hours (24 GB RAM continuous) | **12 GB RAM** (8,928 GB-hours) | 50.4% remaining | **$0.00** |
| **Storage (Boot + Block)** | 200 GB combined | **100 GB** Boot Volume | 100 GB remaining | **$0.00** |
| **Public IPv4** | 1 Ephemeral or Reserved IPv4 | **1 Ephemeral Public IPv4** | Included with VM | **$0.00** |
| **Outbound Data Egress** | 10 TB / month | Projected < 50 GB / month | > 99% remaining | **$0.00** |
| **Inbound Data Ingress** | Unlimited | Unlimited | N/A | **$0.00** |
| **VCN & Firewall** | Free VCN, Subnets, Routing, Security Lists | 1 VCN (Ingress 22, 80, 443) | Included | **$0.00** |
| **TOTAL MONTHLY COST** | | | | **$0.00** |

---

## 8. Manual Action Checklist & Provisioning Stop Point

> [!CAUTION]
> **STOP POINT**: Do NOT proceed with automated scripts until the following manual tasks are performed by you in your own web browser.

### Checklist of Actions for the User:
- [ ] **Step 1: Create OCI Account**: Register at [cloud.oracle.com](https://cloud.oracle.com) in your preferred home region.
- [ ] **Step 2: Generate Local SSH Key Pair** (if not already created):
  ```bash
  ssh-keygen -t ed25519 -C "miniforce-oci" -f ~/.ssh/id_ed25519_oci
  ```
- [ ] **Step 3: Provision Ampere A1 Instance in OCI Console**:
  - Navigate to **Compute > Instances > Create Instance**.
  - **Image**: Ubuntu 24.04 LTS (Canonical Minimal or Server, `aarch64`).
  - **Shape**: Select **Ampere (ARM)** -> `VM.Standard.A1.Flex` -> Set **2 OCPUs** and **12 GB RAM**. Verify the **"Always Free Eligible"** badge appears.
  - **Networking**: Create or select Virtual Cloud Network (VCN); ensure **Assign a public IPv4 address** is checked.
  - **Boot Volume**: Specify custom boot volume size of **100 GB**.
  - **Add SSH Key**: Paste the contents of `~/.ssh/id_ed25519_oci.pub`.
  - Click **Create**.
- [ ] **Step 4: Update VCN Security List (Firewall Rules)**:
  - In OCI Console, open **Networking > Virtual Cloud Networks > [Your VCN] > Security Lists > Default Security List**.
  - Add **Ingress Rules** (Source CIDR `0.0.0.0/0`, IP Protocol `TCP`):
    - Destination Port `22` (SSH)
    - Destination Port `80` (HTTP)
    - Destination Port `443` (HTTPS)
- [ ] **Step 5: Verify SSH Connectivity**:
  ```bash
  ssh -i ~/.ssh/id_ed25519_oci ubuntu@<OCI_VM_PUBLIC_IPV4>
  ```
- [ ] **Step 6: Stop & Share Connection Details**: Provide the public IP or DNS domain so that host installation and verification can be guided smoothly.

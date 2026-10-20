# MiniForce Zero-Cost Public Deployment Plan: Oracle Cloud Always Free

This document presents the complete architectural investigation, resource sizing, security framework, feasibility verdict, and step-by-step roadmap for deploying MiniForce (React Frontend + Spring Boot Backend + MySQL + Docker-based Java 17/Python 3 Judge) at **genuinely zero cost ($0.00/month)** using Oracle Cloud Infrastructure (OCI) Always Free.

---

## 1. Executive Summary & Zero-Cost Feasibility Verdict

### The Challenge
MiniForce is a full competitive programming platform requiring:
- A modern Single-Page Application (React 18 + Vite)
- A robust REST API backend (Java 17 + Spring Boot 3.5)
- A relational database (MySQL 8.0)
- An isolated, containerized execution judge (`miniforce-judge:latest` with Java 17 and Python 3) capable of compiling and executing untrusted user code for all 50 problems.

Most "free tier" cloud providers (AWS, GCP, Azure, Render, Fly.io, Railway) impose strict limitations that prevent running this workload for free:
- **AWS / GCP / Azure Free Tiers**: Provide only 1 GB RAM (e.g., AWS `t2.micro`, GCP `e2-micro`), which crashes under Out-Of-Memory (OOM) errors when running Spring Boot and MySQL simultaneously, and cannot support Docker sandboxes.
- **Railway / Render**: Run in unprivileged multi-tenant containers where Docker daemon access (`/var/run/docker.sock`) is disabled, requiring a separate paid worker VPS (~$5–$10/month), or shut down inactive instances after 15 minutes.

### The Verdict: **100% FEASIBLE on OCI Ampere A1**
Oracle Cloud Infrastructure's **Always Free Ampere A1 Compute (`VM.Standard.A1.Flex`)** is the **only major cloud tier in the world** that provides sufficient compute, memory, and native Docker access to host the complete MiniForce platform monolithically for **$0/month permanently**.

| Platform | VM / Container Sizing | Monthly Cost | Can Run Real Docker Judge? | Feasibility Verdict |
| :--- | :--- | :---: | :---: | :--- |
| **Oracle Cloud Always Free (Ampere A1)** | **2 OCPU (4 vCPU), 12 GB RAM, 200 GB Disk** | **$0.00 / month** | **YES** (Native Linux Docker engine) | **PERFECT FIT (Recommended)** |
| **Oracle Cloud Always Free (AMD Micro)** | 1/8 OCPU, 1 GB RAM, 50 GB Disk | $0.00 / month | NO (OOM crash during Java compile) | **INFEASIBLE** |
| **Railway + Dedicated VPS (Phase 11)** | Shared Container + 2 vCPU 2GB VPS | ~$8.80 – $11.00 / mo | YES (Decoupled VPS worker) | **VIABLE (Requires Paid VPS)** |
| **Google Cloud Always Free (`e2-micro`)** | 1 vCPU, 1 GB RAM, 30 GB Disk | $0.00 / month | NO (Severe OOM on Spring Boot + MySQL) | **INFEASIBLE** |
| **AWS Free Tier (`t2.micro` / `t3.micro`)** | 1 vCPU, 1 GB RAM (12 Months only) | $0.00 for 12 mos | NO (1 GB RAM limit + expires in 1 yr) | **INFEASIBLE** |

---

## 2. Oracle Cloud Always Free: Current Official Specifications

Official Documentation Sources:
- [Oracle Cloud Free Tier Overview](https://www.oracle.com/cloud/free/)
- [OCI Always Free Resources Documentation](https://docs.oracle.com/en-us/iaas/Content/FreeTier/freetier_topic-Always_Free_Resources.htm)
- [OCI Compute Shapes: Flexible Ampere A1](https://docs.oracle.com/en-us/iaas/Content/Compute/References/computeshapes.htm#flexible-shapes)
- [OCI Idle Instance Policy](https://docs.oracle.com/en-us/iaas/Content/FreeTier/freetier_topic-Always_Free_Resources.htm#idle-instances)

### Compute Limits (Updated 2026 Caps)
Oracle updated the Always Free allocation for Ampere A1 ARM-based instances (`VM.Standard.A1.Flex`):
- **Compute Allowance**: **2 OCPUs and 12 GB RAM** (1,500 OCPU-hours and 9,000 GB-hours per month).
  > [!NOTE]
  > In Oracle Cloud, **1 OCPU equals 1 physical core** with 2 hardware execution threads. Therefore, 2 OCPUs provides **4 virtual execution threads** on an Ampere Altra Neoverse N1 processor.
- **AMD Micro Compute**: Up to 2 `VM.Standard.E2.1.Micro` instances (1/8 OCPU, 1 GB RAM each) remain available, but are insufficient for MiniForce.

### Storage & Networking Limits
- **Block / Boot Volume Storage**: **200 GB total** across all Always Free instances (generous allocation allowing a 100–150 GB root volume for Ubuntu 24.04, Docker cache, MySQL data, and compiler scratch files).
- **Outbound Data Transfer (Egress)**: **10 TB per month** free across all regions (far exceeding MiniForce's projected bandwidth).
- **Public Networking**: 1 Reserved or Ephemeral Public IPv4 address included for free, plus free dual-stack IPv6.
- **Oracle Cloud VCN**: Free Virtual Cloud Network with subnets, route tables, and security lists (stateful firewall).

---

## 3. Realistic Resource Sizing Breakdown

MiniForce running on a single `VM.Standard.A1.Flex` instance (2 OCPUs, 12 GB RAM, 100 GB NVMe Boot Volume):

```
┌────────────────────────────────────────────────────────────────────────┐
│           Oracle Cloud Always Free Instance (12,288 MB Total RAM)      │
│                                                                        │
│  [ OS & Systemd: ~300 MB ]   [ MySQL 8.0: ~600 MB ]                    │
│  [ Nginx Web: ~40 MB ]       [ Spring Boot Backend: ~1,200 MB ]        │
│  [ Docker Daemon: ~150 MB ]  [ Judge Sandbox Peak: ~512 MB ]           │
│                                                                        │
│  ────────────────────────────────────────────────────────────────────  │
│  Total Peak Load: ~2,800 MB (23% of total RAM)                         │
│  FREE HEADROOM: ~9,400 MB (77% buffer remaining)                       │
└────────────────────────────────────────────────────────────────────────┘
```

| Component | Technology | Target Heap / Buffer Config | Estimated RAM | Estimated CPU |
| :--- | :--- | :--- | :---: | :---: |
| **Operating System** | Ubuntu 24.04 LTS aarch64 | Kernel, systemd, journald, SSH | ~300 MB | < 0.05 OCPU |
| **Web Server / Reverse Proxy** | Nginx 1.24+ | Serves React static build, proxies `/api` | ~40 MB | < 0.05 OCPU |
| **Relational Database** | MySQL 8.0 (Native ARM64) | `innodb_buffer_pool_size=384M`<br>`max_connections=50` | ~600 MB | 0.1 – 0.2 OCPU |
| **Backend API Service** | Spring Boot 3.5 (Java 17) | `-Xms512m -Xmx1024m`<br>Metaspace ~200MB | ~1,200 MB | 0.2 – 0.5 OCPU |
| **Docker Engine** | Docker CE 27+ / Containerd | Native host daemon | ~150 MB | < 0.05 OCPU |
| **Active Judge Sandbox** | `miniforce-judge:latest` | Per-container limit: `--memory 512m`<br>Timeout: 5000ms | ~512 MB (peak) | 1.0 OCPU (burst) |
| **TOTAL STEADY STATE** | | | **~2,290 MB** | **~0.4 OCPU** |
| **TOTAL PEAK (During Judging)**| | | **~2,802 MB** | **~1.4 OCPU** |
| **AVAILABLE CAPACITY** | **OCI Ampere A1** | **2 OCPUs / 12 GB RAM** | **12,288 MB** | **2.0 OCPU** |

### ARM64 Architecture Compatibility Verification
- **Java 17 & Python 3 Judge Image**: Root `Dockerfile` uses `amazoncorretto:17-alpine` which officially publishes multi-architecture manifests (`linux/amd64` and `linux/arm64/v8`). Tested and validated for both architectures. (Note: `eclipse-temurin:17-jdk-alpine` was audited and found to lack ARM64 manifests, and has been replaced with `amazoncorretto:17-alpine`).
- **Python 3**: Alpine package `python3` (Python 3.14) compiles and executes natively on `aarch64` within `miniforce-judge:latest`.
- **MySQL 8.0**: Official Ubuntu/Debian packages and Docker images support `linux/arm64`.
- **React Frontend**: Static HTML/CSS/JS bundles compiled by Vite are completely platform-independent.

---

## 4. OCI Account Eligibility, Availability & Uncertainties

Before signing up, the following constraints and operational characteristics must be noted:

### 1. Credit Card Verification (Anti-Fraud Gate)
- **Requirement**: Oracle requires a valid payment card to register, even for Always Free accounts.
- **Verification Charge**: A temporary authorization hold (~$1.00 or regional equivalent) is charged and immediately released.
- **Common Failure Causes**:
  - Virtual, prepaid, or disposable cards are automatically rejected.
  - Billing name and address entered in OCI **must match the bank statement letter-for-letter**.
  - International online transactions must be enabled on the card.
  - VPNs or proxy connections during registration trigger automated fraud rejection.

### 2. Regional Capacity ("Out of Host Capacity")
- In high-demand data centers (e.g., Frankfurt, London, Ashburn, Mumbai), Ampere A1 capacity occasionally exhausts.
- When creating an instance, OCI may report: `Out of host capacity for shape VM.Standard.A1.Flex`.
- **Mitigation**:
  - Select Home Regions with strong capacity availability (e.g., US-East Ashburn, US-West Phoenix, or EU regions with multiple Availability Domains).
  - Note: **Home Region cannot be changed once the account is created**.

### 3. Idle Instance Reclamation Policy
- Oracle monitors Always Free compute instances over a rolling 7-day period. An instance is flagged as **idle** if:
  - 95th percentile CPU utilization is **< 20%**, AND
  - 95th percentile network utilization is **< 20%**, AND
  - 95th percentile memory utilization is **< 20%** (on Ampere A1).
- **What happens**: Oracle automatically **stops** the VM. The boot volume and all data are preserved, and the instance can be manually restarted from the console.
- **Mitigations**:
  1. **Active Usage**: Regular use of the platform for contest solving and automated health checks keeps utilization above the threshold.
  2. **Cron Heartbeat**: A lightweight cron task running local health checks and problem verification.
  3. **Pay-As-You-Go (PAYG) Upgrade**: Upgrading the account to PAYG completely eliminates the idle reclamation policy. **As long as resources remain under the Always Free limits (2 OCPU / 12 GB RAM / 200 GB disk), OCI bills $0.00**.

---

## 5. Security Architecture & Docker Sandbox Hardening

Because the complete application and Docker judge reside on a single VM, robust defense-in-depth is essential:

```
[ Public Internet ]
        │
        ├── Port 443 (HTTPS) ──► [ Nginx TLS Termination ]
        ├── Port 80 (HTTP)   ──► [ 301 Redirect to HTTPS ]
        └── Port 22 (SSH)    ──► [ Key-Based Auth Only (Fail2ban) ]
                                            │
                                            ▼
                               [ Localhost Only (127.0.0.1) ]
                               ├── /     ──► React Static Files
                               ├── /api/ ──► Spring Boot API (8080)
                               └── MySQL ──► 127.0.0.1:3306 (Not exposed)
                                            │
                                            ▼
                               [ Local Docker Socket ]
                               /var/run/docker.sock (Never exposed over network)
                                            │
                                            ▼
                               [ Disposable Sandboxes ]
                               ├── --network none
                               ├── --read-only
                               ├── --user 1000:1000
                               ├── --memory 512m
                               └── --cpus 1.0
```

### Security Guardrails
1. **Never Expose the Docker Socket**: `/var/run/docker.sock` is accessible only to the local `miniforce` user on the host. It is never bound to a TCP port, never accessible from the outside world, and never mounted into untrusted containers.
2. **Container Sandbox Hardening**:
   - `--network none`: User code has zero network access. Cannot reach external APIs, internal LAN, or host localhost.
   - `--read-only`: The container filesystem is immutable. User code cannot alter system binaries or persist malware.
   - `--tmpfs /tmp:rw,noexec,nosuid,size=50m`: Disposable scratch space in memory.
   - `--user 1000:1000`: Runs as unprivileged non-root user.
   - `--pids-limit 64`: Prevents fork bomb attacks.
   - Memory and CPU hard limits: Max 512 MB, 1.0 CPU, 5000 ms wall-clock timeout.
3. **Firewall (OCI Security Lists + UFW)**:
   - Inbound allowed: Ports 80 and 443 (public), Port 22 (SSH with ed25519 key authentication).
   - Inbound blocked: Port 3306 (MySQL), Port 8080 (Spring Boot), Port 2375/2376 (Docker).

---

## 6. Step-by-Step Deployment Roadmap

> [!IMPORTANT]
> **Cloud Spending & Action Constraint**: Do NOT perform any of these steps until you have registered your account and given explicit approval to proceed.

### Phase 1: OCI Account Setup & VM Provisioning
1. Register for an [Oracle Cloud Always Free account](https://www.oracle.com/cloud/free/).
2. Select an appropriate Home Region.
3. In the OCI Console, navigate to **Compute** → **Instances** → **Create Instance**.
4. Configure VM settings:
   - **Name**: `miniforce-production`
   - **Image**: `Canonical Ubuntu 24.04 LTS Minimal aarch64`
   - **Shape**: `Ampere` → `VM.Standard.A1.Flex`
   - **OCPUs**: `2`
   - **Memory**: `12 GB`
   - **Boot Volume**: `100 GB` (Custom size within 200 GB limit)
   - **SSH Keys**: Paste your public SSH key (`id_ed25519.pub`).
   - **Networking**: Assign a public IPv4 address.
5. In the **Virtual Cloud Network (VCN)** Security List, add Ingress Rules for Port 80 (HTTP) and Port 443 (HTTPS) from `0.0.0.0/0`.

### Phase 2: Host Software Provisioning
SSH into the instance and install required runtimes:

```bash
# Update and install dependencies
sudo apt-get update && sudo apt-get upgrade -y
sudo apt-get install -y ca-certificates curl gnupg ufw git nginx certbot python3-certbot-nginx mysql-server openjdk-17-jre-headless

# Install official Docker Engine (ARM64)
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# Create dedicated application user
sudo useradd -m -s /bin/bash miniforce
sudo usermod -aG docker miniforce

# Configure UFW Firewall
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

### Phase 3: Database Provisioning & Schema Import
1. Configure MySQL 8.0:
   ```bash
   sudo mysql -e "CREATE DATABASE miniforce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
   sudo mysql -e "CREATE USER 'miniforce'@'127.0.0.1' IDENTIFIED BY 'SECURE_DB_PASSWORD';"
   sudo mysql -e "GRANT ALL PRIVILEGES ON miniforce.* TO 'miniforce'@'127.0.0.1'; FLUSH PRIVILEGES;"
   ```
2. Import baseline schema and all 50 public problems:
   ```bash
   mysql -h 127.0.0.1 -u miniforce -p'SECURE_DB_PASSWORD' miniforce < database/miniforce.sql
   for f in database/migrations/*.sql; do
       mysql -h 127.0.0.1 -u miniforce -p'SECURE_DB_PASSWORD' miniforce < "$f"
   done
   ```
3. Verify problem catalog integrity:
   ```sql
   SELECT COUNT(*) FROM problems WHERE id BETWEEN 1 AND 50; -- Exactly 50
   SELECT COUNT(*) FROM problem_hints; -- Exactly 150
   ```

### Phase 4: Build Judge Sandbox & Deploy Backend
1. Build `miniforce-judge:latest` on ARM64:
   ```bash
   cd /opt/miniforce
   sudo docker build -t miniforce-judge:latest .
   ```
2. Build or copy `miniforce-api-1.0.0.jar` to `/opt/miniforce/app.jar`.
3. Create `/etc/miniforce/miniforce.env`:
   ```ini
   PORT=8080
   SPRING_PROFILES_ACTIVE=prod
   MINIFORCE_DB_URL=jdbc:mysql://127.0.0.1:3306/miniforce?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   MINIFORCE_DB_USER=miniforce
   MINIFORCE_DB_PASSWORD=SECURE_DB_PASSWORD
   MINIFORCE_JWT_SECRET=GENERATED_64_CHAR_HEX_SECRET
   MINIFORCE_FRONTEND_ORIGIN=https://miniforce.yourdomain.com
   MINIFORCE_JUDGE_REAL_ENABLED=true
   MINIFORCE_JUDGE_SCHEDULER_ENABLED=true
   MINIFORCE_JUDGE_WORKER_URL=
   MINIFORCE_JUDGE_INTERNAL_SECRET=
   ```
4. Configure systemd service `/etc/systemd/system/miniforce.service`:
   ```ini
   [Unit]
   Description=MiniForce Backend Service
   After=network.target mysql.service docker.service
   Requires=mysql.service docker.service

   [Service]
   Type=simple
   User=miniforce
   WorkingDirectory=/opt/miniforce
   EnvironmentFile=/etc/miniforce/miniforce.env
   ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar /opt/miniforce/app.jar
   Restart=always
   RestartSec=5

   [Install]
   WantedBy=multi-user.target
   ```
5. Enable and start: `sudo systemctl enable --now miniforce`.

### Phase 5: Deploy React Frontend & Nginx SSL
1. Copy built frontend artifacts (`dist/`) to `/var/www/miniforce/dist`.
2. Configure `/etc/nginx/sites-available/miniforce`:
   ```nginx
   server {
       listen 80;
       server_name miniforce.yourdomain.com;

       root /var/www/miniforce/dist;
       index index.html;

       location / {
           try_files $uri $uri/ /index.html;
       }

       location /api/ {
           proxy_pass http://127.0.0.1:8080/api/;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }
   ```
3. Obtain free SSL certificate:
   ```bash
   sudo ln -s /etc/nginx/sites-available/miniforce /etc/nginx/sites-enabled/
   sudo nginx -t && sudo systemctl reload nginx
   sudo certbot --nginx -d miniforce.yourdomain.com
   ```

### Phase 6: Public End-to-End Verification Checklist
- [ ] Open `https://miniforce.yourdomain.com/` — Jolly Roger logo renders, dark navy aesthetic loads.
- [ ] Navigate to `/problems` — catalog badge displays `50 Problems · Java 17 + Python 3`.
- [ ] Check `/api/health` — returns HTTP 200 `{"status":"UP","database":"UP"}`.
- [ ] Register new account & update avatar in `/profile` — avatar persists across reload.
- [ ] Run & Submit Problem 1 (**Two Sum**) with Java 17 — returns `ACCEPTED`.
- [ ] Run & Submit Problem 1 (**Two Sum**) with Python 3 — returns `ACCEPTED`.
- [ ] Run & Submit Problem 50 (**Merge Two Sorted Lists**) with Python 3 — starter code loads, execution returns `ACCEPTED`.

---

## 7. Rollback & Disaster Recovery Procedures

1. **Pre-Deployment Backup**:
   ```bash
   mysqldump -h 127.0.0.1 -u miniforce -p miniforce | gzip > /opt/miniforce/backups/backup_$(date +%F).sql.gz
   ```
2. **Application Rollback**:
   If an update introduces regressions:
   ```bash
   # Revert to previous JAR or frontend build
   cp /opt/miniforce/app.jar.bak /opt/miniforce/app.jar
   sudo systemctl restart miniforce
   ```
3. **Database Rollback**:
   ```bash
   gunzip < /opt/miniforce/backups/backup_YYYY-MM-DD.sql.gz | mysql -h 127.0.0.1 -u miniforce -p miniforce
   ```
4. **Local Development Preservation**:
   All local configurations, `START-MINIFORCE.bat`, `STOP-MINIFORCE.bat`, and local test suites remain completely untouched and operational.

# MiniForce Dedicated Linux Judge Worker Deployment Guide

This guide describes how to provision, configure, harden, and operate a dedicated Linux virtual machine as the isolated code execution worker for MiniForce.

---

## 1. Architectural Overview & Threat Model

In competitive programming, the judging engine executes **untrusted, arbitrary user-submitted code** in Java 17 and Python 3.

```
┌─────────────────────────────────┐                 ┌──────────────────────────────────────┐
│   Railway PaaS (Multi-Tenant)   │                 │    Dedicated Linux Worker (VPS)      │
│                                 │                 │                                      │
│  React Frontend ──► Spring Boot │  HTTPS / Secret │  Nginx (TLS) ──► Spring Boot Worker │
│  Web Container      Web API     ├────────────────►│                  (Local Host)        │
│                                 │                 │                      │               │
│                                 │                 │                      ▼               │
└─────────────────────────────────┘                 │             Docker Engine Socket     │
                                                    │             /var/run/docker.sock     │
                                                    │                      │               │
                                                    │                      ▼               │
                                                    │            Disposable Sandboxes      │
                                                    │            ┌────────────────────┐    │
                                                    │            │ --network none     │    │
                                                    │            │ --read-only        │    │
                                                    │            │ --user 1000:1000   │    │
                                                    │            │ --memory 512m      │    │
                                                    │            │ --cpus 1.0         │    │
                                                    │            └────────────────────┘    │
                                                    └──────────────────────────────────────┘
```

### Security Isolation Principles
1. **Never Expose the Docker Daemon**: The Docker daemon socket (`/var/run/docker.sock`) is NEVER exposed over the internet, TCP, or SSH tunnel.
2. **REST API Boundary**: The worker exposes ONLY the authenticated endpoint `POST /api/internal/judge/execute`.
3. **Secret Header Authorization**: All requests must supply a valid `X-Judge-Secret` header matching `MINIFORCE_JUDGE_INTERNAL_SECRET`.
4. **Input Constraints**: Payloads are strictly validated:
   - Problem IDs restricted to public catalog `1` through `50`.
   - Languages restricted to `JAVA_17` and `PYTHON_3`.
   - Source code payloads capped at `64 KB`.
5. **Sandbox Hardening**: Every run creates a fresh, isolated container with:
   - No internet/LAN network access (`--network none`)
   - Read-only root filesystem (`--read-only`)
   - Secure temporary directory (`--tmpfs /tmp:rw,noexec,nosuid,size=50m`)
   - Non-root user execution (`--user 1000:1000`)
   - CPU, memory, and process limits (`--memory 512m --cpus 1.0 --pids-limit 64`)

---

## 2. Server Sizing & Cost

A small, dedicated cloud VPS provides ample capacity for running code submissions:

| Cloud Provider | Instance Size | vCPU | RAM | Disk | Estimated Cost |
| :--- | :--- | :---: | :---: | :---: | :--- |
| **Hetzner Cloud** | CX22 | 2 vCPU | 4 GB | 40 GB NVMe | ~€3.80 / month |
| **DigitalOcean** | Basic Droplet | 2 vCPU | 2 GB | 50 GB SSD | ~$12 / month (or $6/mo for 1 vCPU / 1 GB) |
| **AWS Lightsail** | Standard | 2 vCPU | 2 GB | 60 GB SSD | ~$10 / month |

Recommended OS: **Ubuntu 24.04 LTS (x86_64)**.

---

## 3. Provisioning Step-by-Step

### Step 3.1: Install Docker Engine
SSH into the worker instance and install official Docker CE:

```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg ufw

# Add Docker's official GPG key
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# Set up repository
echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# Verify Docker installation
sudo docker run --rm hello-world
```

### Step 3.2: Install Java 17 Runtime & Build Tools
```bash
sudo apt-get install -y openjdk-17-jre-headless git
```

### Step 3.3: Build Judge Sandbox Image (`miniforce-judge:latest`)
Clone the repository or transfer the project `Dockerfile`:

```bash
mkdir -p /opt/miniforce
cd /opt/miniforce

# Copy the repository Dockerfile to /opt/miniforce/Dockerfile
# Build the real judge sandbox image
sudo docker build -t miniforce-judge:latest .

# Verify image is present
sudo docker images | grep miniforce-judge
```

### Step 3.4: Configure Firewall (UFW)
Block all unsolicited inbound traffic except SSH and HTTPS:

```bash
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

---

## 4. Setting Up the Worker Application

### Step 4.1: Deploy Backend Executable
Build the backend JAR on Railway CI or locally (`mvn clean package -DskipTests`) and copy `miniforce-api-1.0.0.jar` to `/opt/miniforce/miniforce-worker.jar`.

### Step 4.2: Create Environment Configuration
Create a secure environment file:

```bash
sudo mkdir -p /etc/miniforce
sudo nano /etc/miniforce/worker.env
```

Add the following configuration:

```ini
PORT=8080
SPRING_PROFILES_ACTIVE=prod

# Judge Settings
MINIFORCE_JUDGE_REAL_ENABLED=true
MINIFORCE_JUDGE_WORKER_URL=
MINIFORCE_JUDGE_INTERNAL_SECRET=YOUR_64_CHAR_HEX_SECRET_HERE

# Security Settings
MINIFORCE_JWT_SECRET=YOUR_64_CHAR_HEX_JWT_SECRET_HERE
MINIFORCE_FRONTEND_ORIGIN=*

# Async Database Queue (Optional: uncomment if worker processes queued jobs)
# MINIFORCE_JUDGE_SCHEDULER_ENABLED=true
# MINIFORCE_DB_URL=jdbc:mysql://<railway-mysql-host>:3306/<database>?useSSL=true
# MINIFORCE_DB_USER=<railway-mysql-user>
# MINIFORCE_DB_PASSWORD=<railway-mysql-password>
```

Secure the permissions:
```bash
sudo chmod 600 /etc/miniforce/worker.env
sudo chown root:root /etc/miniforce/worker.env
```

### Step 4.3: Configure Systemd Service
Create `/etc/systemd/system/miniforce-worker.service`:

```ini
[Unit]
Description=MiniForce Judge Worker Service
After=network.target docker.service
Requires=docker.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/miniforce
EnvironmentFile=/etc/miniforce/worker.env
ExecStart=/usr/bin/java -Xms512m -Xmx1536m -jar /opt/miniforce/miniforce-worker.jar
Restart=always
RestartSec=5
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Reload and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable miniforce-worker
sudo systemctl start miniforce-worker
sudo systemctl status miniforce-worker
```

---

## 5. Reverse Proxy with HTTPS (Nginx & Certbot)

Install Nginx and obtain a free SSL certificate using Let's Encrypt:

```bash
sudo apt-get install -y nginx certbot python3-certbot-nginx
```

Configure `/etc/nginx/sites-available/judge`:

```nginx
server {
    server_name judge.yourdomain.com;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 60s;
        proxy_connect_timeout 10s;
    }
}
```

Enable site and activate SSL:
```bash
sudo ln -s /etc/nginx/sites-available/judge /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
sudo certbot --nginx -d judge.yourdomain.com
```

---

## 6. Verification and Health Checks

### Test Health Endpoint
```bash
curl -i https://judge.yourdomain.com/api/health
# Expected: HTTP 200 OK {"status":"UP", ...}
```

### Test Fail-Closed Defense (Server Secret Unconfigured)
If `MINIFORCE_JUDGE_INTERNAL_SECRET` is unset or blank on the worker:
```bash
curl -i -X POST https://judge.yourdomain.com/api/internal/judge/execute \
  -H "Content-Type: application/json" \
  -H "X-Judge-Secret: any-value" \
  -d '{"problemId":1}'
# Expected: HTTP 403 Forbidden {"status":"SYSTEM_ERROR", "systemErrorMessage":"Internal judge execution service is not configured."}
```

### Test Authentication Rejection (Missing or Invalid Header)
```bash
curl -i -X POST https://judge.yourdomain.com/api/internal/judge/execute \
  -H "Content-Type: application/json" \
  -d '{"problemId":1}'
# Expected: HTTP 401 Unauthorized {"status":"SYSTEM_ERROR", "systemErrorMessage":"Unauthorized: invalid internal judge secret."}
```

### Test Execution with Valid Secret
```bash
curl -i -X POST https://judge.yourdomain.com/api/internal/judge/execute \
  -H "Content-Type: application/json" \
  -H "X-Judge-Secret: YOUR_64_CHAR_HEX_SECRET_HERE" \
  -d '{
    "submissionId": 999,
    "problemId": 1,
    "language": "JAVA_17",
    "sourceCode": "class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0,1}; } }",
    "contract": { "testCases": [{"id":1,"inputData":"[2,7,11,15]\n9","expectedOutput":"[0,1]"}] }
  }'
# Expected: HTTP 200 OK with verdict ACCEPTED
```

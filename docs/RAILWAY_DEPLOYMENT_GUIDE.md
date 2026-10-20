# MiniForce Production Railway Deployment Architecture Guide

This guide details the complete, verified deployment architecture for MiniForce on Railway (Web Application, React Frontend, and MySQL Database) with a dedicated Linux Judge Worker.

---

## 1. Architectural Reality: Competitive Programming in the Cloud

Deploying an automated judging platform like MiniForce requires executing **arbitrary, untrusted user-submitted code** in Java 17 and Python 3. 

In local development, MiniForce leverages the host's Docker engine (`/var/run/docker.sock`), executing user code in disposable, isolated containers constrained by:
- Memory limits (`512MB`)
- CPU quota (`1.0 core`)
- Execution timeout (`5000ms`)
- Read-only root filesystems
- Complete network isolation (`--network none`)
- Non-root unprivileged execution (`1000:1000`)

### The Cloud PaaS Sandbox Constraint
Modern Platform-as-a-Service (PaaS) providers such as **Railway, Render, Fly.io, and Heroku** run applications inside unprivileged multi-tenant containers. Standard Railway container environments:
1. Do not expose a Docker daemon socket (`/var/run/docker.sock`).
2. Prohibit running `docker run` commands inside the app container without root privileges and kernel namespace creation permissions (`CAP_SYS_ADMIN`).
3. Exposing the raw Docker socket over TCP (`DOCKER_HOST`) to public cloud networks is a critical security vulnerability and violates platform isolation rules.

---

## 2. Verified Production Architecture

MiniForce adopts the industry-standard decoupled architecture used by LeetCode and Codeforces:

```
[ Browsers / Clients ]
         │
         ▼
[ Railway: React/Vite SPA ] ─── HTTPS ───► [ Railway: Spring Boot Web API ]
                                                   │                 │
                                                   ▼                 ▼
                                         [ Railway MySQL ]   [ HTTPS / X-Judge-Secret ]
                                                                     │
                                                                     ▼
                                                      [ Dedicated Worker VPS / Droplet ]
                                                      (Ubuntu 24.04 + Docker Engine)
                                                      ├── InternalJudgeWorkerController
                                                      └── Disposable Sandboxes
                                                          (Java 17 & Python 3)
```

### Key Components
1. **Frontend Service (Railway)**:
   - Root Directory: `frontend/react`
   - Static/Node Nixpacks builder runs `npm install && npm run build`
   - Serves SPA via `npm run start` (`vite preview --host 0.0.0.0 --port ${PORT:-5173}`)
   - Build-time environment variable: `VITE_API_URL`
2. **Backend Web API (Railway)**:
   - Root Directory: `backend`
   - Multi-stage Docker build: `backend/Dockerfile`
   - Port dynamically bound: `server.port=${PORT:8080}`
   - Health check endpoint: `GET /api/health` (HTTP 200 with DB status)
   - Synchronous judge requests routed to `RemoteJudgeExecutionAdapter` via `MINIFORCE_JUDGE_WORKER_URL` and `MINIFORCE_JUDGE_INTERNAL_SECRET`
   - Background queue scheduler disabled (`MINIFORCE_JUDGE_SCHEDULER_ENABLED=false`)
3. **Database (Railway MySQL Plugin)**:
   - Managed MySQL 8.0 instance with persistent volume
   - Strict Hibernate validation (`spring.jpa.hibernate.ddl-auto=validate`)
   - Schema and catalog initialized via `database/miniforce.sql` + versioned migrations
4. **Dedicated Judge Worker (Linux VPS)**:
   - Hosted on Hetzner, DigitalOcean, or AWS Lightsail (~$4–$6/month)
   - Runs official Docker Engine with `miniforce-judge:latest` image
   - Serves `POST /api/internal/judge/execute` authenticated via `X-Judge-Secret` header
   - Executes disposable sandboxes locally via `DockerRealJudgeAdapter`

---

## 3. Related Documentation

- Detailed Environment Variables: [`docs/PRODUCTION_ENVIRONMENT_VARIABLES.md`](file:///C:/Users/Yash%20Deshmukh/OneDrive/Desktop/miniforce4/docs/PRODUCTION_ENVIRONMENT_VARIABLES.md)
- Dedicated Worker Setup Guide: [`docs/JUDGE_WORKER_DEPLOYMENT.md`](file:///C:/Users/Yash%20Deshmukh/OneDrive/Desktop/miniforce4/docs/JUDGE_WORKER_DEPLOYMENT.md)
- Database Migration & Rollback: [`docs/PRODUCTION_MIGRATION_AND_ROLLBACK.md`](file:///C:/Users/Yash%20Deshmukh/OneDrive/Desktop/miniforce4/docs/PRODUCTION_MIGRATION_AND_ROLLBACK.md)

---

## 4. Step-by-Step Railway Deployment Walkthrough

### Step 1: Provision the MySQL Database
1. In your Railway project, click **New** → **Database** → **MySQL**.
2. Railway generates variables: `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD`.
3. Follow [`docs/PRODUCTION_MIGRATION_AND_ROLLBACK.md`](file:///C:/Users/Yash%20Deshmukh/OneDrive/Desktop/miniforce4/docs/PRODUCTION_MIGRATION_AND_ROLLBACK.md) to import `database/miniforce.sql` and verify that all 50 problems exist.

### Step 2: Deploy the Spring Boot Backend
1. Click **New** → **GitHub Repo** → select repository.
2. Under service **Settings**:
   - Set **Root Directory** to `backend`.
   - Set **Healthcheck Path** to `/api/health`.
3. Under service **Variables**, add:
   ```ini
   PORT=8080
   SPRING_PROFILES_ACTIVE=prod
   MINIFORCE_DB_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   MINIFORCE_DB_USER=${{MySQL.MYSQLUSER}}
   MINIFORCE_DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
   MINIFORCE_JWT_SECRET=<generated-64-character-hex-secret>
   MINIFORCE_FRONTEND_ORIGIN=https://<your-frontend-subdomain>.up.railway.app
   MINIFORCE_JUDGE_REAL_ENABLED=true
   MINIFORCE_JUDGE_SCHEDULER_ENABLED=false
   MINIFORCE_JUDGE_WORKER_URL=https://<your-judge-worker-domain>
   MINIFORCE_JUDGE_INTERNAL_SECRET=<generated-64-character-hex-secret>
   ```
4. Deploy the service. Verify Railway displays green health check on `/api/health`.

### Step 3: Deploy the React Frontend
1. Click **New** → **GitHub Repo** → select repository.
2. Under service **Settings**:
   - Set **Root Directory** to `frontend/react`.
3. Under service **Variables**, add:
   ```ini
   VITE_API_URL=https://<your-backend-subdomain>.up.railway.app
   ```
4. Railway builds the production bundle (`tsc -b && vite build`) and serves it via preview on `0.0.0.0:${PORT}`.

---

## 5. Post-Deployment Verification Checklist

- [ ] Open `https://<frontend-url>/problems` — catalog loads exactly 50 public problems.
- [ ] Catalog badge shows `50 Problems · Java 17 + Python 3`.
- [ ] Navigate to `/api/health` on backend domain — returns `{"status":"UP","database":"UP"}`.
- [ ] Register a new user account on `/register`.
- [ ] Update user avatar in `/profile` and refresh — avatar persists seamlessly.
- [ ] Visit `/leaderboard` — user displays correctly with avatar.
- [ ] Solve Problem 1 (**Two Sum**) with Java 17 — returns `ACCEPTED`.
- [ ] Solve Problem 1 (**Two Sum**) with Python 3 — returns `ACCEPTED`.
- [ ] Solve Problem 50 (**Merge Two Sorted Lists**) in Python 3 — starter code loads and execution succeeds.

---

## 6. Monthly Cost Estimates

| Component | Platform / Host | Specs | Estimated Cost |
| :--- | :--- | :--- | :--- |
| **Web API + Frontend + MySQL** | Railway | Shared containers ($5/month included credit on Hobby plan) | ~$5.00 / month |
| **Dedicated Judge Worker** | Hetzner Cloud (CX22) / DigitalOcean | 2 vCPU, 4 GB RAM, 40 GB NVMe | ~$3.80 – $6.00 / month |
| **Total Estimated Cost** | | | **~$8.80 – $11.00 / month** |

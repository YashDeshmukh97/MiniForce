# MiniForce Production Environment Variables Reference

This document provides the complete, authoritative inventory of all configuration variables required to deploy MiniForce across Railway (Web API + Frontend + MySQL) and the dedicated Linux Judge Worker.

---

## 1. Web API Service (`backend` on Railway)

The backend service runs the Spring Boot application in production profile (`SPRING_PROFILES_ACTIVE=prod`).

| Environment Variable | Required | Default / Fallback | Sensitivity | Description & Recommended Production Value |
| :--- | :---: | :--- | :---: | :--- |
| `PORT` | Yes (Railway) | `8080` | Low | Assigned dynamically by Railway. Spring Boot binds to `0.0.0.0:${PORT}`. |
| `SPRING_PROFILES_ACTIVE` | **Yes** | `dev` | Low | Must be set to `prod` to activate strict validation and pool tuning. |
| `MINIFORCE_DB_URL` | **Yes** | `${SPRING_DATASOURCE_URL}` | Medium | JDBC connection string. On Railway with MySQL plugin, use:<br>`jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `MINIFORCE_DB_USER` | **Yes** | `${MYSQLUSER:root}` | Medium | Database username. Set to `${{MySQL.MYSQLUSER}}`. |
| `MINIFORCE_DB_PASSWORD` | **Yes** | `${MYSQLPASSWORD:}` | **High** | Database password. Set to `${{MySQL.MYSQLPASSWORD}}`. |
| `MINIFORCE_JWT_SECRET` | **Yes** | *None (Fails startup)* | **CRITICAL** | Minimum 32 characters (recommended 64 hex chars). Generates and verifies HMAC-SHA256 user authentication tokens. |
| `MINIFORCE_FRONTEND_ORIGIN` | **Yes** | *None* | Low | Strict CORS whitelist. Comma-separated allowed origins without trailing slashes. Example: `https://miniforce.up.railway.app,https://miniforce.example.com` |
| `MINIFORCE_DB_POOL_MAX` | No | `20` | Low | Maximum HikariCP database connections. |
| `MINIFORCE_DB_POOL_MIN` | No | `5` | Low | Minimum idle HikariCP database connections. |
| `MINIFORCE_JUDGE_REAL_ENABLED` | **Yes** | `true` | Low | Must remain `true` to judge public problems 1–50 via real Docker execution. |
| `MINIFORCE_JUDGE_SCHEDULER_ENABLED` | **Yes** | `false` | Low | Must be `false` on the web service container because Railway containers cannot run Docker. The queue processor is run on the worker. |
| `MINIFORCE_JUDGE_WORKER_URL` | **Yes** | *Empty (Local)* | Medium | Public or internal URL of the dedicated Linux Judge Worker VM. Example: `https://judge.example.com` or `http://10.0.0.5:8080`. |
| `MINIFORCE_JUDGE_INTERNAL_SECRET` | **Yes** | *Empty* | **CRITICAL** | Secret token sent in the `X-Judge-Secret` header to authorize synchronous Run/Submit code execution on the worker. |
| `MINIFORCE_ADMIN_PASSWORD` | Optional | *Empty* | **High** | Strong passphrase for initial admin bootstrap. When configured, logging in with `admin` rotates and persists a fresh BCrypt hash. |
| `MINIFORCE_ENFORCE_CREDENTIAL_ROTATION` | No | `true` (in prod) | Low | When `true`, disables default development seed hashes (`$2a$10$N9qo...`) across all seeded accounts. |

---

## 2. React Frontend Service (`frontend/react` on Railway)

The frontend is built with Vite and React. Variables prefixed with `VITE_` are embedded into client bundles during the build step.

| Environment Variable | Required | Default / Fallback | Sensitivity | Description & Recommended Production Value |
| :--- | :---: | :--- | :---: | :--- |
| `VITE_API_URL` | **Yes** | `/api` | Low | Public URL of the backend API service. Example: `https://api.miniforce.example.com` or `https://backend-production.up.railway.app`. Automatically normalizes trailing slashes and ensures `/api` routing. |
| `VITE_API_BASE` | Alternative | `${VITE_API_URL}/api` | Low | Alternative full base URL parameter. If `VITE_API_URL` is set, `VITE_API_BASE` is optional. |
| `PORT` | Yes (Railway) | `5173` | Low | Assigned dynamically by Railway. Used by `vite preview --port ${PORT:-5173}`. |

---

## 3. Dedicated Linux Judge Worker VM (Host / VPS)

The worker VM runs Ubuntu 24.04 with Docker Engine and executes either as an asynchronous database queue consumer or as a synchronous judge server behind HTTPS.

| Environment Variable | Required | Default / Fallback | Sensitivity | Description & Recommended Production Value |
| :--- | :---: | :--- | :---: | :--- |
| `PORT` | No | `8080` | Low | Local port bound by Spring Boot on the worker (typically proxied behind Nginx / Caddy on port 443). |
| `SPRING_PROFILES_ACTIVE` | **Yes** | `prod` | Low | Production profile. |
| `MINIFORCE_DB_URL` | Optional* | *None* | Medium | Remote JDBC connection string to Railway MySQL. *Required if running asynchronous DB queue worker. |
| `MINIFORCE_DB_USER` | Optional* | *None* | Medium | Remote database user. |
| `MINIFORCE_DB_PASSWORD` | Optional* | *None* | **High** | Remote database password. |
| `MINIFORCE_JUDGE_REAL_ENABLED` | **Yes** | `true` | Low | Enables execution against local Docker daemon on the worker. |
| `MINIFORCE_JUDGE_SCHEDULER_ENABLED` | Optional | `true` | Low | Set to `true` if the worker polls the database queue for pending submissions. Set to `false` if strictly serving synchronous REST execution. |
| `MINIFORCE_JUDGE_WORKER_URL` | **Yes** | *Empty* | Low | **Must be left empty** so the worker routes execution to its local `DockerRealJudgeAdapter`. |
| `MINIFORCE_JUDGE_INTERNAL_SECRET` | **Yes** | *None* | **CRITICAL** | Shared secret token that must match `MINIFORCE_JUDGE_INTERNAL_SECRET` configured on Railway web API. Requests to `/api/internal/judge/execute` without this secret header are rejected with HTTP 401. |
| `MINIFORCE_JWT_SECRET` | **Yes** | *None* | **High** | Secret used for internal token signing (min 32 characters). |
| `MINIFORCE_FRONTEND_ORIGIN` | No | `*` | Low | Internal CORS setting (not exposed to public browsers). |

---

## 4. Secret Generation Best Practices

Generate cryptographically secure secrets before deployment using standard CLI utilities:

```bash
# Generate 64-character hex secret for MINIFORCE_JWT_SECRET
openssl rand -hex 32

# Generate 64-character hex secret for MINIFORCE_JUDGE_INTERNAL_SECRET
openssl rand -hex 32
```

> [!CAUTION]
> Never commit production secrets, database credentials, or `.env` files to git. Use Railway's encrypted environment variable dashboard for the web platform, and systemd environment files with `chmod 600` on the worker host.

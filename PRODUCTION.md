# MiniForce Production Configuration Guide

This guide details the requirements and best practices for deploying MiniForce to production.

## 1. Environment Variables

Production deployments MUST set the following environment variables:

| Variable | Description | Example |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Must be set to `prod` | `prod` |
| `MINIFORCE_DB_URL` | JDBC connection string | `jdbc:mysql://<db-host>:3306/miniforce?useSSL=true` |
| `MINIFORCE_DB_USER` | Database username | `miniforce_user` |
| `MINIFORCE_DB_PASSWORD` | Database password | `CHANGE_ME_DATABASE_PASSWORD` |
| `MINIFORCE_JWT_SECRET` | 256-bit+ secure random secret | `CHANGE_ME_TO_A_LONG_RANDOM_SECRET` |
| `MINIFORCE_FRONTEND_ORIGIN`| Exact origin of the React client | `https://your-domain.example` |

## 2. Running Locally vs Production

**Local Development (Backend)**
Uses the default `dev` profile. Simply run:
```bash
mvn spring-boot:run
```
Requires `MINIFORCE_DB_PASSWORD` and `MINIFORCE_JWT_SECRET` in your local environment.

**Production (Backend)**
Ensure `SPRING_PROFILES_ACTIVE=prod` is set so it uses `application-prod.properties`.

**Local Development (React)**
```bash
npm run dev
```

**Production (React Build)**
```bash
npm run build
```
Note: If your API is hosted at a different origin than your frontend in production, supply `VITE_API_BASE` as an environment variable *during the build process*. If omitted, the frontend will default to sending API requests to `/api` on the same origin.

## 3. Database Requirements

MiniForce uses MySQL.
- **Production mode (`prod` profile) will NOT auto-create the database schema.** It uses `ddl-auto=validate`. You must run the database schema scripts manually before starting the backend.
- The connection pool (HikariCP) defaults to a max size of 20 and min idle of 5. These can be adjusted via `MINIFORCE_DB_POOL_MAX` and `MINIFORCE_DB_POOL_MIN`.

## 4. Runtime Prerequisites

- **Java**: 26
- **Docker**: The host machine must have Docker installed and the Docker daemon running to support the JudgeWorker sandbox execution.

## 5. Security & Rate Limiting

- Do not expose Docker daemon to external networks.
- MiniForce rate-limits login attempts (10/min) and code submissions (5/min). 
- Actuator endpoints are completely removed/disabled to prevent internal detail leakage.
- Production error handlers strip SQL traces and stack traces before returning JSON responses.

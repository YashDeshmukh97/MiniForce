# MiniForce

## Overview
MiniForce is a full-stack competitive programming platform designed for coding practice. It features a modern React frontend and a Spring Boot API that orchestrates secure, isolated execution of user code via a Docker sandbox, supporting real-time Java and Python judging.

## Features
- User registration/login
- JWT authentication
- Problem catalog
- 100 total problems (50 public problems for normal users)
- Admin access to hidden problems
- Java judging
- Python judging for supported real-judge problems
- Docker sandbox
- Run/Submit workflow
- Submission history
- Profile
- Contests
- Leaderboard
- Discussions
- Admin panel
- Rate limiting
- Production configuration

## Technology Stack

**Frontend:**
- React 19
- TypeScript
- Vite
- Tailwind CSS v4
- React Router v7
- Lucide React
- Framer Motion

**Backend:**
- Java 17
- Spring Boot 3.5
- MySQL 8.0
- JWT
- Maven

**Judge:**
- Docker
- Java
- Python
- Isolated sandbox

## Architecture
The platform is designed around a scalable and secure code execution pipeline:

```
React (Frontend)
↓
Spring Boot REST API
↓
Authentication / Submission Queue
↓
JudgeWorker (Async Poller)
↓
JudgeExecutionRouter
↓
DockerRealJudgeAdapter / MockJudgeExecutionAdapter
↓
Docker Sandbox (miniforce-judge:latest)
↓
Result Persistence (MySQL)
```
*Note: Only the explicitly configured real-judge problems execute user code inside the isolated Docker sandbox. Other problems fallback to a deterministic mock/demo judge.*

## Real Judge Problems
MiniForce currently features full real-judge integration (compilation and sandbox execution) for the following problems:

1. Two Sum (Java, Python)
2. Valid Parentheses (Java, Python)
3. Longest Substring Without Repeating Characters (Java, Python)
8. Best Time to Buy and Sell Stock (Java, Python)
12. Climbing Stairs (Java, Python)
13. Product of Array Except Self (Java, Python)
14. Maximum Subarray (Java, Python)
20. Longest Consecutive Sequence (Java, Python)

## Project Structure
- `frontend/react/` - The React Vite application.
- `backend/` - The Spring Boot application.
- `database/` - MySQL schema and migration files.
- `docs/` - System architecture and deployment documentation.
- `.env` - Environment variable configuration for Compose.
- `docker-compose.yml` - Production container layout.
- `START-MINIFORCE.bat` / `STOP-MINIFORCE.bat` - Local development orchestration scripts.

## Requirements
To run MiniForce locally, you will need:
- Git
- Java 17
- Maven 3.9+
- Node.js (20+)
- npm
- MySQL 8.0+
- Docker Desktop (Required for real judge execution)

## Installation
Clone the repository to your local machine:
```bash
git clone YOUR_GITHUB_REPOSITORY_URL
cd miniforce4
```

### Frontend Setup
To run the modern React frontend:
```bash
cd frontend/react
npm install
npm run dev
```

### Backend Setup
The Spring Boot backend requires a MySQL database and specific environment variables. Development and production configurations are separated via Spring profiles.

### Database Setup
A running MySQL 8.0 instance is required. 
*Note: Production uses `ddl-auto=validate`. You must load the initial schema (`database/miniforce.sql`) manually or rely on the configured flyway/migrations if enabled; it will not auto-generate production tables.*

### Docker Judge Setup
To use the real judge, Docker Desktop MUST be running on your host machine. The backend orchestrates dynamic sandboxes based on the `miniforce-judge:latest` image.
**Sandbox Security Controls:**
- Network isolation (`--network none`)
- Read-only root filesystem (`--read-only`)
- Writable scratch space (`--tmpfs /tmp`)
- Non-root execution (`--user 1000:1000`)
- CPU, memory, and PID limits

## Running MiniForce
For local development, you can quickly spin up the backend API and database connection using the provided automation scripts (if configured for your local paths):
- Run `START-MINIFORCE.bat` to boot the environment.
- Run `STOP-MINIFORCE.bat` to gracefully terminate.

## Environment Variables
The following environment variables configure the application:
- `MINIFORCE_DB_URL`
- `MINIFORCE_DB_USER`
- `MINIFORCE_DB_PASSWORD`
- `MINIFORCE_JWT_SECRET`
- `MINIFORCE_FRONTEND_ORIGIN`
- `MINIFORCE_JUDGE_REAL_ENABLED`
- `VITE_API_BASE` (Frontend build-time API target)

**🚨 IMPORTANT SECURITY WARNING:** NEVER commit `.env`, `.env.production`, passwords, JWT secrets, API keys, SSH private keys, or database dumps to version control.

## Testing
To validate the application build and run regression tests:

**Backend Tests:**
```bash
cd backend
mvn test
```
*Current Baseline:* 92 backend tests passing (0 failures, 0 errors).

**Frontend Build:**
```bash
cd frontend/react
npm run build
```

## Security
MiniForce implements multiple layers of security to protect the host machine from malicious code execution:
- JWT authentication for users and role-based admin access.
- API Rate limiting (login, submission).
- Complete Docker network isolation.
- Read-only sandbox filesystems.
- Non-root user execution (`1000:1000`).
- Strict CPU, memory, and PID limits.
- Process execution timeouts (Compiler: 5s, Runtime: 2s).
- Strict output truncation limits.
- No Docker socket inside the user-code sandbox.
- Environment secret isolation (credentials are never passed into the sandbox container).

## GitHub (Beginner's Guide)
- **Git** is a version control system that tracks changes to your files locally.
- **GitHub** is a remote hosting service that stores your Git repositories online.

To upload this project:
1. Initialize the repository: `git init`
2. Check your tracked files: `git status`
3. Stage all files (respecting `.gitignore`): `git add .`
4. Create your first commit: `git commit -m "Initial MiniForce commit"`
5. Connect your remote repository: `git remote add origin YOUR_GITHUB_REPOSITORY_URL`
6. Upload the code: `git push -u origin main`

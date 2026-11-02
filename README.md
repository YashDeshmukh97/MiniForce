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
MiniForce features full real-judge integration (compilation and sandbox execution) across all 50 canonical public problems (IDs 1–50) in both **Java 17** and **Python 3**. Every problem runs in a dedicated, isolated Docker sandbox container (`miniforce-judge:latest`) with strict security constraints (`--network none`, 512MB RAM, 128 PIDs, 2.0s execution timeout).

## Project Structure
- `frontend/react/` - The React Vite application.
- `backend/` - The Spring Boot application.
- `database/` - MySQL schema and migration files.
- `docs/` - System architecture, audit manifests, and deployment documentation.
- `docker-compose.yml` - Production container layout.
- `START-MINIFORCE.bat` / `STOP-MINIFORCE.bat` - Local demonstration and development orchestration scripts.

## Requirements
To run MiniForce locally, you will need:
- Git
- Java 17 or newer
- Apache Maven 3.8+
- Node.js (18+) & npm
- MySQL 8.0+
- Docker Desktop (Linux container engine active for real judging)

## Running MiniForce

### Recommended: One-Click Demo Launcher
To start the entire platform for demonstrations or local development:
- Run **`START-MINIFORCE.bat`** from the project root.
  - Verifies Java 17+, Maven, Node, npm, MySQL (3306), and Docker Desktop.
  - Verifies or automatically builds the `miniforce-judge:latest` sandbox container.
  - Starts the Spring Boot API (port 8080) and applies all database migrations safely.
  - Starts the React frontend (port 5173) with local reverse proxy to API.
  - Polls for actual HTTP readiness rather than relying on arbitrary sleep delays.
  - Configures unlimited runs and submissions for authentic evaluation.
- Run **`STOP-MINIFORCE.bat`** to safely terminate only the MiniForce processes, containers, and temporary workspaces without touching unrelated host software.

### Manual Setup

#### Frontend Setup
```bash
cd frontend/react
npm install
npm run dev
```

#### Backend Setup
Ensure your local MySQL instance is running with the `miniforce` database. Set the required environment variables (`MINIFORCE_JWT_SECRET`, `MINIFORCE_DB_PASSWORD`) and run:
```bash
cd backend
mvn spring-boot:run
```

## Testing
To validate the application build and run regression tests:

**Backend Tests:**
```bash
cd backend
mvn test
```
*Current Baseline:* 480+ backend tests passing (0 failures, 0 errors, 0 skipped), including all 50 problems verified in real Docker sandbox.

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

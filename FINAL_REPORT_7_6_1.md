# MiniForce Phase 7.6.1 — Judge Deployment Security Review

## 1. Executive Summary
This document analyzes the deployment and security architecture of the MiniForce judge system, focusing specifically on the implications of the backend container orchestrating isolated execution environments via Docker. The primary finding is that the current architecture mandates Docker-out-of-Docker (DooD) socket access, which introduces host-level security implications if the backend process itself is compromised. However, the user-code execution sandbox itself is robustly isolated from the network, filesystem, secrets, and database.

## 2. Current Judge Architecture
The execution path flows precisely as follows:
1. **Frontend:** User submits code via REST API.
2. **Spring Boot API:** `SubmissionController` receives and persists the submission as `QUEUED`.
3. **JudgeWorker:** An asynchronous lease-based worker claims the submission.
4. **JudgeExecutionRouter:** Routes problems 1, 2, 3, 8, 12, 13, 14, 20 to the `DockerRealJudgeAdapter`.
5. **Real Judge Adapter:** Dispatches compilation (Java) and runtime (Java/Python) phases to specific runners (e.g. `JavaCompilerRunner`, `PythonRuntimeRunner`).
6. **DockerRunner:** Constructs a CLI command (`docker run ...`) and executes it natively via `ProcessBuilder`.
7. **Sandbox Container:** The `miniforce-judge:latest` container starts, executing the payload.
8. **OutputComparator:** Compares the container's standard output against test case expected outputs.
9. **Result Persistence:** Updates DB terminal state (`ACCEPTED`, `WRONG_ANSWER`, etc.).

## 3. Docker Socket Requirement
A. **Does backend need Docker daemon access?** Yes, to orchestrate the dynamic sandbox containers.
B. **Is Docker CLI used?** Yes. `DockerRunner.java` uses `ProcessBuilder` calling `docker`.
C. **Is Docker socket required?** Yes, because the backend container's internal Docker CLI must connect to a daemon (via `/var/run/docker.sock` in DooD scenarios) to spawn sibling containers on the host.
D. **Could a remote Docker daemon be used?** Yes, technically, if `DOCKER_HOST=tcp://...` is supplied to the backend environment, but it requires TLS configuration for security.
E. **Could a dedicated judge host be used?** Yes, but it would require architectural changes (a separate microservice/queue consumer).
F. **Could the judge be separated from the API backend?** Yes, it is already decoupled via DB-polling (`JudgeWorker`), making extraction straightforward in the future.

## 4. Docker Socket Security Risk
Mounting `/var/run/docker.sock` into the backend container gives the Spring Boot process access to the host's Docker daemon. 
- **Privilege granted:** Complete root-equivalent control over the host's Docker daemon.
- **Compromise impact:** If an attacker exploits a vulnerability in Spring Boot (e.g., RCE) or the API itself, they can use the docker socket to spawn privileged containers, mount the host root filesystem, and fully compromise the host machine.
- **Non-root protection:** The non-root `spring` user offers limited protection here, as membership in the `docker` group inherently grants root-equivalent escalation paths.
- **Sandbox boundaries:** The sandbox itself prevents user code from accessing the socket (it is NOT mounted into the judge container). The risk solely exists if the *backend* itself is breached.

## 5. Current Sandbox Security Controls
The sandbox controls verified in `DockerRunner.java` are exceptionally strict:
- `--network none` (Total network isolation, verified)
- `--read-only` (Root filesystem immutable, verified)
- `--memory` and `--cpus` (Resource bounding, verified)
- `--pids-limit` (Fork-bomb protection, verified)
- `--user 1000:1000` (Execution as non-root, verified)
- `--rm` (Auto-cleanup, verified)
- `--tmpfs /tmp` (Ephemeral, restricted workspace, verified)
- **Timeouts:** Compiler (5s) and Runtime (2s) enforced natively via Java Futures.
- **Absent vulnerabilities:** The socket is NOT mounted into the sandbox. Host JVM execution is NOT used. Code is executed strictly inside the confined sandbox. No generic `sh -c` vectors found parsing untrusted source directly.

## 6. Threat Model
- **Scenario A: Normal user submits malicious Java/Python code.**
  - *Protection:* Runs inside network-isolated, resource-constrained, non-root sandbox.
  - *Risk:* Minimal. Acceptable.
- **Scenario B: Malicious user attempts to escape the judge container.**
  - *Protection:* No network, read-only root, no socket, user 1000.
  - *Risk:* Container breakout requires a 0-day kernel vulnerability. Acceptable.
- **Scenario C: Malicious user attempts to access backend environment variables.**
  - *Protection:* Sandbox containers are passed empty `Map.of()` environments (except safe language flags like `PYTHONDONTWRITEBYTECODE`). Backend secrets are never passed.
  - *Risk:* None.
- **Scenario D: Malicious user exploits a vulnerability in the Spring Boot backend.**
  - *Protection:* Standard Spring Security.
  - *Risk:* Critical. If the backend is breached, the mounted Docker socket allows host takeover. This is the primary deployment risk.
- **Scenario E: Malicious user compromises the JudgeWorker.**
  - *Protection:* Worker runs in the backend context. Same as Scenario D.
- **Scenario F: Malicious user attempts to manipulate Docker commands through source code.**
  - *Protection:* Source code is passed via temporary mounted files or `stdin`. It is never interpolated into the `ProcessBuilder` string directly.

## 7. Deployment Architecture Comparison

### Option A — Backend + Docker Socket
**Architecture:** Frontend → Backend Container (+ `/var/run/docker.sock`) → Host Daemon → Sandbox
- **Simplicity:** Extremely high. Fits current implementation perfectly.
- **Cost:** Very low (single node).
- **Security risk:** High (if backend is compromised).
- **Suitability:** Suitable for low-risk, early-stage deployment on isolated VMs.

### Option B — Dedicated Judge Host
**Architecture:** Frontend → API Host → DB → Dedicated Judge VM (+ Daemon) → Sandbox
- **Isolation:** Excellent. API compromise does not equal daemon compromise.
- **Complexity:** High. Requires spinning up and networking two VMs.
- **Cost:** Double.
- **Suitability:** Best for long-term scale.

### Option C — Separate Judge Service
**Architecture:** Frontend → Backend Container → Judge Container (+ Socket) → Sandbox
- **Process isolation:** Good. Splits API from Judge process.
- **Socket exposure:** Confined to Judge Container, reducing attack surface.
- **Operational complexity:** Requires architectural extraction of `JudgeWorker`.

### Option D — Remote Docker Daemon
**Architecture:** Backend Container → TLS TCP → Remote Docker Host
- **Network security:** Requires strict mTLS.
- **Latency:** Higher network overhead for orchestrating containers and streaming `stdout`.
- **Complexity:** Very High.

## 8. Recommended Architecture
**Option A (Backend + Docker Socket on a Dedicated Isolated VM)** is recommended for the immediate Phase 7.7 deployment.
**Why:** It is natively compatible with the current architecture, incredibly cost-effective, and operationally simple. 
**Constraint:** The VM hosting this architecture MUST be treated as an isolated computational node containing NO infrastructure secrets other than the DB password and JWT secret. The host VM itself should be firewalled aggressively.

## 9. Required Infrastructure
- Linux VM / VPS (e.g. AWS EC2, DigitalOcean Droplet, GCP Compute Engine)
- Docker Engine installed natively on the host
- Minimum 2GB RAM / 2 vCPUs (to handle concurrent JVMs smoothly)
- Persistent block storage for MySQL
- Public HTTPS Endpoint / Load Balancer
- Standard host-level Firewalls (UFW/Security Groups) closing all ports except 80/443.

## 10. Database Isolation
- **Status:** EXCELLENT.
- User code runs in `--network none`, making it physically impossible for the sandbox to reach the MySQL container on port `3306`, the internet, or the backend API.

## 11. Secret Isolation
- **Status:** EXCELLENT.
- `MINIFORCE_DB_PASSWORD`, `MINIFORCE_JWT_SECRET`, and other environment variables are bounded safely to the Backend container. They are never injected into the `ContainerSpec.env()` map passed to the sandbox.

## 12. Network Isolation
- **Status:** EXCELLENT.
- `--network none` entirely severs the sandbox from the host loopback interface, external internet, and Docker bridge network.

## 13. Filesystem Isolation
- **Status:** EXCELLENT.
- Sandbox root is `--read-only`. Only a `--tmpfs /tmp` scratch pad is writable. The host filesystem is protected.

## 14. Resource Isolation
- **Status:** EXCELLENT.
- Bounded CPU, memory, PIDs, and rigid 2-second runtime timeouts prevent generic resource exhaustion loops. Concurrency bounds at the `JudgeWorker` level prevent DB/thread exhaustion.

## 15. Production Monitoring Requirements
- Host CPU / RAM Utilization (Sandbox spikes)
- Disk usage (MySQL data growth)
- Submission queue depth (detecting stuck workers)
- Error logs for `DockerRunner` and `JudgeExecutionRouter` exceptions

## 16. Remaining Risks

- **Risk 1: Backend RCE leads to Host Takeover via Docker Socket**
  - **Severity:** HIGH
  - **Impact:** Full node compromise.
  - **Mitigation:** Rely on isolated VPS nodes. Limit IAM permissions attached to the VPS instance.
  - **Deployment blocker:** NO (Acceptable for an isolated contest platform VM).

- **Risk 2: Docker Desktop / Host Daemon Timing Flakes**
  - **Severity:** LOW
  - **Impact:** False positive `TIME_LIMIT_EXCEEDED`.
  - **Mitigation:** Linux native Docker daemons respond significantly faster than Docker Desktop for Windows/Mac, minimizing this in production.
  - **Deployment blocker:** NO.

## 17. Final Decision
**YELLOW — DEPLOYMENT POSSIBLE WITH EXPLICIT INFRASTRUCTURE CONTROLS**

The current architecture is secure against malicious code submissions. The architectural constraint (DooD) presents a backend vulnerability amplification, but this is an acceptable tradeoff for immediate deployment provided the host is an isolated VM.

## 18. Phase 7.7 Prerequisites
Before public deployment, the infrastructure MUST:
1. Provide a dedicated Linux VM (VPS) solely for this application.
2. Provide a native Docker Engine on the host.
3. Configure a reverse proxy terminating TLS (HTTPS) on standard ports.
4. Block all external TCP access to the VM except 80/443/SSH.
5. Provide a `.env` file containing safe, strong production credentials explicitly for this environment.

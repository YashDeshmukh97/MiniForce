# MiniForce Phase 18: Safe Public Demo Feasibility & Architectural Review

**Project Root**: `C:\Users\Yash Deshmukh\OneDrive\Desktop\miniforce4`  
**Date**: October 2026  
**Status**: Comprehensive Pre-Demonstration Assessment

> [!IMPORTANT]
> **Zero Cloud Action Policy**:
> - No cloud accounts or resources were provisioned or accessed.
> - No payment methods were registered, no billing was activated, and no domains were purchased.
> - The application was **not** exposed to the public internet.
> - The frozen 50-problem catalog, seed data, and migrations remain 100% unchanged.

---

## 1. Executive Summary & Comparative Matrix

To demonstrate MiniForce safely without financial commitment, three primary operational approaches exist:

| Evaluation Dimension | Option 1: Local-Only Demo (Screen Share / Video / In-Person) | Option 2: Temporary Cloudflare Quick Tunnel (`trycloudflare.com`) | Option 3: OCI Always Free Cloud Deployment (Ampere A1 ARM64) |
| :--- | :--- | :--- | :--- |
| **Hosting Model** | Local developer workstation (`127.0.0.1:5173` & `8080`). | Local stack exposed via ephemeral Cloudflare outbound reverse tunnel. | Dedicated cloud Linux VM (Ubuntu 24.04 ARM64, Nginx, MySQL, Spring Boot, Docker). |
| **Cost & Billing Risk** | **Absolute $0.00** (Zero billing capability). | **Absolute $0.00** (Quick Tunnels require no account or credit card). | **Qualified $0.00** (Contingent on staying strictly within Always Free tier quotas). |
| **Prerequisites** | Existing workstation with Docker Desktop, Java 17, Node.js, MySQL running. | Local workstation running MiniForce + temporary `cloudflared` binary download. | International credit/debit card for identity hold, OCI account approval, regional Ampere A1 capacity. |
| **Uptime Limitation** | Active only while developer runs `START-MINIFORCE.bat`. | Ephemeral. URL changes on every restart. Cloudflare drops tunnels after inactivity. | 24/7 permanent (subject to OCI 7-day idle reclamation policy). |
| **Java & Python Judge** | **YES**: 100% operational across all 50 problems (native local execution). | **YES**: Executes on developer's local Docker Desktop engine. | **YES**: Executes natively on host ARM64 Docker CE engine. |
| **Security Risk Profile** | **ZERO EXTERNAL ATTACK SURFACE**: No ports exposed to LAN or internet. | **HIGH TO CRITICAL**: Exposes local personal workstation to public arbitrary code execution and default credentials. | **MODERATE (CONTAINED)**: Dedicated isolated VM. Residual risk is limited to cloud VM compromise, not personal machine. |
| **Suitability for Review** | **BEST & SAFEST**: Ideal for code review, grading, portfolio defense, and video capture. | **ACCEPTABLE FOR SUPERVISED LIVE DEMO ONLY** (with strict credential rotation and short duration). | **VIABLE FOR PERSISTENT PUBLIC ACCESS** (if cloud account and capacity are secured). |

---

## 2. Part 1: Detailed Evaluation of Cloudflare Quick Tunnels

### 2.1 Mechanism & Routing Topology
A Cloudflare Quick Tunnel operates by running `cloudflared tunnel --url http://127.0.0.1:5173`. It establishes an encrypted outbound QUIC/HTTP2 connection to Cloudflare's edge network and assigns an ephemeral domain (e.g., `https://random-words.trycloudflare.com`).

```
[Public Evaluator / Browser]
         │ HTTPS (Port 443)
         ▼
[Cloudflare Edge Network]
         │ Outbound Encrypted Tunnel
         ▼
[cloudflared daemon on Developer Workstation]
         │ HTTP (127.0.0.1:5173)
         ▼
[Vite Dev Server (Port 5173)]
   ├── Serves React Static / SPA Assets
   └── Proxies '/api/*' ────► [Spring Boot Backend (Port 8080)]
                                  ├── MySQL Database (Port 3306)
                                  └── Local Docker Engine (Sandbox Judge)
```

### 2.2 Frontend & Backend Routing Verification
- **Vite Proxy Integration**: In `frontend/react/vite.config.ts`, lines 18–23 specify:
  ```typescript
  proxy: {
    '/api': {
      target: 'http://127.0.0.1:8080',
      changeOrigin: true,
    },
  }
  ```
  Because Vite proxies `/api` server-side on `127.0.0.1`, a Quick Tunnel pointing directly to `http://127.0.0.1:5173` correctly routes both the frontend UI and all API calls over the single tunnel URL.
- **CORS Behavior**: From the browser's perspective, both the assets and the `/api/` endpoints share the single origin (`https://random-words.trycloudflare.com`). Consequently, the browser does not initiate cross-origin preflight requests, avoiding CORS blocking even if the backend dev profile defaults are local.
- **Base URL Resolution**: In `frontend/react/src/services/api.ts`, `resolveBaseUrl()` falls back to `/api` when relative. This ensures seamless client communication over the tunnel.
- **Workstation Prerequisite**: The developer's personal machine must remain powered on, awake, and running `START-MINIFORCE.bat` (MySQL, Spring Boot, Vite, Docker Desktop).

### 2.3 Critical Vulnerabilities of an Unprotected Quick Tunnel
Exposing the local development stack directly to a public Quick Tunnel introduces severe risks:

1. **Default Seeded Account Exposure**:
   In `database/miniforce.sql`, default demo credentials are fixed and documented:
   - Administrator: `admin` / `password`
   - Demo User: `demo` / `password`
   Anyone discovering or scanning the tunnel URL can immediately log into the admin account, access `/api/admin/**`, manipulate problem data, or access user profiles.
2. **Internal Judge Endpoint Exposure**:
   In `backend/src/main/java/com/miniforce/config/SecurityConfig.java`, `/api/internal/judge/**` is marked `permitAll()`. In `InternalJudgeWorkerController.java`, the secret check is bypassed if `expectedSecret.isEmpty()`. In development mode, `MINIFORCE_JUDGE_INTERNAL_SECRET` is typically unset. Consequently, an external user could send raw execution requests to `/api/internal/judge/execute`.
3. **Arbitrary Code Execution on Developer Workstation**:
   Unlike a disposable cloud VM, a Quick Tunnel routes untrusted code execution from internet strangers directly into the Docker Desktop engine on the developer's personal workstation. If a zero-day container breakout occurs, the attacker gains access to the developer's local operating system, files, and local network.

---

## 3. Part 2: Detailed Evaluation of OCI Always Free

### 3.1 Official Reality vs. Uncertainties
Deploying to Oracle Cloud Infrastructure (OCI) Always Free provides an isolated Linux server environment, but comes with practical uncertainties that prevent claiming an unconditional guarantee:

1. **Account Registration & Financial Hold**:
   - Oracle requires a valid international credit or debit card for identity validation.
   - A temporary pre-authorization hold (~$1 / ₹80 / €1) is placed and released.
   - Virtual cards, prepaid cards, and debit cards lacking 3D Secure / international recurring authorization are routinely declined.
2. **Regional Capacity Exhaustion ("Out of Capacity")**:
   - The Ampere A1 shape (`VM.Standard.A1.Flex`, 2–4 OCPUs, 12–24 GB RAM) is in high global demand.
   - In popular free-tier regions (e.g., Ashburn, Frankfurt, Mumbai, Hyderabad, Phoenix), provisioning frequently fails with:
     `Out of host capacity for shape VM.Standard.A1.Flex`.
   - Provisioning cannot be guaranteed on demand; it often requires automated retry scripts or off-peak attempts over several days.
3. **7-Day Idle Instance Reclamation Policy**:
   - Under official OCI Always Free terms, Oracle monitors instance utilization over 7-day rolling windows.
   - If 95th-percentile utilization across **CPU, network, AND memory are all below 20%**, the instance is flagged as idle and may be terminated.
   - Evading this policy with synthetic stress scripts violates acceptable use guidelines. The natural footprint of Spring Boot (~1.2 GB base memory) and actual user judging must provide legitimate activity.
4. **Billing Risk Boundary**:
   - While the defined shape (2 OCPU / 12 GB RAM / 100 GB Boot Volume) is $0.00, billing occurs immediately if:
     - An accidental secondary instance or detached boot volume pushes total block storage over 200 GB.
     - Paid services outside Always Free (e.g. NAT gateways, public load balancers) are added.

---

## 4. Part 3: Public Submission Security Assessment

### 4.1 Threat Modeling Public Code Submissions
Operating a public online judge allows anonymous external visitors to compile and execute arbitrary native code. The following defense-in-depth controls are enforced by MiniForce:

| Security Domain | Implemented Control in MiniForce | Residual Risk |
| :--- | :--- | :--- |
| **Authentication** | Submissions require a valid JWT token (`/api/submissions`). | Public registration (`/api/auth/register`) is open. Anyone can create an account and submit. |
| **Rate Limiting** | `RateLimitInterceptor` enforces 5 submissions per 60s window per user/IP. | An attacker can cycle multiple registered accounts to saturate the queue. |
| **Network Isolation** | Sandbox runs with `--network none`. | Container cannot make outbound connections or reach host ports (3306, 8080). |
| **Filesystem Isolation** | Root filesystem is `--read-only`. Only scratch workspace is mounted `rw`. | Host root and system binaries are safe from container modification. |
| **Kernel Hardening** | Hardened in Phase 16 with `--cap-drop=ALL` and `--security-opt=no-new-privileges`. | Blocks Linux capability misuse and setuid binary escalation. |
| **Host Privilege Boundary** | Co-located single VM / workstation model: backend user belongs to `docker` group. | **High**: If an RCE vulnerability exists in Spring Boot, host root escalation is possible via `/var/run/docker.sock`. |

### 4.2 Security Recommendation for Demos
- **Do NOT expose an unrestricted judge on a personal workstation** via Quick Tunnel without access controls.
- If a temporary live web demo is mandatory, enforce one of the following controls:
  1. **Cloudflare Zero Trust Access**: Place a Cloudflare Access PIN or email whitelist in front of the tunnel so only designated evaluators can reach it.
  2. **Rotate Default Passwords**: Change the default passwords for `admin` and `demo` accounts immediately before launching any public link.
  3. **Set Internal Judge Secret**: Configure `MINIFORCE_JUDGE_INTERNAL_SECRET` in the environment to prevent direct invocation of `/api/internal/judge/execute`.

---

## 5. Part 4: Comparative Breakdown of Demonstration Options

### Option 1: Local-Only Demonstration (Screen Share / Video Walkthrough)
- **Prerequisites**: Existing local setup running `START-MINIFORCE.bat`.
- **Cost**: **$0.00** (Zero billing capability).
- **Security**: **Zero attack surface**. No network ports exposed beyond localhost.
- **Uptime**: Controlled by the presenter.
- **Judge Capability**: Full Java 17 and Python 3 execution across all 50 problems.
- **Assessment**: The optimal, professional method for project defense, academic evaluation, and portfolio demonstration. Completely avoids internet hostility, default credential attacks, and cloud provisioning hurdles.

### Option 2: Temporary Cloudflare Quick Tunnel (`trycloudflare.com`)
- **Prerequisites**: `cloudflared` binary, local MiniForce running, rotated default passwords, strong internal secret.
- **Cost**: **$0.00** (No credit card or Cloudflare account required).
- **Security**: **Elevated Risk**. Personal workstation becomes accessible via public URL.
- **Uptime**: Ephemeral session (typically 1–2 hours during presentation).
- **Judge Capability**: Full Java 17 and Python 3 execution on local Docker engine.
- **Assessment**: Viable only for interactive, scheduled demonstrations where an external evaluator must click buttons on their own device. Must be terminated immediately after the presentation.

### Option 3: OCI Always Free Dedicated Cloud Deployment
- **Prerequisites**: Approved OCI account, available Ampere A1 capacity in home region, SSH configuration, domain/DNS.
- **Cost**: **Qualified $0.00** (Requires ongoing monitoring of storage quotas and idle policies).
- **Security**: **Isolated**. The application is confined to a dedicated cloud VM; personal machine is unaffected.
- **Uptime**: 24/7 persistent hosting.
- **Judge Capability**: Full Java 17 and Python 3 execution on host ARM64 Docker engine.
- **Assessment**: The best option for a persistent public web presence, but subject to cloud capacity availability and registration gates.

---

## 6. Final Recommendation

### **RECOMMENDED APPROACH: OPTION 1 (LOCAL DEMO / VIDEO) AS PRIMARY, OPTION 2 AS SUPERVISED BACKUP**

1. **For Project Review, Grading, and Interviews**:
   - Use **Option 1 (Local Demonstration via Screen Share / Recorded Video)**.
   - It eliminates 100% of security risks, requires zero cloud configuration, guarantees zero financial liability, and showcases the fully functional platform (all 50 problems, Java/Python judging, hints, and leaderboard) without network latency.
2. **If an Evaluator Explicitly Requests a Live Link**:
   - Use **Option 2 (Temporary Cloudflare Quick Tunnel)** under strict supervision:
     - Rotate the default `admin` and `demo` passwords before starting.
     - Set a strong random `MINIFORCE_JUDGE_INTERNAL_SECRET`.
     - Start the tunnel only for the duration of the interview.
     - Terminate the tunnel immediately upon completion (`Ctrl+C`).
3. **For Persistent 24/7 Cloud Hosting**:
   - Pursue **Option 3 (OCI Always Free)** only when you have secured account approval and verified Ampere A1 capacity in your region.

*No cloud resources were created or altered. All local systems remain secure and ready.*

# MiniForce — Final Submission Readiness Report

## Project
**MiniForce** — A competitive programming platform with real Docker-sandboxed code execution.

## Architecture
```
Frontend (HTML/JS)
  → Spring Boot REST API (Java 26 / Spring Boot 3.5)
    → JWT Authentication
    → Submission Queue / JudgeWorker (@Scheduled polling)
      → JudgeExecutionRouter (feature-flag gated)
        → DockerRealJudgeAdapter (Problems 1-3)
        → MockJudgeExecutionAdapter (Problems 4-100)
      → Docker Sandbox (eclipse-temurin:17-jdk-alpine)
        → CompilerRunner (javac, 5s timeout)
        → RuntimeRunner (java, 2s timeout)
        → OutputComparator (ArrayExact / BooleanExact / IntegerExact)
      → Result persistence → Status polling
```

## Real Judge
| Problem | Slug | Status | Components |
|---------|------|--------|------------|
| 1 | two-sum | **REAL** | TwoSumInputCodec, TwoSumHarnessGenerator, ArrayExactComparator |
| 2 | valid-parentheses | **REAL** | ValidParenthesesInputCodec, ValidParenthesesHarnessGenerator, BooleanExactComparator |
| 3 | longest-substring | **REAL** | LongestSubstringInputCodec, LongestSubstringHarnessGenerator, IntegerExactComparator |

## Mock Judge
Problems 4–100 use `MockJudgeExecutionAdapter` (returns randomized ACCEPTED/WRONG_ANSWER).

## Deferred
Problem 17 (Group Anagrams) — blocked by test-data ID collision (IDs 27-30 duplicated) and unordered output ambiguity. Database is READ ONLY; cannot repair without schema/data changes.

## Security Controls (All VERIFIED)

| Control | Status | Evidence |
|---------|--------|----------|
| No shell command with untrusted source | ✅ VERIFIED | ProcessBuilder with tokenized args, no sh -c |
| No `sh -c` / `cmd /c` | ✅ VERIFIED | All commands passed as List\<String\> |
| No host JVM execution of user code | ✅ VERIFIED | All execution inside Docker containers |
| No privileged Docker container | ✅ VERIFIED | No --privileged flag |
| No Docker socket mount | ✅ VERIFIED | Only temp workspace mounted |
| No host project directory mount | ✅ VERIFIED | Workspaces in OS temp dir |
| Network disabled | ✅ VERIFIED | --network none |
| Root filesystem read-only | ✅ VERIFIED | --read-only with limited tmpfs |
| Non-root execution | ✅ VERIFIED | --user 1000:1000 |
| CPU bounded | ✅ VERIFIED | --cpus 1.0 |
| Memory bounded | ✅ VERIFIED | --memory 512m |
| PID bounded | ✅ VERIFIED | --pids-limit 32 |
| Compiler timeout bounded | ✅ VERIFIED | 5 seconds (production DEFAULT) |
| Runtime timeout bounded | ✅ VERIFIED | 2 seconds (production DEFAULT) |
| Stdout capped | ✅ VERIFIED | 1 MB with truncation |
| Stderr capped | ✅ VERIFIED | 1 MB with truncation |
| Workspace cleanup | ✅ VERIFIED | finally blocks with recursive delete |
| No secret/env leakage | ✅ VERIFIED | No -e/--env flags on containers |
| Filename sanitization | ✅ VERIFIED | Regex: ^[a-zA-Z0-9_]+\\.java\$ |
| Classname sanitization | ✅ VERIFIED | Regex: ^[a-zA-Z0-9_]+\$ |
| Terminal state overwrite protection | ✅ VERIFIED | isTerminal() guard in markTerminal() |

## Testing
```
Tests run: 55
Failures: 0
Errors:   0
Skipped:  0
Build:    SUCCESS
Time:     01:35 min
```

## Timeout Policy Audit
- **Production DEFAULT**: 5s compiler, 2s runtime (restored from D.10.1's 15s/10s)
- **TEST_GENEROUS**: 15s compiler, 10s runtime (isolated to integration tests only)
- **Rationale**: The D.10.1 increase to 15s/10s was a test-stability workaround for cold Docker container startup on WSL2. It weakened DoS resistance by 300%/500%. Now properly isolated: production uses tight bounds, tests that need cold-start tolerance use TEST_GENEROUS explicitly.
- **Verification**: `testTimeoutHandling` proves actual process termination (sleep 10 vs 5s timeout). `testRuntimeTimeout` proves JVM termination (Thread.sleep 10000 vs 2s timeout).

## Database
- ❌ No schema changes
- ❌ No seed changes
- ❌ No test-case changes
- ❌ No ID changes
- ❌ No migrations
- 100-problem catalog frozen

## Submission State Machine
```
QUEUED → claim (COMPILING) → execution → terminal state
```
**Terminal states**: ACCEPTED, WRONG_ANSWER, COMPILATION_ERROR, RUNTIME_ERROR, TIME_LIMIT_EXCEEDED, MEMORY_LIMIT_EXCEEDED, SYSTEM_ERROR

**Protections**:
- Atomic DB claim with conditional UPDATE
- Worker identity verification (UUID-based claimedBy)
- 5-minute lease expiration
- Retry counter (max 3 → SYSTEM_ERROR)
- Terminal state overwrite guard (added D.11)

## Frontend/Backend Audit
- ✅ Login: `login.html#email` → `auth.js querySelector('#email')` — compatible
- ✅ Profile: `profile.js` uses `data.user.fullName` (GET) and `up.fullName` (PUT) — matches backend DTOs
- ✅ "2,400+ challenges" corrected to "100 challenges" in `problems.html`
- ✅ No hardcoded secrets in frontend
- ✅ No JWT/API keys exposed

## Feature Flag
```properties
# application.properties (DEFAULT = OFF)
miniforce.judge.real-enabled=false
```
- OFF: All problems → MockJudgeExecutionAdapter
- ON: Problems 1,2,3 → DockerRealJudgeAdapter; all others → Mock. Fail closed.

## Startup Requirements
```bash
# Environment variables
MINIFORCE_DB_URL=jdbc:mysql://localhost:3306/miniforce?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
MINIFORCE_DB_USER=root
MINIFORCE_DB_PASSWORD=<your_password>
MINIFORCE_JWT_SECRET=<at_least_32_characters>

# Backend
cd backend
mvn spring-boot:run

# Frontend (any static server on the project root)
# e.g., VS Code Live Server on port 5500

# Docker Desktop must be running (for real judge)
```

## Demo Flow
1. Start Docker Desktop
2. Start backend: `cd backend && set MINIFORCE_JWT_SECRET=my_demo_secret_key_32_chars_min && set MINIFORCE_DB_PASSWORD=<pw> && mvn spring-boot:run -Dminiforce.judge.real-enabled=true`
3. Start frontend (Live Server on project root, port 5500)
4. Open `http://localhost:5500`
5. **Register** a new account or **Login**
6. Navigate to **Problems**
7. Open **Problem 1 (Two Sum)**
8. Submit correct Java solution → observe **ACCEPTED** (real Docker execution)
9. Submit incorrect solution → observe **WRONG_ANSWER**
10. Submit syntax error → observe **COMPILATION_ERROR**
11. Open **Problem 2 (Valid Parentheses)** → demonstrate real execution
12. Open **Problem 4** → observe mock result (different path)

## Known Limitations
1. Only Problems 1-3 use real Docker execution
2. Problems 4-100 remain on MockJudge (returns random verdicts)
3. Problem 17 deferred (test-data collision + unordered output ambiguity)
4. Generic ProblemContract V2 not yet implemented (each problem needs manual Codec/Harness/Comparator)
5. Language support is Java-only (no C++, no Python)
6. MockJudge behavior is not equivalent to a real competitive-programming judge
7. `SubmissionResponse` DTO omits `compilerOutput` and `runtimeErrorOutput` fields
8. No JPA `@Version` optimistic locking (relies on custom query checks)
9. `RUNNING` state is not persisted during execution (jumps from COMPILING to terminal)

## Future Work
- ProblemContract V2 (schema-driven problem metadata)
- Problem 4+ real judge expansion
- Problem 17 data cleanup and unordered-output comparator
- C++ language support
- Python language support
- `SubmissionResponse` DTO enrichment (compiler output, runtime errors)
- JPA `@Version` optimistic locking
- Production deployment (Kubernetes, Redis queue)

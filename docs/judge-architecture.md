# Future isolated Java judge architecture

This is a design document only. Phase 7.0 does not compile or execute user code.

`	ext
Frontend
  -> HTTPS API request
Spring API
  -> validate request
  -> persist submission/job (never compile/execute here)
  -> return job/submission ID
Submission/job queue
  -> leased job
Isolated judge worker
  -> fetch immutable metadata
Sandbox -> compiler/runtime -> generated harness -> tests -> comparator
  -> capture stdout/stderr/exit code/runtime
Result store -> Spring API -> frontend
`

## 1. Judge Job Model

Submissions will transition through the following well-defined states:
- QUEUED: Accepted by the API and waiting for a worker lease.
- COMPILING: Worker has claimed the job and is building the source.
- RUNNING: Compilation succeeded; executing test cases.
- ACCEPTED: All test cases passed within limits.
- WRONG_ANSWER: Execution completed, but output comparator failed.
- COMPILE_ERROR: The Java 17 compiler failed.
- RUNTIME_ERROR: Process crashed or threw an unhandled exception.
- TIME_LIMIT_EXCEEDED: Wall-clock or CPU timeout reached.
- MEMORY_LIMIT_EXCEEDED: Process attempted to allocate beyond the memory quota.
- SYSTEM_ERROR: A worker crash, sandbox failure, or internal failure.

Valid Transitions:
QUEUED -> COMPILING -> RUNNING -> [Terminal states: ACCEPTED, WRONG_ANSWER, RUNTIME_ERROR, TIME_LIMIT_EXCEEDED, MEMORY_LIMIT_EXCEEDED]
COMPILING -> [Terminal state: COMPILE_ERROR]
Any state -> SYSTEM_ERROR (on catastrophic failure).

## 2. Judge Worker

The Judge Worker is a strictly separated process/service boundary from the Spring API. Its responsibilities:
- Receives a job payload.
- Fetches immutable problem and test metadata.
- Creates an isolated, temporary workspace.
- Compiles the Java 17 source code.
- Executes the compiled code strictly inside a sandbox boundary.
- Captures stdout, stderr, exit code, and runtime metrics.
- Cleans the workspace completely after execution.
- Reports a structured result payload back to the API/database.

## 3. Sandbox Boundary (Interface Definition)

The sandbox is the enforcement layer for all code execution. The Spring Boot process itself must NEVER be the execution sandbox. 

Required Controls:
- CPU/Time Limit: Hard enforcement of maximum execution time.
- Memory Limit: Cgroup/ulimit-based cap on memory consumption.
- Process Count Limit: Prevent fork-bombs (e.g., pids.max).
- Filesystem Isolation: Chroot/overlayfs preventing host access.
- Network Isolation: Complete disabling of outbound/inbound networking.
- Read-Only Base Environment: The system libraries and Java runtime are strictly read-only.
- Writable Temporary Workspace: A bounded, ephemeral directory for outputs.
- Execution Timeout: Hard kill of the entire process tree on timeout.
- Output-Size Limit: Truncation of excessive stdout/stderr (e.g., 2MB max).
- Cleanup: Ephemeral environment destruction post-run.
- Non-Root Execution: Sandbox and user code must run as a strictly unprivileged user (e.g., nobody).

## 4. Backward Compatibility

The existing SafeJavaDemoJudge must remain available as the default, safe implementation. It will continue to process requests in a read-only, non-executing manner until the real execution path (worker + sandbox) is fully implemented, verified, and explicitly enabled via configuration.

## 5. Implementation Roadmap

The future implementation is broken into small, safe phases:
- Phase A: Contracts and schema definition (This phase).
- Phase B: Worker skeleton without arbitrary execution (queue consumer and mock responses).
- Phase C: Sandbox implementation (Docker/Podman/nsjail boundaries defined locally).
- Phase D: Java compile/execute inside the sandbox (wiring javac/java).
- Phase E: Problem adapters (mapping existing DB input/output to execution).
- Phase F: Comparator implementation (exact, floating-point, array logic).
- Phase G: Integration testing (end-to-end flow with malicious/timeout tests).
- Phase H: Production hardening (observability, load testing, final toggle).

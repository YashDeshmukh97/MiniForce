# MiniForce Sandbox Architecture (Phase D.6)

This document formally defines the Phase D.6 Java Execution Sandbox design.

## 29 Design Dimensions & Guarantee Classification

Every security control is explicitly classified using one of the following states:
- **DESIGNED**: Architecture defined but not physically present.
- **IMPLEMENTED**: Logic written in the application but relies on an external system.
- **VERIFIED**: Proven to work by automated tests or direct environment confirmation.
- **DEFERRED**: Explicitly acknowledged as lacking current support or mechanism.
- **NOT_AVAILABLE**: The underlying host technology is missing, blocking implementation/verification.

1. **Judge worker process boundary**: **VERIFIED**. Spring Boot API is isolated from execution logic by JudgeExecutionPort.
2. **Submission hand-off**: **VERIFIED**. Data is passed via ExecutionRequest DTOs.
3. **Temporary working directory lifecycle**: **VERIFIED**. /tmp/miniforce_exec_<uuid> cleanup verified in finally block via WorkspaceManager.
4. **Source-file generation**: **DESIGNED**. Harness mapping strategy is defined but waiting for runtime integration.
5. **Compilation command boundary**: **VERIFIED**. Docker javac execution rigorously isolates user code with strict compiler argument boundaries.
6. **Runtime command boundary**: **VERIFIED**. Docker java execution isolates the JVM inside the container.
7. **CPU limit**: **VERIFIED**. Configured via ResourcePolicy (1 CPU). Docker metadata and Java vailableProcessors confirm limit.
8. **Memory limit**: **VERIFIED**. Configured via ResourcePolicy (512MB). Docker limits JVM heap bounds explicitly.
9. **Process/PID limit**: **VERIFIED**. Configured via ResourcePolicy (32 PIDs) and passed to Docker.
10. **Output-size limit**: **VERIFIED**. Streams safely truncated dynamically (preventing pipe deadlocks and host OOM) setting explicit observational flags.
11. **Filesystem isolation**: **VERIFIED**. Container uses read-only root.
12. **Network isolation**: **VERIFIED**. Container uses --network none.
13. **Read-only base filesystem**: **VERIFIED**. Base JDK image protection using --read-only.
14. **Writable temporary workspace**: **VERIFIED**. /workspace isolated volume mapping.
15. **Non-root execution**: **VERIFIED**. Unprivileged user execution (--user 1000:1000).
16. **Hard process termination**: **VERIFIED**. Supervisor SIGKILL on timeout + fallback docker rm -f.
17. **Cleanup after every submission**: **VERIFIED**. Transient container flag --rm used and workspace tests guarantee disk flush.
18. **Timeout handling**: **VERIFIED**. Fallback to TIME_LIMIT_EXCEEDED natively handled with robust stream cleanup.
19. **Compilation-error handling**: **VERIFIED**. Pipeline handles COMPILATION_ERROR correctly tracking outputs.
20. **Runtime-error handling**: **VERIFIED**. Non-zero exits natively captured by RuntimeRunner.
21. **System-error handling**: **VERIFIED**. JudgeWorker catches exceptions and safely records SYSTEM_ERROR.
22. **Concurrency limits**: **DEFERRED**. See Concurrency Constraints section below.
23. **Worker crash recovery**: **VERIFIED**. Handled by JobClaimService lease expiration.
24. **Preventing sandbox escape**: **VERIFIED** (Configuration). Namespaces/cgroups configured via Docker Desktop on WSL2, though security guarantees against kernel 0-days are not comprehensively proven.
25. **Preventing access to application secrets**: **VERIFIED**. No env variables mapped.
26. **Preventing access to MySQL credentials**: **VERIFIED**. Network lockout enforces complete isolation.
27. **Preventing access to host filesystem**: **VERIFIED**. Only the unique scratch space is mounted.
28. **Preventing network access**: **VERIFIED**. Ensured via --network none.
29. **Resource exhaustion protection**: **VERIFIED**. PID limits, memory limits, CPU caps configured and verified via metadata.

---

### Phase D.6 Important Notice

- **Internal Capability Only:** Runtime execution is implemented only as an isolated internal capability.
- **Normal Flow Unaffected:** Normal MiniForce submissions are NOT yet routed to this engine.
- **Future Work:** The comparator, problem harness, and user submission integration remain future work.

---

### Concurrency Constraints

- **Maximum concurrent judge executions**: **DEFERRED**. Currently relies implicitly on Spring @Scheduled thread pool size. Maximum known concurrent execution is whatever Spring Boot invokes by default (usually 1 for fixed-delay). A formal bounded semaphore or dedicated ExecutorService is deferred.
- **Queue backpressure**: **DEFERRED**. Rejection of Submit requests when queue is full is deferred.
- **Worker concurrency**: **IMPLEMENTED**. Isolated per @Scheduled invocation, safeguarded by row-level DB locks.
- **Per-submission resource limits**: **VERIFIED**. Backed by ResourcePolicy injection.
- **Per-user submission rate limiting**: **DEFERRED**. To be implemented in API Gateway / Security layer.
- **Maximum output size**: **VERIFIED**. Specified natively via ResourcePolicy.
- **Maximum workspace size (Disk Quota)**: **DEFERRED**. Windows + Docker Desktop bind-mounted workspaces lack a practical disk-size enforcement mechanism natively available in the current host environment.
- **Maximum total execution duration**: **VERIFIED**. Specified natively via ResourcePolicy timeout fields.

---

### Observability

Runtime and compiler results strictly expose safe, bounded metadata structured in specific Result objects:
- **duration**: Long mapping exact milliseconds.
- **timeout**: Boolean state natively tracking process wait limit.
- **exit code**: Propagated reliably from Docker.
- **outputTruncated**: Boolean explicitly signifying stream bounds were hit.
- **systemError**: Nullable string isolating platform-level faults (never containing credentials/source).
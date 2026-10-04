# Future Judge Threat Model & Testing Strategy

This threat model applies to the later isolated-worker implementation. The Spring API host must NOT be used as a sandbox.

## 1. Security Threat Model Expansion

| Threat | Required Mitigation |
|---|---|
| Malicious Java Submissions | All execution occurs within a heavily restricted sandbox (chroot/namespaces). |
| Infinite Loops / CPU Exhaustion | Strict CPU quota and wall-clock timeouts. The sandbox must SIGKILL the entire process tree on timeout. |
| Memory Exhaustion | Enforced cgroup memory limits. Output truncated to prevent buffer OOMs. Terminal state: MEMORY_LIMIT_EXCEEDED. |
| Fork/Process Spawning | Strict process-count limits (e.g., pids.max=64). Runs as unprivileged user to prevent container escapes. |
| Filesystem Access / Symlink Traversal | Sandbox environment is read-only. Writable temporary workspace is isolated, bounded, and ephemeral. Path traversal blocked by chroot. |
| Network Access | Network namespaces strictly isolate the sandbox. Outbound and inherited listening sockets are completely disabled. |
| Excessive Output | Output streams (stdout/stderr) are capped at a specific size (e.g., 2MB). Writes beyond this are dropped. |
| Classpath Abuse / Reflection Tricks | Fixed compilation/execution classpath. Worker framework classes are shielded. Java SecurityManager or module restrictions may augment container limits. |
| Worker Compromise | The sandbox has no host credentials. Even if escaped, the worker node has limited API access (restricted token) and cannot access the database directly. |
| Queue Flooding | API layer enforces per-user rate limiting and global concurrency limits (backpressure) before queueing. |

## 2. Testing Strategy

The eventual worker implementation will be rigorously tested against the following scenarios using mock and real submissions:
- Valid Submission Lifecycle: Source builds, runs tests, accepts valid answers.
- Compile Failure: Intentional syntax errors correctly surface as COMPILE_ERROR with bounded diagnostics.
- Wrong Answer: Logic errors return WRONG_ANSWER via comparators.
- Runtime Error: Division by zero or out-of-bounds array access correctly surfaces as RUNTIME_ERROR.
- Timeout: while(true) loops safely terminate via SIGKILL and yield TIME_LIMIT_EXCEEDED.
- Memory Limit: Gigantic array allocations crash safely and yield MEMORY_LIMIT_EXCEEDED.
- Output Limit: Massive System.out.println loops truncate output safely without crashing the worker.
- Malicious/Untrusted Source: Submissions attempting to read /etc/passwd, execute Runtime.getRuntime().exec(), or open sockets are denied by the sandbox.
- Worker Failure: Simulated crash ensures jobs return to the queue or fall to SYSTEM_ERROR.
- Cleanup: Verification that workspaces are purged entirely after terminal states.
- Concurrent Jobs: Stress testing multiple jobs under heavy load.

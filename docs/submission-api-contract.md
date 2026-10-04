# Submission API Contract & Migration Strategy

## 1. Submission API

The API boundary for submissions is strictly defined to ensure decoupling and security:
- Validate Request: Authenticates users, rate-limits requests, validates maximum source size (e.g., 50KB), and verifies problem existence.
- Persist Submission/Job: Saves the incoming source code and transitions to a durable QUEUED state. It queues a job payload for the async workers.
- No Compilation in Spring: The Spring API process must NEVER compile or execute user code.
- Return Identifier: The API immediately returns a stable submissionId or jobId and a clear state model. The frontend polls this ID for updates.

## 2. Data Migration Strategy

To support the judge architecture, the database schema will require additions. (Note: This migration is NOT executed in this phase, only proposed).

Minimum schema additions required:

- Submissions Table Updates:
  - status: Expanded to cover QUEUED, COMPILING, RUNNING, ACCEPTED, WRONG_ANSWER, COMPILE_ERROR, RUNTIME_ERROR, TIME_LIMIT_EXCEEDED, MEMORY_LIMIT_EXCEEDED, SYSTEM_ERROR.
  - runtime_ms: Actual execution time (integer).
  - memory_kb: Peak memory consumption (integer).
  - compiler_output: Text field for bounded build diagnostics.

- Test Case Execution Results Table (New):
  - id (PK)
  - submission_id (FK to submissions)
  - test_case_id (FK to test_cases)
  - status (Individual verdict)
  - runtime_ms, memory_kb

- Judge Metadata Table (New):
  - Maps problem_id to contract execution metadata (method name, parameter types, comparator type, time/memory limits, language configurations).

The data migration will be purely additive to the legacy schema, ensuring problems and test_cases core records remain frozen.

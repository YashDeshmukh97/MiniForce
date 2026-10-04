# Problem Harness & Comparator Architecture

This document formalizes the Phase D.7 architecture for executing heterogeneous problem types in the MiniForce Sandbox.

## 1. Current Data Contract

**Audit Findings:**
- Problem: Contains starter_code but lacks structured method signatures (method name, parameter types, return types) and invocation strategies.
- TestCase: Contains input_data and expected_output as raw unstructured TEXT.
- ProblemContract: Currently a lightweight DTO containing ComparatorType, limits, and raw test cases, but missing dynamic execution metadata.
- **Anomalies**: Test case IDs 27-30 are heavily duplicated in the SQL seed, causing problems 16 and 17 to lack effective test coverage. 

## 2. ProblemContract V2

To support dynamic harness generation, the ProblemContract will be expanded to include:

- 	argetMethodName (String): e.g., "twoSum"
- parameterTypes (List<String>): e.g., ["int[]", "int"]
- eturnType (String): e.g., "int[]"
- invocationStrategy (Enum): METHOD, CLASS_SIMULATION
- inputRepresentation (Enum): e.g., JSON, CUSTOM
- outputRepresentation (Enum): JSON, STRING
- comparatorType (Enum): Maps to the Comparator Strategy below.
- 	imeLimitMs (int), memoryLimitMb (int)

## 3. Input Representation & Codec Architecture

Because the 100-problem catalog is heterogeneous (scalars, matrices, string arguments, parallel array lists), **no universal parser** will be attempted.

Instead, the architecture utilizes a bespoke InputCodec interface:
- parse(String rawInput, ProblemContract contract)
- alidate(...)
- serializeForHarness(...)

The TestCaseAdapter delegates parsing to the appropriate codec implementation corresponding to the problem's data family. Missing metadata or unparseable inputs must explicitly fail validation rather than attempting to guess intent silently.

## 4. Output Representation

Outputs from the executed user code will be strictly serialized to JSON to standard output by the generated harness, allowing the Spring API comparator to perform type-safe equality checks.

## 5. Generated Harness Model

**Concept:**
A HarnessGenerator builds a Main.java inside the isolated workspace alongside Solution.java.
- **Method Execution**: 
  1. Reads parsed, JSON-structured inputs from stdin.
  2. Uses standard deserialization (embedded in the container) to construct arguments based on parameterTypes.
  3. Invokes 
ew Solution().targetMethod(...).
  4. Prints serialized output to stdout.
- **Class Simulation**:
  1. Reads parallel arrays of commands and arguments.
  2. Iterates, instantiates the target class, invokes methods via reflection, tracks results in a JSON array.
  3. Prints final array.

## 6. Comparator Strategy

The comparison logic will execute **in the Spring Boot API**, not the sandbox container, using the structured JSON output.

1. **EXACT / TRIMMED_EXACT**: Simple string equality. (String/Boolean returns)
2. **INTEGER / FLOAT_WITH_TOLERANCE**: Numeric parsing and delta checks.
3. **ARRAY_EXACT**: Deep equality of JSON arrays.
4. **ARRAY_UNORDERED**: JSON array parsing followed by sorting/frequency mapping.
5. **MATRIX_EXACT**: 2D JSON array deep equality.
6. **CUSTOM**: Strategy-pattern interface for bespoke verification.

## 7. TestCase Adapter

An adapter layer translates existing raw 	est_cases rows dynamically at execution time using the explicit InputCodec. Raw legacy test data remains completely untouched in the database.

## 8. Catalog Migration Strategy

**Metadata Extraction**: We will implement an automated parser (dry-run script) over starter_code to deduce methodName, eturnType, and parameterTypes.
**Data Integrity**: A future migration script will append structured metadata to a problem_metadata table, allowing iterative, non-destructive rollout of the execution engine while MockJudgeExecutionAdapter remains the active default.

## 9. Pilot Problem Selection

When Phase D.8 begins, we will pilot:
- **Problem 1 (Two Sum)**: Array return, METHOD invocation, ARRAY_EXACT.
- **Problem 2 (Valid Parentheses)**: Boolean return, METHOD invocation, EXACT_TEXT.
- **Problem 3 (Longest Substring...)**: Integer return, METHOD invocation, INTEGER.
- **Problem 69 (MedianFinder)**: CLASS_SIMULATION invocation.

*(Note: Problem 17 is currently excluded from pilot consideration until its test cases are repaired).*

## 10. Security Boundary

- **Harness execution**: Main.java executes inside the --network none Docker sandbox.
- **No injection**: The harness is compiled directly into the isolated workspace. Arguments are passed purely through stream I/O (stdin), avoiding shell interpolation entirely.
- **Comparisons**: Executed safely in the API layer, parsing output bounded to 1MB. 

## 11. Error/Verdict Mapping

- javac failure -> COMPILATION_ERROR
- Non-zero JVM exit or uncaught harness exception -> RUNTIME_ERROR
- Process Timeout -> TIME_LIMIT_EXCEEDED
- Comparator logic passes -> ACCEPTED
- Comparator logic fails -> WRONG_ANSWER

## 12. Deferred Items

- Full production backpressure and queuing.
- Disk quota enforcement on Windows bind mounts.

## 13. Explicit Non-Goals

- Inventing a universal regex parser for all possible edge cases of malformed inputs.
- Changing the live database schema prematurely during design.
- Implementing the runtime adapter before the design is audited.
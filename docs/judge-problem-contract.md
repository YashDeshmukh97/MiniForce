# Future Problem Judging Contract

The current problems and test_cases tables remain the preserved content layer. A future real judge needs a separate, versioned metadata layer to bridge legacy string records into executable contracts.

## 1. Problem Execution Contract

The existing 100 problems have heterogeneous test data (arrays, strings, primitives). To execute them reliably, the following metadata contract must be mapped per-problem:
- Callable Method Name: e.g., countInversions, twoSum.
- Parameter Types: Ordered list of types (e.g., [int[], int]).
- Return Type: The expected output type (e.g., int[], boolean).
- Invocation Strategy: e.g., METHOD_CALL (via generated harness) or STDIN_STDOUT (direct I/O).
- Input Representation: How legacy DB strings are deserialized into parameter types (e.g., JSON arrays, CSV).
- Output Representation: How the return value is serialized for comparison.
- Output Normalization: Rules for whitespace stripping, case-insensitivity, or canonical sorting.
- Comparator Type: The engine used to grade the output.
- Constraints: Constraints exposed to the UI/compiler.
- Time Limit: Per-test timeout (e.g., 2000ms).
- Memory Limit: Per-test cap (e.g., 256MB).
- Java Template/Starter Contract: The boilerplate provided to the user (e.g., class Solution { ... }).

We will not convert the entire problem bank to stdin/stdout automatically. Method-level invocation with a generated harness is preferred for complex types.

## 2. Java 17 Compilation/Execution Contract

The worker requires a strictly defined contract for compilation and execution to avoid hardcoding commands in the API layer:
- Source File Naming: Enforced naming convention, e.g., Solution.java.
- Class Naming: Public class must match the source file (e.g., public class Solution).
- Compilation Command: Abstracted command (e.g., javac -encoding UTF-8 -source 17 -target 17 Solution.java Main.java).
- Execution Command: Abstracted command (e.g., java -Xmx256m -XX:+UseSerialGC Main).
- Compile-Error Capture: stderr collected from javac, bounded to 10KB, and reported as COMPILE_ERROR.
- Runtime-Error Capture: stderr from java, bounded, capturing stack traces for RUNTIME_ERROR.
- Timeout Handling: Sandbox intercepts timeouts, sending SIGKILL to the JVM process tree.
- Stdout/Stderr Handling: Streams are written to bounded files, avoiding memory exhaustion.
- Exit-Code Handling: 
  - 0: Process completed, check comparator.
  - > 0: Runtime error or memory limit (e.g., OOMKilled).

## 3. Comparator Architecture

Comparators define how actual output matches expected output. The architecture will support the following interfaces:
- Exact Scalar Comparison: Strictly matches primitives/strings.
- Normalized Text Comparison: Ignores trailing whitespace/newlines.
- Arrays: Order-sensitive element-by-element comparison.
- Matrices: Multi-dimensional strict comparison.
- Unordered Collections: Compares elements ignoring index order where appropriate (e.g., set equivalence).
- Floating-Point Tolerance: Accepts output within a specified epsilon (e.g., 1e-5) where appropriate.

Interfaces and configuration rules will be implemented before any concrete comparator logic.

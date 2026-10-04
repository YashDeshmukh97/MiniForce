package com.miniforce.judge.runner;

public record ResourcePolicy(
    int memoryLimitMb,
    double cpuLimit,
    int pidsLimit,
    long compilerTimeoutSeconds,
    long runtimeTimeoutSeconds,
    int maxOutputBytes
) {
    /** Production defaults — tight bounds to prevent DoS and resource exhaustion. */
    public static final ResourcePolicy DEFAULT = new ResourcePolicy(
        512,        // 512 MB
        1.0,        // 1 CPU
        32,         // 32 PIDs
        5L,         // 5s compiler timeout
        2L,         // 2s runtime timeout
        1024 * 1024 // 1 MB max output
    );

    /**
     * Relaxed policy for integration tests that launch Docker containers with
     * cold JVM startup overhead on WSL2/CI. NOT for production use.
     */
    public static final ResourcePolicy TEST_GENEROUS = new ResourcePolicy(
        512,        // 512 MB
        1.0,        // 1 CPU
        32,         // 32 PIDs
        15L,        // 15s compiler — accounts for cold container + javac startup
        10L,        // 10s runtime — accounts for cold JVM class loading
        1024 * 1024 // 1 MB max output
    );
}

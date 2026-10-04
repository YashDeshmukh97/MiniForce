package com.miniforce.judge.runner;

public record RunnerResult(
    int exitCode,
    String stdout,
    String stderr,
    boolean timeout,
    String systemError,
    long durationMs,
    boolean outputTruncated
) {}
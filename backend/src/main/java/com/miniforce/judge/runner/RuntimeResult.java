package com.miniforce.judge.runner;

public record RuntimeResult(
    boolean success,
    String stdout,
    String stderr,
    int exitCode,
    boolean timeout,
    String systemError,
    long durationMs,
    boolean outputTruncated
) {}
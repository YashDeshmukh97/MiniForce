package com.miniforce.judge.runner;

public record CompilerResult(
    boolean success,
    String output,
    boolean timeout,
    String systemError,
    boolean outputTruncated
) {}
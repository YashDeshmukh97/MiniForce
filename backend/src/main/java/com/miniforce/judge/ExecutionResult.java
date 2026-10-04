package com.miniforce.judge;

import com.miniforce.model.SubmissionStatus;

public record ExecutionResult(
    SubmissionStatus status,
    Integer runtimeMs,
    Integer memoryKb,
    String compilerOutput,
    String runtimeErrorOutput,
    Long failingTestCaseId,
    String systemErrorMessage
) {}
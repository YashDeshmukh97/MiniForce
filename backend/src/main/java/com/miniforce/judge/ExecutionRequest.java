package com.miniforce.judge;

public record ExecutionRequest(
    Long submissionId,
    Long problemId,
    String language,
    String sourceCode,
    ProblemContract contract
) {}

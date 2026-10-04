package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;

public interface JudgeExecutionPort {
    ExecutionResult execute(ExecutionRequest request);
}
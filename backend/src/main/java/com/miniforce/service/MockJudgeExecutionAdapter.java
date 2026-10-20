package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.model.SubmissionStatus;
import org.springframework.stereotype.Component;

@Component
public class MockJudgeExecutionAdapter implements JudgeExecutionPort {
    @Override
    public ExecutionResult execute(ExecutionRequest request) {
        // Deterministic mock logic for tests. NOT invoking java/javac.
        String code = request.sourceCode();
        if (code.contains("COMPILATION_ERROR_MOCK")) {
            return new ExecutionResult(SubmissionStatus.COMPILATION_ERROR, null, null, "Mock compilation error", null, null, null);
        }
        if (code.contains("WRONG_ANSWER_MOCK")) {
            return new ExecutionResult(SubmissionStatus.WRONG_ANSWER, 10, 1024, null, null, null, null);
        }
        if (code.contains("SYSTEM_ERROR_MOCK")) {
            return new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null, "Mock system error");
        }
        if (code.contains("ACCEPTED_MOCK")) {
            return new ExecutionResult(SubmissionStatus.ACCEPTED, 15, 2048, null, null, null, null);
        }
        
        return new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null, "This problem does not currently have an executable judge.");
    }
}

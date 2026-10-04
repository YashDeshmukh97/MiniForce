package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class JudgeExecutionRouter implements JudgeExecutionPort {

    private static final Logger log = LoggerFactory.getLogger(JudgeExecutionRouter.class);

    private final boolean realEnabled;
    private final MockJudgeExecutionAdapter mockAdapter;
    private final DockerRealJudgeAdapter realAdapter;

    public JudgeExecutionRouter(
            @Value("${miniforce.judge.real-enabled:false}") boolean realEnabled,
            MockJudgeExecutionAdapter mockAdapter,
            DockerRealJudgeAdapter realAdapter) {
        this.realEnabled = realEnabled;
        this.mockAdapter = mockAdapter;
        this.realAdapter = realAdapter;
    }

    @Override
    public ExecutionResult execute(ExecutionRequest request) {
        if (realEnabled && request.problemId() != null && (request.problemId() == 1L || request.problemId() == 2L || request.problemId() == 3L || request.problemId() == 8L || request.problemId() == 12L || request.problemId() == 13L || request.problemId() == 14L || request.problemId() == 20L)) {
            log.info("Routing submission {} (problem {}) to REAL judge", request.submissionId(), request.problemId());
            return realAdapter.execute(request);
        } else {
            log.info("Routing submission {} (problem {}) to MOCK judge", request.submissionId(), request.problemId());
            return mockAdapter.execute(request);
        }
    }
}


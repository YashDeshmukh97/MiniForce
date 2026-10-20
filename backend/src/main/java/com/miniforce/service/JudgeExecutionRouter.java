package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.model.SubmissionStatus;
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
    private final String workerUrl;
    private final MockJudgeExecutionAdapter mockAdapter;
    private final DockerRealJudgeAdapter realAdapter;
    private final RemoteJudgeExecutionAdapter remoteAdapter;

    @org.springframework.beans.factory.annotation.Autowired
    public JudgeExecutionRouter(
            @Value("${miniforce.judge.real-enabled:false}") boolean realEnabled,
            @Value("${miniforce.judge.worker-url:}") String workerUrl,
            MockJudgeExecutionAdapter mockAdapter,
            DockerRealJudgeAdapter realAdapter,
            RemoteJudgeExecutionAdapter remoteAdapter) {
        this.realEnabled = realEnabled;
        this.workerUrl = workerUrl != null ? workerUrl.trim() : "";
        this.mockAdapter = mockAdapter;
        this.realAdapter = realAdapter;
        this.remoteAdapter = remoteAdapter;
    }

    public JudgeExecutionRouter(
            boolean realEnabled,
            MockJudgeExecutionAdapter mockAdapter,
            DockerRealJudgeAdapter realAdapter) {
        this(realEnabled, "", mockAdapter, realAdapter, null);
    }

    @Override
    public ExecutionResult execute(ExecutionRequest request) {
        if (realEnabled) {
            Long pid = request.problemId();
            if (pid != null && pid >= 1L && pid <= 50L) {
                if (!workerUrl.isEmpty() && remoteAdapter != null) {
                    log.info("Routing submission {} (problem {}) to REMOTE worker at {}", request.submissionId(), request.problemId(), workerUrl);
                    return remoteAdapter.execute(request);
                } else {
                    log.info("Routing submission {} (problem {}) to LOCAL REAL judge", request.submissionId(), request.problemId());
                    return realAdapter.execute(request);
                }
            } else {
                log.warn("Submission {} (problem {}) has no executable judge implementation.", request.submissionId(), request.problemId());
                return new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null, "This problem does not currently have an executable judge.");
            }
        } else {
            log.info("Routing submission {} (problem {}) to MOCK judge", request.submissionId(), request.problemId());
            return mockAdapter.execute(request);
        }
    }
}


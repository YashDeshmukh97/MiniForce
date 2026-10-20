
package com.miniforce.service;

import com.miniforce.model.Submission;
import com.miniforce.model.SubmissionStatus;
import com.miniforce.model.TestCase;
import com.miniforce.repository.TestCaseRepository;
import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.judge.ProblemContract;
import com.miniforce.judge.TestCaseRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JudgeWorker {
    private static final Logger log = LoggerFactory.getLogger(JudgeWorker.class);
    private final JobClaimService claimService;
    private final JudgeExecutionPort executionPort;
    private final TestCaseRepository testCaseRepository;

    public JudgeWorker(JobClaimService claimService, JudgeExecutionPort executionPort, TestCaseRepository testCaseRepository) {
        this.claimService = claimService;
        this.executionPort = executionPort;
        this.testCaseRepository = testCaseRepository;
        log.info("============== JudgeWorker initialized with port: " + executionPort.getClass().getName());
    }

    @org.springframework.beans.factory.annotation.Value("${miniforce.judge.scheduler-enabled:true}")
    private boolean schedulerEnabled = true;

    @Scheduled(fixedDelay = 2000)
    public void pollQueue() {
        if (!schedulerEnabled) return;
        processNext();
    }

    public synchronized void processNext() {
        Optional<Submission> job = claimService.claimNextJob();
        if (job.isEmpty()) return;

        Submission s = job.get();
        log.info("============== JudgeWorker claimed job: " + s.getId() + " for problem: " + s.getProblem().getId());
        try {
            List<TestCase> dbTestCases = testCaseRepository.findByProblemId(s.getProblem().getId());
            List<TestCaseRequest> tcRequests = dbTestCases.stream()
                .map(tc -> new TestCaseRequest(tc.getId(), tc.getInputData(), tc.getExpectedOutput()))
                .collect(Collectors.toList());

            ProblemContract contract = new ProblemContract(null, 2000, 256000, 1024, tcRequests);
            ExecutionRequest request = new ExecutionRequest(s.getId(), s.getProblem().getId(), s.getLanguage(), s.getCode(), contract);
            
            log.info("============== Executing with port: " + executionPort.getClass().getName());
            ExecutionResult result = executionPort.execute(request);
            log.info("============== Result: " + result.status());
            claimService.markTerminal(s.getId(), result);
        } catch (Exception e) {
            e.printStackTrace();
            claimService.markTerminal(s.getId(), new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null, e.getMessage()));
        }
    }
}


package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.judge.TestCaseRequest;
import com.miniforce.judge.pilot.*;
import com.miniforce.model.SubmissionStatus;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DockerRealJudgeAdapter implements JudgeExecutionPort {

    private static final Logger log = LoggerFactory.getLogger(DockerRealJudgeAdapter.class);
    private final DockerPilotJudge pilotJudge;

    public DockerRealJudgeAdapter(DockerPilotJudge pilotJudge) {
        this.pilotJudge = pilotJudge;
    }

    @Override
    public ExecutionResult execute(ExecutionRequest request) {
        if (request.contract() == null || request.contract().testCases() == null || request.contract().testCases().isEmpty()) {
            return new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null, "Problem has no formal test cases. Cannot execute.");
        }

        Long pid = request.problemId();
        InputCodec codec;
        String harnessCode;
        OutputComparator comparator;
        
                if (pid == 1L) {
            codec = new TwoSumInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? TwoSumPythonHarnessGenerator.generate() : TwoSumHarnessGenerator.generate();
            comparator = new ArrayExactComparator();
        } else if (pid == 2L) {
            codec = new ValidParenthesesInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? ValidParenthesesPythonHarnessGenerator.generate() : ValidParenthesesHarnessGenerator.generate();
            comparator = new BooleanExactComparator();
        } else if (pid == 3L) {
            codec = new LongestSubstringInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? LongestSubstringPythonHarnessGenerator.generate() : LongestSubstringHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 8L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? MaxProfitPythonHarnessGenerator.generate() : MaxProfitHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 12L) {
            codec = new SingleIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? ClimbStairsPythonHarnessGenerator.generate() : ClimbStairsHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 13L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? ProductExceptSelfPythonHarnessGenerator.generate() : ProductExceptSelfHarnessGenerator.generate();
            comparator = new ArrayExactComparator();
        } else if (pid == 14L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? MaxSubArrayPythonHarnessGenerator.generate() : MaxSubArrayHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 20L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? LongestConsecutivePythonHarnessGenerator.generate() : LongestConsecutiveHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else {
            return new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null, "Unsupported problem for real judge: " + pid);
        }

        log.info("Executing Real Judge for problem {} submission {}", pid, request.submissionId());

        int totalRuntime = 0;
        int maxMemory = 0;

        for (TestCaseRequest tc : request.contract().testCases()) {
            PilotResult res = pilotJudge.executeTestCase(request.language(), request.sourceCode(), tc.input(), tc.expectedOutput(), codec, harnessCode, comparator);
            
            if (!"ACCEPTED".equals(res.verdict())) {
                SubmissionStatus status = mapStatus(res.verdict());
                log.info("Submission {} failed on test case {} with verdict {}", request.submissionId(), tc.id(), res.verdict());
                return new ExecutionResult(
                    status,
                    totalRuntime, maxMemory,
                    "COMPILATION_ERROR".equals(res.verdict()) ? res.actualOutput() : null,
                    "RUNTIME_ERROR".equals(res.verdict()) ? res.actualOutput() : null,
                    tc.id(),
                    res.systemError()
                );
            }
            totalRuntime += 10;
        }

        log.info("Submission {} ACCEPTED", request.submissionId());
        return new ExecutionResult(
            SubmissionStatus.ACCEPTED,
            totalRuntime, maxMemory, null, null, null, null
        );
    }
    
    private SubmissionStatus mapStatus(String verdict) {
        return switch (verdict) {
            case "ACCEPTED" -> SubmissionStatus.ACCEPTED;
            case "WRONG_ANSWER" -> SubmissionStatus.WRONG_ANSWER;
            case "COMPILATION_ERROR" -> SubmissionStatus.COMPILATION_ERROR;
            case "RUNTIME_ERROR" -> SubmissionStatus.RUNTIME_ERROR;
            case "TIME_LIMIT_EXCEEDED" -> SubmissionStatus.TIME_LIMIT_EXCEEDED;
            case "SYSTEM_ERROR" -> SubmissionStatus.SYSTEM_ERROR;
            default -> SubmissionStatus.SYSTEM_ERROR;
        };
    }
}


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
        } else if (pid == 4L) {
            codec = new MergeIntervalsInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : MergeIntervalsHarnessGenerator.generate();
            comparator = new ArrayExactComparator();
        } else if (pid == 5L) {
            codec = new CourseScheduleInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : CourseScheduleHarnessGenerator.generate();
            comparator = new BooleanExactComparator();
        } else if (pid == 6L) {
            codec = new MedianSortedArraysInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : MedianSortedArraysHarnessGenerator.generate();
            comparator = new DoubleExactComparator();
        } else if (pid == 7L) {
            codec = new WordLadderInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : WordLadderHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 8L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? MaxProfitPythonHarnessGenerator.generate() : MaxProfitHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 9L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : ReverseLinkedListHarnessGenerator.generate();
            comparator = new ArrayExactComparator();
        } else if (pid == 10L) {
            codec = new CharGridInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : NumIslandsHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 11L) {
            codec = new IntArrayIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : CoinChangeHarnessGenerator.generate();
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
        } else if (pid == 15L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : TrapRainWaterHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 16L) {
            codec = new TwoStringInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : ValidAnagramHarnessGenerator.generate();
            comparator = new BooleanExactComparator();
        } else if (pid == 17L) {
            codec = new StringArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : GroupAnagramsHarnessGenerator.generate();
            comparator = new NestedUnorderedListStringComparator();
        } else if (pid == 18L) {
            codec = new IntArrayIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : TopKFrequentHarnessGenerator.generate();
            comparator = new UnorderedIntArrayComparator();
        } else if (pid == 19L) {
            codec = new StringListInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : EncodeDecodeHarnessGenerator.generate();
            comparator = new ArrayExactComparator();
        } else if (pid == 20L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? LongestConsecutivePythonHarnessGenerator.generate() : LongestConsecutiveHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 21L) {
            codec = new CharGridInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : new ValidSudokuHarnessGenerator().generateHarness(request.sourceCode());
            comparator = new BooleanExactComparator();
        } else if (pid == 22L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : new ContainsDuplicateHarnessGenerator().generateHarness(request.sourceCode());
            comparator = new BooleanExactComparator();
        } else if (pid == 23L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : new FindDisappearedNumbersHarnessGenerator().generateHarness(request.sourceCode());
            comparator = new UnorderedIntArrayComparator();
        } else if (pid == 24L) {
            codec = new TwoIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : new IntersectionHarnessGenerator().generateHarness(request.sourceCode());
            comparator = new UnorderedIntArrayComparator();
        } else if (pid == 25L) {
            codec = new TwoStringInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : new IsomorphicStringsHarnessGenerator().generateHarness(request.sourceCode());
            comparator = new BooleanExactComparator();
        } else if (pid == 26L) {
            codec = new SingleStringInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : new ValidPalindromeHarnessGenerator().generateHarness(request.sourceCode());
            comparator = new BooleanExactComparator();
        } else if (pid == 27L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : new ThreeSumHarnessGenerator().generateHarness(request.sourceCode());
            comparator = new NestedUnorderedListIntegerComparator();
        } else if (pid == 28L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : new ContainerWithMostWaterHarnessGenerator().generateHarness(request.sourceCode());
            comparator = new IntegerExactComparator();
        } else if (pid == 29L) {
            codec = new TwoStringInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : new MinimumWindowSubstringHarnessGenerator().generateHarness(request.sourceCode());
            comparator = new StringExactComparator();
        } else if (pid == 30L) {
            codec = new StringIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : new LongestRepeatingCharacterReplacementHarnessGenerator().generateHarness(request.sourceCode());
            comparator = new IntegerExactComparator();
        } else if (pid == 31L) {
            codec = new P5TwoStringInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : PermutationInStringHarnessGenerator.generate();
            comparator = new BooleanExactComparator();
        } else if (pid == 32L) {
            codec = new P5IntArrayIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : MaximumAverageSubarrayIHarnessGenerator.generate();
            comparator = new DoubleExactComparator();
        } else if (pid == 33L) {
            codec = new P5TwoStringInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : FindAllAnagramsInAStringHarnessGenerator.generate();
            comparator = new ArrayExactComparator();
        } else if (pid == 34L) {
            codec = new P5IntArrayIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : SubarraysWithKDifferentIntegersHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 35L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : FruitIntoBasketsHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 36L) {
            codec = new ClassOperationsInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : MinStackHarnessGenerator.generate();
            comparator = new StringExactComparator();
        } else if (pid == 37L) {
            codec = new P5StringArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : EvaluateReversePolishNotationHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 38L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : DailyTemperaturesHarnessGenerator.generate();
            comparator = new ArrayExactComparator();
        } else if (pid == 39L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : LargestRectangleInHistogramHarnessGenerator.generate();
            comparator = new IntegerExactComparator();
        } else if (pid == 40L) {
            codec = new ClassOperationsInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : ImplementQueueUsingStacksHarnessGenerator.generate();
            comparator = new StringExactComparator();
        } else if (pid == 41L) {
            codec = new P5IntArrayIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : Problem41HarnessGenerator.generate(request.sourceCode());
            comparator = new ArrayExactComparator();
        } else if (pid == 42L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : Problem42HarnessGenerator.generate(request.sourceCode());
            comparator = new ArrayExactComparator();
        } else if (pid == 43L) {
            codec = new P5IntArrayIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : Problem43HarnessGenerator.generate(request.sourceCode());
            comparator = new IntegerExactComparator();
        } else if (pid == 44L) {
            codec = new P6MatrixIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : Problem44HarnessGenerator.generate(request.sourceCode());
            comparator = new BooleanExactComparator();
        } else if (pid == 45L) {
            codec = new P5IntArrayIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : Problem45HarnessGenerator.generate(request.sourceCode());
            comparator = new IntegerExactComparator();
        } else if (pid == 46L) {
            codec = new SingleIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : Problem46HarnessGenerator.generate(request.sourceCode());
            comparator = new IntegerExactComparator();
        } else if (pid == 47L) {
            codec = new P5IntArrayIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : Problem47HarnessGenerator.generate(request.sourceCode());
            comparator = new IntegerExactComparator();
        } else if (pid == 48L) {
            codec = new P6ClassOperationsStringInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : Problem48HarnessGenerator.generate(request.sourceCode());
            comparator = new ArrayExactComparator();
        } else if (pid == 49L) {
            codec = new P5IntArrayIntInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : Problem49HarnessGenerator.generate(request.sourceCode());
            comparator = new IntegerExactComparator();
        } else if (pid == 50L) {
            codec = new P6TwoIntArrayInputCodec();
            harnessCode = "PYTHON_3".equals(request.language()) ? null : Problem50HarnessGenerator.generate(request.sourceCode());
            comparator = new ArrayExactComparator();
        } else {
            return new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null, "Unsupported problem for real judge: " + pid);
        }

        if ("PYTHON_3".equals(request.language())) {
            harnessCode = PythonHarnessRegistry.getHarness(pid);
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


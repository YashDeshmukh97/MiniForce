package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.judge.ProblemContract;
import com.miniforce.judge.TestCaseRequest;
import com.miniforce.model.SubmissionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class AsteroidCollisionPythonRegressionTest {

    @Autowired
    private DockerRealJudgeAdapter realJudge;

    private static final String CORRECT_PYTHON_SOLUTION = """
        class Solution:
            def asteroidCollision(self, asteroids: list[int]) -> list[int]:
                stack = []
                for a in asteroids:
                    while stack and a < 0 < stack[-1]:
                        if stack[-1] < -a:
                            stack.pop()
                            continue
                        elif stack[-1] == -a:
                            stack.pop()
                        break
                    else:
                        stack.append(a)
                return stack
        """;

    private static final String BUGGY_INFINITE_LOOP_SOLUTION = """
        class Solution:
            def asteroidCollision(self, asteroids: list[int]) -> list[int]:
                stack = []
                for a in asteroids:
                    while stack and a < 0 and stack[-1] > 0:
                        if stack[-1] < -a:
                            stack.pop()
                            continue
                        elif stack[-1] == -a:
                            stack.pop()
                            break
                        # Missing break when stack[-1] > -a causes infinite loop on [5,10,-5]
                    else:
                        stack.append(a)
                return stack
        """;

    @ParameterizedTest(name = "Input {0} -> Expected {1}")
    @CsvSource(delimiter = '|', value = {
        "'[5,10,-5]'     | '[5,10]'",
        "'[8,-8]'        | '[]'",
        "'[10,2,-5]'     | '[10]'",
        "'[-2,-1,1,2]'   | '[-2,-1,1,2]'"
    })
    @DisplayName("Verify Asteroid Collision (Problem 42) all 4 correctness test cases in Python")
    public void testCorrectnessCases(String input, String expected) {
        TestCaseRequest tc = new TestCaseRequest(1L, input, expected);
        ProblemContract contract = new ProblemContract(null, 2000, 256000, 1024, List.of(tc));
        ExecutionRequest req = new ExecutionRequest(42001L, 42L, "PYTHON_3", CORRECT_PYTHON_SOLUTION, contract);

        ExecutionResult result = realJudge.execute(req);

        assertThat(result.status())
            .as("Expected ACCEPTED for input " + input + " but got " + result.status() + " (err: " + result.systemErrorMessage() + ")")
            .isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Verify all 4 correctness cases evaluated together in a single contract")
    public void testAllCasesTogether() {
        List<TestCaseRequest> cases = List.of(
            new TestCaseRequest(1L, "[5,10,-5]", "[5,10]"),
            new TestCaseRequest(2L, "[8,-8]", "[]"),
            new TestCaseRequest(3L, "[10,2,-5]", "[10]"),
            new TestCaseRequest(4L, "[-2,-1,1,2]", "[-2,-1,1,2]")
        );
        ProblemContract contract = new ProblemContract(null, 2000, 256000, 1024, cases);
        ExecutionRequest req = new ExecutionRequest(42002L, 42L, "PYTHON_3", CORRECT_PYTHON_SOLUTION, contract);

        ExecutionResult result = realJudge.execute(req);

        assertEquals(SubmissionStatus.ACCEPTED, result.status());
        assertThat(result.runtimeMs()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Verify buggy stack algorithm with missing break times out safely")
    public void testBuggyStackAlgorithmTimeLimitExceeded() {
        TestCaseRequest tc = new TestCaseRequest(78L, "[5,10,-5]", "[5,10]");
        ProblemContract contract = new ProblemContract(null, 2000, 256000, 1024, List.of(tc));
        ExecutionRequest req = new ExecutionRequest(42003L, 42L, "PYTHON_3", BUGGY_INFINITE_LOOP_SOLUTION, contract);

        ExecutionResult result = realJudge.execute(req);

        assertEquals(SubmissionStatus.TIME_LIMIT_EXCEEDED, result.status());
    }

    @Test
    @DisplayName("Verify wrong answer returns WRONG_ANSWER status")
    public void testWrongAnswer() {
        String wrongCode = """
            class Solution:
                def asteroidCollision(self, asteroids: list[int]) -> list[int]:
                    return [0]
            """;
        TestCaseRequest tc = new TestCaseRequest(78L, "[5,10,-5]", "[5,10]");
        ProblemContract contract = new ProblemContract(null, 2000, 256000, 1024, List.of(tc));
        ExecutionRequest req = new ExecutionRequest(42004L, 42L, "PYTHON_3", wrongCode, contract);

        ExecutionResult result = realJudge.execute(req);

        assertEquals(SubmissionStatus.WRONG_ANSWER, result.status());
    }
}

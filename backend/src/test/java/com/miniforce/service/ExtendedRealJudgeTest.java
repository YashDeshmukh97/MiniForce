package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.judge.TestCaseRequest;
import com.miniforce.judge.ProblemContract;
import com.miniforce.model.SubmissionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ExtendedRealJudgeTest {

    @Autowired
    private DockerRealJudgeAdapter realJudge;

    private ExecutionRequest buildReq(Long pid, String code, List<TestCaseRequest> testCases) {
        ProblemContract contract = new ProblemContract(null, 5000, 5000, 512, testCases);
        return new ExecutionRequest(1L, pid, "java", code, contract);
    }

    @Test
    void testProblem8_Accepted() {
        String code = "class Solution { public int maxProfit(int[] prices) { return 5; } }";
        var tcs = List.of(new TestCaseRequest(1L, "[7,1,5,3,6,4]", "5"));
        ExecutionResult res = realJudge.execute(buildReq(8L, code, tcs));
        assertThat(res.status()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem8_WrongAnswer() {
        String code = "class Solution { public int maxProfit(int[] prices) { return 0; } }";
        var tcs = List.of(new TestCaseRequest(1L, "[7,1,5,3,6,4]", "5"));
        ExecutionResult res = realJudge.execute(buildReq(8L, code, tcs));
        assertThat(res.status()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
    }
    
    @Test
    void testProblem8_CompilationError() {
        String code = "class Solution { public int maxProfit(int[] prices) { return x; } }";
        var tcs = List.of(new TestCaseRequest(1L, "[7,1,5,3,6,4]", "5"));
        ExecutionResult res = realJudge.execute(buildReq(8L, code, tcs));
        assertThat(res.status()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
    }

    @Test
    void testProblem12_Accepted() {
        String code = "class Solution { public int climbStairs(int n) { return n; } }";
        var tcs = List.of(new TestCaseRequest(1L, "2", "2"));
        ExecutionResult res = realJudge.execute(buildReq(12L, code, tcs));
        assertThat(res.status()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem13_Accepted() {
        String code = "class Solution { public int[] productExceptSelf(int[] nums) { return new int[]{24,12,8,6}; } }";
        var tcs = List.of(new TestCaseRequest(1L, "[1,2,3,4]", "[24,12,8,6]"));
        ExecutionResult res = realJudge.execute(buildReq(13L, code, tcs));
        assertThat(res.status()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem14_Accepted() {
        String code = "class Solution { public int maxSubArray(int[] nums) { return 6; } }";
        var tcs = List.of(new TestCaseRequest(1L, "[-2,1,-3,4,-1,2,1,-5,4]", "6"));
        ExecutionResult res = realJudge.execute(buildReq(14L, code, tcs));
        assertThat(res.status()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem20_Accepted() {
        String code = "class Solution { public int longestConsecutive(int[] nums) { return 4; } }";
        var tcs = List.of(new TestCaseRequest(1L, "[100,4,200,1,3,2]", "4"));
        ExecutionResult res = realJudge.execute(buildReq(20L, code, tcs));
        assertThat(res.status()).isEqualTo(SubmissionStatus.ACCEPTED);
    }
    
    @Test
    void testProblem1_Regression() {
        String code = "class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0,1}; } }";
        var tcs = List.of(new TestCaseRequest(1L, "[2,7,11,15]; 9", "[0,1]"));
        ExecutionResult res = realJudge.execute(buildReq(1L, code, tcs));
        assertThat(res.status()).isEqualTo(SubmissionStatus.ACCEPTED);
    }
}



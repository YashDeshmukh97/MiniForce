package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.judge.ProblemContract;
import com.miniforce.model.SubmissionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "miniforce.judge.real-enabled=true"
})
public class JudgeIntegrityTest {

    @Autowired
    private JudgeExecutionRouter router;

    private ExecutionRequest buildReq(Long pid, String code) {
        ProblemContract contract = new ProblemContract(null, 5000, 5000, 512, List.of());
        return new ExecutionRequest(1L, pid, "java", code, contract);
    }

    @Test
    void test1_Problem4_YashIsKing_NotAccepted() {
        ExecutionResult res = router.execute(buildReq(4L, "YASH IS KING"));
        assertThat(res.status()).isNotEqualTo(SubmissionStatus.ACCEPTED);
        assertThat(res.status()).isEqualTo(SubmissionStatus.SYSTEM_ERROR);
    }

    @Test
    void test2_Problem5_YashIsKing_NotAccepted() {
        ExecutionResult res = router.execute(buildReq(5L, "YASH IS KING"));
        assertThat(res.status()).isNotEqualTo(SubmissionStatus.ACCEPTED);
        assertThat(res.status()).isEqualTo(SubmissionStatus.SYSTEM_ERROR);
    }

    @Test
    void test3_Problem50_YashIsKing_NotAccepted() {
        ExecutionResult res = router.execute(buildReq(50L, "YASH IS KING"));
        assertThat(res.status()).isNotEqualTo(SubmissionStatus.ACCEPTED);
        assertThat(res.status()).isEqualTo(SubmissionStatus.SYSTEM_ERROR);
    }

    @Test
    void test4_RealJudge_Problem1_InvalidJava_NotAccepted() {
        var tcs = List.of(new com.miniforce.judge.TestCaseRequest(1L, "[2,7,11,15]; 9", "[0,1]"));
        ProblemContract contract = new ProblemContract(null, 5000, 5000, 512, tcs);
        ExecutionResult res = router.execute(new ExecutionRequest(1L, 1L, "java", "YASH IS KING", contract));
        assertThat(res.status()).isNotEqualTo(SubmissionStatus.ACCEPTED);
        assertThat(res.status()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
    }

    @Test
    void test5_RealJudge_Problem1_IncorrectSolution_WrongAnswer() {
        String code = "class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0, 0}; } }";
        var tcs = List.of(new com.miniforce.judge.TestCaseRequest(1L, "[2,7,11,15]; 9", "[0,1]"));
        ProblemContract contract = new ProblemContract(null, 5000, 5000, 512, tcs);
        ExecutionResult res = router.execute(new ExecutionRequest(1L, 1L, "java", code, contract));
        assertThat(res.status()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
    }

    @Test
    void test6_RealJudge_Problem1_CorrectSolution_Accepted() {
        String code = "class Solution { public int[] twoSum(int[] nums, int target) { for(int i=0;i<nums.length;i++){ for(int j=i+1;j<nums.length;j++){ if(nums[i]+nums[j]==target) return new int[]{i,j}; } } return new int[]{}; } }";
        var tcs = List.of(new com.miniforce.judge.TestCaseRequest(1L, "[2,7,11,15]; 9", "[0,1]"));
        ProblemContract contract = new ProblemContract(null, 5000, 5000, 512, tcs);
        ExecutionResult res = router.execute(new ExecutionRequest(1L, 1L, "java", code, contract));
        assertThat(res.status()).isEqualTo(SubmissionStatus.ACCEPTED);
    }
}

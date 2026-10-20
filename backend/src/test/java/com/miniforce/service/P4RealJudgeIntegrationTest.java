package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.judge.ProblemContract;
import com.miniforce.judge.TestCaseRequest;
import com.miniforce.model.SubmissionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"miniforce.judge.real-enabled=true"})
@ActiveProfiles("test")
public class P4RealJudgeIntegrationTest {

    @Autowired
    private DockerRealJudgeAdapter realJudge;

    private ExecutionRequest buildReq(Long pid, String code, List<TestCaseRequest> testCases) {
        ProblemContract contract = new ProblemContract(null, 5000, 5000, 512, testCases);
        return new ExecutionRequest(1L, pid, "java", code, contract);
    }

    @Test
    void testProblem21_ValidSudoku() {
        String code = "class Solution { public boolean isValidSudoku(char[][] board) { return true; } }";
        var tcs = List.of(new TestCaseRequest(1L, "[[\"5\",\"3\",\".\",\".\",\"7\",\".\",\".\",\".\",\".\"],[\"6\",\".\",\".\",\"1\",\"9\",\"5\",\".\",\".\",\".\"],[\".\",\"9\",\"8\",\".\",\".\",\".\",\".\",\"6\",\".\"],[\"8\",\".\",\".\",\".\",\"6\",\".\",\".\",\".\",\"3\"],[\"4\",\".\",\".\",\"8\",\".\",\"3\",\".\",\".\",\"1\"],[\"7\",\".\",\".\",\".\",\"2\",\".\",\".\",\".\",\"6\"],[\".\",\"6\",\".\",\".\",\".\",\".\",\"2\",\"8\",\".\"],[\".\",\".\",\".\",\"4\",\"1\",\"9\",\".\",\".\",\"5\"],[\".\",\".\",\".\",\".\",\"8\",\".\",\".\",\"7\",\"9\"]]", "true"));
        ExecutionResult res = realJudge.execute(buildReq(21L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED); // because true matches "true" output
    }

    @Test
    void testProblem22_ContainsDuplicate() {
        String code = "import java.util.HashSet; class Solution { public boolean containsDuplicate(int[] nums) { return true; } }";
        var tcs = List.of(new TestCaseRequest(1L, "[1,2,3,1]", "true"));
        ExecutionResult res = realJudge.execute(buildReq(22L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem23_FindDisappearedNumbers() {
        String code = "import java.util.*; class Solution { public List<Integer> findDisappearedNumbers(int[] nums) { return Arrays.asList(5, 6); } }";
        var tcs = List.of(new TestCaseRequest(1L, "[4,3,2,7,8,2,3,1]", "[5,6]"));
        ExecutionResult res = realJudge.execute(buildReq(23L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem24_Intersection() {
        String code = "class Solution { public int[] intersection(int[] nums1, int[] nums2) { return new int[]{2}; } }";
        var tcs = List.of(new TestCaseRequest(1L, "[1,2,2,1]; [2,2]", "[2]"));
        ExecutionResult res = realJudge.execute(buildReq(24L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem25_IsomorphicStrings() {
        String code = "class Solution { public boolean isIsomorphic(String s, String t) { return true; } }";
        var tcs = List.of(new TestCaseRequest(1L, "\"egg\"; \"add\"", "true"));
        ExecutionResult res = realJudge.execute(buildReq(25L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem26_ValidPalindrome() {
        String code = "class Solution { public boolean isPalindrome(String s) { return true; } }";
        var tcs = List.of(new TestCaseRequest(1L, "\"A man, a plan, a canal: Panama\"", "true"));
        ExecutionResult res = realJudge.execute(buildReq(26L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem27_3Sum() {
        String code = "import java.util.*; class Solution { public List<List<Integer>> threeSum(int[] nums) { return Arrays.asList(Arrays.asList(-1,-1,2), Arrays.asList(-1,0,1)); } }";
        var tcs = List.of(new TestCaseRequest(1L, "[-1,0,1,2,-1,-4]", "[[-1,-1,2],[-1,0,1]]"));
        ExecutionResult res = realJudge.execute(buildReq(27L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem28_ContainerWithMostWater() {
        String code = "class Solution { public int maxArea(int[] height) { return 49; } }";
        var tcs = List.of(new TestCaseRequest(1L, "[1,8,6,2,5,4,8,3,7]", "49"));
        ExecutionResult res = realJudge.execute(buildReq(28L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem29_MinimumWindowSubstring() {
        String code = "class Solution { public String minWindow(String s, String t) { return \"BANC\"; } }";
        var tcs = List.of(new TestCaseRequest(1L, "\"ADOBECODEBANC\"; \"ABC\"", "\"BANC\""));
        ExecutionResult res = realJudge.execute(buildReq(29L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem30_LongestRepeatingChar() {
        String code = "class Solution { public int characterReplacement(String s, int k) { return 4; } }";
        var tcs = List.of(new TestCaseRequest(1L, "\"ABAB\"; 2", "4"));
        ExecutionResult res = realJudge.execute(buildReq(30L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }
}

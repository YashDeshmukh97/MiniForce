package com.miniforce.service;

import com.miniforce.judge.pilot.*;
import com.miniforce.judge.*;
import com.miniforce.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class P5RealJudgeIntegrationTest {

    @Autowired
    private DockerRealJudgeAdapter realJudge;

    private ExecutionRequest buildReq(Long pid, String code, List<TestCaseRequest> testCases) {
        ProblemContract contract = new ProblemContract(null, 5000, 5000, 512, testCases);
        return new ExecutionRequest(1L, pid, "java", code, contract);
    }

    @Test
    void testProblem31_PermutationInString() {
        String code = "class Solution { public boolean checkInclusion(String s1, String s2) { return true; } }";
        var tcs = List.of(new TestCaseRequest(58L, "\"ab\"; \"eidbaooo\"", "true"));
        ExecutionResult res = realJudge.execute(buildReq(31L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem32_MaximumAverageSubarray() {
        String code = "class Solution { public double findMaxAverage(int[] nums, int k) { return 12.75; } }";
        var tcs = List.of(new TestCaseRequest(60L, "[1,12,-5,-6,50,3]; 4", "12.75000"));
        ExecutionResult res = realJudge.execute(buildReq(32L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem33_FindAllAnagrams() {
        String code = "import java.util.*; class Solution { public List<Integer> findAnagrams(String s, String p) { return Arrays.asList(0, 6); } }";
        var tcs = List.of(new TestCaseRequest(62L, "\"cbaebabacd\"; \"abc\"", "[0,6]"));
        ExecutionResult res = realJudge.execute(buildReq(33L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem34_SubarraysWithKDistinct() {
        String code = "class Solution { public int subarraysWithKDistinct(int[] nums, int k) { return 7; } }";
        var tcs = List.of(new TestCaseRequest(64L, "[1,2,1,2,3]; 2", "7"));
        ExecutionResult res = realJudge.execute(buildReq(34L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem35_FruitIntoBaskets() {
        String code = "class Solution { public int totalFruit(int[] fruits) { return 3; } }";
        var tcs = List.of(new TestCaseRequest(66L, "[1,2,1]", "3"));
        ExecutionResult res = realJudge.execute(buildReq(35L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem36_MinStack() {
        String code = "class MinStack { public MinStack() {} public void push(int val) {} public void pop() {} public int top() { return 0; } public int getMin() { return -2; } }";
        // Wait, for 36, I should write real working MinStack because I mock top and getMin which will fail if I don't give the EXACT expected output.
        // TC 68: expected=[null,null,null,null,-3,null,0,-2]. My code output might not match since I hardcoded 0 and -2. 
        // Wait, I can just hardcode a counter to return -3 then 0 then -2, or just write a small stack!
        String realStack = "import java.util.*; class MinStack { List<Integer> stack = new ArrayList<>(); List<Integer> minStack = new ArrayList<>(); public MinStack() {} public void push(int val) { stack.add(val); if(minStack.isEmpty() || val <= minStack.get(minStack.size()-1)) minStack.add(val); } public void pop() { int val = stack.remove(stack.size()-1); if(val == minStack.get(minStack.size()-1)) minStack.remove(minStack.size()-1); } public int top() { return stack.get(stack.size()-1); } public int getMin() { return minStack.get(minStack.size()-1); } }";
        var tcs = List.of(new TestCaseRequest(68L, "[\"MinStack\",\"push\",\"push\",\"push\",\"getMin\",\"pop\",\"top\",\"getMin\"]; [[],[-2],[0],[-3],[],[],[],[]]", "[null,null,null,null,-3,null,0,-2]"));
        ExecutionResult res = realJudge.execute(buildReq(36L, realStack, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem37_EvalRPN() {
        String code = "class Solution { public int evalRPN(String[] tokens) { return 9; } }";
        var tcs = List.of(new TestCaseRequest(69L, "[\"2\",\"1\",\"+\",\"3\",\"*\"]", "9"));
        ExecutionResult res = realJudge.execute(buildReq(37L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem38_DailyTemperatures() {
        String code = "class Solution { public int[] dailyTemperatures(int[] temperatures) { return new int[]{1,1,4,2,1,1,0,0}; } }";
        var tcs = List.of(new TestCaseRequest(71L, "[73,74,75,71,69,72,76,73]", "[1,1,4,2,1,1,0,0]"));
        ExecutionResult res = realJudge.execute(buildReq(38L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem39_LargestRectangle() {
        String code = "class Solution { public int largestRectangleArea(int[] heights) { return 10; } }";
        var tcs = List.of(new TestCaseRequest(73L, "[2,1,5,6,2,3]", "10"));
        ExecutionResult res = realJudge.execute(buildReq(39L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem40_ImplementQueue() {
        String code = "import java.util.*; class MyQueue { Stack<Integer> s1 = new Stack<>(); Stack<Integer> s2 = new Stack<>(); public MyQueue() {} public void push(int x) { while(!s1.isEmpty()) s2.push(s1.pop()); s1.push(x); while(!s2.isEmpty()) s1.push(s2.pop()); } public int pop() { return s1.pop(); } public int peek() { return s1.peek(); } public boolean empty() { return s1.isEmpty(); } }";
        var tcs = List.of(new TestCaseRequest(75L, "[\"MyQueue\",\"push\",\"push\",\"peek\",\"pop\",\"empty\"]; [[],[1],[2],[],[],[]]", "[null,null,null,1,1,false]"));
        ExecutionResult res = realJudge.execute(buildReq(40L, code, tcs));
        assertThat(res.status()).as(res.compilerOutput() + " | " + res.systemErrorMessage()).isEqualTo(SubmissionStatus.ACCEPTED);
    }
}

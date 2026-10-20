package com.miniforce.service;

import static org.junit.jupiter.api.Assertions.*;

import com.miniforce.model.Problem;
import com.miniforce.model.SubmissionStatus;
import com.miniforce.model.TestCase;
import java.util.List;
import org.junit.jupiter.api.Test;

class SafeJavaDemoJudgeTest {
    private final SafeJavaDemoJudge judge = new SafeJavaDemoJudge();

    @Test
    void genericProblemsRejectWithSystemErrorNow() {
        String source = "class Solution { // MINIFORCE: ACCEPT public int solve() { return 1; } }";
        var verdict = judge.judge(problem("merge-intervals"), "JAVA_17", source, List.of(testCase()));

        assertEquals(SubmissionStatus.SYSTEM_ERROR, verdict.status());
        assertEquals(12 + source.length() % 37, verdict.runtimeMs());
        assertTrue(verdict.message().contains("Run mode is disabled"));
    }

    @Test
    void testCaseContentDoesNotChangeTheDemoPatternVerdict() {
        String source = "class Solution { // MINIFORCE: ACCEPT public int solve() { return 1; } }";
        var withoutCases = judge.judge(problem("merge-intervals"), "JAVA_17", source, List.of());
        var withDifferentCase = judge.judge(problem("merge-intervals"), "JAVA_17", source, List.of(testCase()));

        assertEquals(withoutCases.status(), withDifferentCase.status());
        assertEquals(withoutCases.runtimeMs(), withDifferentCase.runtimeMs());
        assertEquals(0, withoutCases.total());
        assertEquals(1, withDifferentCase.total());
    }

    @Test
    void malformedButMarkedGenericSourceIsNotCompiled() {
        String malformed = "class Solution { // MINIFORCE: ACCEPT this is not valid Java at all !!!! }";
        var verdict = judge.judge(problem("coin-change"), "JAVA_17", malformed, List.of());

        assertEquals(SubmissionStatus.SYSTEM_ERROR, verdict.status());
    }

    @Test
    void twoSumRuleIsDeterministicAndReturnsSystemError() {
        String matching = "import java.util.*; class Solution { public int[] twoSum(int[] nums, int target) { HashMap<Integer,Integer> seen = new HashMap<>(); return new int[]{}; } }";
        String markerOnly = "class Solution { // MINIFORCE: ACCEPT public int solve() { return 1; } }";

        assertEquals(SubmissionStatus.SYSTEM_ERROR, judge.judge(problem("two-sum"), "JAVA_17", matching, List.of()).status());
        assertEquals(SubmissionStatus.SYSTEM_ERROR, judge.judge(problem("two-sum"), "JAVA_17", markerOnly, List.of()).status());
    }

    @Test
    void basicClassCheckProducesCompilationError() {
        var verdict = judge.judge(problem("merge-intervals"), "JAVA_17", "// MINIFORCE: ACCEPT", List.of());
        assertEquals(SubmissionStatus.COMPILATION_ERROR, verdict.status());
        assertNull(verdict.runtimeMs());
    }

    private Problem problem(String slug) { Problem p = new Problem(); p.setSlug(slug); return p; }
    private TestCase testCase() { TestCase t = new TestCase(); t.setInputData("ignored"); t.setExpectedOutput("ignored"); t.setSampleCase(true); return t; }
}

package com.miniforce.service;

import com.miniforce.dto.SubmissionDtos.SubmissionRequest;
import com.miniforce.dto.SubmissionDtos.SubmissionResponse;
import com.miniforce.model.SubmissionStatus;
import com.miniforce.model.User;
import com.miniforce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RunExecutionTest {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserRepository userRepository;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.miniforce.service.ProblemService problemService;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.miniforce.repository.TestCaseRepository testCaseRepository;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 1L);
        user.setUsername("run_tester");
        user.setEmail("run_tester@example.com");

        com.miniforce.model.Problem p1 = new com.miniforce.model.Problem();
        org.springframework.test.util.ReflectionTestUtils.setField(p1, "id", 1L);
        org.mockito.Mockito.when(problemService.entity(1L)).thenReturn(p1);
        
        com.miniforce.model.Problem p31 = new com.miniforce.model.Problem();
        org.springframework.test.util.ReflectionTestUtils.setField(p31, "id", 31L);
        org.mockito.Mockito.when(problemService.entity(31L)).thenReturn(p31);

        com.miniforce.model.Problem p4 = new com.miniforce.model.Problem();
        org.springframework.test.util.ReflectionTestUtils.setField(p4, "id", 4L);
        org.mockito.Mockito.when(problemService.entity(4L)).thenReturn(p4);

        com.miniforce.model.Problem p50 = new com.miniforce.model.Problem();
        org.springframework.test.util.ReflectionTestUtils.setField(p50, "id", 50L);
        org.mockito.Mockito.when(problemService.entity(50L)).thenReturn(p50);

        com.miniforce.model.Problem p51 = new com.miniforce.model.Problem();
        org.springframework.test.util.ReflectionTestUtils.setField(p51, "id", 51L);
        org.mockito.Mockito.when(problemService.entity(51L)).thenReturn(p51);

        com.miniforce.model.TestCase tc1 = new com.miniforce.model.TestCase();
        org.springframework.test.util.ReflectionTestUtils.setField(tc1, "id", 1L);
        tc1.setInputData("[2,7,11,15]; 9");
        tc1.setExpectedOutput("[0,1]");
        tc1.setSampleCase(true);
        org.mockito.Mockito.when(testCaseRepository.findByProblemId(1L)).thenReturn(java.util.List.of(tc1));
        org.mockito.Mockito.when(testCaseRepository.findByProblemId(31L)).thenReturn(java.util.List.of());
        org.mockito.Mockito.when(testCaseRepository.findByProblemId(50L)).thenReturn(java.util.List.of());
        org.mockito.Mockito.when(testCaseRepository.findByProblemId(51L)).thenReturn(java.util.List.of());

        com.miniforce.model.TestCase tc4 = new com.miniforce.model.TestCase();
        org.springframework.test.util.ReflectionTestUtils.setField(tc4, "id", 4L);
        tc4.setInputData("[[1,3],[2,6],[8,10]]");
        tc4.setExpectedOutput("[[1,6],[8,10]]");
        tc4.setSampleCase(true);
        org.mockito.Mockito.when(testCaseRepository.findByProblemId(4L)).thenReturn(java.util.List.of(tc4));
    }

    @Test
    void testA_SupportedProblem_CorrectCode_Accepted() {
        String code = "class Solution { public int[] twoSum(int[] nums, int target) { for(int i=0;i<nums.length;i++){ for(int j=i+1;j<nums.length;j++){ if(nums[i]+nums[j]==target) return new int[]{i,j}; } } return new int[]{}; } }";
        SubmissionResponse res = submissionService.run(user, new SubmissionRequest(1L, null, "JAVA_17", code));
        System.out.println("COMPILER OUTPUT A: " + res.compilerOutput());
        assertThat(res.status()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testB_SupportedProblem_WrongCode_WrongAnswer() {
        String code = "class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0, 0}; } }";
        SubmissionResponse res = submissionService.run(user, new SubmissionRequest(1L, null, "JAVA_17", code));
        assertThat(res.status()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
    }

    @Test
    void testC_SupportedProblem_CompilationError() {
        String code = "class Solution { public int[] twoSum(int[] nums, int target) { YASH IS KING } }";
        SubmissionResponse res = submissionService.run(user, new SubmissionRequest(1L, null, "JAVA_17", code));
        assertThat(res.status()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
    }

    @Test
    void testD_SupportedProblem_RuntimeError() {
        String code = "class Solution { public int[] twoSum(int[] nums, int target) { throw new RuntimeException(\"fail\"); } }";
        SubmissionResponse res = submissionService.run(user, new SubmissionRequest(1L, null, "JAVA_17", code));
        assertThat(res.status()).isEqualTo(SubmissionStatus.RUNTIME_ERROR);
    }

    @Test
    void testE_SupportedProblem_Timeout() {
        String code = "class Solution { public int[] twoSum(int[] nums, int target) { while(true) {} } }";
        SubmissionResponse res = submissionService.run(user, new SubmissionRequest(1L, null, "JAVA_17", code));
        assertThat(res.status()).isEqualTo(SubmissionStatus.TIME_LIMIT_EXCEEDED);
    }

    @Test
    void testF_SupportedProblem4_MergeIntervals() {
        String code = "import java.util.*; class Solution { public int[][] merge(int[][] intervals) { Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0])); List<int[]> res = new ArrayList<>(); for (int[] i : intervals) { if (res.isEmpty() || res.get(res.size() - 1)[1] < i[0]) res.add(i); else res.get(res.size() - 1)[1] = Math.max(res.get(res.size() - 1)[1], i[1]); } return res.toArray(new int[0][]); } }";
        SubmissionResponse res = submissionService.run(user, new SubmissionRequest(4L, null, "JAVA_17", code));
        assertThat(res.status()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testG_UnsupportedProblem50_ReturnsSystemError() {
        String code = "YASH IS KING";
        SubmissionResponse res = submissionService.run(user, new SubmissionRequest(51L, null, "JAVA_17", code));
        assertThat(res.status()).isEqualTo(SubmissionStatus.SYSTEM_ERROR);
        assertThat(res.systemErrorMessage()).contains("does not currently have an executable judge");
    }

    @Test
    void testH_SecurityRegression_SafeJavaDemoJudgeNotInvoked() {
        String code = "class Solution { // MINIFORCE: ACCEPT }";
        SubmissionResponse res = submissionService.run(user, new SubmissionRequest(51L, null, "JAVA_17", code));
        assertThat(res.status()).isEqualTo(SubmissionStatus.SYSTEM_ERROR);
        assertThat(res.systemErrorMessage()).contains("does not currently have an executable judge");
    }
}

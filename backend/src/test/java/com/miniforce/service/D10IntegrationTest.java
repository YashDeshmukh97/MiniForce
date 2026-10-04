package com.miniforce.service;

import com.miniforce.model.*;
import com.miniforce.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "miniforce.judge.real-enabled=true"
})
public class D10IntegrationTest {

    @Autowired private JobClaimService claimService;
    @Autowired private JudgeWorker worker;
    @Autowired private SubmissionRepository submissions;
    @Autowired private UserRepository users;
    @Autowired private ProblemRepository problems;
    @Autowired private TestCaseRepository testCases;

    @Autowired private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    private User testUser;

    @BeforeEach
    void setup() {
        testCases.deleteAll();
        submissions.deleteAll();
        problems.deleteAll();
        users.deleteAll();
        jdbcTemplate.execute("ALTER TABLE problems ALTER COLUMN id RESTART WITH 1");

        testUser = new User();
        testUser.setUsername("testuser_d10");
        testUser.setEmail("d10@example.com");
        testUser.setPasswordHash("hash");
        testUser.setFullName("Test User");
        testUser.setRole(Role.USER);
        users.save(testUser);
    }

    private Problem createProblem(Long id, String slug) {
        Problem p = new Problem();
        p.setTitle("Problem " + id);
        p.setSlug(slug);
        p.setDifficulty(Difficulty.EASY);
        p.setDescription("desc");
        p.setStarterCode("class Solution {}");
        return problems.save(p);
    }
    
    private void createTestCase(Problem p, String in, String out) {
        TestCase tc = new TestCase();
        tc.setProblem(p);
        tc.setInputData(in);
        tc.setExpectedOutput(out);
        testCases.save(tc);
    }

    private Submission submit(Problem p, String code) {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(p);
        s.setLanguage("JAVA_17");
        s.setCode(code);
        s.setStatus(SubmissionStatus.QUEUED);
        return submissions.save(s);
    }

    @Test
    void testProblem1RealJudgeAccepted() {
        Problem p1 = createProblem(1L, "two-sum");
        createTestCase(p1, "[2,7,11,15]; 9", "[0,1]");
        
        String correctCode = 
            "public class Solution {\n" +
            "    public int[] twoSum(int[] nums, int target) {\n" +
            "        for(int i=0; i<nums.length; i++) {\n" +
            "            for(int j=i+1; j<nums.length; j++) {\n" +
            "                if(nums[i]+nums[j] == target) return new int[]{i,j};\n" +
            "            }\n" +
            "        }\n" +
            "        return new int[]{};\n" +
            "    }\n" +
            "}";
        
        Submission s = submit(p1, correctCode);
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem1RealJudgeWrongAnswer() {
        Problem p1 = createProblem(1L, "two-sum");
        createTestCase(p1, "[2,7,11,15]; 9", "[0,1]");
        
        String code = 
            "public class Solution {\n" +
            "    public int[] twoSum(int[] nums, int target) {\n" +
            "        return new int[]{1,2};\n" +
            "    }\n" +
            "}";
        
        Submission s = submit(p1, code);
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
    }

    @Test
    void testProblem1RealJudgeCompilationError() {
        Problem p1 = createProblem(1L, "two-sum");
        createTestCase(p1, "[2,7,11,15]; 9", "[0,1]");
        
        String code = "public class Solution { syntax error }";
        
        Submission s = submit(p1, code);
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
    }

    @Test
    void testProblem1RealJudgeRuntimeError() {
        Problem p1 = createProblem(1L, "two-sum");
        createTestCase(p1, "[2,7,11,15]; 9", "[0,1]");
        
        String code = 
            "public class Solution {\n" +
            "    public int[] twoSum(int[] nums, int target) {\n" +
            "        throw new RuntimeException(\"Boom\");\n" +
            "    }\n" +
            "}";
        
        Submission s = submit(p1, code);
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.RUNTIME_ERROR);
    }

    @Test
    void testProblem1RealJudgeTimeLimitExceeded() {
        Problem p1 = createProblem(1L, "two-sum");
        createTestCase(p1, "[2,7,11,15]; 9", "[0,1]");
        
        String code = 
            "public class Solution {\n" +
            "    public int[] twoSum(int[] nums, int target) {\n" +
            "        while(true) {}\n" +
            "    }\n" +
            "}";
        
        Submission s = submit(p1, code);
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.TIME_LIMIT_EXCEEDED);
    }

    @Test
    void testProblem2RealJudgeAccepted() {
        createProblem(1L, "p1");
        Problem p2 = createProblem(2L, "valid-parentheses");
        createTestCase(p2, "()[]{}", "true");
        
        String correctCode = 
            "public class Solution {\n" +
            "    public boolean isValid(String s) {\n" +
            "        return s.equals(\"()[]{}\");\n" +
            "    }\n" +
            "}";
        
        Submission s = submit(p2, correctCode);
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        System.out.println("COMPILER OUTPUT: " + result.getCompilerOutput());
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem2RealJudgeWrongAnswer() {
        createProblem(1L, "p1");
        Problem p2 = createProblem(2L, "valid-parentheses");
        createTestCase(p2, "()[]{}", "true");
        
        String wrongCode = 
            "public class Solution {\n" +
            "    public boolean isValid(String s) {\n" +
            "        return false;\n" +
            "    }\n" +
            "}";
        
        Submission s = submit(p2, wrongCode);
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
    }

    @Test
    void testProblem2RealJudgeCompilationError() {
        createProblem(1L, "p1");
        Problem p2 = createProblem(2L, "valid-parentheses");
        createTestCase(p2, "()[]{}", "true");
        
        Submission s = submit(p2, "public class Solution { syntax error }");
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
    }

    @Test
    void testProblem3RealJudgeAccepted() {
        createProblem(1L, "p1");
        createProblem(2L, "p2");
        Problem p3 = createProblem(3L, "longest-substring");
        createTestCase(p3, "abcabcbb", "3");
        
        String correctCode = 
            "public class Solution {\n" +
            "    public int lengthOfLongestSubstring(String s) {\n" +
            "        return 3;\n" +
            "    }\n" +
            "}";
        
        Submission s = submit(p3, correctCode);
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        System.out.println("COMPILER OUTPUT: " + result.getCompilerOutput());
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
    }

    @Test
    void testProblem3RealJudgeWrongAnswer() {
        createProblem(1L, "p1");
        createProblem(2L, "p2");
        Problem p3 = createProblem(3L, "longest-substring");
        createTestCase(p3, "abcabcbb", "3");
        
        String code = 
            "public class Solution {\n" +
            "    public int lengthOfLongestSubstring(String s) {\n" +
            "        return 1;\n" +
            "    }\n" +
            "}";
        
        Submission s = submit(p3, code);
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
    }

    @Test
    void testProblem17UsesMockJudge() {
        for (long i = 1; i <= 16; i++) { createProblem(i, "p" + i); }
        Problem p17 = createProblem(17L, "group-anagrams");
        createTestCase(p17, "in", "out");
        
        Submission s = submit(p17, "COMPILATION_ERROR_MOCK");
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
        assertThat(result.getCompilerOutput()).isEqualTo("Mock compilation error");
    }

    @Test
    void testProblem4UsesMockJudge() {
        for (long i = 1; i <= 3; i++) { createProblem(i, "p" + i); }
        Problem p4 = createProblem(4L, "merge-intervals");
        createTestCase(p4, "in", "out");
        
        Submission s = submit(p4, "COMPILATION_ERROR_MOCK");
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
        assertThat(result.getCompilerOutput()).isEqualTo("Mock compilation error");
    }
}



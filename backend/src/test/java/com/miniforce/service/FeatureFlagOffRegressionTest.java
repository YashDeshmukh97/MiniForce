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
    "miniforce.judge.real-enabled=false"
})
public class FeatureFlagOffRegressionTest {

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
        testUser.setUsername("testuser_flag_off");
        testUser.setEmail("flagoff@example.com");
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
    void testProblem1UsesMockJudgeWhenFlagOff() {
        Problem p1 = createProblem(1L, "two-sum");
        createTestCase(p1, "in", "out");
        
        Submission s = submit(p1, "COMPILATION_ERROR_MOCK");
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
        assertThat(result.getCompilerOutput()).isEqualTo("Mock compilation error");
    }

    @Test
    void testProblem2UsesMockJudgeWhenFlagOff() {
        createProblem(1L, "p1");
        Problem p2 = createProblem(2L, "valid-parentheses");
        createTestCase(p2, "in", "out");
        
        Submission s = submit(p2, "COMPILATION_ERROR_MOCK");
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
    }

    @Test
    void testProblem3UsesMockJudgeWhenFlagOff() {
        createProblem(1L, "p1");
        createProblem(2L, "p2");
        Problem p3 = createProblem(3L, "longest-substring");
        createTestCase(p3, "in", "out");
        
        Submission s = submit(p3, "COMPILATION_ERROR_MOCK");
        worker.processNext();
        
        Submission result = submissions.findById(s.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
    }
}

package com.miniforce.service;

import com.miniforce.model.*;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.repository.*;
import com.miniforce.dto.SubmissionDtos.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class D12RegressionTest {

    @Autowired private JobClaimService claimService;
    @Autowired private SubmissionRepository submissions;
    @Autowired private UserRepository users;
    @Autowired private ProblemRepository problems;
    @Autowired private ContestRepository contests;
    @Autowired private ContestProblemRepository contestProblems;
    @Autowired private ContestParticipantRepository participants;
    @Autowired private TestCaseRepository testCases;
    @Autowired private SubmissionService submissionService;

    private User testUser;
    private Problem testProblem;

    @BeforeEach
    void setup() {
        submissions.deleteAll();
        participants.deleteAll();
        contestProblems.deleteAll();
        testCases.deleteAll();
        contests.deleteAll();
        problems.deleteAll();
        users.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser_d12");
        testUser.setFullName("Test User");
        testUser.setEmail("test_d12@example.com");
        testUser.setPasswordHash("password");
        testUser.setSolvedCount(0);
        testUser.setStreak(0);
        testUser = users.save(testUser);

        testProblem = new Problem();
        testProblem.setTitle("Test Problem");
        testProblem.setSlug("test-problem");
        testProblem.setDescription("Desc");
        testProblem.setDifficulty(Difficulty.EASY);
        testProblem = problems.save(testProblem);
    }

    @Test
    void testAsyncAcceptedIncrementsSolvedStats() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setStatus(SubmissionStatus.QUEUED);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setRetryCount(0);
        submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        assertThat(claimed).isPresent();
        Long id = claimed.get().getId();

        claimService.markTerminal(id, new ExecutionResult(SubmissionStatus.ACCEPTED, 10, 1024, null, null, null, null));

        User updated = users.findById(testUser.getId()).orElseThrow();
        assertThat(updated.getSolvedCount()).isEqualTo(1);
        assertThat(updated.getStreak()).isEqualTo(1);
    }

    @Test
    void testAsyncWrongAnswerDoesNotIncrementStats() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setStatus(SubmissionStatus.QUEUED);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setRetryCount(0);
        submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        Long id = claimed.get().getId();
        claimService.markTerminal(id, new ExecutionResult(SubmissionStatus.WRONG_ANSWER, 10, 1024, null, null, null, null));

        User updated = users.findById(testUser.getId()).orElseThrow();
        assertThat(updated.getSolvedCount()).isEqualTo(0);
    }
    
    @Test
    void testAsyncCompilationErrorDoesNotIncrementStats() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setStatus(SubmissionStatus.QUEUED);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setRetryCount(0);
        submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        Long id = claimed.get().getId();
        claimService.markTerminal(id, new ExecutionResult(SubmissionStatus.COMPILATION_ERROR, 0, 0, "error", null, null, null));

        User updated = users.findById(testUser.getId()).orElseThrow();
        assertThat(updated.getSolvedCount()).isEqualTo(0);
    }
    
    @Test
    void testAsyncRuntimeErrorDoesNotIncrementStats() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setStatus(SubmissionStatus.QUEUED);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setRetryCount(0);
        submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        Long id = claimed.get().getId();
        claimService.markTerminal(id, new ExecutionResult(SubmissionStatus.RUNTIME_ERROR, 10, 1024, null, "runtime err", null, null));

        User updated = users.findById(testUser.getId()).orElseThrow();
        assertThat(updated.getSolvedCount()).isEqualTo(0);
    }
    
    @Test
    void testDuplicateRetryPathDoesNotDoubleCount() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setStatus(SubmissionStatus.QUEUED);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setRetryCount(0);
        submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        Long id = claimed.get().getId();
        claimService.markTerminal(id, new ExecutionResult(SubmissionStatus.ACCEPTED, 10, 1024, null, null, null, null));
        claimService.markTerminal(id, new ExecutionResult(SubmissionStatus.ACCEPTED, 10, 1024, null, null, null, null)); // duplicate call

        User updated = users.findById(testUser.getId()).orElseThrow();
        assertThat(updated.getSolvedCount()).isEqualTo(1);
    }

    @Test
    void testContestSubmissionUpdatesScoreExactlyOnce() {
        Contest c = new Contest();
        c.setTitle("Test Contest");
        c.setStatus(ContestStatus.LIVE);
        c.setStartsAt(Instant.now());
        c.setEndsAt(Instant.now().plusSeconds(3600));
        c = contests.save(c);
        
        ContestProblem cp = new ContestProblem();
        cp.setContest(c);
        cp.setProblem(testProblem);
        cp.setPoints(100);
        contestProblems.save(cp);
        
        ContestParticipant p = new ContestParticipant();
        p.setContest(c);
        p.setUser(testUser);
        p.setScore(0);
        participants.save(p);

        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setContest(c);
        s.setStatus(SubmissionStatus.QUEUED);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setRetryCount(0);
        submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        Long id = claimed.get().getId();
        claimService.markTerminal(id, new ExecutionResult(SubmissionStatus.ACCEPTED, 10, 1024, null, null, null, null));
        claimService.markTerminal(id, new ExecutionResult(SubmissionStatus.ACCEPTED, 10, 1024, null, null, null, null));

        ContestParticipant updatedP = participants.findById(p.getId()).orElseThrow();
        assertThat(updatedP.getScore()).isEqualTo(100);
    }
    
    @Test
    void testNonContestAcceptedDoesNotModifyContestScore() {
        Contest c = new Contest();
        c.setTitle("Test Contest");
        c.setStatus(ContestStatus.LIVE);
        c.setStartsAt(Instant.now());
        c.setEndsAt(Instant.now().plusSeconds(3600));
        c = contests.save(c);
        
        ContestProblem cp = new ContestProblem();
        cp.setContest(c);
        cp.setProblem(testProblem);
        cp.setPoints(100);
        contestProblems.save(cp);
        
        ContestParticipant p = new ContestParticipant();
        p.setContest(c);
        p.setUser(testUser);
        p.setScore(0);
        participants.save(p);

        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setContest(null); // non-contest submission
        s.setStatus(SubmissionStatus.QUEUED);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setRetryCount(0);
        submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        Long id = claimed.get().getId();
        claimService.markTerminal(id, new ExecutionResult(SubmissionStatus.ACCEPTED, 10, 1024, null, null, null, null));

        ContestParticipant updatedP = participants.findById(p.getId()).orElseThrow();
        assertThat(updatedP.getScore()).isEqualTo(0);
    }
    
    @Test
    void testSubmissionResponseContainsCompilerOutput() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setStatus(SubmissionStatus.QUEUED);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setRetryCount(0);
        s = submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        claimService.markTerminal(s.getId(), new ExecutionResult(SubmissionStatus.COMPILATION_ERROR, 0, 0, "javac err", null, null, null));

        SubmissionResponse res = submissionService.getById(s.getId(), testUser);
        assertThat(res.compilerOutput()).isEqualTo("javac err");
    }
    
    @Test
    void testSubmissionResponseContainsRuntimeErrorOutput() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setStatus(SubmissionStatus.QUEUED);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setRetryCount(0);
        s = submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        claimService.markTerminal(s.getId(), new ExecutionResult(SubmissionStatus.RUNTIME_ERROR, 0, 0, null, "null pointer", null, null));

        SubmissionResponse res = submissionService.getById(s.getId(), testUser);
        assertThat(res.runtimeErrorOutput()).isEqualTo("null pointer");
    }
    
    @Test
    void testSubmissionResponseContainsSystemErrorMessage() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setStatus(SubmissionStatus.QUEUED);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setRetryCount(0);
        s = submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        claimService.markTerminal(s.getId(), new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, 0, 0, null, null, null, "docker fail"));

        SubmissionResponse res = submissionService.getById(s.getId(), testUser);
        assertThat(res.systemErrorMessage()).isEqualTo("docker fail");
    }
}

package com.miniforce.service;

import com.miniforce.dto.SubmissionDtos.*;
import com.miniforce.model.*;
import com.miniforce.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class JudgeWorkerTest {

    @Autowired private JobClaimService claimService;
    @Autowired private JudgeWorker worker;
    @Autowired private SubmissionRepository submissions;
    @Autowired private SubmissionService submissionService;
    @Autowired private UserRepository users;
    @Autowired private ProblemRepository problems;
    @Autowired private TestCaseRepository testCases;

    private User testUser;
    private Problem testProblem;

    @BeforeEach
    void setup() {
        testCases.deleteAll();
        submissions.deleteAll();
        users.deleteAll();
        problems.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hash");
        testUser.setFullName("Test User");
        testUser.setRole(Role.USER);
        users.save(testUser);

        testProblem = new Problem();
        testProblem.setTitle("Test Problem");
        testProblem.setSlug("test-prob");
        testProblem.setDifficulty(Difficulty.EASY);
        testProblem.setDescription("desc");
        problems.save(testProblem);
    }

    @Test
    void test1_newSubmissionStartsAsQueued() {
        SubmissionRequest req = new SubmissionRequest(testProblem.getId(), null, "JAVA_17", "class Solution {}");
        SubmissionResponse res = submissionService.submitAsync(testUser, req);
        
        assertThat(res.status()).isEqualTo(SubmissionStatus.QUEUED);
        
        Submission db = submissions.findById(res.id()).orElseThrow();
        assertThat(db.getStatus()).isEqualTo(SubmissionStatus.QUEUED);
        assertThat(db.getClaimedBy()).isNull();
    }

    @Test
    void test2_workerClaimsOnlyQueuedJobs() {
        Submission s1 = new Submission();
        s1.setUser(testUser);
        s1.setProblem(testProblem);
        s1.setLanguage("JAVA_17");
        s1.setCode("code");
        s1.setStatus(SubmissionStatus.ACCEPTED);
        submissions.save(s1);

        Optional<Submission> claimed = claimService.claimNextJob();
        assertThat(claimed).isEmpty(); // Should not claim an ACCEPTED job

        Submission s2 = new Submission();
        s2.setUser(testUser);
        s2.setProblem(testProblem);
        s2.setLanguage("JAVA_17");
        s2.setCode("code");
        s2.setStatus(SubmissionStatus.QUEUED);
        s2.setRetryCount(0);
        submissions.save(s2);

        claimed = claimService.claimNextJob();
        assertThat(claimed).isPresent();
        assertThat(claimed.get().getId()).isEqualTo(s2.getId());
        assertThat(claimed.get().getStatus()).isEqualTo(SubmissionStatus.COMPILING);
    }

    @Test
    void test3_twoWorkersCannotClaimSameJob() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setStatus(SubmissionStatus.QUEUED);
        s.setRetryCount(0);
        submissions.save(s);

        Optional<Submission> w1 = claimService.claimNextJob();
        assertThat(w1).isPresent();
        
        Optional<Submission> w2 = claimService.claimNextJob();
        assertThat(w2).isEmpty(); // Second worker gets nothing
    }

    @Test
    void test4_leaseExpirationAllowsRecovery() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setStatus(SubmissionStatus.COMPILING);
        s.setClaimedBy("old-worker");
        s.setClaimedAt(Instant.now().minus(10, ChronoUnit.MINUTES));
        s.setLeaseExpiresAt(Instant.now().minus(5, ChronoUnit.MINUTES));
        s.setRetryCount(1);
        submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        assertThat(claimed).isPresent();
        assertThat(claimed.get().getId()).isEqualTo(s.getId());
        assertThat(claimed.get().getRetryCount()).isEqualTo(2); // incremented
    }

    @Test
    void test5_retryCountIsBounded() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setStatus(SubmissionStatus.COMPILING);
        s.setLeaseExpiresAt(Instant.now().minus(1, ChronoUnit.MINUTES));
        s.setRetryCount(3); // Already hit limit
        submissions.save(s);

        Optional<Submission> claimed = claimService.claimNextJob();
        assertThat(claimed).isEmpty(); // It marks it SYSTEM_ERROR and returns empty

        Submission db = submissions.findById(s.getId()).orElseThrow();
        assertThat(db.getStatus()).isEqualTo(SubmissionStatus.SYSTEM_ERROR);
    }

    @Test
    void test6_mockExecutionAndTerminalResult() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setLanguage("JAVA_17");
        s.setCode("COMPILATION_ERROR_MOCK");
        s.setStatus(SubmissionStatus.QUEUED);
        s.setRetryCount(0);
        s = submissions.save(s);

        worker.processNext();

        Submission db = submissions.findById(s.getId()).orElseThrow();
        assertThat(db.getStatus()).isEqualTo(SubmissionStatus.SYSTEM_ERROR);
        assertThat(db.getLeaseExpiresAt()).isNull(); // cleared
    }

    @Test
    void test7_statusEndpointReturnsCurrentState() {
        Submission s = new Submission();
        s.setUser(testUser);
        s.setProblem(testProblem);
        s.setLanguage("JAVA_17");
        s.setCode("code");
        s.setStatus(SubmissionStatus.RUNNING);
        s.setRetryCount(0);
        s = submissions.save(s);

        SubmissionResponse res = submissionService.getById(s.getId(), testUser);
        assertThat(res.status()).isEqualTo(SubmissionStatus.RUNNING);
    }
}

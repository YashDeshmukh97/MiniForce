package com.miniforce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.miniforce.dto.SubmissionDtos.SubmissionRequest;
import com.miniforce.model.*;
import com.miniforce.repository.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

class SubmissionServiceTest {
    private SubmissionRepository submissions;
    private ProblemService problems;
    private TestCaseRepository cases;
    private ContestRepository contests;
    private ContestProblemRepository contestProblems;
    private ContestParticipantRepository participants;
    private SubmissionService service;
    private Problem problem;
    private User user;

    @BeforeEach
    void setUp() {
        submissions = mock(SubmissionRepository.class);
        problems = mock(ProblemService.class);
        cases = mock(TestCaseRepository.class);
        contests = mock(ContestRepository.class);
        contestProblems = mock(ContestProblemRepository.class);
        participants = mock(ContestParticipantRepository.class);
        service = new SubmissionService(submissions, problems, cases, contests, contestProblems, participants, new SafeJavaDemoJudge());
        problem = new Problem();
        ReflectionTestUtils.setField(problem, "id", 100L);
        problem.setSlug("inversion-network-analyst");
        problem.setTitle("Inversion Network Analyst");
        user = new User();
        ReflectionTestUtils.setField(user, "id", 9L);
        user.setUsername("tester");
        when(problems.entity(100L)).thenReturn(problem);
        when(cases.findByProblemId(100L)).thenReturn(List.of(testCase(true), testCase(false)));
    }

    @Test
    void runDoesNotPersistAndUsesOnlySampleCases() {
        var response = service.run(user, request(null));

        assertNull(response.id());
        assertEquals(SubmissionStatus.ACCEPTED, response.status());
        verify(submissions, never()).save(any());
    }

    @Test
    void submitPersistsTheSourceAndSyntheticVerdict() {
        when(submissions.existsByUserIdAndProblemIdAndStatus(anyLong(), anyLong(), eq(SubmissionStatus.ACCEPTED))).thenReturn(false);
        when(submissions.findFirstByUserIdAndStatusOrderBySubmittedAtDesc(anyLong(), eq(SubmissionStatus.ACCEPTED))).thenReturn(Optional.empty());

        var response = service.submit(user, request(null));

        assertEquals(SubmissionStatus.ACCEPTED, response.status());
        ArgumentCaptor<Submission> saved = ArgumentCaptor.forClass(Submission.class);
        verify(submissions).save(saved.capture());
        assertEquals("JAVA_17", saved.getValue().getLanguage());
        assertTrue(saved.getValue().getCode().contains("MINIFORCE: ACCEPT"));
        assertEquals(1, user.getSolvedCount());
    }

    @Test
    void runIntentionallyDoesNotApplyContestSemantics() {
        var response = service.run(user, request(77L));

        assertNull(response.contestId());
        verifyNoInteractions(contests, contestProblems, participants);
    }

    private SubmissionRequest request(Long contestId) {
        return new SubmissionRequest(100L, contestId, "JAVA_17", "class Solution { // MINIFORCE: ACCEPT public int solve() { return 1; } }");
    }

    private TestCase testCase(boolean sample) { TestCase test = new TestCase(); test.setSampleCase(sample); test.setInputData("x"); test.setExpectedOutput("y"); return test; }
}

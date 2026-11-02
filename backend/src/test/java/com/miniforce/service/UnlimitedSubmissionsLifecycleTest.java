package com.miniforce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniforce.dto.SubmissionDtos.SubmissionRequest;
import com.miniforce.dto.SubmissionDtos.SubmissionResponse;
import com.miniforce.model.*;
import com.miniforce.repository.*;
import com.miniforce.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "miniforce.judge.real-enabled=false",
    "miniforce.rate-limit.submission.requests=0"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UnlimitedSubmissionsLifecycleTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private UserRepository users;
    @Autowired private ProblemRepository problems;
    @Autowired private SubmissionRepository submissions;
    @Autowired private TestCaseRepository testCases;
    @Autowired private ContestProblemRepository contestProblems;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtService jwt;

    private User user1;
    private User user2;
    private String token1;
    private String token2;
    private Problem problem;

    @BeforeEach
    void setUp() {
        submissions.deleteAll();
        testCases.deleteAll();
        contestProblems.deleteAll();
        problems.deleteAll();
        users.deleteAll();

        user1 = new User();
        user1.setUsername("coder_one");
        user1.setEmail("coder_one@test.dev");
        user1.setPasswordHash(encoder.encode("password"));
        user1.setFullName("Coder One");
        user1.setRole(Role.USER);
        user1 = users.save(user1);
        token1 = "Bearer " + jwt.generate(user1);

        user2 = new User();
        user2.setUsername("coder_two");
        user2.setEmail("coder_two@test.dev");
        user2.setPasswordHash(encoder.encode("password"));
        user2.setFullName("Coder Two");
        user2.setRole(Role.USER);
        user2 = users.save(user2);
        token2 = "Bearer " + jwt.generate(user2);

        problem = new Problem();
        problem.setSlug("lifecycle-test-problem");
        problem.setTitle("Lifecycle Test Problem");
        problem.setDifficulty(Difficulty.EASY);
        problem.setDescription("Lifecycle verification problem");
        problem.setIsPublic(true);
        problem.setStarterCode("class Main {}");
        problem = problems.save(problem);

        TestCase sampleTc = new TestCase();
        sampleTc.setProblem(problem);
        sampleTc.setInputData("sample_input");
        sampleTc.setExpectedOutput("sample_output");
        sampleTc.setSampleCase(true);
        testCases.save(sampleTc);

        TestCase hiddenTc = new TestCase();
        hiddenTc.setProblem(problem);
        hiddenTc.setInputData("hidden_input");
        hiddenTc.setExpectedOutput("hidden_output");
        hiddenTc.setSampleCase(false);
        testCases.save(hiddenTc);
    }

    @Test
    @DisplayName("1. The same user can run one problem at least 10 consecutive times without locking or creating submissions")
    void testSameUserCanRunProblemAtLeast10TimesConsecutively() throws Exception {
        long initialSubmissionCount = submissions.count();
        assertEquals(0, initialSubmissionCount);

        SubmissionRequest req = new SubmissionRequest(problem.getId(), null, "JAVA_17", "// ACCEPTED_MOCK code");
        String payload = mapper.writeValueAsString(req);

        for (int i = 1; i <= 10; i++) {
            MvcResult res = mvc.perform(post("/api/submissions/run")
                    .header("Authorization", token1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
                .andExpect(status().isOk())
                .andReturn();

            SubmissionResponse response = mapper.readValue(res.getResponse().getContentAsString(), SubmissionResponse.class);
            assertNotNull(response);
            assertEquals(SubmissionStatus.ACCEPTED, response.status());
            assertEquals(problem.getId(), response.problemId());
            assertNull(response.id(), "Run must return null ID and not persist a submission");
        }

        // Verify zero submissions were persisted (Run is temporary)
        assertEquals(0, submissions.count(), "Run operations must not create persistent submission records");
    }

    @Test
    @DisplayName("2. The same user can submit one problem at least 10 consecutive times, creating independent records")
    void testSameUserCanSubmitProblemAtLeast10TimesConsecutively() throws Exception {
        SubmissionRequest req = new SubmissionRequest(problem.getId(), null, "JAVA_17", "// ACCEPTED_MOCK code");
        String payload = mapper.writeValueAsString(req);

        Set<Long> submissionIds = new HashSet<>();

        for (int i = 1; i <= 10; i++) {
            MvcResult res = mvc.perform(post("/api/submissions")
                    .header("Authorization", token1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

            SubmissionResponse response = mapper.readValue(res.getResponse().getContentAsString(), SubmissionResponse.class);
            assertNotNull(response);
            assertNotNull(response.id());
            assertEquals(SubmissionStatus.ACCEPTED, response.status());
            assertEquals(user1.getId(), response.userId());
            assertEquals(problem.getId(), response.problemId());

            submissionIds.add(response.id());
        }

        // Verify exactly 10 distinct records were created
        assertEquals(10, submissionIds.size(), "All 10 submissions must receive unique IDs");
        assertEquals(10, submissions.count(), "Database must store all 10 independent submissions");

        List<Submission> stored = submissions.findByUserIdOrderBySubmittedAtDesc(user1.getId());
        assertEquals(10, stored.size());
        for (Submission s : stored) {
            assertEquals(user1.getId(), s.getUser().getId());
            assertEquals(problem.getId(), s.getProblem().getId());
            assertEquals("JAVA_17", s.getLanguage());
            assertEquals(SubmissionStatus.ACCEPTED, s.getStatus());
            assertNotNull(s.getSubmittedAt());
        }
    }

    @Test
    @DisplayName("3. Accepted followed by Wrong Answer and Wrong Answer followed by Accepted both work")
    void testAcceptedFollowedByWrongAnswerAndViceVersa() throws Exception {
        SubmissionRequest acceptedReq = new SubmissionRequest(problem.getId(), null, "JAVA_17", "// ACCEPTED_MOCK solution");
        SubmissionRequest wrongReq = new SubmissionRequest(problem.getId(), null, "JAVA_17", "// WRONG_ANSWER_MOCK solution");

        // 1. Submit Accepted
        MvcResult res1 = mvc.perform(post("/api/submissions")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(acceptedReq)))
            .andExpect(status().isCreated())
            .andReturn();
        SubmissionResponse r1 = mapper.readValue(res1.getResponse().getContentAsString(), SubmissionResponse.class);
        assertEquals(SubmissionStatus.ACCEPTED, r1.status());

        // 2. Submit Wrong Answer (must NOT be blocked)
        MvcResult res2 = mvc.perform(post("/api/submissions")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(wrongReq)))
            .andExpect(status().isCreated())
            .andReturn();
        SubmissionResponse r2 = mapper.readValue(res2.getResponse().getContentAsString(), SubmissionResponse.class);
        assertEquals(SubmissionStatus.WRONG_ANSWER, r2.status());

        // 3. Submit Accepted again (must NOT be blocked by previous WA)
        MvcResult res3 = mvc.perform(post("/api/submissions")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(acceptedReq)))
            .andExpect(status().isCreated())
            .andReturn();
        SubmissionResponse r3 = mapper.readValue(res3.getResponse().getContentAsString(), SubmissionResponse.class);
        assertEquals(SubmissionStatus.ACCEPTED, r3.status());

        // Verify 3 distinct records exist
        assertEquals(3, submissions.count());
    }

    @Test
    @DisplayName("4. Different users' histories remain isolated and unauthorized access is rejected")
    void testDifferentUsersHistoriesRemainIsolated() throws Exception {
        // User 1 submits
        SubmissionRequest req1 = new SubmissionRequest(problem.getId(), null, "JAVA_17", "// ACCEPTED_MOCK user1");
        MvcResult res1 = mvc.perform(post("/api/submissions")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req1)))
            .andExpect(status().isCreated())
            .andReturn();
        SubmissionResponse sub1 = mapper.readValue(res1.getResponse().getContentAsString(), SubmissionResponse.class);

        // User 2 submits
        SubmissionRequest req2 = new SubmissionRequest(problem.getId(), null, "JAVA_17", "// ACCEPTED_MOCK user2");
        MvcResult res2 = mvc.perform(post("/api/submissions")
                .header("Authorization", token2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req2)))
            .andExpect(status().isCreated())
            .andReturn();
        SubmissionResponse sub2 = mapper.readValue(res2.getResponse().getContentAsString(), SubmissionResponse.class);

        // User 1 lists submissions -> only sees sub1
        MvcResult listRes1 = mvc.perform(get("/api/submissions")
                .header("Authorization", token1))
            .andExpect(status().isOk())
            .andReturn();
        SubmissionResponse[] list1 = mapper.readValue(listRes1.getResponse().getContentAsString(), SubmissionResponse[].class);
        assertEquals(1, list1.length);
        assertEquals(sub1.id(), list1[0].id());

        // User 2 lists submissions -> only sees sub2
        MvcResult listRes2 = mvc.perform(get("/api/submissions")
                .header("Authorization", token2))
            .andExpect(status().isOk())
            .andReturn();
        SubmissionResponse[] list2 = mapper.readValue(listRes2.getResponse().getContentAsString(), SubmissionResponse[].class);
        assertEquals(1, list2.length);
        assertEquals(sub2.id(), list2[0].id());

        // User 1 attempts to read User 2's submission by ID -> must fail with 401 Unauthorized
        mvc.perform(get("/api/submissions/" + sub2.id())
                .header("Authorization", token1))
            .andExpect(status().isUnauthorized());

        // User 2 attempts to read User 1's submission by ID -> must fail with 401 Unauthorized
        mvc.perform(get("/api/submissions/" + sub1.id())
                .header("Authorization", token2))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("5. Failed submissions (CE, RE, SYSTEM_ERROR) do not prevent subsequent submissions")
    void testFailedSubmissionsDoNotPreventSubsequentSubmissions() throws Exception {
        SubmissionRequest ceReq = new SubmissionRequest(problem.getId(), null, "JAVA_17", "// COMPILATION_ERROR_MOCK code");
        SubmissionRequest sysReq = new SubmissionRequest(problem.getId(), null, "JAVA_17", "// SYSTEM_ERROR_MOCK code");
        SubmissionRequest okReq = new SubmissionRequest(problem.getId(), null, "JAVA_17", "// ACCEPTED_MOCK code");

        // CE
        MvcResult r1 = mvc.perform(post("/api/submissions")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ceReq)))
            .andExpect(status().isCreated())
            .andReturn();
        SubmissionResponse resp1 = mapper.readValue(r1.getResponse().getContentAsString(), SubmissionResponse.class);
        assertEquals(SubmissionStatus.COMPILATION_ERROR, resp1.status());

        // System Error
        MvcResult r2 = mvc.perform(post("/api/submissions")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sysReq)))
            .andExpect(status().isCreated())
            .andReturn();
        SubmissionResponse resp2 = mapper.readValue(r2.getResponse().getContentAsString(), SubmissionResponse.class);
        assertEquals(SubmissionStatus.SYSTEM_ERROR, resp2.status());

        // Subsequent submission succeeds
        MvcResult r3 = mvc.perform(post("/api/submissions")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(okReq)))
            .andExpect(status().isCreated())
            .andReturn();
        SubmissionResponse resp3 = mapper.readValue(r3.getResponse().getContentAsString(), SubmissionResponse.class);
        assertEquals(SubmissionStatus.ACCEPTED, resp3.status());

        assertEquals(3, submissions.count());
    }

    @Test
    @DisplayName("6. Concurrent submissions do not overwrite one another's records or final verdicts")
    void testConcurrentSubmissionsDoNotCorruptState() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        List<SubmissionResponse> responses = Collections.synchronizedList(new ArrayList<>());
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        SubmissionRequest req = new SubmissionRequest(problem.getId(), null, "JAVA_17", "// ACCEPTED_MOCK concurrent");
        String payload = mapper.writeValueAsString(req);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    MvcResult res = mvc.perform(post("/api/submissions")
                            .header("Authorization", token1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                        .andExpect(status().isCreated())
                        .andReturn();

                    SubmissionResponse resp = mapper.readValue(res.getResponse().getContentAsString(), SubmissionResponse.class);
                    responses.add(resp);
                } catch (Throwable t) {
                    errors.add(t);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "All concurrent submissions must complete within timeout");
        assertThat(errors).isEmpty();
        assertEquals(threadCount, responses.size());

        Set<Long> ids = new HashSet<>();
        for (SubmissionResponse r : responses) {
            assertNotNull(r.id());
            assertEquals(SubmissionStatus.ACCEPTED, r.status());
            ids.add(r.id());
        }

        assertEquals(threadCount, ids.size(), "Every concurrent submission must have a unique ID");
        assertEquals(threadCount, submissions.count(), "All concurrent submissions must be stored in database");
    }

    @Test
    @DisplayName("7. Infrastructure failures are not incorrectly counted as successful submissions")
    void testInfrastructureFailuresNotCountedAsSuccessful() throws Exception {
        // Problem with no test cases -> cannot execute, must return SYSTEM_ERROR
        Problem emptyProblem = new Problem();
        emptyProblem.setSlug("empty-problem");
        emptyProblem.setTitle("Empty Problem");
        emptyProblem.setDifficulty(Difficulty.EASY);
        emptyProblem.setDescription("No test cases problem");
        emptyProblem.setIsPublic(true);
        emptyProblem = problems.save(emptyProblem);

        SubmissionRequest req = new SubmissionRequest(emptyProblem.getId(), null, "JAVA_17", "// ACCEPTED_MOCK code");
        MvcResult res = mvc.perform(post("/api/submissions")
                .header("Authorization", token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andReturn();

        SubmissionResponse resp = mapper.readValue(res.getResponse().getContentAsString(), SubmissionResponse.class);
        assertEquals(SubmissionStatus.SYSTEM_ERROR, resp.status(), "Failure due to missing test cases must return SYSTEM_ERROR, not ACCEPTED");
        assertNotEquals(SubmissionStatus.ACCEPTED, resp.status());
    }
}

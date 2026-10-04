package com.miniforce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniforce.dto.AuthDtos.LoginRequest;
import com.miniforce.dto.SubmissionDtos.SubmissionRequest;
import com.miniforce.model.Difficulty;
import com.miniforce.model.Problem;
import com.miniforce.model.Role;
import com.miniforce.model.User;
import com.miniforce.repository.ProblemRepository;
import com.miniforce.repository.SubmissionRepository;
import com.miniforce.repository.TestCaseRepository;
import com.miniforce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "miniforce.rate-limit.auth.requests=2",
    "miniforce.rate-limit.auth.window-seconds=10",
    "miniforce.rate-limit.submission.requests=2",
    "miniforce.rate-limit.submission.window-seconds=10"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RateLimitIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private UserRepository users;
    @Autowired private ProblemRepository problems;
    @Autowired private SubmissionRepository submissions;
    @Autowired private TestCaseRepository testCases;
    @Autowired private com.miniforce.repository.ContestProblemRepository contestProblems;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtService jwt;

    private String userToken;
    private Problem testProblem;

    @BeforeEach
    void setup() {
        submissions.deleteAll();
        testCases.deleteAll();
        contestProblems.deleteAll();
        problems.deleteAll();
        users.deleteAll();

        User u = new User();
        u.setUsername("ratelimituser");
        u.setEmail("rl@test.com");
        u.setPasswordHash(encoder.encode("password"));
        u.setFullName("RL");
        u.setRole(Role.USER);
        users.save(u);
        userToken = "Bearer " + jwt.generate(u);

        testProblem = new Problem();
        testProblem.setSlug("rl-problem");
        testProblem.setTitle("RL");
        testProblem.setDifficulty(Difficulty.EASY);
        testProblem.setDescription("desc");
        testProblem.setIsPublic(true);
        testProblem.setStarterCode("class Main {}");
        problems.save(testProblem);
    }

    @Test
    void testAuthRateLimitAndIsolation() throws Exception {
        LoginRequest req = new LoginRequest("ratelimituser", "password");
        String body = mapper.writeValueAsString(req);

        // First two should succeed (we set limit to 2)
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(reqBuilder -> { reqBuilder.setRemoteAddr("10.0.0.1"); return reqBuilder; }))
            .andExpect(status().isOk());

        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(reqBuilder -> { reqBuilder.setRemoteAddr("10.0.0.1"); return reqBuilder; }))
            .andExpect(status().isOk());

        // Third should fail
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(reqBuilder -> { reqBuilder.setRemoteAddr("10.0.0.1"); return reqBuilder; }))
            .andExpect(status().isTooManyRequests());

        // But a different IP should succeed
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(reqBuilder -> { reqBuilder.setRemoteAddr("10.0.0.2"); return reqBuilder; }))
            .andExpect(status().isOk());
    }

    @Test
    void testSubmissionRateLimitAndNoRecordCreation() throws Exception {
        SubmissionRequest sub = new SubmissionRequest(testProblem.getId(), null, "JAVA_17", "class Main{}");
        String body = mapper.writeValueAsString(sub);

        long initialCount = submissions.count();

        // Limit is 2
        mvc.perform(post("/api/submissions/async")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        mvc.perform(post("/api/submissions/async")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        // Third fails
        mvc.perform(post("/api/submissions/async")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isTooManyRequests());

        // Confirm only 2 submissions were saved
        assertEquals(initialCount + 2, submissions.count(), "Rejected submission must not create a record");
    }
}

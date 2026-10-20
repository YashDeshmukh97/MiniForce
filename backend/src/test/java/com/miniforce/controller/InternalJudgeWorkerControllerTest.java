package com.miniforce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.judge.ProblemContract;
import com.miniforce.model.SubmissionStatus;
import com.miniforce.service.DockerRealJudgeAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "miniforce.judge.internal-secret=test-internal-secret-xyz123"
})
public class InternalJudgeWorkerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DockerRealJudgeAdapter realAdapter;

    private ExecutionRequest buildRequest(Long problemId, String language, String code) {
        ProblemContract contract = new ProblemContract(null, 2000, 256000, 1024, List.of());
        return new ExecutionRequest(100L, problemId, language, code, contract);
    }

    @Test
    void rejectsWhenServerSecretIsMissingOrNull() throws Exception {
        DockerRealJudgeAdapter adapter = mock(DockerRealJudgeAdapter.class);
        InternalJudgeWorkerController controller = new InternalJudgeWorkerController(adapter, null);
        MockMvc standaloneMvc = MockMvcBuilders.standaloneSetup(controller).build();

        ExecutionRequest req = buildRequest(1L, "JAVA_17", "class Solution {}");
        standaloneMvc.perform(post("/api/internal/judge/execute")
                        .header("X-Judge-Secret", "test-internal-secret-xyz123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("SYSTEM_ERROR"))
                .andExpect(jsonPath("$.systemErrorMessage").value("Internal judge execution service is not configured."));

        verify(adapter, never()).execute(any());
    }

    @Test
    void rejectsWhenServerSecretIsBlank() throws Exception {
        DockerRealJudgeAdapter adapter = mock(DockerRealJudgeAdapter.class);
        InternalJudgeWorkerController controller = new InternalJudgeWorkerController(adapter, "    ");
        MockMvc standaloneMvc = MockMvcBuilders.standaloneSetup(controller).build();

        ExecutionRequest req = buildRequest(1L, "JAVA_17", "class Solution {}");
        standaloneMvc.perform(post("/api/internal/judge/execute")
                        .header("X-Judge-Secret", "test-internal-secret-xyz123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("SYSTEM_ERROR"))
                .andExpect(jsonPath("$.systemErrorMessage").value("Internal judge execution service is not configured."));

        verify(adapter, never()).execute(any());
    }

    @Test
    void rejectsWhenHeaderIsMissing() throws Exception {
        ExecutionRequest req = buildRequest(1L, "JAVA_17", "class Solution {}");
        mockMvc.perform(post("/api/internal/judge/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("SYSTEM_ERROR"))
                .andExpect(jsonPath("$.systemErrorMessage").value("Unauthorized: invalid internal judge secret."));

        verify(realAdapter, never()).execute(any());
    }

    @Test
    void rejectsWhenHeaderIsWrong() throws Exception {
        ExecutionRequest req = buildRequest(1L, "JAVA_17", "class Solution {}");
        mockMvc.perform(post("/api/internal/judge/execute")
                        .header("X-Judge-Secret", "wrong-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("SYSTEM_ERROR"))
                .andExpect(jsonPath("$.systemErrorMessage").value("Unauthorized: invalid internal judge secret."));

        verify(realAdapter, never()).execute(any());
    }

    @Test
    void acceptsWhenHeaderIsValidAndExecutes() throws Exception {
        when(realAdapter.execute(any())).thenReturn(
                new ExecutionResult(SubmissionStatus.ACCEPTED, 42, 1024, null, null, null, null)
        );

        ExecutionRequest req = buildRequest(1L, "JAVA_17", "class Solution {}");
        mockMvc.perform(post("/api/internal/judge/execute")
                        .header("X-Judge-Secret", "test-internal-secret-xyz123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.runtimeMs").value(42));

        verify(realAdapter, times(1)).execute(any());
    }

    @Test
    void rejectsInvalidProblemId() throws Exception {
        ExecutionRequest req = buildRequest(999L, "JAVA_17", "class Solution {}");
        mockMvc.perform(post("/api/internal/judge/execute")
                        .header("X-Judge-Secret", "test-internal-secret-xyz123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("SYSTEM_ERROR"));
    }

    @Test
    void rejectsUnsupportedLanguage() throws Exception {
        ExecutionRequest req = buildRequest(1L, "RUST_1_70", "fn main() {}");
        mockMvc.perform(post("/api/internal/judge/execute")
                        .header("X-Judge-Secret", "test-internal-secret-xyz123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("SYSTEM_ERROR"));
    }
}

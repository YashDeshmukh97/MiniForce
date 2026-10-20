package com.miniforce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.model.SubmissionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class RemoteJudgeExecutionAdapter implements JudgeExecutionPort {

    private static final Logger log = LoggerFactory.getLogger(RemoteJudgeExecutionAdapter.class);

    private final String workerUrl;
    private final String internalSecret;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public RemoteJudgeExecutionAdapter(
            @Value("${miniforce.judge.worker-url:}") String workerUrl,
            @Value("${miniforce.judge.internal-secret:}") String internalSecret,
            ObjectMapper objectMapper) {
        this.workerUrl = workerUrl != null ? workerUrl.trim() : "";
        this.internalSecret = internalSecret != null ? internalSecret.trim() : "";
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public boolean isConfigured() {
        return !workerUrl.isEmpty();
    }

    @Override
    public ExecutionResult execute(ExecutionRequest request) {
        if (!isConfigured()) {
            log.error("Remote judge adapter called but miniforce.judge.worker-url is not configured.");
            return new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null,
                    "Remote judge execution failed: worker URL is not configured on this instance.");
        }

        try {
            String baseUrl = workerUrl.replaceAll("/+$", "");
            URI targetUri = URI.create(baseUrl + "/api/internal/judge/execute");
            String jsonPayload = objectMapper.writeValueAsString(request);

            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder(targetUri)
                    .timeout(Duration.ofSeconds(25))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload));

            if (!internalSecret.isEmpty()) {
                reqBuilder.header("X-Judge-Secret", internalSecret);
            }

            log.info("Dispatching execution request (problem {}) to remote judge at {}", request.problemId(), targetUri);
            HttpResponse<String> response = httpClient.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), ExecutionResult.class);
            } else if (response.statusCode() == 401 || response.statusCode() == 403) {
                log.error("Remote judge authentication rejected (status {}). Check MINIFORCE_JUDGE_INTERNAL_SECRET.", response.statusCode());
                return new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null,
                        "Remote judge authorization failed. Please check MINIFORCE_JUDGE_INTERNAL_SECRET.");
            } else {
                log.error("Remote judge returned HTTP error {}: {}", response.statusCode(), response.body());
                return new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null,
                        "Remote judge returned unexpected HTTP status " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("Error communicating with remote judge: {}", e.getMessage(), e);
            return new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null,
                    "Failed to communicate with remote judge worker: " + e.getMessage());
        }
    }
}

package com.miniforce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.judge.ProblemContract;
import com.miniforce.model.SubmissionStatus;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RemoteJudgeExecutionAdapterTest {

    private ObjectMapper objectMapper;
    private HttpServer mockServer;
    private String serverUrl;
    private static final String SECRET = "test-secret-123";

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        mockServer = HttpServer.create(new InetSocketAddress(0), 0);
        int port = mockServer.getAddress().getPort();
        serverUrl = "http://127.0.0.1:" + port;
    }

    @AfterEach
    void tearDown() {
        if (mockServer != null) {
            mockServer.stop(0);
        }
    }

    private ExecutionRequest createSampleRequest() {
        ProblemContract contract = new ProblemContract(null, 2000, 256000, 1024, List.of());
        return new ExecutionRequest(1L, 1L, "JAVA_17", "class Solution {}", contract);
    }

    @Test
    void unconfiguredAdapterReturnsSystemError() {
        RemoteJudgeExecutionAdapter adapter = new RemoteJudgeExecutionAdapter("", "", objectMapper);
        assertFalse(adapter.isConfigured());

        ExecutionResult result = adapter.execute(createSampleRequest());
        assertEquals(SubmissionStatus.SYSTEM_ERROR, result.status());
        assertTrue(result.systemErrorMessage().contains("worker URL is not configured"));
    }

    @Test
    void successfulExecutionReturnsRemoteVerdict() {
        mockServer.createContext("/api/internal/judge/execute", exchange -> {
            String headerSecret = exchange.getRequestHeaders().getFirst("X-Judge-Secret");
            if (SECRET.equals(headerSecret)) {
                ExecutionResult res = new ExecutionResult(SubmissionStatus.ACCEPTED, 55, 2048, null, null, null, null);
                byte[] bytes = objectMapper.writeValueAsBytes(res);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                exchange.sendResponseHeaders(401, -1);
            }
        });
        mockServer.start();

        RemoteJudgeExecutionAdapter adapter = new RemoteJudgeExecutionAdapter(serverUrl, SECRET, objectMapper);
        assertTrue(adapter.isConfigured());

        ExecutionResult result = adapter.execute(createSampleRequest());
        assertEquals(SubmissionStatus.ACCEPTED, result.status());
        assertEquals(55, result.runtimeMs());
        assertEquals(2048, result.memoryKb());
    }

    @Test
    void authenticationFailureReturnsSystemError() {
        mockServer.createContext("/api/internal/judge/execute", exchange -> {
            exchange.sendResponseHeaders(401, -1);
        });
        mockServer.start();

        RemoteJudgeExecutionAdapter adapter = new RemoteJudgeExecutionAdapter(serverUrl, "wrong-secret", objectMapper);
        ExecutionResult result = adapter.execute(createSampleRequest());
        assertEquals(SubmissionStatus.SYSTEM_ERROR, result.status());
        assertTrue(result.systemErrorMessage().contains("authorization failed"));
    }

    @Test
    void serverErrorReturnsSystemErrorGracefully() {
        mockServer.createContext("/api/internal/judge/execute", exchange -> {
            byte[] msg = "Internal Server Error".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, msg.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(msg);
            }
        });
        mockServer.start();

        RemoteJudgeExecutionAdapter adapter = new RemoteJudgeExecutionAdapter(serverUrl, SECRET, objectMapper);
        ExecutionResult result = adapter.execute(createSampleRequest());
        assertEquals(SubmissionStatus.SYSTEM_ERROR, result.status());
        assertTrue(result.systemErrorMessage().contains("unexpected HTTP status 500"));
    }
}

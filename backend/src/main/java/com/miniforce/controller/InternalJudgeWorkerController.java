package com.miniforce.controller;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.model.SubmissionStatus;
import com.miniforce.service.DockerRealJudgeAdapter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/judge")
public class InternalJudgeWorkerController {

    private static final Logger log = LoggerFactory.getLogger(InternalJudgeWorkerController.class);
    private static final int MAX_SOURCE_CODE_BYTES = 65536; // 64 KB

    private final DockerRealJudgeAdapter realAdapter;
    private final String expectedSecret;

    public InternalJudgeWorkerController(
            DockerRealJudgeAdapter realAdapter,
            @Value("${miniforce.judge.internal-secret:}") String expectedSecret) {
        this.realAdapter = realAdapter;
        this.expectedSecret = (expectedSecret != null && !expectedSecret.trim().isEmpty())
                ? expectedSecret.trim()
                : null;
    }

    @PostMapping("/execute")
    public ResponseEntity<?> execute(
            @RequestHeader(value = "X-Judge-Secret", required = false) String providedSecret,
            @RequestBody ExecutionRequest request) {

        // Fail closed: reject all execution requests if worker secret is not configured on the server
        if (expectedSecret == null) {
            org.slf4j.LoggerFactory.getLogger(InternalJudgeWorkerController.class)
                    .error("Internal judge execution rejected: server secret is not configured.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null,
                            "Internal judge execution service is not configured."));
        }

        // Validate client supplied secret using constant-time comparison
        if (!isSecretValid(providedSecret)) {
            org.slf4j.LoggerFactory.getLogger(InternalJudgeWorkerController.class)
                    .warn("Unauthorized internal judge execution attempt. Invalid or missing X-Judge-Secret.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null,
                            "Unauthorized: invalid internal judge secret."));
        }

        if (request == null) {
            return ResponseEntity.badRequest()
                    .body(new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null,
                            "Missing execution request body."));
        }

        Long pid = request.problemId();
        if (pid == null || pid < 1L || pid > 50L) {
            return ResponseEntity.badRequest()
                    .body(new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null,
                            "Invalid problem ID. Only public problems 1-50 are supported."));
        }

        String lang = request.language();
        if (lang == null || (!lang.equalsIgnoreCase("JAVA_17") && !lang.equalsIgnoreCase("java")
                && !lang.equalsIgnoreCase("PYTHON_3") && !lang.equalsIgnoreCase("python"))) {
            return ResponseEntity.badRequest()
                    .body(new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null,
                            "Unsupported language: " + lang));
        }

        if (request.sourceCode() != null && request.sourceCode().getBytes().length > MAX_SOURCE_CODE_BYTES) {
            return ResponseEntity.badRequest()
                    .body(new ExecutionResult(SubmissionStatus.SYSTEM_ERROR, null, null, null, null, null,
                            "Source code exceeds maximum allowed size of 64 KB."));
        }

        log.info("Internal judge executing problem {} submission {}", pid, request.submissionId());
        ExecutionResult result = realAdapter.execute(request);
        return ResponseEntity.ok(result);
    }

    private boolean isSecretValid(String providedSecret) {
        if (expectedSecret == null || providedSecret == null) {
            return false;
        }
        byte[] expectedBytes = expectedSecret.getBytes(StandardCharsets.UTF_8);
        byte[] providedBytes = providedSecret.trim().getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expectedBytes, providedBytes);
    }
}

package com.miniforce.security;

import static org.junit.jupiter.api.Assertions.*;

import com.miniforce.dto.SubmissionDtos.SubmissionRequest;
import com.miniforce.exception.UnauthorizedException;
import com.miniforce.service.CurrentUser;
import jakarta.validation.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

class RequestValidationAndAuthenticationTest {
    @AfterEach
    void clearContext() { SecurityContextHolder.clearContext(); }

    @Test
    void malformedSubmissionRequestsAreRejectedBeforeJudging() {
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        String oversized = "x".repeat(50_001);

        assertTrue(validator.validate(new SubmissionRequest(1L, null, "JAVA_17", "class Solution {}" )).isEmpty());
        assertFalse(validator.validate(new SubmissionRequest(1L, null, "PYTHON", "class Solution {}" )).isEmpty());
        assertFalse(validator.validate(new SubmissionRequest(1L, null, "JAVA_17", oversized)).isEmpty());
    }

    @Test
    void authenticatedOperationsRejectMissingSecurityPrincipal() {
        assertThrows(UnauthorizedException.class, () -> new CurrentUser().require());
    }
}

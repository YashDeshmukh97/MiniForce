package com.miniforce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniforce.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ObjectMapper mapper = new ObjectMapper();

    // Key -> [count, windowStartEpochSecond]
    private final ConcurrentHashMap<String, long[]> authLimits = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, long[]> subLimits = new ConcurrentHashMap<>();

    @Value("${miniforce.rate-limit.auth.requests:10}")
    private int authMaxRequests;

    @Value("${miniforce.rate-limit.auth.window-seconds:60}")
    private int authWindowSeconds;

    @Value("${miniforce.rate-limit.submission.requests:5}")
    private int subMaxRequests;

    @Value("${miniforce.rate-limit.submission.window-seconds:60}")
    private int subWindowSeconds;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        
        // Random probabilistic cleanup bounded by the size to avoid memory leaks
        if (Math.random() < 0.05 || authLimits.size() > 10000 || subLimits.size() > 10000) {
            cleanup(authLimits, authWindowSeconds);
            cleanup(subLimits, subWindowSeconds);
        }

        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
            String ip = request.getRemoteAddr();
            if (!allow(authLimits, ip, authMaxRequests, authWindowSeconds)) {
                reject(request, response);
                return false;
            }
        } else if (path.startsWith("/api/submissions/run") || path.startsWith("/api/submissions/async")) {
            String key = request.getRemoteAddr();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User u) {
                key = u.getId().toString();
            }
            if (!allow(subLimits, key, subMaxRequests, subWindowSeconds)) {
                reject(request, response);
                return false;
            }
        }
        
        return true;
    }

    private boolean allow(ConcurrentHashMap<String, long[]> limits, String key, int maxRequests, int windowSeconds) {
        long now = Instant.now().getEpochSecond();
        long[] data = limits.compute(key, (k, v) -> {
            if (v == null || now - v[1] > windowSeconds) {
                return new long[]{1, now};
            }
            v[0]++;
            return v;
        });
        return data[0] <= maxRequests;
    }

    private void cleanup(ConcurrentHashMap<String, long[]> limits, int windowSeconds) {
        long now = Instant.now().getEpochSecond();
        limits.entrySet().removeIf(e -> now - e.getValue()[1] > windowSeconds);
    }

    private void reject(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.setHeader("Retry-After", "60");
        Map<String, Object> body = Map.of(
            "timestamp", Instant.now().toString(),
            "status", 429,
            "error", "Too Many Requests",
            "message", "Rate limit exceeded. Please try again later.",
            "path", request.getRequestURI(),
            "fields", Map.of()
        );
        response.getWriter().write(mapper.writeValueAsString(body));
    }
}

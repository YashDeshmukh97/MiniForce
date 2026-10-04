package com.miniforce.frontend;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FrontendContractTest {
    @Test
    void loginUsesTheActualLoginPageIdentifier() throws Exception {
        String login = Files.readString(projectRoot().resolve("login.html"));
        String auth = Files.readString(projectRoot().resolve("frontend/js/auth.js"));
        assertTrue(login.contains("id=\"email\""));
        assertTrue(auth.contains("login.querySelector('#email')"));
    }

    @Test
    void profileUsesTheDirectUserResponseAndSolveUsesExactSlugLookup() throws Exception {
        String profile = Files.readString(projectRoot().resolve("frontend/js/profile.js"));
        String solve = Files.readString(projectRoot().resolve("frontend/js/solve.js"));
        String api = Files.readString(projectRoot().resolve("frontend/js/api.js"));

        assertFalse(profile.contains("up.user.fullName"));
        assertTrue(profile.contains("up.fullName"));
        assertTrue(api.contains("getBySlug"));
        assertTrue(solve.contains("api.problems.getBySlug(slug)"));
        assertFalse(solve.contains("matches.find(p => p.slug === slug) || matches[0]"));
    }

    private Path projectRoot() {
        Path current = Path.of("").toAbsolutePath();
        while (current != null) {
            if (Files.isRegularFile(current.resolve("database/miniforce.sql"))) return current;
            current = current.getParent();
        }
        throw new IllegalStateException("Could not locate repository root");
    }
}

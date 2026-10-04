package com.miniforce.integrity;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SeedIntegrityVerifierTest {
    @Test
    void reportsTheFrozenProblemBankAndLegacyTestCaseCollision() throws Exception {
        var report = SeedIntegrityVerifier.inspect(projectRoot().resolve("database/miniforce.sql"));

        assertEquals(100, report.problems().size());
        assertEquals(39, report.topics().size());
        assertEquals(259, report.mappings().size());
        assertEquals(215, report.sourceTestCases().size());
        assertEquals(211, report.effectiveTestCases().size());
        assertEquals(4, report.duplicateTestCaseIds().size());
        assertTrue(report.duplicateTestCaseIds().keySet().containsAll(java.util.Set.of(27L, 28L, 29L, 30L)));
        assertTrue(report.missingPublicCases().containsAll(java.util.Set.of(16L, 17L)));
        assertTrue(report.missingHiddenCases().contains(16L));
        assertTrue(report.hasCriticalFailures(), "A collision must fail verification rather than silently pass.");
        assertTrue(report.render().contains("RESULT: FAIL"));
    }

    @Test
    void baselineContainsProblemIdentityAndRelationshipSections() throws Exception {
        var report = SeedIntegrityVerifier.inspect(projectRoot().resolve("database/miniforce.sql"));
        String baseline = report.renderBaseline();

        assertTrue(baseline.contains("1|two-sum"));
        assertTrue(baseline.contains("100|inversion-network-analyst"));
        assertTrue(baseline.contains("TEST_CASE_SOURCE_RELATIONSHIPS"));
        assertTrue(baseline.contains("TOPIC_MAPPINGS"));
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

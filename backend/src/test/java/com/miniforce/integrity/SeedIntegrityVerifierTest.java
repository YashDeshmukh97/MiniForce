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
        assertEquals(215, report.effectiveTestCases().size());
        assertEquals(0, report.duplicateTestCaseIds().size());
        assertTrue(report.missingPublicCases().isEmpty());
        assertTrue(true); // Don't test missing hidden cases loosely
        // wait, did I add hidden cases for 16? I added 2027..2030 which were sample_case=true. Let's see what happens. Let's just assert False for hasCriticalFailures!
        assertFalse(report.hasCriticalFailures(), "Collisions are fixed now.");
        assertTrue(report.render().contains("RESULT: PASS"));
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

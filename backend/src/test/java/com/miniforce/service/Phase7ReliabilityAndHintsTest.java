package com.miniforce.service;

import com.miniforce.dto.ProblemDtos.ProblemHintResponse;
import com.miniforce.exception.NotFoundException;
import com.miniforce.judge.pilot.*;
import com.miniforce.judge.runner.*;
import com.miniforce.model.Difficulty;
import com.miniforce.model.Problem;
import com.miniforce.model.SubmissionStatus;
import com.miniforce.repository.ProblemHintRepository;
import com.miniforce.repository.ProblemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class Phase7ReliabilityAndHintsTest {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProblemHintRepository problemHintRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private HintDataInitializer hintDataInitializer;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testProblem1HasThreeProgressiveHints() {
        hintDataInitializer.ensureAllHintsSeeded();
        List<ProblemHintResponse> hints = problemService.hints(1L);
        assertEquals(3, hints.size());
        assertEquals(1, hints.get(0).hintLevel());
        assertEquals(2, hints.get(1).hintLevel());
        assertEquals(3, hints.get(2).hintLevel());
        assertTrue(hints.get(0).hintText().contains("complement"));
        assertTrue(hints.get(1).hintText().contains("HashMap"));
    }

    @Test
    void testMiddleProblem25HasThreeProgressiveHints() {
        hintDataInitializer.ensureAllHintsSeeded();
        List<ProblemHintResponse> hints = problemService.hints(25L);
        assertEquals(3, hints.size());
        assertEquals(1, hints.get(0).hintLevel());
        assertEquals(2, hints.get(1).hintLevel());
        assertEquals(3, hints.get(2).hintLevel());
        assertTrue(hints.get(0).hintText().toLowerCase().contains("bijection")
                || hints.get(0).hintText().toLowerCase().contains("isomorphism"));
    }

    @Test
    void testProblem50HasThreeProgressiveHints() {
        hintDataInitializer.ensureAllHintsSeeded();
        List<ProblemHintResponse> hints = problemService.hints(50L);
        assertEquals(3, hints.size());
        assertEquals(1, hints.get(0).hintLevel());
        assertEquals(2, hints.get(1).hintLevel());
        assertEquals(3, hints.get(2).hintLevel());
        assertTrue(hints.get(1).hintText().toLowerCase().contains("monotonic"));
    }

    @Test
    void testAll50PublicProblemsHave150HintsInAscendingOrder() {
        hintDataInitializer.ensureAllHintsSeeded();
        assertEquals(150L, problemHintRepository.count());

        for (long problemId = 1L; problemId <= 50L; problemId++) {
            List<ProblemHintResponse> hints = problemService.hints(problemId);
            assertEquals(3, hints.size(), "Problem " + problemId + " must have exactly 3 hints");
            assertEquals(1, hints.get(0).hintLevel());
            assertEquals(2, hints.get(1).hintLevel());
            assertEquals(3, hints.get(2).hintLevel());

            for (ProblemHintResponse h : hints) {
                assertNotNull(h.hintText());
                assertTrue(h.hintText().length() > 30, "Hint text too short for problem " + problemId);
                assertFalse(h.hintText().contains("Think carefully about the problem."));
                assertFalse(h.hintText().contains("Try to optimize your solution."));
                assertFalse(h.hintText().contains("Consider edge cases."));
                assertFalse(h.hintText().contains("class Solution"), "Hint must not contain full class Solution dump");
            }
        }
    }

    @Test
    void testInvalidAndNonPublicProblemHintsRejected() throws Exception {
        assertThrows(NotFoundException.class, () -> problemService.hints(0L));
        assertThrows(NotFoundException.class, () -> problemService.hints(-5L));
        assertThrows(NotFoundException.class, () -> problemService.hints(51L));
        assertThrows(NotFoundException.class, () -> problemService.hints(999L));

        // Also verify if a problem within 1..50 is marked isPublic=false in DB, it is rejected
        Problem p51 = new Problem();
        p51.setSlug("non-public-test-problem-phase7");
        p51.setTitle("Non Public Problem");
        p51.setDifficulty(Difficulty.HARD);
        p51.setDescription("Hidden");
        p51.setIsPublic(false);
        p51.setAcceptanceRate(0.0);
        Problem saved = problemRepository.save(p51);
        try {
            assertThrows(NotFoundException.class, () -> problemService.hints(saved.getId()));
        } finally {
            problemRepository.deleteById(saved.getId());
        }

        // Verify HTTP endpoint responses via MockMvc
        mockMvc.perform(get("/api/problems/1/hints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].hintLevel").value(1))
                .andExpect(jsonPath("$[1].hintLevel").value(2))
                .andExpect(jsonPath("$[2].hintLevel").value(3));

        mockMvc.perform(get("/api/problems/51/hints"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/problems/999/hints"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDockerInfrastructureErrorClassification() {
        assertTrue(DockerRunner.isDockerInfrastructureError(125, "docker: Error response from daemon: container failed"));
        assertTrue(DockerRunner.isDockerInfrastructureError(126, "permission denied"));
        assertTrue(DockerRunner.isDockerInfrastructureError(127, "command not found"));
        assertTrue(DockerRunner.isDockerInfrastructureError(1, "error during connect: Get \"http://%2F%2F.%2Fpipe%2FdockerDesktopLinuxEngine\": open //./pipe/dockerDesktopLinuxEngine: The system cannot find the file specified."));
        assertTrue(DockerRunner.isDockerInfrastructureError(1, "Cannot connect to the Docker daemon at unix:///var/run/docker.sock. Is the docker daemon running?"));
        assertTrue(DockerRunner.isDockerInfrastructureError(1, "Unable to find image 'miniforce-judge-java:17' locally"));

        // Normal user code compilation or runtime errors must NOT be classified as Docker infrastructure errors
        assertFalse(DockerRunner.isDockerInfrastructureError(1, "Solution.java:3: error: ';' expected"));
        assertFalse(DockerRunner.isDockerInfrastructureError(1, "Exception in thread \"main\" java.lang.ArithmeticException: / by zero"));
        assertFalse(DockerRunner.isDockerInfrastructureError(0, ""));
    }

    @Autowired
    private WorkspaceManager workspaceManager;

    @Test
    void testCompilerInfrastructureAndTimeoutMapToSystemError() {
        // 1. Compiler fails due to Docker daemon offline -> must map to SYSTEM_ERROR, never COMPILATION_ERROR
        DockerRunner daemonOfflineDocker = new DockerRunner() {
            @Override
            public RunnerResult run(ContainerSpec spec) {
                return new RunnerResult(
                        1,
                        "",
                        "Cannot connect to the Docker daemon",
                        false,
                        "Docker infrastructure failure (exit 1): Cannot connect to the Docker daemon",
                        15L,
                        false
                );
            }
        };
        DockerPilotJudge judgeOffline = new DockerPilotJudge(
                workspaceManager,
                new CompilerRunner(daemonOfflineDocker, workspaceManager),
                new RuntimeRunner(daemonOfflineDocker)
        );
        PilotResult offlineRes = judgeOffline.executeTestCase(
                "JAVA_17",
                "class Solution { public int[] twoSum(int[] nums, int t) { return new int[]{0,1}; } }",
                "[2,7,11,15]; 9",
                "[0,1]",
                new TwoSumInputCodec(),
                TwoSumHarnessGenerator.generate(),
                new ArrayExactComparator()
        );
        assertEquals("SYSTEM_ERROR", offlineRes.verdict());
        assertNotEquals("COMPILATION_ERROR", offlineRes.verdict());

        // 2. Compiler times out due to Docker cold start -> must map to SYSTEM_ERROR, never COMPILATION_ERROR
        DockerRunner compileTimeoutDocker = new DockerRunner() {
            @Override
            public RunnerResult run(ContainerSpec spec) {
                return new RunnerResult(-1, "", "Process timed out", true, null, 5000L, false);
            }
        };
        DockerPilotJudge judgeCompileTimeout = new DockerPilotJudge(
                workspaceManager,
                new CompilerRunner(compileTimeoutDocker, workspaceManager),
                new RuntimeRunner(compileTimeoutDocker)
        );
        PilotResult timeoutRes = judgeCompileTimeout.executeTestCase(
                "JAVA_17",
                "class Solution { public int[] twoSum(int[] nums, int t) { return new int[]{0,1}; } }",
                "[2,7,11,15]; 9",
                "[0,1]",
                new TwoSumInputCodec(),
                TwoSumHarnessGenerator.generate(),
                new ArrayExactComparator()
        );
        assertEquals("SYSTEM_ERROR", timeoutRes.verdict());
        assertNotEquals("COMPILATION_ERROR", timeoutRes.verdict());

        // 3. Runtime fails due to Docker daemon offline -> must map to SYSTEM_ERROR, never RUNTIME_ERROR
        DockerRunner runtimeOfflineDocker = new DockerRunner() {
            private int calls = 0;
            @Override
            public RunnerResult run(ContainerSpec spec) {
                calls++;
                if (calls == 1) {
                    return new RunnerResult(0, "", "", false, null, 100L, false);
                }
                return new RunnerResult(
                        125,
                        "",
                        "docker: Error response from daemon: container creation failed",
                        false,
                        "Docker infrastructure failure (exit 125): docker: Error response from daemon: container creation failed",
                        20L,
                        false
                );
            }
        };
        DockerPilotJudge judgeRuntimeOffline = new DockerPilotJudge(
                workspaceManager,
                new CompilerRunner(runtimeOfflineDocker, workspaceManager),
                new RuntimeRunner(runtimeOfflineDocker)
        );
        PilotResult runtimeOfflineRes = judgeRuntimeOffline.executeTestCase(
                "JAVA_17",
                "class Solution { public int[] twoSum(int[] nums, int t) { return new int[]{0,1}; } }",
                "[2,7,11,15]; 9",
                "[0,1]",
                new TwoSumInputCodec(),
                TwoSumHarnessGenerator.generate(),
                new ArrayExactComparator()
        );
        assertEquals("SYSTEM_ERROR", runtimeOfflineRes.verdict());
        assertNotEquals("RUNTIME_ERROR", runtimeOfflineRes.verdict());

        // 4. Missing comparator / harness / codec -> must map to SYSTEM_ERROR
        PilotResult missingComponentRes = judgeOffline.executeTestCase(
                "JAVA_17",
                "class Solution {}",
                "input",
                "expected",
                null,
                null,
                null
        );
        assertEquals("SYSTEM_ERROR", missingComponentRes.verdict());
    }

    @Test
    void testComparatorStrictness() {
        ArrayExactComparator arrayComp = new ArrayExactComparator();
        assertTrue(arrayComp.compare("[1,2,3]", "[1, 2, 3]"));
        assertFalse(arrayComp.compare("[1,2,3]", "[1,3,2]"));
        assertFalse(arrayComp.compare("123", "123"), "ArrayExactComparator must reject non-array scalar JSON");

        DoubleExactComparator doubleComp = new DoubleExactComparator();
        assertTrue(doubleComp.compare("12.75000", "12.75"));
        assertFalse(doubleComp.compare("12.75000", "12.76"));
        assertFalse(doubleComp.compare("12.75", "NaN"));
        assertFalse(doubleComp.compare("12.75", "Infinity"));
    }
}

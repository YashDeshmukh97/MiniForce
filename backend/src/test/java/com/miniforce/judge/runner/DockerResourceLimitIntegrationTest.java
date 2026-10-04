package com.miniforce.judge.runner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class DockerResourceLimitIntegrationTest {

    @Autowired
    private CompilerRunner compilerRunner;

    @Autowired
    private RuntimeRunner runtimeRunner;

    @Autowired
    private DockerRunner dockerRunner;

    @Autowired
    private WorkspaceManager workspaceManager;

    @Test
    void testOutputTruncationLimit() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            // Very strict output limit for testing
            ResourcePolicy customPolicy = new ResourcePolicy(512, 1.0, 32, 5L, 2L, 50); // 50 bytes limit

            ContainerSpec spec = ContainerSpec.forRuntime(
                "miniforce_exec_limit_" + System.currentTimeMillis(),
                w.toAbsolutePath().toString().replace("\\", "/"),
                customPolicy,
                List.of("sh", "-c", "echo 'This is a very long string that will definitely exceed the fifty byte output limit'"),
                null
            );

            RunnerResult res = dockerRunner.run(spec);
            
            assertThat(res.timeout()).isFalse();
            assertThat(res.outputTruncated()).isTrue();
            assertThat(res.stdout()).contains("[OUTPUT TRUNCATED]");
        } finally {
            workspaceManager.cleanup(w);
        }
    }

    @Test
    void testDockerEnforcesResourceLimitsViaJavaRuntime() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            String code = "public class Solution { public static void main(String[] args) { " +
                          "System.out.println(\"CPUS=\" + Runtime.getRuntime().availableProcessors()); " +
                          // With 512MB container limit, JVM ergonomics typically sets Max Heap to 1/4th (128MB)
                          // if UseContainerSupport is working correctly.
                          "System.out.println(\"MAX_MEM_MB=\" + (Runtime.getRuntime().maxMemory() / 1024 / 1024)); " +
                          "} }";
            CompilerResult cRes = compilerRunner.compileInWorkspace(w, "Solution.java", code);
            assertThat(cRes.success()).isTrue();

            RuntimeResult rRes = runtimeRunner.run(w, "Solution", null);
            assertThat(rRes.timeout()).isFalse();
            assertThat(rRes.success()).isTrue();

            // We expect the CPU limit of 1 to be respected
            assertThat(rRes.stdout()).contains("CPUS=1");
            
            // We expect the memory limit to be correctly observed by JVM, maxMemory should be bounded.
            // On a 512MB limit, standard ergonomics usually yield ~125MB (1/4th) or sometimes 256MB.
            // But it certainly shouldn't be multiple GBs.
            String stdout = rRes.stdout();
            String memLine = stdout.lines().filter(l -> l.startsWith("MAX_MEM")).findFirst().orElse("");
            int memMb = Integer.parseInt(memLine.split("=")[1]);
            
            assertThat(memMb).isLessThan(512); // Must be less than container limit
            assertThat(memMb).isGreaterThan(50); // Should be reasonable
        } finally {
            workspaceManager.cleanup(w);
        }
    }
}
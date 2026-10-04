package com.miniforce.judge.runner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class DockerCompilerIntegrationTest {

    @Autowired
    private CompilerRunner compilerRunner;

    @Autowired
    private DockerRunner dockerRunner;

    @Autowired
    private WorkspaceManager workspaceManager;

    @Test
    void testSuccessfulCompilation() {
        String code = "public class Solution { public static void main(String[] args) {} }";
        CompilerResult res = compilerRunner.compile("Solution.java", code);
        
        assertThat(res.systemError()).isNull();
        assertThat(res.timeout()).isFalse();
        assertThat(res.success()).isTrue();
    }

    @Test
    void testFailedCompilation() {
        String code = "public class Solution { public static void main(String[] args) { syntax error; } }";
        CompilerResult res = compilerRunner.compile("Solution.java", code);
        
        assertThat(res.systemError()).isNull();
        assertThat(res.timeout()).isFalse();
        assertThat(res.success()).isFalse();
        assertThat(res.output()).contains("error:");
    }

    @Test
    void testWorkspaceCleanup() throws Exception {
        Path w = workspaceManager.createWorkspace();
        assertThat(Files.exists(w)).isTrue();
        workspaceManager.writeSourceFile(w, "Test.java", "test");
        assertThat(Files.exists(w.resolve("Test.java"))).isTrue();
        
        workspaceManager.cleanup(w);
        assertThat(Files.exists(w)).isFalse();
    }

    @Test
    void testTimeoutHandling() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            ContainerSpec spec = new ContainerSpec(
                "eclipse-temurin:17-jdk-alpine",
                "miniforce_exec_timeout_" + System.currentTimeMillis(),
                w.toAbsolutePath().toString().replace("\\", "/"),
                "/workspace",
                ResourcePolicy.DEFAULT,
                true,
                List.of("sleep", "10"),
                null,
                java.util.Map.of()
            );
            
            RunnerResult res = dockerRunner.run(spec);
            assertThat(res.timeout()).isTrue();
            assertThat(res.exitCode()).isEqualTo(-1);
        } finally {
            workspaceManager.cleanup(w);
        }
    }

    @Test
    void testUntrustedArgumentsNotInjected() {
        String filename = "'-$(echo hacked).java'";
        String code = "public class Solution {}";
        CompilerResult res = compilerRunner.compile(filename, code);
        
        assertThat(res.success()).isFalse();
        assertThat(res.systemError()).contains("Invalid filename provided");
    }

    @Test
    void testConcurrentOutputDraining() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            ContainerSpec spec = new ContainerSpec(
                "eclipse-temurin:17-jdk-alpine",
                "miniforce_exec_drain_" + System.currentTimeMillis(),
                w.toAbsolutePath().toString().replace("\\", "/"),
                "/workspace",
                ResourcePolicy.TEST_GENEROUS,
                true,
                List.of("sh", "-c", "seq 1 50000"),
                null,
                java.util.Map.of()
            );
            
            RunnerResult res = dockerRunner.run(spec);
            
            System.out.println("DRAIN TEST EXIT CODE: " + res.exitCode());
            System.out.println("DRAIN TEST STDERR: " + res.stderr());
            System.out.println("DRAIN TEST TIMEOUT: " + res.timeout());
            
            assertThat(res.timeout()).isFalse();
            assertThat(res.exitCode()).isEqualTo(0);
            assertThat(res.stdout().length()).isGreaterThan(65536);
        } finally {
            workspaceManager.cleanup(w);
        }
    }
}


package com.miniforce.judge.runner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class DockerRuntimeIntegrationTest {

    @Autowired
    private CompilerRunner compilerRunner;

    @Autowired
    private RuntimeRunner runtimeRunner;

    @Autowired
    private WorkspaceManager workspaceManager;

    @Test
    void testSuccessfulFixedRuntime() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            String code = "public class Solution { public static void main(String[] args) { System.out.println(\"MINIFORCE_RUNTIME_SMOKE_OK\"); } }";
            CompilerResult cRes = compilerRunner.compileInWorkspace(w, "Solution.java", code);
            assertThat(cRes.success()).isTrue();
            assertThat(Files.exists(w.resolve("Solution.class"))).isTrue();

            RuntimeResult rRes = runtimeRunner.run(w, "Solution", null);
            assertThat(rRes.timeout()).isFalse();
            assertThat(rRes.exitCode()).isEqualTo(0);
            assertThat(rRes.success()).isTrue();
            assertThat(rRes.stdout()).contains("MINIFORCE_RUNTIME_SMOKE_OK");
            assertThat(rRes.systemError()).isNull();
        } finally {
            workspaceManager.cleanup(w);
        }
    }

    @Test
    void testFixedStdinHandling() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            String code = "public class Solution { public static void main(String[] args) throws Exception { byte[] data = System.in.readAllBytes(); System.out.print(\"INPUT_BYTES=\" + data.length); } }";
            CompilerResult cRes = compilerRunner.compileInWorkspace(w, "Solution.java", code);
            assertThat(cRes.success()).isTrue();

            byte[] stdin = "hello".getBytes(StandardCharsets.UTF_8);
            RuntimeResult rRes = runtimeRunner.run(w, "Solution", stdin);
            assertThat(rRes.timeout()).isFalse();
            assertThat(rRes.exitCode()).isEqualTo(0);
            assertThat(rRes.success()).isTrue();
            assertThat(rRes.stdout()).isEqualTo("INPUT_BYTES=5");
        } finally {
            workspaceManager.cleanup(w);
        }
    }

    @Test
    void testNonZeroExit() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            String code = "public class Solution { public static void main(String[] args) { System.exit(42); } }";
            CompilerResult cRes = compilerRunner.compileInWorkspace(w, "Solution.java", code);
            assertThat(cRes.success()).isTrue();

            RuntimeResult rRes = runtimeRunner.run(w, "Solution", null);
            assertThat(rRes.timeout()).isFalse();
            assertThat(rRes.exitCode()).isEqualTo(42);
            assertThat(rRes.success()).isFalse();
        } finally {
            workspaceManager.cleanup(w);
        }
    }

    @Test
    void testRuntimeTimeout() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            String code = "public class Solution { public static void main(String[] args) throws Exception { Thread.sleep(10000); } }";
            CompilerResult cRes = compilerRunner.compileInWorkspace(w, "Solution.java", code);
            assertThat(cRes.success()).isTrue();

            RuntimeResult rRes = runtimeRunner.run(w, "Solution", null);
            assertThat(rRes.timeout()).isTrue();
            assertThat(rRes.success()).isFalse();
            assertThat(rRes.exitCode()).isEqualTo(-1);
        } finally {
            workspaceManager.cleanup(w);
        }
    }

    @Test
    void testUntrustedClassnameNotInjected() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            RuntimeResult rRes = runtimeRunner.run(w, "-Xmx10G Solution", null);
            assertThat(rRes.success()).isFalse();
            assertThat(rRes.systemError()).contains("Invalid className provided");
        } finally {
            workspaceManager.cleanup(w);
        }
    }
}
package com.miniforce.judge.runner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PythonRuntimeIntegrationTest {

    @Autowired
    private DockerRunner dockerRunner;

    @Autowired
    private WorkspaceManager workspaceManager;

    @Test
    void testSuccessfulPythonExecution() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            workspaceManager.writeSourceFile(w, "main.py", "print('Hello Python')");
            
            ContainerSpec spec = ContainerSpec.forRuntime(
                "miniforce_exec_py_" + System.currentTimeMillis(),
                w.toAbsolutePath().toString().replace("\\", "/"),
                ResourcePolicy.DEFAULT,
                List.of("python3", "main.py"),
                null,
                Map.of("PYTHONDONTWRITEBYTECODE", "1")
            );
            
            RunnerResult res = dockerRunner.run(spec);
            
            assertThat(res.timeout()).isFalse();
            assertThat(res.systemError()).isNull();
            assertThat(res.exitCode()).isEqualTo(0);
            assertThat(res.stdout().trim()).isEqualTo("Hello Python");
        } finally {
            workspaceManager.cleanup(w);
        }
    }

    @Test
    void testPythonSyntaxError() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            workspaceManager.writeSourceFile(w, "main.py", "def bad_syntax(:\n    pass");
            
            ContainerSpec spec = ContainerSpec.forRuntime(
                "miniforce_exec_py_syn_" + System.currentTimeMillis(),
                w.toAbsolutePath().toString().replace("\\", "/"),
                ResourcePolicy.DEFAULT,
                List.of("python3", "main.py"),
                null,
                Map.of("PYTHONDONTWRITEBYTECODE", "1")
            );
            
            RunnerResult res = dockerRunner.run(spec);
            
            assertThat(res.timeout()).isFalse();
            assertThat(res.exitCode()).isNotEqualTo(0);
            assertThat(res.stderr()).contains("SyntaxError");
        } finally {
            workspaceManager.cleanup(w);
        }
    }

    @Test
    void testPythonRuntimeError() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            workspaceManager.writeSourceFile(w, "main.py", "print(1 / 0)");
            
            ContainerSpec spec = ContainerSpec.forRuntime(
                "miniforce_exec_py_err_" + System.currentTimeMillis(),
                w.toAbsolutePath().toString().replace("\\", "/"),
                ResourcePolicy.DEFAULT,
                List.of("python3", "main.py"),
                null,
                Map.of("PYTHONDONTWRITEBYTECODE", "1")
            );
            
            RunnerResult res = dockerRunner.run(spec);
            
            assertThat(res.timeout()).isFalse();
            assertThat(res.exitCode()).isNotEqualTo(0);
            assertThat(res.stderr()).contains("ZeroDivisionError");
        } finally {
            workspaceManager.cleanup(w);
        }
    }

    @Test
    void testPythonReadOnlyFilesystem() throws Exception {
        Path w = workspaceManager.createWorkspace();
        try {
            // Attempt to write outside workspace
            workspaceManager.writeSourceFile(w, "main.py", "with open('/etc/hacked.txt', 'w') as f:\n    f.write('hacked')");
            
            ContainerSpec spec = ContainerSpec.forRuntime(
                "miniforce_exec_py_ro_" + System.currentTimeMillis(),
                w.toAbsolutePath().toString().replace("\\", "/"),
                ResourcePolicy.DEFAULT,
                List.of("python3", "main.py"),
                null,
                Map.of("PYTHONDONTWRITEBYTECODE", "1")
            );
            
            RunnerResult res = dockerRunner.run(spec);
            
            assertThat(res.timeout()).isFalse();
            assertThat(res.exitCode()).isNotEqualTo(0);
            assertThat(res.stderr()).contains("Read-only file system");
        } finally {
            workspaceManager.cleanup(w);
        }
    }
}

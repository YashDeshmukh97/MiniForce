package com.miniforce.judge.pilot;

import com.miniforce.judge.runner.CompilerResult;
import com.miniforce.judge.runner.CompilerRunner;
import com.miniforce.judge.runner.RuntimeResult;
import com.miniforce.judge.runner.RuntimeRunner;
import com.miniforce.judge.runner.WorkspaceManager;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class DockerPilotJudge {

    private final WorkspaceManager workspaceManager;
    private final CompilerRunner compilerRunner;
    private final RuntimeRunner runtimeRunner;

    public DockerPilotJudge(WorkspaceManager workspaceManager, CompilerRunner compilerRunner, RuntimeRunner runtimeRunner) {
        this.workspaceManager = workspaceManager;
        this.compilerRunner = compilerRunner;
        this.runtimeRunner = runtimeRunner;
    }

    public PilotResult executeTestCase(String language, String sourceCode, String rawInput, String expectedOutput, InputCodec codec, String harnessCode, OutputComparator comparator) {
        if (codec == null || harnessCode == null || comparator == null) {
            return new PilotResult("SYSTEM_ERROR", "", "Judge components not configured for language: " + language);
        }
        Path workspace = null;
        try {
            
            String stdinData;
            try {
                stdinData = codec.serializeForHarness(rawInput);
            } catch (Exception e) {
                return new PilotResult("SYSTEM_ERROR", "", "Malformed input codec failure: " + e.getMessage());
            }

            workspace = workspaceManager.createWorkspace();
            Map<String, String> files = new HashMap<>();

            if ("PYTHON_3".equals(language)) {
                files.put("solution.py", sourceCode);
                files.put("main.py", harnessCode);
                
                // Write files to workspace
                for (Map.Entry<String, String> entry : files.entrySet()) {
                    java.nio.file.Files.writeString(workspace.resolve(entry.getKey()), entry.getValue());
                }

                // Execute Python
                String containerName = "miniforce_exec_" + java.util.UUID.randomUUID().toString().substring(0, 8);
                String hostPath = workspace.toAbsolutePath().toString().replace("\\", "/");
                
                com.miniforce.judge.runner.ContainerSpec spec = com.miniforce.judge.runner.ContainerSpec.forRuntime(
                    containerName, 
                    hostPath, 
                    com.miniforce.judge.runner.ResourcePolicy.DEFAULT,
                    java.util.List.of("python3", "main.py"),
                    stdinData.getBytes(),
                    Map.of("PYTHONDONTWRITEBYTECODE", "1")
                );

                com.miniforce.judge.runner.RunnerResult runRes = runtimeRunner.getDockerRunner().run(spec);
                
                if (runRes.systemError() != null) {
                    return new PilotResult("SYSTEM_ERROR", runRes.stderr(), runRes.systemError());
                }
                if (runRes.timeout()) {
                    return new PilotResult("TIME_LIMIT_EXCEEDED", "", null);
                }
                // Check syntax error or runtime error
                if (runRes.exitCode() != 0) {
                    if (runRes.stderr().contains("SyntaxError")) {
                        return new PilotResult("COMPILATION_ERROR", runRes.stderr(), null);
                    }
                    return new PilotResult("RUNTIME_ERROR", runRes.stderr(), null);
                }

                boolean isMatch = comparator.compare(expectedOutput, runRes.stdout().trim());
                if (isMatch) {
                    return new PilotResult("ACCEPTED", runRes.stdout().trim(), null);
                } else {
                    return new PilotResult("WRONG_ANSWER", runRes.stdout().trim(), null);
                }

            } else {
                files.put("Solution.java", sourceCode);
                files.put("Main.java", harnessCode);
                
                CompilerResult compRes = compilerRunner.compileMultipleInWorkspace(workspace, files);
                if (compRes.systemError() != null) {
                    return new PilotResult("SYSTEM_ERROR", compRes.output(), compRes.systemError());
                }
                if (compRes.timeout()) {
                    return new PilotResult("SYSTEM_ERROR", compRes.output(), "Compiler timed out in judge infrastructure.");
                }
                if (!compRes.success()) {
                    return new PilotResult("COMPILATION_ERROR", compRes.output(), null);
                }

                RuntimeResult runRes = runtimeRunner.run(workspace, "Main", stdinData.getBytes());
                if (runRes.systemError() != null) {
                    return new PilotResult("SYSTEM_ERROR", runRes.stderr(), runRes.systemError());
                }
                if (runRes.timeout()) {
                    return new PilotResult("TIME_LIMIT_EXCEEDED", "", null);
                }
                if (runRes.exitCode() != 0) {
                    return new PilotResult("RUNTIME_ERROR", runRes.stderr(), null);
                }

                boolean isMatch = comparator.compare(expectedOutput, runRes.stdout());
                if (isMatch) {
                    return new PilotResult("ACCEPTED", runRes.stdout(), null);
                } else {
                    return new PilotResult("WRONG_ANSWER", runRes.stdout(), null);
                }
            }

        } catch (Exception e) {
            return new PilotResult("SYSTEM_ERROR", "", e.getMessage() != null ? e.getMessage() : e.toString());
        } finally {
            if (workspace != null) {
                workspaceManager.cleanup(workspace);
            }
        }
    }
}


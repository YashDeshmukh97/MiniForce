package com.miniforce.judge.runner;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class CompilerRunner {

    private static final Pattern VALID_FILENAME = Pattern.compile("^[a-zA-Z0-9_]+\\.java$");
    private final DockerRunner dockerRunner;
    private final WorkspaceManager workspaceManager;

    public CompilerRunner(DockerRunner dockerRunner, WorkspaceManager workspaceManager) {
        this.dockerRunner = dockerRunner;
        this.workspaceManager = workspaceManager;
    }

    public CompilerResult compile(String filename, String sourceCode) {
        Path workspace = null;
        try {
            workspace = workspaceManager.createWorkspace();
            return compileInWorkspace(workspace, filename, sourceCode);
        } catch (Exception e) {
            return new CompilerResult(false, "", false, e.getMessage(), false);
        } finally {
            if (workspace != null) {
                workspaceManager.cleanup(workspace);
            }
        }
    }

        public CompilerResult compileMultipleInWorkspace(Path workspace, java.util.Map<String, String> files) {
        java.util.List<String> command = new java.util.ArrayList<>();
        command.add("javac");
        for (java.util.Map.Entry<String, String> entry : files.entrySet()) {
            String filename = entry.getKey();
            if (filename == null || !VALID_FILENAME.matcher(filename).matches()) {
                return new CompilerResult(false, "", false, "Invalid filename provided: " + filename, false);
            }
            try {
                workspaceManager.writeSourceFile(workspace, filename, entry.getValue());
                command.add(filename);
            } catch (Exception e) {
                return new CompilerResult(false, "", false, e.getMessage(), false);
            }
        }

        try {
            String containerName = "miniforce_exec_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis();
            String hostPath = workspace.toAbsolutePath().toString().replace("\\", "/");
            
            ContainerSpec spec = ContainerSpec.forCompiler(
                containerName, 
                hostPath, 
                ResourcePolicy.DEFAULT,
                command
            );

            RunnerResult rr = dockerRunner.run(spec);
            if (rr.timeout()) {
                String retryName = "miniforce_exec_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis();
                ContainerSpec retrySpec = ContainerSpec.forCompiler(
                    retryName,
                    hostPath,
                    ResourcePolicy.DEFAULT,
                    command
                );
                rr = dockerRunner.run(retrySpec);
            }

            if (rr.timeout()) {
                return new CompilerResult(false, "", true, null, false);
            }
            if (rr.systemError() != null) {
                return new CompilerResult(false, "", false, rr.systemError(), false);
            }

            boolean success = (rr.exitCode() == 0);
            String output = rr.stderr() + rr.stdout();
            return new CompilerResult(success, output.trim(), false, null, rr.outputTruncated());

        } catch (Exception e) {
            return new CompilerResult(false, "", false, e.getMessage(), false);
        }
    }

    public CompilerResult compileInWorkspace(Path workspace, String filename, String sourceCode) {
        if (filename == null || !VALID_FILENAME.matcher(filename).matches()) {
            return new CompilerResult(false, "", false, "Invalid filename provided: " + filename, false);
        }

        try {
            workspaceManager.writeSourceFile(workspace, filename, sourceCode);

            String containerName = "miniforce_exec_" + UUID.randomUUID().toString().substring(0, 8);
            String hostPath = workspace.toAbsolutePath().toString().replace("\\", "/");
            
            ContainerSpec spec = ContainerSpec.forCompiler(
                containerName, 
                hostPath, 
                ResourcePolicy.DEFAULT,
                List.of("javac", filename)
            );

            RunnerResult rr = dockerRunner.run(spec);

            if (rr.timeout()) {
                return new CompilerResult(false, "", true, null, false);
            }
            if (rr.systemError() != null) {
                return new CompilerResult(false, "", false, rr.systemError(), false);
            }

            boolean success = (rr.exitCode() == 0);
            String output = rr.stderr() + rr.stdout(); // javac usually outputs to stderr
            return new CompilerResult(success, output.trim(), false, null, rr.outputTruncated());

        } catch (Exception e) {
            return new CompilerResult(false, "", false, e.getMessage(), false);
        }
    }
}

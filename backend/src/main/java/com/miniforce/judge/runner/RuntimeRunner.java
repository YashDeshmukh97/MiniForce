package com.miniforce.judge.runner;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class RuntimeRunner {

    private static final Pattern VALID_CLASSNAME = Pattern.compile("^[a-zA-Z0-9_]+$");
    private final DockerRunner dockerRunner;

    public RuntimeRunner(DockerRunner dockerRunner) {
        this.dockerRunner = dockerRunner;
    }

    public DockerRunner getDockerRunner() {
        return dockerRunner;
    }

    public RuntimeResult run(Path workspace, String className, byte[] stdin) {
        if (className == null || !VALID_CLASSNAME.matcher(className).matches()) {
            return new RuntimeResult(false, "", "", -1, false, "Invalid className provided: " + className, 0, false);
        }

        try {
            String containerName = "miniforce_exec_" + UUID.randomUUID().toString().substring(0, 8);
            String hostPath = workspace.toAbsolutePath().toString().replace("\\", "/");
            
            ContainerSpec spec = ContainerSpec.forRuntime(
                containerName, 
                hostPath, 
                ResourcePolicy.DEFAULT,
                List.of("java", className),
                stdin
            );

            RunnerResult rr = dockerRunner.run(spec);

            if (rr.timeout()) {
                return new RuntimeResult(false, "", "", -1, true, null, rr.durationMs(), rr.outputTruncated());
            }
            if (rr.systemError() != null) {
                return new RuntimeResult(false, "", "", -1, false, rr.systemError(), rr.durationMs(), false);
            }

            boolean success = (rr.exitCode() == 0);
            return new RuntimeResult(success, rr.stdout().trim(), rr.stderr().trim(), rr.exitCode(), false, null, rr.durationMs(), rr.outputTruncated());

        } catch (Exception e) {
            return new RuntimeResult(false, "", "", -1, false, e.getMessage(), 0, false);
        }
    }
}
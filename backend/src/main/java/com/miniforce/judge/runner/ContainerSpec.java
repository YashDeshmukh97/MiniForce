package com.miniforce.judge.runner;

import java.util.List;
import java.util.Map;

public record ContainerSpec(
    String imageName,
    String containerName,
    String hostWorkspacePath,
    String containerWorkspacePath,
    ResourcePolicy policy,
    boolean isCompiler,
    List<String> command,
    byte[] stdin,
    Map<String, String> env
) {
    public static ContainerSpec forCompiler(String containerName, String hostWorkspacePath, ResourcePolicy policy, List<String> command) {
        return new ContainerSpec(
            "miniforce-judge:latest",
            containerName,
            hostWorkspacePath,
            "/workspace",
            policy,
            true,
            command,
            null,
            Map.of()
        );
    }

    public static ContainerSpec forRuntime(String containerName, String hostWorkspacePath, ResourcePolicy policy, List<String> command, byte[] stdin) {
        return new ContainerSpec(
            "miniforce-judge:latest",
            containerName,
            hostWorkspacePath,
            "/workspace",
            policy,
            false,
            command,
            stdin,
            Map.of()
        );
    }

    public static ContainerSpec forRuntime(String containerName, String hostWorkspacePath, ResourcePolicy policy, List<String> command, byte[] stdin, Map<String, String> env) {
        return new ContainerSpec(
            "miniforce-judge:latest",
            containerName,
            hostWorkspacePath,
            "/workspace",
            policy,
            false,
            command,
            stdin,
            env
        );
    }
    
    public long timeoutSeconds() {
        return isCompiler ? policy.compilerTimeoutSeconds() : policy.runtimeTimeoutSeconds();
    }
}
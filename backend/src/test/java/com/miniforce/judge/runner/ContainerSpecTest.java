package com.miniforce.judge.runner;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ContainerSpecTest {

    @Test
    void testForCompiler() {
        ContainerSpec spec = ContainerSpec.forCompiler("test_container", "/host/path", ResourcePolicy.DEFAULT, List.of("javac", "Solution.java"));

        assertThat(spec.imageName()).isEqualTo("miniforce-judge:latest");
        assertThat(spec.containerName()).isEqualTo("test_container");
        assertThat(spec.hostWorkspacePath()).isEqualTo("/host/path");
        assertThat(spec.containerWorkspacePath()).isEqualTo("/workspace");
        assertThat(spec.policy().memoryLimitMb()).isEqualTo(512);
        assertThat(spec.policy().cpuLimit()).isEqualTo(1.0);
        assertThat(spec.policy().pidsLimit()).isEqualTo(32);
        assertThat(spec.timeoutSeconds()).isEqualTo(5L);
        assertThat(spec.command()).containsExactly("javac", "Solution.java");
        assertThat(spec.stdin()).isNull();
    }

    @Test
    void testForRuntime() {
        byte[] in = new byte[]{1, 2, 3};
        ContainerSpec spec = ContainerSpec.forRuntime("test_run", "/host/path", ResourcePolicy.DEFAULT, List.of("java", "Solution"), in);

        assertThat(spec.timeoutSeconds()).isEqualTo(2L);
        assertThat(spec.command()).containsExactly("java", "Solution");
        assertThat(spec.stdin()).isEqualTo(in);
    }
}

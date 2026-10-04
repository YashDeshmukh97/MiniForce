package com.miniforce.judge.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class DockerRunner {
    private static final Logger log = LoggerFactory.getLogger(DockerRunner.class);

    record StreamOutput(String content, boolean truncated) {}

    public RunnerResult run(ContainerSpec spec) {
        List<String> cmd = new ArrayList<>();
        cmd.add("docker");
        cmd.add("run");
        cmd.add("--rm"); // Clean up container on exit
        cmd.add("-i"); // Keep STDIN open even if not attached
        cmd.add("--name");
        cmd.add(spec.containerName());
        cmd.add("--network");
        cmd.add("none");
        cmd.add("--read-only");
        cmd.add("--memory");
        cmd.add(spec.policy().memoryLimitMb() + "m");
        cmd.add("--cpus");
        cmd.add(String.valueOf(spec.policy().cpuLimit()));
        cmd.add("--pids-limit");
        cmd.add(String.valueOf(spec.policy().pidsLimit()));
        cmd.add("--user");
        cmd.add("1000:1000");

        if (spec.env() != null) {
            spec.env().forEach((k, v) -> {
                cmd.add("-e");
                cmd.add(k + "=" + v);
            });
        }

        cmd.add("--tmpfs");
        cmd.add("/tmp:rw,noexec,nosuid,size=50m");
        cmd.add("-v");
        cmd.add(spec.hostWorkspacePath() + ":" + spec.containerWorkspacePath() + ":rw");
        cmd.add("-w");
        cmd.add(spec.containerWorkspacePath());
        cmd.add(spec.imageName());
        cmd.addAll(spec.command());

        log.info("Starting docker container: {} for spec: {}", spec.containerName(), spec.imageName());

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = null;
        try {
            long startTime = System.currentTimeMillis();
            process = pb.start();
            
            final Process activeProcess = process; // Effectively final for lambdas

            // Inject stdin if present
            if (spec.stdin() != null && spec.stdin().length > 0) {
                try (OutputStream os = activeProcess.getOutputStream()) {
                    os.write(spec.stdin());
                    os.flush();
                }
            } else {
                activeProcess.getOutputStream().close();
            }

            int maxBytes = spec.policy().maxOutputBytes();

            // Start reading streams concurrently to prevent pipe deadlock
            CompletableFuture<StreamOutput> stdoutFuture = CompletableFuture.supplyAsync(() -> readStream(activeProcess.getInputStream(), maxBytes));
            CompletableFuture<StreamOutput> stderrFuture = CompletableFuture.supplyAsync(() -> readStream(activeProcess.getErrorStream(), maxBytes));

            boolean finished = process.waitFor(spec.timeoutSeconds(), TimeUnit.SECONDS);
            long duration = System.currentTimeMillis() - startTime;

            if (!finished) {
                log.warn("Container {} timed out after {} ms", spec.containerName(), duration);
                cleanupContainer(spec.containerName());
                process.destroyForcibly();
                return new RunnerResult(-1, "", "", true, null, duration, false);
            }

            // Wait briefly for streams to finish reading after process exit
            StreamOutput stdout = stdoutFuture.get(1, TimeUnit.SECONDS);
            StreamOutput stderr = stderrFuture.get(1, TimeUnit.SECONDS);

            log.info("Container {} finished with exit code {} in {} ms", spec.containerName(), process.exitValue(), duration);
            boolean outputTruncated = stdout.truncated() || stderr.truncated();
            return new RunnerResult(process.exitValue(), stdout.content(), stderr.content(), false, null, duration, outputTruncated);

        } catch (Exception e) {
            log.error("System error executing docker container {}", spec.containerName(), e);
            cleanupContainer(spec.containerName());
            return new RunnerResult(-1, "", "", false, e.getMessage(), 0, false);
        } finally {
            if (process != null) {
                process.destroyForcibly();
            }
        }
    }

    private void cleanupContainer(String containerName) {
        try {
            log.info("Force cleaning up container {}", containerName);
            ProcessBuilder cleanupPb = new ProcessBuilder("docker", "rm", "-f", containerName);
            Process p = cleanupPb.start();
            p.waitFor(5, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            // Best effort
        }
    }

    private StreamOutput readStream(InputStream is, int maxBytes) {
        try {
            byte[] buffer = new byte[8192];
            int totalRead = 0;
            int read;
            boolean truncated = false;
            StringBuilder sb = new StringBuilder();
            while ((read = is.read(buffer)) != -1) {
                if (totalRead + read > maxBytes) {
                    if (!truncated) {
                        int allowed = maxBytes - totalRead;
                        if (allowed > 0) {
                            sb.append(new String(buffer, 0, allowed, StandardCharsets.UTF_8));
                        }
                        sb.append("\n[OUTPUT TRUNCATED]");
                        truncated = true;
                    }
                    // Continue reading but discard to prevent pipe deadlock
                } else {
                    sb.append(new String(buffer, 0, read, StandardCharsets.UTF_8));
                }
                totalRead += read;
            }
            return new StreamOutput(sb.toString(), truncated);
        } catch (Exception e) {
            return new StreamOutput("[Error reading stream: " + e.getMessage() + "]", false);
        }
    }
}
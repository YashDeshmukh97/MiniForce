package com.miniforce.judge.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.stream.Stream;

@Component
public class WorkspaceManager {
    private static final Logger log = LoggerFactory.getLogger(WorkspaceManager.class);

    public Path createWorkspace() throws IOException {
        Path tempDir = Files.createTempDirectory("miniforce_exec_");
        // Ensure it is writable by anyone so Docker user 1000 can write to it if needed
        File f = tempDir.toFile();
        f.setReadable(true, false);
        f.setWritable(true, false);
        f.setExecutable(true, false);
        return tempDir;
    }

    public Path writeSourceFile(Path workspace, String filename, String source) throws IOException {
        Path filePath = workspace.resolve(filename);
        Files.writeString(filePath, source, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        // Ensure file is readable by Docker user 1000
        File f = filePath.toFile();
        f.setReadable(true, false);
        f.setWritable(true, false);
        return filePath;
    }

    public void cleanup(Path workspace) {
        if (workspace == null || !Files.exists(workspace)) return;
        
        try (Stream<Path> walk = Files.walk(workspace)) {
            walk.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
            log.info("Cleaned up workspace {}", workspace);
        } catch (IOException e) {
            log.warn("Failed to fully clean up workspace {}", workspace, e);
        }
    }
}
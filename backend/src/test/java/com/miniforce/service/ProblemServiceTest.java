package com.miniforce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.miniforce.exception.NotFoundException;
import com.miniforce.model.Difficulty;
import com.miniforce.model.Problem;
import com.miniforce.repository.ProblemRepository;
import com.miniforce.repository.SubmissionRepository;
import com.miniforce.repository.TestCaseRepository;
import com.miniforce.repository.TopicRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProblemServiceTest {
    @Test
    void exactSlugLookupReturnsProblem100AndRejectsUnknownSlug() {
        ProblemRepository repository = mock(ProblemRepository.class);
        Problem problem = new Problem();
        problem.setSlug("inversion-network-analyst");
        problem.setTitle("Inversion Network Analyst");
        problem.setDifficulty(Difficulty.HARD);
        problem.setDescription("description");
        problem.setStarterCode("class Solution {}");
        problem.setConstraintsText("constraint");
        problem.setIsPublic(true);
        when(repository.findBySlug("inversion-network-analyst")).thenReturn(Optional.of(problem));
        when(repository.findBySlug("partial-inversion")).thenReturn(Optional.empty());
        ProblemService service = new ProblemService(repository, mock(TopicRepository.class), mock(TestCaseRepository.class), mock(SubmissionRepository.class));

        assertEquals("inversion-network-analyst", service.getBySlug("inversion-network-analyst", null).slug());
        assertThrows(NotFoundException.class, () -> service.getBySlug("partial-inversion", null));
    }

    @Test
    void problemSearchQueryOrdersByStablePrimaryKey() throws Exception {
        String source = Files.readString(projectRoot().resolve("backend/src/main/java/com/miniforce/repository/ProblemRepository.java"));
        assertTrue(source.contains("order by p.id asc"));
    }

    private Path projectRoot() {
        Path current = Path.of("").toAbsolutePath();
        while (current != null) {
            if (Files.isRegularFile(current.resolve("database/miniforce.sql"))) return current;
            current = current.getParent();
        }
        throw new IllegalStateException("Could not locate repository root");
    }
}

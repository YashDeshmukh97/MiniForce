package com.miniforce.service;

import static org.junit.jupiter.api.Assertions.*;

import com.miniforce.dto.ProblemDtos.ProblemDetail;
import com.miniforce.dto.ProblemDtos.ProblemSummary;
import com.miniforce.exception.NotFoundException;
import com.miniforce.model.Difficulty;
import com.miniforce.model.Role;
import com.miniforce.model.User;
import com.miniforce.model.Problem;
import com.miniforce.repository.ProblemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PublicCatalogIntegrationTest {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private com.miniforce.repository.SubmissionRepository submissionRepository;

    @Autowired
    private com.miniforce.repository.TestCaseRepository testCaseRepository;

    @Autowired
    private com.miniforce.repository.ContestProblemRepository contestProblemRepository;

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        submissionRepository.deleteAll();
        testCaseRepository.deleteAll();
        contestProblemRepository.deleteAll();
        problemRepository.deleteAll();

        for (int i = 1; i <= 100; i++) {
            Problem p = new Problem();
            p.setSlug("problem-" + i);
            p.setTitle("Problem " + i);
            p.setDifficulty(Difficulty.EASY);
            p.setDescription("Desc");
            p.setStarterCode("");
            p.setConstraintsText("");
            p.setAcceptanceRate(50.0);
            p.setIsPublic(i <= 50);
            problemRepository.save(p);
        }
    }

    @Test
    void testPublicListReturnsExactly50AndNoHidden() {
        List<ProblemSummary> list = problemService.list(null, null, null, null);
        assertEquals(50, list.size(), "Public list should return exactly 50 problems");
        for (ProblemSummary p : list) {
            int num = Integer.parseInt(p.slug().split("-")[1]);
            assertTrue(num <= 50, "Hidden problem found in list");
        }
    }

    @Test
    void testSearchCannotRevealHidden() {
        List<ProblemSummary> list = problemService.list("a", null, null, null);
        for (ProblemSummary p : list) {
            int num = Integer.parseInt(p.slug().split("-")[1]);
            assertTrue(num <= 50, "Hidden problem found in search");
        }
    }

    @Test
    void testDifficultyFilterCannotRevealHidden() {
        List<ProblemSummary> list = problemService.list(null, Difficulty.EASY, null, null);
        for (ProblemSummary p : list) {
            int num = Integer.parseInt(p.slug().split("-")[1]);
            assertTrue(num <= 50, "Hidden problem found in difficulty filter");
        }
    }

    @Test
    void testDirectHiddenProblemAccessBlocked() {
        assertThrows(NotFoundException.class, () -> problemService.getBySlug("problem-51", null));
    }

    @Test
    void testPublicProblemDetailWorks() {
        ProblemDetail detail = problemService.getBySlug("problem-1", null);
        assertEquals("problem-1", detail.slug());
    }

    @Test
    void testAdminBehaviorRemainsCorrect() {
        User admin = new User();
        admin.setRole(Role.ADMIN);
        List<ProblemSummary> list = problemService.list(null, null, null, admin);
        assertEquals(100, list.size(), "Admin should see all 100 problems");
        
        ProblemDetail detail = problemService.getBySlug("problem-51", admin);
        assertEquals("problem-51", detail.slug(), "Admin should be able to access hidden problem");
    }

    @Test
    void testFirst8DisplayOrder() {
        List<ProblemSummary> list = problemService.list(null, null, null, null);
        assertTrue(list.size() >= 3);
        assertEquals("problem-1", list.get(0).slug());
        assertEquals("problem-2", list.get(1).slug());
    }
}

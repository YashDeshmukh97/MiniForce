package com.miniforce.repository;

import com.miniforce.model.ProblemHint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProblemHintRepository extends JpaRepository<ProblemHint, Long> {
    List<ProblemHint> findByProblemIdOrderByHintLevelAsc(Long problemId);
    boolean existsByProblemIdAndHintLevel(Long problemId, int hintLevel);
}

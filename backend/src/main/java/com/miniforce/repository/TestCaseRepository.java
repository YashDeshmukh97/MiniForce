package com.miniforce.repository;
import com.miniforce.model.TestCase; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface TestCaseRepository extends JpaRepository<TestCase,Long> { List<TestCase> findByProblemId(Long problemId); long countByProblemId(Long problemId); }

package com.miniforce.repository;
import com.miniforce.model.ContestProblem; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ContestProblemRepository extends JpaRepository<ContestProblem,Long> { List<ContestProblem> findByContestId(Long contestId); }

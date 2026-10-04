package com.miniforce.repository;
import com.miniforce.model.Contest; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ContestRepository extends JpaRepository<Contest,Long> { List<Contest> findAllByOrderByStartsAtAsc(); }

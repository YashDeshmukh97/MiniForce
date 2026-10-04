package com.miniforce.repository;
import com.miniforce.model.Discussion; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface DiscussionRepository extends JpaRepository<Discussion,Long> { List<Discussion> findByDeletedFalseOrderByCreatedAtDesc(); long countByDeletedFalse(); }

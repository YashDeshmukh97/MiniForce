package com.miniforce.repository;
import com.miniforce.model.Topic; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface TopicRepository extends JpaRepository<Topic,Long> { Optional<Topic> findByNameIgnoreCase(String name); }

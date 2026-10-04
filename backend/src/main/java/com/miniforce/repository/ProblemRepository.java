package com.miniforce.repository;
import com.miniforce.model.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*;
public interface ProblemRepository extends JpaRepository<Problem,Long> {
 @Query("select distinct p from Problem p left join fetch p.topics t where (:search is null or lower(p.title) like lower(concat('%',:search,'%')) or lower(t.name) like lower(concat('%',:search,'%'))) and (:difficulty is null or p.difficulty=:difficulty) and (:topic is null or lower(t.name)=lower(:topic)) and (:isPublic is null or p.isPublic = :isPublic) order by p.id asc") List<Problem> search(@Param("search") String search,@Param("difficulty") Difficulty difficulty,@Param("topic") String topic,@Param("isPublic") Boolean isPublic);
 Optional<Problem> findBySlug(String slug);
}

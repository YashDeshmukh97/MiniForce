package com.miniforce.repository;
import com.miniforce.model.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.time.*; import java.util.*;
public interface SubmissionRepository extends JpaRepository<Submission,Long> { 
  List<Submission> findTop20ByUserIdOrderBySubmittedAtDesc(Long userId); 
  List<Submission> findByProblemIdOrderBySubmittedAtDesc(Long problemId); 
  List<Submission> findByUserIdOrderBySubmittedAtDesc(Long userId); 
  boolean existsByUserIdAndProblemIdAndStatus(Long userId,Long problemId,SubmissionStatus status); 
  boolean existsByUserIdAndProblemIdAndStatusAndIdNot(Long userId,Long problemId,SubmissionStatus status,Long id); 
  boolean existsByUserIdAndProblemIdAndStatusAndContestId(Long userId,Long problemId,SubmissionStatus status,Long contestId); 
  boolean existsByUserIdAndProblemIdAndStatusAndContestIdAndIdNot(Long userId,Long problemId,SubmissionStatus status,Long contestId,Long id); 
  Optional<Submission> findFirstByUserIdAndStatusOrderBySubmittedAtDesc(Long userId,SubmissionStatus status); 
  Optional<Submission> findFirstByUserIdAndStatusAndIdNotOrderBySubmittedAtDesc(Long userId,SubmissionStatus status,Long id); 
  @Query("select distinct s.problem.id from Submission s where s.user.id=:userId and s.status = 'ACCEPTED'") 
  Set<Long> findSolvedProblemIdsByUserId(@Param("userId") Long userId); 
  @Query(value="select DATE(submitted_at) as d, count(*) as c from submissions where user_id=:userId and status = 'ACCEPTED' and submitted_at>=:after group by DATE(submitted_at)",nativeQuery=true) 
  List<Object[]> activity(@Param("userId") Long userId,@Param("after") Instant after); 

  @Query(value = "SELECT * FROM submissions WHERE status = 'QUEUED' OR (status IN ('COMPILING', 'RUNNING') AND lease_expires_at < :now) ORDER BY submitted_at ASC LIMIT 1", nativeQuery = true)
  Optional<Submission> findNextJobToClaim(@Param("now") Instant now);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE Submission s SET s.status = :newStatus, s.claimedBy = :workerId, s.claimedAt = :now, s.leaseExpiresAt = :expiresAt, s.retryCount = s.retryCount + 1 WHERE s.id = :id AND (s.status = com.miniforce.model.SubmissionStatus.QUEUED OR s.leaseExpiresAt < :now)")
  int claimJob(@Param("id") Long id, @Param("newStatus") SubmissionStatus newStatus, @Param("workerId") String workerId, @Param("now") Instant now, @Param("expiresAt") Instant expiresAt);
}

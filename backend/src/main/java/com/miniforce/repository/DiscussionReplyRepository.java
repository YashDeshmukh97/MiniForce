package com.miniforce.repository;
import com.miniforce.model.DiscussionReply; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface DiscussionReplyRepository extends JpaRepository<DiscussionReply,Long> { List<DiscussionReply> findByDiscussionIdOrderByCreatedAtAsc(Long discussionId); long countByDiscussionId(Long discussionId); }

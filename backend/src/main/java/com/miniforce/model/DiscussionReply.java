package com.miniforce.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="discussion_replies",indexes={@Index(name="idx_reply_discussion",columnList="discussion_id")}) public class DiscussionReply {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="discussion_id") private Discussion discussion; @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="author_id") private User author; @Column(nullable=false,columnDefinition="TEXT") private String body; @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
 public Long getId(){return id;} public Discussion getDiscussion(){return discussion;} public void setDiscussion(Discussion v){discussion=v;} public User getAuthor(){return author;} public void setAuthor(User v){author=v;} public String getBody(){return body;} public void setBody(String v){body=v;} public Instant getCreatedAt(){return createdAt;}
}

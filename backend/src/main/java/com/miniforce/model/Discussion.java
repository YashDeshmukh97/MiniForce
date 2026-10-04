package com.miniforce.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="discussions",indexes={@Index(name="idx_discussion_created",columnList="created_at")}) public class Discussion {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="author_id") private User author; @Column(nullable=false,length=220) private String title; @Column(nullable=false,columnDefinition="TEXT") private String body; @Column(nullable=false) private boolean deleted; @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
 public Long getId(){return id;} public User getAuthor(){return author;} public void setAuthor(User v){author=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getBody(){return body;} public void setBody(String v){body=v;} public boolean isDeleted(){return deleted;} public void setDeleted(boolean v){deleted=v;} public Instant getCreatedAt(){return createdAt;}
}

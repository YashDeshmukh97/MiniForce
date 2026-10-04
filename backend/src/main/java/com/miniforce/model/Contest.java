package com.miniforce.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="contests", indexes={@Index(name="idx_contest_status",columnList="status")}) public class Contest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,length=180) private String title; @Column(columnDefinition="TEXT") private String description;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) private ContestStatus status; @Column(name="starts_at",nullable=false) private Instant startsAt; @Column(name="ends_at",nullable=false) private Instant endsAt; @Column(nullable=false) private boolean rated;
 public Long getId(){return id;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public ContestStatus getStatus(){return status;} public void setStatus(ContestStatus v){status=v;} public Instant getStartsAt(){return startsAt;} public void setStartsAt(Instant v){startsAt=v;} public Instant getEndsAt(){return endsAt;} public void setEndsAt(Instant v){endsAt=v;} public boolean isRated(){return rated;} public void setRated(boolean v){rated=v;}
}

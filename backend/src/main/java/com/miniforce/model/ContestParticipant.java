package com.miniforce.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="contest_participants",uniqueConstraints=@UniqueConstraint(columnNames={"contest_id","user_id"})) public class ContestParticipant {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false) @JoinColumn(name="contest_id") private Contest contest; @ManyToOne(optional=false) @JoinColumn(name="user_id") private User user; @Column(name="joined_at",nullable=false) private Instant joinedAt=Instant.now(); @Column(nullable=false) private int score;
 public Long getId(){return id;} public Contest getContest(){return contest;} public void setContest(Contest v){contest=v;} public User getUser(){return user;} public void setUser(User v){user=v;} public Instant getJoinedAt(){return joinedAt;} public int getScore(){return score;} public void setScore(int v){score=v;}
}

package com.miniforce.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "users", indexes = {@Index(name="idx_users_rating", columnList="rating")})
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true, length=40) private String username;
  @Column(nullable=false, unique=true, length=160) private String email;
  @Column(name="full_name", nullable=false, length=100) private String fullName;
  @Column(name="password_hash", nullable=false, length=100) private String passwordHash;
  @Enumerated(EnumType.STRING) @Column(nullable=false, length=10) private Role role = Role.USER;
  @Column(length=400) private String bio = "";
  @Column(nullable=false) private int rating = 1200;
  @Column(name="solved_count", nullable=false) private int solvedCount;
  @Column(nullable=false) private int streak;
  @Column(name="joined_at", nullable=false, updatable=false) private Instant joinedAt = Instant.now();
  @Column(name="avatar_key", nullable=false, length=50) private String avatarKey = "avatar_default";
  
  public Long getId(){return id;} public String getUsername(){return username;} public void setUsername(String v){username=v;}
  public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
  public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;} public Role getRole(){return role;} public void setRole(Role v){role=v;}
  public String getBio(){return bio;} public void setBio(String v){bio=v;} public int getRating(){return rating;} public void setRating(int v){rating=v;}
  public int getSolvedCount(){return solvedCount;} public void setSolvedCount(int v){solvedCount=v;} public int getStreak(){return streak;} public void setStreak(int v){streak=v;} public Instant getJoinedAt(){return joinedAt;}
  public String getAvatarKey(){return avatarKey;} public void setAvatarKey(String v){avatarKey=v;}
}

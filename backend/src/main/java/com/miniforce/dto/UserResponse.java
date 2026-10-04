package com.miniforce.dto;
import com.miniforce.model.*; import java.time.Instant; import java.util.*;
public record UserResponse(Long id,String username,String fullName,Role role,String bio,int rating,int solvedCount,int streak,Instant joinedAt,String avatarKey) { public static UserResponse from(User u){return new UserResponse(u.getId(),u.getUsername(),u.getFullName(),u.getRole(),u.getBio(),u.getRating(),u.getSolvedCount(),u.getStreak(),u.getJoinedAt(),u.getAvatarKey());} }

package com.miniforce.dto;
import com.miniforce.model.*;
public final class AdminDtos {
    private AdminDtos() {}
    public record AdminDashboardResponse(long users,long problems,long submissions,long discussions,long contests) {}
    public record AdminUserResponse(Long id,String username,String email,String fullName,Role role) {
        public static AdminUserResponse from(User u) { return new AdminUserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getFullName(), u.getRole()); }
    }
}

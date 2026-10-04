package com.miniforce.dto;
import jakarta.validation.constraints.*;
public final class AuthDtos { private AuthDtos(){}
 public record RegisterRequest(@NotBlank @Size(max=100) String fullName,@NotBlank @Pattern(regexp="^[A-Za-z0-9_]{3,40}$",message="username must be 3-40 letters, numbers, or underscores") String username,@NotBlank @Email @Size(max=160) String email,@NotBlank @Size(min=8,max=100) String password) {}
 public record LoginRequest(@NotBlank String identity,@NotBlank String password) {}
 public record AuthResponse(String token,UserResponse user) {}
}

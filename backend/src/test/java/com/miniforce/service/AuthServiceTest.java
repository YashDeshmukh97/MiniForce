package com.miniforce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.miniforce.dto.AuthDtos.LoginRequest;
import com.miniforce.exception.UnauthorizedException;
import com.miniforce.model.User;
import com.miniforce.repository.UserRepository;
import com.miniforce.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {
    @Test
    void validUsernameLoginReturnsATokenAndUser() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwt = mock(JwtService.class);
        User user = new User();
        user.setUsername("coder");
        user.setEmail("coder@example.test");
        user.setFullName("Coder Test");
        user.setPasswordHash("hash");
        when(users.findByUsernameIgnoreCase("coder")).thenReturn(Optional.of(user));
        when(encoder.matches("password", "hash")).thenReturn(true);
        when(jwt.generate(user)).thenReturn("signed-token");

        var response = new AuthService(users, encoder, jwt).login(new LoginRequest("coder", "password"));

        assertEquals("signed-token", response.token());
        assertEquals("coder", response.user().username());
    }

    @Test
    void invalidLoginDoesNotRevealWhetherTheUserExists() {
        UserRepository users = mock(UserRepository.class);
        when(users.findByEmailIgnoreCase("unknown@example.test")).thenReturn(Optional.empty());

        var failure = assertThrows(UnauthorizedException.class, () -> new AuthService(users, mock(PasswordEncoder.class), mock(JwtService.class))
                .login(new LoginRequest("unknown@example.test", "password")));

        assertEquals("Invalid email/username or password", failure.getMessage());
    }
}

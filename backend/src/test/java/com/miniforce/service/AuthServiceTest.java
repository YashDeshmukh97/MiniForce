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

    private static final String DEV_SEED_BCRYPT_HASH = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

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

    @Test
    void productionRejectsLoginWithUnrotatedDevelopmentSeedHash() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwt = mock(JwtService.class);

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@miniforce.dev");
        admin.setPasswordHash(DEV_SEED_BCRYPT_HASH);
        when(users.findByUsernameIgnoreCase("admin")).thenReturn(Optional.of(admin));

        AuthService authService = new AuthService(users, encoder, jwt, true, null);

        var failure = assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("admin", "password")));

        assertTrue(failure.getMessage().contains("Development default credentials are disabled"));
        verify(users, never()).save(any());
    }

    @Test
    void productionAllowsAdminLoginWithConfiguredBootstrapPasswordAndRotatesHash() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwt = mock(JwtService.class);

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@miniforce.dev");
        admin.setPasswordHash(DEV_SEED_BCRYPT_HASH);
        when(users.findByUsernameIgnoreCase("admin")).thenReturn(Optional.of(admin));
        when(encoder.encode("ProductionAdminSecret999!")).thenReturn("new-salted-bcrypt-hash");
        when(jwt.generate(admin)).thenReturn("admin-token");

        AuthService authService = new AuthService(users, encoder, jwt, true, "ProductionAdminSecret999!");

        var response = authService.login(new LoginRequest("admin", "ProductionAdminSecret999!"));

        assertNotNull(response);
        assertEquals("admin-token", response.token());
        assertEquals("new-salted-bcrypt-hash", admin.getPasswordHash());
        verify(users, times(1)).save(admin);
    }

    @Test
    void productionRejectsAdminLoginWhenBootstrapPasswordMismatch() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwt = mock(JwtService.class);

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@miniforce.dev");
        admin.setPasswordHash(DEV_SEED_BCRYPT_HASH);
        when(users.findByUsernameIgnoreCase("admin")).thenReturn(Optional.of(admin));

        AuthService authService = new AuthService(users, encoder, jwt, true, "ProductionAdminSecret999!");

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("admin", "WrongPassword!")));

        assertEquals(DEV_SEED_BCRYPT_HASH, admin.getPasswordHash());
        verify(users, never()).save(any());
    }

    @Test
    void developmentAllowsLoginWithSeedHashWhenRotationDisabled() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwt = mock(JwtService.class);

        User user = new User();
        user.setUsername("demo");
        user.setEmail("demo@miniforce.dev");
        user.setPasswordHash(DEV_SEED_BCRYPT_HASH);
        when(users.findByUsernameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(encoder.matches("password", DEV_SEED_BCRYPT_HASH)).thenReturn(true);
        when(jwt.generate(user)).thenReturn("demo-token");

        AuthService authService = new AuthService(users, encoder, jwt, false, null);

        var response = authService.login(new LoginRequest("demo", "password"));
        assertEquals("demo-token", response.token());
        verify(users, never()).save(any());
    }
}

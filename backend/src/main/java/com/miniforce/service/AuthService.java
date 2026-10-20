package com.miniforce.service;

import com.miniforce.dto.*;
import com.miniforce.dto.AuthDtos.*;
import com.miniforce.exception.*;
import com.miniforce.model.*;
import com.miniforce.repository.UserRepository;
import com.miniforce.security.JwtService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String DEV_SEED_BCRYPT_HASH = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final boolean enforceCredentialRotation;
    private final String adminBootstrapPassword;

    @Autowired
    public AuthService(
            UserRepository u,
            PasswordEncoder e,
            JwtService j,
            @Value("${miniforce.auth.enforce-credential-rotation:false}") boolean enforceCredentialRotation,
            @Value("${miniforce.admin.initial-password:}") String adminBootstrapPassword) {
        this.users = u;
        this.encoder = e;
        this.jwt = j;
        this.enforceCredentialRotation = enforceCredentialRotation;
        this.adminBootstrapPassword = (adminBootstrapPassword != null && !adminBootstrapPassword.trim().isEmpty())
                ? adminBootstrapPassword.trim()
                : null;
    }

    public AuthService(UserRepository u, PasswordEncoder e, JwtService j) {
        this(u, e, j, false, null);
    }

    @Transactional
    public AuthResponse register(RegisterRequest r) {
        String username = r.username().trim().toLowerCase();
        String email = r.email().trim().toLowerCase();
        if (users.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("Username is already taken");
        }
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email is already registered");
        }
        User u = new User();
        u.setFullName(r.fullName().trim());
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(r.password()));
        users.save(u);
        return new AuthResponse(jwt.generate(u), UserResponse.from(u));
    }

    @Transactional
    public AuthResponse login(LoginRequest r) {
        String identity = r.identity().trim();
        User u = identity.contains("@")
                ? users.findByEmailIgnoreCase(identity)
                        .orElseThrow(() -> new UnauthorizedException("Invalid email/username or password"))
                : users.findByUsernameIgnoreCase(identity)
                        .orElseThrow(() -> new UnauthorizedException("Invalid email/username or password"));

        boolean isDevSeedHash = DEV_SEED_BCRYPT_HASH.equals(u.getPasswordHash());

        if (enforceCredentialRotation && isDevSeedHash) {
            // Check if admin bootstrap password is provided via environment
            if ("admin".equalsIgnoreCase(u.getUsername()) && adminBootstrapPassword != null) {
                byte[] expected = adminBootstrapPassword.getBytes(StandardCharsets.UTF_8);
                byte[] actual = r.password().getBytes(StandardCharsets.UTF_8);
                if (MessageDigest.isEqual(expected, actual)) {
                    // Safe rotation: update password hash to the administrator's chosen password
                    u.setPasswordHash(encoder.encode(adminBootstrapPassword));
                    users.save(u);
                    log.info("Admin credentials successfully rotated via environment configuration.");
                    return new AuthResponse(jwt.generate(u), UserResponse.from(u));
                }
            }
            log.warn("Blocked login attempt with unrotated development seed credentials.");
            throw new UnauthorizedException("Development default credentials are disabled in this environment. Credential rotation required.");
        }

        if (!encoder.matches(r.password(), u.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email/username or password");
        }
        return new AuthResponse(jwt.generate(u), UserResponse.from(u));
    }

    @Transactional(readOnly = true)
    public UserResponse me(User u) {
        User fresh = users.findById(u.getId()).orElse(u);
        return UserResponse.from(fresh);
    }
}

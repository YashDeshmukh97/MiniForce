package com.miniforce.service;

import com.miniforce.dto.UserDtos.UpdateAvatarRequest;
import com.miniforce.model.User;
import com.miniforce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class D13AvatarTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void defaultAvatarIsAssigned() {
        User u = new User();
        u.setUsername("avataruser1");
        u.setEmail("av1@test.com");
        u.setFullName("Avatar User");
        u.setPasswordHash("hash");
        userRepository.saveAndFlush(u);
        
        assertEquals("avatar_default", u.getAvatarKey());
    }
    
    @Test
    public void canUpdateToValidAvatar() {
        User u = new User();
        u.setUsername("avataruser2");
        u.setEmail("av2@test.com");
        u.setFullName("Avatar User");
        u.setPasswordHash("hash");
        userRepository.saveAndFlush(u);
        
        var resp = userService.updateAvatar(u, new UpdateAvatarRequest("fox_01"));
        assertEquals("fox_01", resp.avatarKey());
        assertEquals("fox_01", u.getAvatarKey());
        assertEquals("fox_01", userRepository.findById(u.getId()).orElseThrow().getAvatarKey());
    }
    
    @Test
    public void throwsOnInvalidAvatar() {
        User u = new User();
        u.setUsername("avataruser3");
        u.setEmail("av3@test.com");
        u.setFullName("Avatar User");
        u.setPasswordHash("hash");
        userRepository.saveAndFlush(u);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateAvatar(u, new UpdateAvatarRequest("invalid_avatar_123"));
        });
    }
}

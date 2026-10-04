package com.miniforce.repository;
import com.miniforce.model.User; import org.springframework.data.jpa.repository.*; import java.util.*;
public interface UserRepository extends JpaRepository<User,Long> { Optional<User> findByUsernameIgnoreCase(String username); Optional<User> findByEmailIgnoreCase(String email); boolean existsByUsernameIgnoreCase(String username); boolean existsByEmailIgnoreCase(String email); List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username,String email); }

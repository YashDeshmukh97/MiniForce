package com.miniforce.security;
import com.miniforce.model.User; import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service; import javax.crypto.SecretKey; import java.nio.charset.StandardCharsets; import java.time.*; import java.util.*;
@Service public class JwtService {
 private final SecretKey key; private final long expirationHours;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-hours}") long expirationHours){ if(secret.length()<32) throw new IllegalArgumentException("MINIFORCE_JWT_SECRET must be at least 32 characters"); this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.expirationHours=expirationHours; }
 public String generate(User user){Instant now=Instant.now(); return Jwts.builder().subject(user.getId().toString()).claim("role",user.getRole().name()).issuedAt(Date.from(now)).expiration(Date.from(now.plus(Duration.ofHours(expirationHours)))).signWith(key).compact();}
 public Long userId(String token){return Long.valueOf(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject());}
}

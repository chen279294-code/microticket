package com.example.microticket.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expireSeconds:86400}")
    private long expireSeconds;

    private Key key;

    @PostConstruct
    public void init() {
        // jjwt requires key >= 256 bits for HS256; pad/trim to be safe
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            for (int i = bytes.length; i < 32; i++) padded[i] = (byte) '0';
            bytes = padded;
        }
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(Long userId, String username, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expireSeconds);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("username", username)
                .claim("role", role)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }
}

package com.example.Backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Access token (15 minutes)
    public String generateAccessToken(String email, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        long expiry = now + 1000 * 60 * 15; // 15 mins
        return buildToken(email, claims, now, expiry);
    }

    // Refresh token (30 days)
    public String generateRefreshToken(String email) {
        long now = System.currentTimeMillis();
        long expiry = now + 1000L * 60 * 60 * 24 * 30; // 30 days
        return buildToken(email, Map.of(), now, expiry);
    }

    // Shared method for token building
    private String buildToken(String subject, Map<String, Object> claims, long issuedAt, long expiry) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(issuedAt))
                .expiration(new Date(expiry))
                .signWith(key)
                .compact();
    }

    // Extract email (subject) from token
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Validate token (expiration and signature)
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
    
    public String extractSubject(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
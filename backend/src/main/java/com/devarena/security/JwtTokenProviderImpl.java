package com.devarena.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Production-ready JJWT implementation of the JwtTokenProvider contract.
 */
@Component
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProviderImpl.class);

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProviderImpl(
            @Value("${devarena.security.jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}") String secret,
            @Value("${devarena.security.jwt.expiration-ms:86400000}") long accessExpirationMs,
            @Value("${devarena.security.jwt.refresh-expiration-ms:604800000}") long refreshExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    public String generateAccessToken(String username, Set<UserRole> roles) {
        Instant now = Instant.now();
        List<String> roleStrings = roles.stream().map(UserRole::name).toList();

        return Jwts.builder()
                .subject(username)
                .claim("roles", roleStrings)
                .claim("type", "ACCESS")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessExpirationMs)))
                .signWith(key)
                .compact();
    }

    @Override
    public String generateRefreshToken(String username) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .claim("type", "REFRESH")
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(refreshExpirationMs)))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) return false;
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<UserRole> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<String> rolesList = claims.get("roles", List.class);
        if (rolesList == null) {
            return Set.of(UserRole.ROLE_USER);
        }

        return rolesList.stream()
                .map(UserRole::fromString)
                .collect(Collectors.toSet());
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }
}

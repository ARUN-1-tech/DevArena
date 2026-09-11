package com.devarena.security;

import java.util.Set;

/**
 * Extension point contract for JWT token generation, parsing, and validation.
 * Full token signing and verification will be implemented in Module 03 (Authentication).
 */
public interface JwtTokenProvider {

    /**
     * Generates a short-lived access token for the authenticated user.
     *
     * @param username user handle
     * @param roles assigned user roles
     * @return signed JWT access token string
     */
    String generateAccessToken(String username, Set<UserRole> roles);

    /**
     * Generates a long-lived refresh token for token rotation.
     *
     * @param username user handle
     * @return signed JWT refresh token string
     */
    String generateRefreshToken(String username);

    /**
     * Validates signature, expiration, and claims of the provided token.
     *
     * @param token JWT string
     * @return true if token is structurally sound and unexpired
     */
    boolean validateToken(String token);

    /**
     * Extracts username from the token claims subject.
     *
     * @param token JWT string
     * @return username handle
     */
    String getUsernameFromToken(String token);

    /**
     * Extracts granted authorities/roles from the token claims.
     *
     * @param token JWT string
     * @return set of user roles
     */
    Set<UserRole> getRolesFromToken(String token);
}

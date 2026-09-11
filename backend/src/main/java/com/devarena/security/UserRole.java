package com.devarena.security;

/**
 * Standard user authorization roles in DevArena.
 */
public enum UserRole {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static UserRole fromString(String role) {
        if (role == null) return ROLE_USER;
        String normalized = role.toUpperCase();
        if (normalized.equals("ADMIN") || normalized.equals("ROLE_ADMIN")) {
            return ROLE_ADMIN;
        }
        return ROLE_USER;
    }
}

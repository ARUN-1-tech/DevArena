package com.devarena.admin.dto;

import com.devarena.security.UserRole;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class AdminPlayerDto {

    private UUID id;
    private String username;
    private String email;
    private Set<UserRole> roles;
    private boolean enabled;
    private boolean accountNonLocked;
    private int xp;
    private int level;
    private int mmr;
    private int riskScore;
    private Instant createdAt;

    public AdminPlayerDto() {}

    public AdminPlayerDto(UUID id, String username, String email, Set<UserRole> roles,
                          boolean enabled, boolean accountNonLocked, int xp, int level,
                          int mmr, int riskScore, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.xp = xp;
        this.level = level;
        this.mmr = mmr;
        this.riskScore = riskScore;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<UserRole> getRoles() { return roles; }
    public void setRoles(Set<UserRole> roles) { this.roles = roles; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isAccountNonLocked() { return accountNonLocked; }
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getMmr() { return mmr; }
    public void setMmr(int mmr) { this.mmr = mmr; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String username;
        private String email;
        private Set<UserRole> roles;
        private boolean enabled;
        private boolean accountNonLocked;
        private int xp;
        private int level;
        private int mmr;
        private int riskScore;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder roles(Set<UserRole> roles) { this.roles = roles; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder accountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; return this; }
        public Builder xp(int xp) { this.xp = xp; return this; }
        public Builder level(int level) { this.level = level; return this; }
        public Builder mmr(int mmr) { this.mmr = mmr; return this; }
        public Builder riskScore(int riskScore) { this.riskScore = riskScore; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public AdminPlayerDto build() {
            return new AdminPlayerDto(id, username, email, roles, enabled, accountNonLocked, xp, level, mmr, riskScore, createdAt);
        }
    }
}

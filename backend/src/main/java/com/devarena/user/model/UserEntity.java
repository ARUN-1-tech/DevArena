package com.devarena.user.model;

import com.devarena.common.model.BaseAuditEntity;
import com.devarena.security.UserRole;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Persistent entity representing a registered DevArena user account.
 */
@Entity
@Table(name = "users")
public class UserEntity extends BaseAuditEntity {

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 32)
    private Set<UserRole> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProfileEntity profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PlayerStatsEntity stats;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PlayerProgressionEntity progression;

    public UserEntity() {}

    public UserEntity(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles.add(UserRole.ROLE_USER);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setUser(this);
        }
    }

    public PlayerStatsEntity getStats() {
        return stats;
    }

    public void setStats(PlayerStatsEntity stats) {
        this.stats = stats;
        if (stats != null) {
            stats.setUser(this);
        }
    }

    public PlayerProgressionEntity getProgression() {
        return progression;
    }

    public void setProgression(PlayerProgressionEntity progression) {
        this.progression = progression;
        if (progression != null) {
            progression.setUser(this);
        }
    }
}

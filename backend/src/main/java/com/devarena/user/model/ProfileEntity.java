package com.devarena.user.model;

import com.devarena.common.model.BaseAuditEntity;
import jakarta.persistence.*;

/**
 * Persistent entity representing a player's public profile and persona.
 */
@Entity
@Table(name = "profiles")
public class ProfileEntity extends BaseAuditEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "avatar", nullable = false, length = 100)
    private String avatar = "avatar-1";

    @Column(name = "bio", length = 255)
    private String bio;

    public ProfileEntity() {}

    public ProfileEntity(UserEntity user, String username, String displayName, String avatar) {
        this.user = user;
        this.username = username;
        this.displayName = displayName;
        this.avatar = avatar != null ? avatar : "avatar-1";
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}

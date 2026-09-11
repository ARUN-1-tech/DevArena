package com.devarena.user.model;

import com.devarena.common.model.BaseAuditEntity;
import jakarta.persistence.*;

/**
 * Persistent entity tracking player XP and leveling progression.
 */
@Entity
@Table(name = "player_progression")
public class PlayerProgressionEntity extends BaseAuditEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "level", nullable = false)
    private int level = 1;

    @Column(name = "current_xp", nullable = false)
    private int currentXp = 0;

    @Column(name = "xp_to_next_level", nullable = false)
    private int xpToNextLevel = 100;

    public PlayerProgressionEntity() {}

    public PlayerProgressionEntity(UserEntity user) {
        this.user = user;
        this.level = 1;
        this.currentXp = 0;
        this.xpToNextLevel = 100;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCurrentXp() {
        return currentXp;
    }

    public void setCurrentXp(int currentXp) {
        this.currentXp = currentXp;
    }

    public int getXpToNextLevel() {
        return xpToNextLevel;
    }

    public void setXpToNextLevel(int xpToNextLevel) {
        this.xpToNextLevel = xpToNextLevel;
    }
}

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

    @Column(name = "total_xp", nullable = false)
    private int totalXp = 0;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    private int longestStreak = 0;

    @Column(name = "last_activity_date")
    private java.time.LocalDate lastActivityDate;

    @Column(name = "challenges_solved", nullable = false)
    private int challengesSolved = 0;

    @Column(name = "quests_completed", nullable = false)
    private int questsCompleted = 0;

    public PlayerProgressionEntity() {}

    public PlayerProgressionEntity(UserEntity user) {
        this.user = user;
        this.level = 1;
        this.currentXp = 0;
        this.xpToNextLevel = 100;
        this.totalXp = 0;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.challengesSolved = 0;
        this.questsCompleted = 0;
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

    public int getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public java.time.LocalDate getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(java.time.LocalDate lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public int getChallengesSolved() {
        return challengesSolved;
    }

    public void setChallengesSolved(int challengesSolved) {
        this.challengesSolved = challengesSolved;
    }

    public int getQuestsCompleted() {
        return questsCompleted;
    }

    public void setQuestsCompleted(int questsCompleted) {
        this.questsCompleted = questsCompleted;
    }
}

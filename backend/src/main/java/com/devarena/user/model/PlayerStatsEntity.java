package com.devarena.user.model;

import com.devarena.common.model.BaseAuditEntity;
import jakarta.persistence.*;

/**
 * Persistent entity storing competitive stats, ELO rating, and battle records.
 */
@Entity
@Table(name = "player_stats")
public class PlayerStatsEntity extends BaseAuditEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "rating", nullable = false)
    private int rating = 1000;

    @Column(name = "wins", nullable = false)
    private int wins = 0;

    @Column(name = "losses", nullable = false)
    private int losses = 0;

    @Column(name = "draws", nullable = false)
    private int draws = 0;

    @Column(name = "win_streak", nullable = false)
    private int winStreak = 0;

    @Column(name = "highest_rating", nullable = false)
    private int highestRating = 1000;

    public PlayerStatsEntity() {}

    public PlayerStatsEntity(UserEntity user) {
        this.user = user;
        this.rating = 1000;
        this.highestRating = 1000;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
        if (rating > this.highestRating) {
            this.highestRating = rating;
        }
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
    }

    public int getHighestRating() {
        return highestRating;
    }

    public void setHighestRating(int highestRating) {
        this.highestRating = highestRating;
    }
}

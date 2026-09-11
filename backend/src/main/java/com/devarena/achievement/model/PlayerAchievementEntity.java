package com.devarena.achievement.model;

import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "player_achievements",
       uniqueConstraints = @UniqueConstraint(name = "uq_player_achievement", columnNames = {"user_id", "achievement_id"}))
public class PlayerAchievementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "achievement_id", nullable = false)
    private AchievementEntity achievement;

    @Column(name = "unlocked_at", nullable = false)
    private Instant unlockedAt = Instant.now();

    @Column(name = "xp_awarded", nullable = false)
    private int xpAwarded;

    public PlayerAchievementEntity() {}

    public PlayerAchievementEntity(UserEntity user, AchievementEntity achievement, int xpAwarded) {
        this.user = user;
        this.achievement = achievement;
        this.xpAwarded = xpAwarded;
        this.unlockedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public AchievementEntity getAchievement() { return achievement; }
    public void setAchievement(AchievementEntity achievement) { this.achievement = achievement; }

    public Instant getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(Instant unlockedAt) { this.unlockedAt = unlockedAt; }

    public int getXpAwarded() { return xpAwarded; }
    public void setXpAwarded(int xpAwarded) { this.xpAwarded = xpAwarded; }
}

package com.devarena.skill.model;

import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "player_skills",
       uniqueConstraints = @UniqueConstraint(name = "uq_player_skill", columnNames = {"user_id", "skill_id"}))
public class PlayerSkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private SkillEntity skill;

    @Column(name = "current_level", nullable = false)
    private int currentLevel = 1;

    @Column(name = "current_xp", nullable = false)
    private int currentXp = 0;

    @Column(name = "mastery_percentage", nullable = false)
    private int masteryPercentage = 20;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public PlayerSkillEntity() {}

    public PlayerSkillEntity(UserEntity user, SkillEntity skill) {
        this.user = user;
        this.skill = skill;
        this.currentLevel = 1;
        this.currentXp = 0;
        this.masteryPercentage = 20;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public SkillEntity getSkill() { return skill; }
    public void setSkill(SkillEntity skill) { this.skill = skill; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public int getCurrentXp() { return currentXp; }
    public void setCurrentXp(int currentXp) { this.currentXp = currentXp; }

    public int getMasteryPercentage() { return masteryPercentage; }
    public void setMasteryPercentage(int masteryPercentage) { this.masteryPercentage = masteryPercentage; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

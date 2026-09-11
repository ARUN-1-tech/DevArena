package com.devarena.achievement.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "achievements")
public class AchievementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 64)
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AchievementCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "requirement_type", nullable = false, length = 64)
    private RequirementType requirementType;

    @Column(name = "requirement_value", nullable = false)
    private int requirementValue;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AchievementRarity rarity;

    @Column(name = "is_secret", nullable = false)
    private boolean isSecret = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public AchievementEntity() {}

    public AchievementEntity(String code, String name, String description, String icon,
                             AchievementCategory category, RequirementType requirementType,
                             int requirementValue, int xpReward, AchievementRarity rarity, boolean isSecret) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.category = category;
        this.requirementType = requirementType;
        this.requirementValue = requirementValue;
        this.xpReward = xpReward;
        this.rarity = rarity;
        this.isSecret = isSecret;
        this.isActive = true;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public AchievementCategory getCategory() { return category; }
    public void setCategory(AchievementCategory category) { this.category = category; }

    public RequirementType getRequirementType() { return requirementType; }
    public void setRequirementType(RequirementType requirementType) { this.requirementType = requirementType; }

    public int getRequirementValue() { return requirementValue; }
    public void setRequirementValue(int requirementValue) { this.requirementValue = requirementValue; }

    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }

    public AchievementRarity getRarity() { return rarity; }
    public void setRarity(AchievementRarity rarity) { this.rarity = rarity; }

    public boolean isSecret() { return isSecret; }
    public void setSecret(boolean secret) { isSecret = secret; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

package com.devarena.challenge.model;

import com.devarena.common.model.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "challenges")
public class ChallengeEntity extends BaseAuditEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 32)
    private ChallengeDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 64)
    private ChallengeCategory category;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward;

    @Column(name = "estimated_minutes", nullable = false)
    private int estimatedMinutes;

    @Column(name = "tags")
    private String tags;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ChallengeStatus status = ChallengeStatus.PUBLISHED;

    public ChallengeEntity() {}

    public ChallengeEntity(String title, String slug, String description, ChallengeDifficulty difficulty,
                           ChallengeCategory category, int xpReward, int estimatedMinutes, String tags) {
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.difficulty = difficulty;
        this.category = category;
        this.xpReward = xpReward;
        this.estimatedMinutes = estimatedMinutes;
        this.tags = tags;
        this.status = ChallengeStatus.PUBLISHED;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ChallengeDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(ChallengeDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public ChallengeCategory getCategory() {
        return category;
    }

    public void setCategory(ChallengeCategory category) {
        this.category = category;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }
}

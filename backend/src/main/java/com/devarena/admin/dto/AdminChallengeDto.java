package com.devarena.admin.dto;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeStatus;

import java.time.Instant;
import java.util.UUID;

public class AdminChallengeDto {

    private UUID id;
    private String title;
    private String slug;
    private String description;
    private ChallengeDifficulty difficulty;
    private ChallengeCategory category;
    private int xpReward;
    private int estimatedMinutes;
    private String tags;
    private ChallengeStatus status;
    private int testCaseCount;
    private Instant createdAt;

    public AdminChallengeDto() {}

    public AdminChallengeDto(UUID id, String title, String slug, String description,
                             ChallengeDifficulty difficulty, ChallengeCategory category,
                             int xpReward, int estimatedMinutes, String tags,
                             ChallengeStatus status, int testCaseCount, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.difficulty = difficulty;
        this.category = category;
        this.xpReward = xpReward;
        this.estimatedMinutes = estimatedMinutes;
        this.tags = tags;
        this.status = status;
        this.testCaseCount = testCaseCount;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ChallengeDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(ChallengeDifficulty difficulty) { this.difficulty = difficulty; }

    public ChallengeCategory getCategory() { return category; }
    public void setCategory(ChallengeCategory category) { this.category = category; }

    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public ChallengeStatus getStatus() { return status; }
    public void setStatus(ChallengeStatus status) { this.status = status; }

    public int getTestCaseCount() { return testCaseCount; }
    public void setTestCaseCount(int testCaseCount) { this.testCaseCount = testCaseCount; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String title;
        private String slug;
        private String description;
        private ChallengeDifficulty difficulty;
        private ChallengeCategory category;
        private int xpReward;
        private int estimatedMinutes;
        private String tags;
        private ChallengeStatus status;
        private int testCaseCount;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder slug(String slug) { this.slug = slug; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder difficulty(ChallengeDifficulty difficulty) { this.difficulty = difficulty; return this; }
        public Builder category(ChallengeCategory category) { this.category = category; return this; }
        public Builder xpReward(int xpReward) { this.xpReward = xpReward; return this; }
        public Builder estimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; return this; }
        public Builder tags(String tags) { this.tags = tags; return this; }
        public Builder status(ChallengeStatus status) { this.status = status; return this; }
        public Builder testCaseCount(int testCaseCount) { this.testCaseCount = testCaseCount; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public AdminChallengeDto build() {
            return new AdminChallengeDto(id, title, slug, description, difficulty, category, xpReward, estimatedMinutes, tags, status, testCaseCount, createdAt);
        }
    }
}

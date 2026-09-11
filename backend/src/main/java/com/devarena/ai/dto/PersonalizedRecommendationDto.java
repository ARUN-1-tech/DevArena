package com.devarena.ai.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PersonalizedRecommendationDto {

    private String weakSkill;
    private String reason;
    private List<RecommendedChallengeDto> recommendedChallenges = new ArrayList<>();

    public PersonalizedRecommendationDto() {}

    public PersonalizedRecommendationDto(String weakSkill, String reason, List<RecommendedChallengeDto> recommendedChallenges) {
        this.weakSkill = weakSkill;
        this.reason = reason;
        this.recommendedChallenges = recommendedChallenges;
    }

    public String getWeakSkill() { return weakSkill; }
    public void setWeakSkill(String weakSkill) { this.weakSkill = weakSkill; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public List<RecommendedChallengeDto> getRecommendedChallenges() { return recommendedChallenges; }
    public void setRecommendedChallenges(List<RecommendedChallengeDto> recommendedChallenges) { this.recommendedChallenges = recommendedChallenges; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String weakSkill;
        private String reason;
        private List<RecommendedChallengeDto> recommendedChallenges = new ArrayList<>();

        public Builder weakSkill(String weakSkill) { this.weakSkill = weakSkill; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder recommendedChallenges(List<RecommendedChallengeDto> recommendedChallenges) { this.recommendedChallenges = recommendedChallenges; return this; }

        public PersonalizedRecommendationDto build() {
            return new PersonalizedRecommendationDto(weakSkill, reason, recommendedChallenges);
        }
    }

    public static class RecommendedChallengeDto {
        private UUID id;
        private String title;
        private String slug;
        private String difficulty;
        private String category;
        private int xpReward;

        public RecommendedChallengeDto() {}

        public RecommendedChallengeDto(UUID id, String title, String slug, String difficulty, String category, int xpReward) {
            this.id = id;
            this.title = title;
            this.slug = slug;
            this.difficulty = difficulty;
            this.category = category;
            this.xpReward = xpReward;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }

        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public int getXpReward() { return xpReward; }
        public void setXpReward(int xpReward) { this.xpReward = xpReward; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private String title;
            private String slug;
            private String difficulty;
            private String category;
            private int xpReward;

            public Builder id(UUID id) { this.id = id; return this; }
            public Builder title(String title) { this.title = title; return this; }
            public Builder slug(String slug) { this.slug = slug; return this; }
            public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
            public Builder category(String category) { this.category = category; return this; }
            public Builder xpReward(int xpReward) { this.xpReward = xpReward; return this; }

            public RecommendedChallengeDto build() {
                return new RecommendedChallengeDto(id, title, slug, difficulty, category, xpReward);
            }
        }
    }
}

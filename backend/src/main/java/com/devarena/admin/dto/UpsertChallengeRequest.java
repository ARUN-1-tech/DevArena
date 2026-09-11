package com.devarena.admin.dto;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UpsertChallengeRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Slug is required")
    private String slug;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Difficulty is required")
    private ChallengeDifficulty difficulty;

    @NotNull(message = "Category is required")
    private ChallengeCategory category;

    private int xpReward;
    private int estimatedMinutes;
    private String tags;
    private ChallengeStatus status;

    private List<TestCaseItem> testCases;

    public UpsertChallengeRequest() {}

    public UpsertChallengeRequest(String title, String slug, String description, ChallengeDifficulty difficulty,
                                  ChallengeCategory category, int xpReward, int estimatedMinutes, String tags,
                                  ChallengeStatus status, List<TestCaseItem> testCases) {
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.difficulty = difficulty;
        this.category = category;
        this.xpReward = xpReward;
        this.estimatedMinutes = estimatedMinutes;
        this.tags = tags;
        this.status = status;
        this.testCases = testCases;
    }

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

    public List<TestCaseItem> getTestCases() { return testCases; }
    public void setTestCases(List<TestCaseItem> testCases) { this.testCases = testCases; }

    public static class TestCaseItem {
        private String input;
        private String expectedOutput;
        private boolean hidden;
        private int orderIndex;
        private String explanation;

        public TestCaseItem() {}

        public TestCaseItem(String input, String expectedOutput, boolean hidden, int orderIndex, String explanation) {
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.hidden = hidden;
            this.orderIndex = orderIndex;
            this.explanation = explanation;
        }

        public String getInput() { return input; }
        public void setInput(String input) { this.input = input; }

        public String getExpectedOutput() { return expectedOutput; }
        public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }

        public boolean isHidden() { return hidden; }
        public void setHidden(boolean hidden) { this.hidden = hidden; }

        public int getOrderIndex() { return orderIndex; }
        public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }
}

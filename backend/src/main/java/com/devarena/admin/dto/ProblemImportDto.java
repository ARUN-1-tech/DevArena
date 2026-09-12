package com.devarena.admin.dto;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeStatus;
import com.devarena.challenge.model.ProblemType;

import java.util.List;

/**
 * DTO used for bulk importing problems via the admin API.
 */
public class ProblemImportDto {

    private String title;
    private String slug;
    private String description;
    private ChallengeDifficulty difficulty;
    private ChallengeCategory category;
    private ProblemType problemType;
    private int xpReward;
    private int estimatedMinutes;
    private String tags;
    private String supportedLanguages;
    private String sourceReference;
    private ChallengeStatus status;
    private List<TestCaseImportItem> testCases;

    public ProblemImportDto() {}

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

    public ProblemType getProblemType() { return problemType; }
    public void setProblemType(ProblemType problemType) { this.problemType = problemType; }

    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getSupportedLanguages() { return supportedLanguages; }
    public void setSupportedLanguages(String supportedLanguages) { this.supportedLanguages = supportedLanguages; }

    public String getSourceReference() { return sourceReference; }
    public void setSourceReference(String sourceReference) { this.sourceReference = sourceReference; }

    public ChallengeStatus getStatus() { return status; }
    public void setStatus(ChallengeStatus status) { this.status = status; }

    public List<TestCaseImportItem> getTestCases() { return testCases; }
    public void setTestCases(List<TestCaseImportItem> testCases) { this.testCases = testCases; }

    public static class TestCaseImportItem {
        private String input;
        private String expectedOutput;
        private boolean hidden;
        private int orderIndex;
        private String explanation;

        public TestCaseImportItem() {}

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

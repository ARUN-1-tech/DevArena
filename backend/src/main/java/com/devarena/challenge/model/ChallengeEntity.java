package com.devarena.challenge.model;

import com.devarena.common.model.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "challenges", indexes = {
        @Index(name = "idx_challenges_difficulty", columnList = "difficulty"),
        @Index(name = "idx_challenges_category", columnList = "category"),
        @Index(name = "idx_challenges_problem_type", columnList = "problem_type"),
        @Index(name = "idx_challenges_status", columnList = "status"),
        @Index(name = "idx_challenges_slug", columnList = "slug"),
        @Index(name = "idx_challenges_title", columnList = "title")
})
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

    @Enumerated(EnumType.STRING)
    @Column(name = "problem_type", nullable = false, length = 32)
    private ProblemType problemType = ProblemType.CODING;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward;

    @Column(name = "estimated_minutes", nullable = false)
    private int estimatedMinutes;

    @Column(name = "time_limit_seconds", nullable = false)
    private int timeLimitSeconds = 900;

    @Column(name = "tags")
    private String tags;

    @Column(name = "options", columnDefinition = "TEXT")
    private String options;

    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(name = "hints", columnDefinition = "TEXT")
    private String hints;

    @Column(name = "solution_approach", columnDefinition = "TEXT")
    private String solutionApproach;

    @Column(name = "source")
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ChallengeStatus status = ChallengeStatus.PUBLISHED;

    public ChallengeEntity() {}

    public ChallengeEntity(String title, String slug, String description, ChallengeDifficulty difficulty,
                           ChallengeCategory category, int xpReward, int estimatedMinutes, String tags) {
        this(title, slug, description, difficulty, category, ProblemType.CODING, xpReward, estimatedMinutes, tags);
    }

    public ChallengeEntity(String title, String slug, String description, ChallengeDifficulty difficulty,
                           ChallengeCategory category, ProblemType problemType, int xpReward, int estimatedMinutes, String tags) {
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.difficulty = difficulty;
        this.category = category;
        this.problemType = problemType != null ? problemType : ProblemType.CODING;
        this.xpReward = xpReward;
        this.estimatedMinutes = estimatedMinutes;
        this.timeLimitSeconds = estimatedMinutes * 60;
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

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
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

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public void setTimeLimitSeconds(int timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getHints() {
        return hints;
    }

    public void setHints(String hints) {
        this.hints = hints;
    }

    public String getSolutionApproach() {
        return solutionApproach;
    }

    public void setSolutionApproach(String solutionApproach) {
        this.solutionApproach = solutionApproach;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }
}

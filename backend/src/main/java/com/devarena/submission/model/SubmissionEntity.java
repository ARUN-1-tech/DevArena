package com.devarena.submission.model;

import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.common.model.BaseAuditEntity;
import com.devarena.execution.model.ExecutionLanguage;
import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "submissions")
public class SubmissionEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private ChallengeEntity challenge;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 32)
    private ExecutionLanguage language;

    @Column(name = "source_code", nullable = false, columnDefinition = "TEXT")
    private String sourceCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private SubmissionStatus status = SubmissionStatus.QUEUED;

    @Column(name = "passed_tests", nullable = false)
    private int passedTests = 0;

    @Column(name = "total_tests", nullable = false)
    private int totalTests = 0;

    @Column(name = "execution_time_ms", nullable = false)
    private long executionTimeMs = 0L;

    @Column(name = "memory_used_bytes", nullable = false)
    private long memoryUsedBytes = 0L;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "completed_at")
    private Instant completedAt;

    public SubmissionEntity() {}

    public SubmissionEntity(UserEntity user, ChallengeEntity challenge, ExecutionLanguage language, String sourceCode) {
        this.user = user;
        this.challenge = challenge;
        this.language = language;
        this.sourceCode = sourceCode;
        this.status = SubmissionStatus.QUEUED;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ChallengeEntity getChallenge() {
        return challenge;
    }

    public void setChallenge(ChallengeEntity challenge) {
        this.challenge = challenge;
    }

    public ExecutionLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ExecutionLanguage language) {
        this.language = language;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public int getPassedTests() {
        return passedTests;
    }

    public void setPassedTests(int passedTests) {
        this.passedTests = passedTests;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public long getMemoryUsedBytes() {
        return memoryUsedBytes;
    }

    public void setMemoryUsedBytes(long memoryUsedBytes) {
        this.memoryUsedBytes = memoryUsedBytes;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}

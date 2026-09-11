package com.devarena.ai.model;

import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_usage_records")
public class AiUsageRecordEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private ChallengeEntity challenge;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 32)
    private AiCoachRequestType requestType;

    @Column(name = "tokens_used", nullable = false)
    private int tokensUsed;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public AiUsageRecordEntity() {}

    public AiUsageRecordEntity(UserEntity user, ChallengeEntity challenge, AiCoachRequestType requestType, int tokensUsed) {
        this.user = user;
        this.challenge = challenge;
        this.requestType = requestType;
        this.tokensUsed = tokensUsed;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public ChallengeEntity getChallenge() { return challenge; }
    public void setChallenge(ChallengeEntity challenge) { this.challenge = challenge; }

    public AiCoachRequestType getRequestType() { return requestType; }
    public void setRequestType(AiCoachRequestType requestType) { this.requestType = requestType; }

    public int getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UserEntity user;
        private ChallengeEntity challenge;
        private AiCoachRequestType requestType;
        private int tokensUsed;

        public Builder user(UserEntity user) { this.user = user; return this; }
        public Builder challenge(ChallengeEntity challenge) { this.challenge = challenge; return this; }
        public Builder requestType(AiCoachRequestType requestType) { this.requestType = requestType; return this; }
        public Builder tokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; return this; }

        public AiUsageRecordEntity build() {
            return new AiUsageRecordEntity(user, challenge, requestType, tokensUsed);
        }
    }
}

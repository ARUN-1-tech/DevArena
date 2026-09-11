package com.devarena.challenge.model;

import com.devarena.common.model.BaseAuditEntity;
import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "player_challenges", uniqueConstraints = {
        @UniqueConstraint(name = "uq_player_challenges", columnNames = {"user_id", "challenge_id"})
})
public class PlayerChallengeEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private ChallengeEntity challenge;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ChallengeProgressStatus status = ChallengeProgressStatus.NOT_STARTED;

    @Column(name = "completed_at")
    private Instant completedAt;

    public PlayerChallengeEntity() {}

    public PlayerChallengeEntity(UserEntity user, ChallengeEntity challenge, ChallengeProgressStatus status) {
        this.user = user;
        this.challenge = challenge;
        this.status = status;
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

    public ChallengeProgressStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeProgressStatus status) {
        this.status = status;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}

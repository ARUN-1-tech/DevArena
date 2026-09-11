package com.devarena.challenge.model;

import com.devarena.common.model.BaseAuditEntity;
import com.devarena.execution.model.ExecutionLanguage;
import jakarta.persistence.*;

@Entity
@Table(name = "challenge_starter_codes", uniqueConstraints = {
        @UniqueConstraint(name = "uq_challenge_starter_code", columnNames = {"challenge_id", "language"})
})
public class ChallengeStarterCodeEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private ChallengeEntity challenge;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 32)
    private ExecutionLanguage language;

    @Column(name = "starter_code", nullable = false, columnDefinition = "TEXT")
    private String starterCode;

    public ChallengeStarterCodeEntity() {}

    public ChallengeStarterCodeEntity(ChallengeEntity challenge, ExecutionLanguage language, String starterCode) {
        this.challenge = challenge;
        this.language = language;
        this.starterCode = starterCode;
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

    public String getStarterCode() {
        return starterCode;
    }

    public void setStarterCode(String starterCode) {
        this.starterCode = starterCode;
    }
}

package com.devarena.challenge.model;

import com.devarena.common.model.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "challenge_test_cases")
public class ChallengeTestCaseEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private ChallengeEntity challenge;

    @Column(name = "input", nullable = false, columnDefinition = "TEXT")
    private String input;

    @Column(name = "expected_output", nullable = false, columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(name = "hidden", nullable = false)
    private boolean hidden = false;

    @Column(name = "order_index", nullable = false)
    private int orderIndex = 0;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    public ChallengeTestCaseEntity() {}

    public ChallengeTestCaseEntity(ChallengeEntity challenge, String input, String expectedOutput, boolean hidden, int orderIndex, String explanation) {
        this.challenge = challenge;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.hidden = hidden;
        this.orderIndex = orderIndex;
        this.explanation = explanation;
    }

    public ChallengeEntity getChallenge() {
        return challenge;
    }

    public void setChallenge(ChallengeEntity challenge) {
        this.challenge = challenge;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}

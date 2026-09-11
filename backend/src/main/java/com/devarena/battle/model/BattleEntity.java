package com.devarena.battle.model;

import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.common.model.BaseAuditEntity;
import com.devarena.submission.model.SubmissionEntity;
import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "battles")
public class BattleEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id", nullable = false)
    private UserEntity player1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id", nullable = false)
    private UserEntity player2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private ChallengeEntity challenge;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private BattleStatus status = BattleStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @Column(name = "player1_status", nullable = false, length = 32)
    private PlayerBattleStatus player1Status = PlayerBattleStatus.CONNECTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "player2_status", nullable = false, length = 32)
    private PlayerBattleStatus player2Status = PlayerBattleStatus.CONNECTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private UserEntity winner;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds = 900; // 15 minutes default

    @Column(name = "player1_xp_delta", nullable = false)
    private int player1XpDelta = 0;

    @Column(name = "player2_xp_delta", nullable = false)
    private int player2XpDelta = 0;

    @Column(name = "player1_rating_delta", nullable = false)
    private int player1RatingDelta = 0;

    @Column(name = "player2_rating_delta", nullable = false)
    private int player2RatingDelta = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_submission_id")
    private SubmissionEntity player1Submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_submission_id")
    private SubmissionEntity player2Submission;

    @Column(name = "finish_reason", length = 64)
    private String finishReason;

    public BattleEntity() {}

    public BattleEntity(UserEntity player1, UserEntity player2, ChallengeEntity challenge, int durationSeconds) {
        this.player1 = player1;
        this.player2 = player2;
        this.challenge = challenge;
        this.durationSeconds = durationSeconds;
        this.status = BattleStatus.WAITING;
        this.player1Status = PlayerBattleStatus.CONNECTED;
        this.player2Status = PlayerBattleStatus.CONNECTED;
    }

    public UserEntity getPlayer1() {
        return player1;
    }

    public void setPlayer1(UserEntity player1) {
        this.player1 = player1;
    }

    public UserEntity getPlayer2() {
        return player2;
    }

    public void setPlayer2(UserEntity player2) {
        this.player2 = player2;
    }

    public ChallengeEntity getChallenge() {
        return challenge;
    }

    public void setChallenge(ChallengeEntity challenge) {
        this.challenge = challenge;
    }

    public BattleStatus getStatus() {
        return status;
    }

    public void setStatus(BattleStatus status) {
        this.status = status;
    }

    public PlayerBattleStatus getPlayer1Status() {
        return player1Status;
    }

    public void setPlayer1Status(PlayerBattleStatus player1Status) {
        this.player1Status = player1Status;
    }

    public PlayerBattleStatus getPlayer2Status() {
        return player2Status;
    }

    public void setPlayer2Status(PlayerBattleStatus player2Status) {
        this.player2Status = player2Status;
    }

    public UserEntity getWinner() {
        return winner;
    }

    public void setWinner(UserEntity winner) {
        this.winner = winner;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getPlayer1XpDelta() {
        return player1XpDelta;
    }

    public void setPlayer1XpDelta(int player1XpDelta) {
        this.player1XpDelta = player1XpDelta;
    }

    public int getPlayer2XpDelta() {
        return player2XpDelta;
    }

    public void setPlayer2XpDelta(int player2XpDelta) {
        this.player2XpDelta = player2XpDelta;
    }

    public int getPlayer1RatingDelta() {
        return player1RatingDelta;
    }

    public void setPlayer1RatingDelta(int player1RatingDelta) {
        this.player1RatingDelta = player1RatingDelta;
    }

    public int getPlayer2RatingDelta() {
        return player2RatingDelta;
    }

    public void setPlayer2RatingDelta(int player2RatingDelta) {
        this.player2RatingDelta = player2RatingDelta;
    }

    public SubmissionEntity getPlayer1Submission() {
        return player1Submission;
    }

    public void setPlayer1Submission(SubmissionEntity player1Submission) {
        this.player1Submission = player1Submission;
    }

    public SubmissionEntity getPlayer2Submission() {
        return player2Submission;
    }

    public void setPlayer2Submission(SubmissionEntity player2Submission) {
        this.player2Submission = player2Submission;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }
}

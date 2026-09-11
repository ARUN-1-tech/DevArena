package com.devarena.social.model;

import com.devarena.battle.model.BattleEntity;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friend_battle_invites")
public class FriendBattleInviteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private UserEntity inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", nullable = false)
    private UserEntity invitee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private ChallengeEntity challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_id")
    private BattleEntity battle;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BattleInviteStatus status = BattleInviteStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "expires_at")
    private Instant expiresAt;

    public FriendBattleInviteEntity() {}

    public FriendBattleInviteEntity(UserEntity inviter, UserEntity invitee, ChallengeEntity challenge) {
        this.inviter = inviter;
        this.invitee = invitee;
        this.challenge = challenge;
        this.status = BattleInviteStatus.PENDING;
        this.createdAt = Instant.now();
        this.expiresAt = Instant.now().plusSeconds(300); // 5 minute expiry
    }

    public UUID getId() {
        return id;
    }

    public UserEntity getInviter() {
        return inviter;
    }

    public void setInviter(UserEntity inviter) {
        this.inviter = inviter;
    }

    public UserEntity getInvitee() {
        return invitee;
    }

    public void setInvitee(UserEntity invitee) {
        this.invitee = invitee;
    }

    public ChallengeEntity getChallenge() {
        return challenge;
    }

    public void setChallenge(ChallengeEntity challenge) {
        this.challenge = challenge;
    }

    public BattleEntity getBattle() {
        return battle;
    }

    public void setBattle(BattleEntity battle) {
        this.battle = battle;
    }

    public BattleInviteStatus getStatus() {
        return status;
    }

    public void setStatus(BattleInviteStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}

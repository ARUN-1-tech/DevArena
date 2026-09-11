package com.devarena.team.model;

import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "team_invites")
public class TeamInviteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private UserEntity inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", nullable = false)
    private UserEntity invitee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TeamInviteStatus status = TeamInviteStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public TeamInviteEntity() {}

    public TeamInviteEntity(TeamEntity team, UserEntity inviter, UserEntity invitee) {
        this.team = team;
        this.inviter = inviter;
        this.invitee = invitee;
        this.status = TeamInviteStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
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

    public TeamInviteStatus getStatus() {
        return status;
    }

    public void setStatus(TeamInviteStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

package com.devarena.team.model;

import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "team_members", uniqueConstraints = {
        @UniqueConstraint(name = "uq_team_member_user", columnNames = {"user_id"}),
        @UniqueConstraint(name = "uq_team_member_team_user", columnNames = {"team_id", "user_id"})
})
public class TeamMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private TeamRole role = TeamRole.MEMBER;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt = Instant.now();

    public TeamMemberEntity() {}

    public TeamMemberEntity(TeamEntity team, UserEntity user, TeamRole role) {
        this.team = team;
        this.user = user;
        this.role = role;
        this.joinedAt = Instant.now();
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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public TeamRole getRole() {
        return role;
    }

    public void setRole(TeamRole role) {
        this.role = role;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }
}

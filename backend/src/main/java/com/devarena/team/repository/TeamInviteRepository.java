package com.devarena.team.repository;

import com.devarena.team.model.TeamInviteEntity;
import com.devarena.team.model.TeamInviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamInviteRepository extends JpaRepository<TeamInviteEntity, UUID> {

    List<TeamInviteEntity> findByInviteeIdAndStatusOrderByCreatedAtDesc(UUID inviteeId, TeamInviteStatus status);

    List<TeamInviteEntity> findByTeamIdAndStatusOrderByCreatedAtDesc(UUID teamId, TeamInviteStatus status);

    Optional<TeamInviteEntity> findByTeamIdAndInviteeIdAndStatus(UUID teamId, UUID inviteeId, TeamInviteStatus status);
}

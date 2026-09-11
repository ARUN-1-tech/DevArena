package com.devarena.team.repository;

import com.devarena.team.model.TeamMemberEntity;
import com.devarena.team.model.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, UUID> {

    Optional<TeamMemberEntity> findByUserId(UUID userId);

    List<TeamMemberEntity> findByTeamIdOrderByRoleAscJoinedAtAsc(UUID teamId);

    Optional<TeamMemberEntity> findByTeamIdAndUserId(UUID teamId, UUID userId);

    boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);

    long countByTeamId(UUID teamId);

    long countByTeamIdAndRole(UUID teamId, TeamRole role);
}

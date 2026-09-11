package com.devarena.challenge.repository;

import com.devarena.challenge.model.ChallengeProgressStatus;
import com.devarena.challenge.model.PlayerChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerChallengeRepository extends JpaRepository<PlayerChallengeEntity, UUID> {

    Optional<PlayerChallengeEntity> findByUserIdAndChallengeId(UUID userId, UUID challengeId);

    List<PlayerChallengeEntity> findByUserId(UUID userId);

    long countByUserIdAndStatus(UUID userId, ChallengeProgressStatus status);
}

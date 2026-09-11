package com.devarena.challenge.repository;

import com.devarena.challenge.model.ChallengeStarterCodeEntity;
import com.devarena.execution.model.ExecutionLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChallengeStarterCodeRepository extends JpaRepository<ChallengeStarterCodeEntity, UUID> {
    Optional<ChallengeStarterCodeEntity> findByChallengeIdAndLanguage(UUID challengeId, ExecutionLanguage language);
    List<ChallengeStarterCodeEntity> findByChallengeId(UUID challengeId);
    boolean existsByChallengeIdAndLanguage(UUID challengeId, ExecutionLanguage language);
}

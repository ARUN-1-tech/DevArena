package com.devarena.challenge.repository;

import com.devarena.challenge.model.ChallengeTestCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeTestCaseRepository extends JpaRepository<ChallengeTestCaseEntity, UUID> {
    List<ChallengeTestCaseEntity> findByChallengeIdOrderByOrderIndexAsc(UUID challengeId);
    List<ChallengeTestCaseEntity> findByChallengeIdAndHiddenFalseOrderByOrderIndexAsc(UUID challengeId);
    long countByChallengeId(UUID challengeId);
}

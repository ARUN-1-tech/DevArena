package com.devarena.submission.repository;

import com.devarena.submission.model.SubmissionEntity;
import com.devarena.submission.model.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, UUID> {
    Page<SubmissionEntity> findByUserIdAndChallengeIdOrderByCreatedAtDesc(UUID userId, UUID challengeId, Pageable pageable);
    Page<SubmissionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Optional<SubmissionEntity> findByIdAndUserId(UUID id, UUID userId);
    long countByUserId(UUID userId);
    long countByUserIdAndStatus(UUID userId, SubmissionStatus status);
    long countByUserIdAndChallengeId(UUID userId, UUID challengeId);
    long countByUserIdAndChallengeIdAndStatus(UUID userId, UUID challengeId, SubmissionStatus status);
}

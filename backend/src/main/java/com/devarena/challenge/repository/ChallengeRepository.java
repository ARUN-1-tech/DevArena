package com.devarena.challenge.repository;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.model.ChallengeStatus;
import com.devarena.challenge.model.ProblemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<ChallengeEntity, UUID>, JpaSpecificationExecutor<ChallengeEntity> {

    Optional<ChallengeEntity> findBySlug(String slug);

    Optional<ChallengeEntity> findBySlugIgnoreCase(String slug);

    Optional<ChallengeEntity> findByTitleIgnoreCase(String title);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsByTitleIgnoreCase(String title);

    List<ChallengeEntity> findTop5ByStatusOrderByCreatedAtDesc(ChallengeStatus status);

    long countByStatus(ChallengeStatus status);

    long countByDifficultyAndStatus(ChallengeDifficulty difficulty, ChallengeStatus status);

    long countByCategoryAndStatus(ChallengeCategory category, ChallengeStatus status);

    long countByProblemTypeAndStatus(ProblemType problemType, ChallengeStatus status);
}

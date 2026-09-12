package com.devarena.challenge.repository;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.model.ChallengeStatus;
import com.devarena.challenge.model.ProblemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<ChallengeEntity, UUID> {

    Optional<ChallengeEntity> findBySlug(String slug);

    Optional<ChallengeEntity> findBySlugIgnoreCase(String slug);

    Optional<ChallengeEntity> findByTitleIgnoreCase(String title);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsByTitleIgnoreCase(String title);

    @Query("SELECT c FROM ChallengeEntity c WHERE c.status = :status " +
           "AND (:difficulty IS NULL OR c.difficulty = :difficulty) " +
           "AND (:category IS NULL OR c.category = :category) " +
           "AND (:problemType IS NULL OR c.problemType = :problemType) " +
           "AND (:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(c.tags) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ChallengeEntity> searchChallenges(
            @Param("status") ChallengeStatus status,
            @Param("difficulty") ChallengeDifficulty difficulty,
            @Param("category") ChallengeCategory category,
            @Param("problemType") ProblemType problemType,
            @Param("search") String search,
            Pageable pageable
    );

    List<ChallengeEntity> findTop5ByStatusOrderByCreatedAtDesc(ChallengeStatus status);

    long countByStatus(ChallengeStatus status);

    long countByDifficultyAndStatus(ChallengeDifficulty difficulty, ChallengeStatus status);

    long countByCategoryAndStatus(ChallengeCategory category, ChallengeStatus status);

    long countByProblemTypeAndStatus(ProblemType problemType, ChallengeStatus status);
}

package com.devarena.achievement.repository;

import com.devarena.achievement.model.PlayerAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerAchievementRepository extends JpaRepository<PlayerAchievementEntity, UUID> {

    List<PlayerAchievementEntity> findByUserIdOrderByUnlockedAtDesc(UUID userId);

    Optional<PlayerAchievementEntity> findByUserIdAndAchievementId(UUID userId, UUID achievementId);

    boolean existsByUserIdAndAchievementId(UUID userId, UUID achievementId);

    long countByUserId(UUID userId);

    @Query("SELECT pa.achievement.id FROM PlayerAchievementEntity pa WHERE pa.user.id = :userId")
    List<UUID> findUnlockedAchievementIdsByUserId(@Param("userId") UUID userId);
}

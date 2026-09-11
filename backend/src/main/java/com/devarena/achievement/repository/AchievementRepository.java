package com.devarena.achievement.repository;

import com.devarena.achievement.model.AchievementCategory;
import com.devarena.achievement.model.AchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<AchievementEntity, UUID> {
    Optional<AchievementEntity> findByCode(String code);
    List<AchievementEntity> findByIsActiveTrue();
    List<AchievementEntity> findByCategoryAndIsActiveTrue(AchievementCategory category);
}

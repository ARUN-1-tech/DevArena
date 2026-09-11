package com.devarena.skill.repository;

import com.devarena.skill.model.PlayerSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerSkillRepository extends JpaRepository<PlayerSkillEntity, UUID> {

    List<PlayerSkillEntity> findByUserId(UUID userId);

    Optional<PlayerSkillEntity> findByUserIdAndSkillId(UUID userId, UUID skillId);

    Optional<PlayerSkillEntity> findByUserIdAndSkillCode(UUID userId, String skillCode);

    @Query("SELECT ps FROM PlayerSkillEntity ps WHERE ps.user.id = :userId ORDER BY ps.masteryPercentage DESC, ps.currentLevel DESC")
    List<PlayerSkillEntity> findTopSkillsByUserId(@Param("userId") UUID userId);
}

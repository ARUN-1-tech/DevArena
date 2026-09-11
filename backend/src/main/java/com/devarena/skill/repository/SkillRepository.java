package com.devarena.skill.repository;

import com.devarena.skill.model.SkillCategory;
import com.devarena.skill.model.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, UUID> {
    Optional<SkillEntity> findByCode(String code);
    List<SkillEntity> findAllByOrderByCategoryAscOrderIndexAsc();
    List<SkillEntity> findByCategoryOrderByOrderIndexAsc(SkillCategory category);
}

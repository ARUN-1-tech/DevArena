package com.devarena.skill.dto;

import com.devarena.skill.model.SkillCategory;
import com.devarena.skill.model.SkillEntity;

import java.util.UUID;

public record SkillDto(
        UUID id,
        String code,
        String name,
        SkillCategory category,
        String description,
        String icon,
        int maxLevel,
        UUID prerequisiteId,
        String prerequisiteName,
        int orderIndex
) {
    public static SkillDto fromEntity(SkillEntity entity) {
        return new SkillDto(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getIcon(),
                entity.getMaxLevel(),
                entity.getPrerequisite() != null ? entity.getPrerequisite().getId() : null,
                entity.getPrerequisite() != null ? entity.getPrerequisite().getName() : null,
                entity.getOrderIndex()
        );
    }
}

package com.devarena.skill.dto;

import com.devarena.skill.model.SkillCategory;

import java.util.List;
import java.util.UUID;

public record SkillTreeNodeDto(
        UUID id,
        String code,
        String name,
        SkillCategory category,
        String description,
        String icon,
        int maxLevel,
        UUID prerequisiteId,
        String prerequisiteCode,
        int orderIndex,
        List<SkillTreeNodeDto> children
) {}

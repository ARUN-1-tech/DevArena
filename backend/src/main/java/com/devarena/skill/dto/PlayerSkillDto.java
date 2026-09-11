package com.devarena.skill.dto;

import com.devarena.skill.model.SkillCategory;

import java.util.UUID;

public record PlayerSkillDto(
        UUID id,
        UUID skillId,
        String code,
        String name,
        SkillCategory category,
        String description,
        String icon,
        int currentLevel,
        int maxLevel,
        int currentXp,
        int xpToNextLevel,
        int masteryPercentage,
        boolean unlocked,
        UUID prerequisiteId,
        String prerequisiteName
) {}

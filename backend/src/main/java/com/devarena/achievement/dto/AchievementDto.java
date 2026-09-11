package com.devarena.achievement.dto;

import com.devarena.achievement.model.AchievementCategory;
import com.devarena.achievement.model.AchievementEntity;
import com.devarena.achievement.model.AchievementRarity;
import com.devarena.achievement.model.RequirementType;

import java.util.UUID;

public record AchievementDto(
        UUID id,
        String code,
        String name,
        String description,
        String icon,
        AchievementCategory category,
        RequirementType requirementType,
        int requirementValue,
        int xpReward,
        AchievementRarity rarity,
        boolean isSecret
) {
    public static AchievementDto fromEntity(AchievementEntity entity) {
        return new AchievementDto(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getIcon(),
                entity.getCategory(),
                entity.getRequirementType(),
                entity.getRequirementValue(),
                entity.getXpReward(),
                entity.getRarity(),
                entity.isSecret()
        );
    }
}

package com.devarena.achievement.dto;

import com.devarena.achievement.model.AchievementCategory;
import com.devarena.achievement.model.AchievementRarity;

import java.time.Instant;
import java.util.UUID;

public record PlayerAchievementDto(
        UUID id,
        String code,
        String name,
        String description,
        String icon,
        AchievementCategory category,
        AchievementRarity rarity,
        int xpReward,
        boolean unlocked,
        Instant unlockedAt,
        int currentProgress,
        int targetProgress,
        int progressPercentage
) {}

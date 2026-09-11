package com.devarena.social.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PublicPlayerProfileDto(
        UUID id,
        String username,
        String displayName,
        String avatar,
        String bio,
        int level,
        int rating,
        int rank,
        int totalXp,
        int solvedChallenges,
        int battleWins,
        int battleLosses,
        double battleWinRate,
        int winStreak,
        List<PublicSkillDto> topSkills,
        List<PublicAchievementDto> achievements,
        List<PublicActivityDto> recentActivities,
        boolean isFriend,
        boolean hasPendingRequest,
        boolean online
) {
    public record PublicSkillDto(
            String code,
            String name,
            String category,
            String icon,
            int level,
            int masteryPercentage
    ) {}

    public record PublicAchievementDto(
            String code,
            String name,
            String description,
            String icon,
            String rarity,
            Instant unlockedAt
    ) {}

    public record PublicActivityDto(
            String type,
            String description,
            Instant timestamp
    ) {}
}

package com.devarena.progression.dto;

public record PlayerProgressionDto(
        int level,
        int currentXp,
        int xpToNextLevel,
        int totalXp,
        int currentStreak,
        int longestStreak,
        int challengesSolved,
        int questsCompleted,
        int xpPercentage
) {}

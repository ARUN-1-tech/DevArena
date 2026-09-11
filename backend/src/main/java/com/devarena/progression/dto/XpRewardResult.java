package com.devarena.progression.dto;

public record XpRewardResult(
        int previousXp,
        int newXp,
        int previousLevel,
        int newLevel,
        int totalXp,
        int xpEarned,
        int xpToNextLevel,
        boolean leveledUp
) {}

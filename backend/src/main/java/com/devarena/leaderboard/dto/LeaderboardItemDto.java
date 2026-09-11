package com.devarena.leaderboard.dto;

import java.util.UUID;

public record LeaderboardItemDto(
        int rank,
        UUID userId,
        String username,
        String displayName,
        String avatar,
        int level,
        int rating,
        int totalXp,
        int wins,
        int losses,
        int solvedChallenges,
        double winRate
) {}

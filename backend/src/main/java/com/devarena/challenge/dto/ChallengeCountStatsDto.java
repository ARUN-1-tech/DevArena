package com.devarena.challenge.dto;

import java.util.Map;

public record ChallengeCountStatsDto(
        long totalChallenges,
        long totalSolved,
        Map<String, Long> byDifficulty,
        Map<String, Long> byCategory,
        Map<String, Long> byProblemType
) {}

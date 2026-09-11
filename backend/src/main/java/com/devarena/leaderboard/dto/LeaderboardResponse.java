package com.devarena.leaderboard.dto;

import java.util.List;

public record LeaderboardResponse(
        String type, // "GLOBAL", "WEEKLY", "MONTHLY"
        int page,
        int size,
        long totalElements,
        int totalPages,
        List<LeaderboardItemDto> top3,
        List<LeaderboardItemDto> rankings,
        LeaderboardItemDto myRank
) {}

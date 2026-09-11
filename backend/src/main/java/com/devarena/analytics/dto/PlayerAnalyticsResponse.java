package com.devarena.analytics.dto;

import java.util.List;
import java.util.Map;

public record PlayerAnalyticsResponse(
        // Overview KPIs
        int currentLevel,
        int totalXp,
        int currentMmr,
        int mmrChange30Days,
        int currentStreak,
        int longestStreak,

        // Challenge Statistics
        long totalChallengesAttempted,
        int totalChallengesSolved,
        double challengeSolveRate,
        long totalSubmissions,
        long successfulSubmissions,
        double submissionSuccessRate,
        Map<String, Integer> difficultyDistribution, // EASY -> count, MEDIUM -> count, HARD -> count
        Map<String, Integer> categoryDistribution,

        // Battle Statistics
        int battleWins,
        int battleLosses,
        int battleDraws,
        double battleWinRate,

        // Skill Mastery
        String strongestSkill,
        String weakestSkill,
        List<SkillProgressSummaryDto> topSkills,

        // Time Series Trends (last 14 days)
        List<DailyXpTrendDto> dailyXpTrend,
        List<RecentBattleSummaryDto> recentBattles
) {
    public record SkillProgressSummaryDto(
            String name,
            String category,
            String icon,
            int level,
            int masteryPercentage
    ) {}

    public record DailyXpTrendDto(
            String date, // "YYYY-MM-DD"
            int xpEarned
    ) {}

    public record RecentBattleSummaryDto(
            String opponentUsername,
            String outcome, // "WIN", "LOSS", "DRAW"
            int ratingDelta,
            int xpEarned,
            String challengeTitle,
            String endedAt
    ) {}
}

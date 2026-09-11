package com.devarena.arena.dto;

import com.devarena.challenge.dto.ChallengeCardDto;
import com.devarena.progression.dto.PlayerProgressionDto;
import com.devarena.quest.dto.DailyQuestDto;

import java.util.List;

public record ArenaHomeDto(
        PlayerSummaryDto player,
        PlayerProgressionDto progression,
        PlayerStatsDto stats,
        List<DailyQuestDto> dailyQuests,
        List<ChallengeCardDto> recommendedChallenges,
        List<PlayerActivityDto> recentActivity,
        ProgressionMilestoneDto nextMilestone
) {}

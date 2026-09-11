package com.devarena.battle.dto;

import com.devarena.challenge.model.ChallengeDifficulty;

import java.time.Instant;
import java.util.UUID;

public record BattleHistoryItemDto(
        UUID battleId,
        String challengeTitle,
        ChallengeDifficulty difficulty,
        String opponentUsername,
        String opponentAvatarUrl,
        String outcome, // "WIN", "LOSS", "DRAW"
        int ratingDelta,
        int xpEarned,
        Instant completedAt
) {}

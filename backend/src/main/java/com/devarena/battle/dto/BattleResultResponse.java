package com.devarena.battle.dto;

import com.devarena.battle.model.BattleStatus;

import java.time.Instant;
import java.util.UUID;

public record BattleResultResponse(
        UUID battleId,
        BattleStatus status,
        String outcome, // "WIN", "LOSS", "DRAW"
        UUID winnerId,
        String finishReason,
        int durationSeconds,
        int xpEarned,
        int ratingDelta,
        int newRating,
        String opponentUsername,
        String opponentAvatarUrl,
        int opponentRating,
        int opponentRatingDelta,
        String challengeTitle,
        Instant endedAt
) {}

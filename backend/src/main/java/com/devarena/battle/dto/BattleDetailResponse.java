package com.devarena.battle.dto;

import com.devarena.battle.model.BattleStatus;
import com.devarena.challenge.dto.ChallengeDetailDto;

import java.time.Instant;
import java.util.UUID;

public record BattleDetailResponse(
        UUID id,
        BattleStatus status,
        BattlePlayerDto player1,
        BattlePlayerDto player2,
        ChallengeDetailDto challenge,
        int durationSeconds,
        Instant startedAt,
        Instant endedAt,
        UUID winnerId,
        String finishReason,
        boolean isPlayer1
) {}

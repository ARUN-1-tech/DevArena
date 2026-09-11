package com.devarena.matchmaking.dto;

import java.util.UUID;

public record MatchmakingStatusResponse(
        boolean inQueue,
        int waitTimeSeconds,
        int searchRadius,
        int playerRating,
        UUID matchedBattleId
) {}

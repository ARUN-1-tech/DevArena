package com.devarena.battle.dto;

import java.time.Instant;
import java.util.UUID;

public record CustomDuelRoomDto(
        String roomCode,
        UUID hostId,
        String hostUsername,
        String hostDisplayName,
        String hostAvatar,
        int hostRating,
        UUID challengeId,
        String challengeTitle,
        String difficulty,
        int durationSeconds,
        String status,
        UUID battleId,
        Instant createdAt
) {}

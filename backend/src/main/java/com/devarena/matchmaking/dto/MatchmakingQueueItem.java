package com.devarena.matchmaking.dto;

import java.time.Instant;
import java.util.UUID;

public record MatchmakingQueueItem(
        UUID userId,
        String username,
        String avatarUrl,
        int rating,
        int level,
        Instant queuedAt
) {}

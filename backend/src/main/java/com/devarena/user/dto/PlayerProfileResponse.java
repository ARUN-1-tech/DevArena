package com.devarena.user.dto;

import java.util.Set;
import java.util.UUID;

public record PlayerProfileResponse(
        UUID id,
        String username,
        String email,
        String displayName,
        String avatar,
        String bio,
        Set<String> roles,
        PlayerStatsDto stats,
        PlayerProgressionDto progression
) {
    public record PlayerStatsDto(
            int rating,
            int wins,
            int losses,
            int draws,
            int winStreak,
            int highestRating
    ) {}

    public record PlayerProgressionDto(
            int level,
            int currentXp,
            int xpToNextLevel
    ) {}
}

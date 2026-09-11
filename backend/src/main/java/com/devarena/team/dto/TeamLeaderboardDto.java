package com.devarena.team.dto;

import java.util.UUID;

public record TeamLeaderboardDto(
        int rank,
        UUID id,
        String name,
        String slug,
        String avatar,
        String ownerUsername,
        int memberCount,
        int maxMembers,
        int rating,
        int wins,
        int losses,
        int battles,
        double winRate
) {}

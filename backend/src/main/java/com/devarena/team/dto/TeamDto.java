package com.devarena.team.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TeamDto(
        UUID id,
        String name,
        String slug,
        String description,
        String avatar,
        UUID ownerId,
        String ownerUsername,
        int maxMembers,
        int memberCount,
        int rating,
        int wins,
        int losses,
        int battles,
        double winRate,
        List<TeamMemberDto> members,
        Instant createdAt
) {}

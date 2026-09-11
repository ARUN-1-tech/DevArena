package com.devarena.team.dto;

import com.devarena.team.model.TeamRole;

import java.time.Instant;
import java.util.UUID;

public record TeamMemberDto(
        UUID id,
        UUID userId,
        String username,
        String displayName,
        String avatar,
        TeamRole role,
        int level,
        int rating,
        boolean online,
        Instant joinedAt
) {}

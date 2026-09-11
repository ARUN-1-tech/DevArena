package com.devarena.team.dto;

import com.devarena.team.model.TeamInviteStatus;

import java.time.Instant;
import java.util.UUID;

public record TeamInviteDto(
        UUID id,
        UUID teamId,
        String teamName,
        String teamAvatar,
        UUID inviterId,
        String inviterUsername,
        UUID inviteeId,
        String inviteeUsername,
        TeamInviteStatus status,
        Instant createdAt
) {}

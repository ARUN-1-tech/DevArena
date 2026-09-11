package com.devarena.social.dto;

import com.devarena.social.model.BattleInviteStatus;

import java.time.Instant;
import java.util.UUID;

public record FriendBattleInviteDto(
        UUID id,
        UUID inviterId,
        String inviterUsername,
        String inviterDisplayName,
        String inviterAvatar,
        UUID inviteeId,
        String inviteeUsername,
        UUID challengeId,
        String challengeTitle,
        BattleInviteStatus status,
        UUID battleId,
        Instant createdAt,
        Instant expiresAt
) {}

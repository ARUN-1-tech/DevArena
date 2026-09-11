package com.devarena.social.dto;

import java.time.Instant;
import java.util.UUID;

public record FriendDto(
        UUID id,
        UUID friendId,
        String username,
        String displayName,
        String avatar,
        int level,
        int rating,
        boolean online,
        Instant friendsSince
) {}

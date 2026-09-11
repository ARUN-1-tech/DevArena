package com.devarena.social.dto;

import com.devarena.social.model.FriendRequestStatus;

import java.time.Instant;
import java.util.UUID;

public record FriendRequestDto(
        UUID id,
        UUID senderId,
        String senderUsername,
        String senderDisplayName,
        String senderAvatar,
        int senderLevel,
        int senderRating,
        UUID receiverId,
        String receiverUsername,
        FriendRequestStatus status,
        Instant createdAt
) {}

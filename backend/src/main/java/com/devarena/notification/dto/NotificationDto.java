package com.devarena.notification.dto;

import com.devarena.notification.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        NotificationType type,
        String title,
        String message,
        String referenceType,
        String referenceId,
        boolean read,
        Instant createdAt
) {}

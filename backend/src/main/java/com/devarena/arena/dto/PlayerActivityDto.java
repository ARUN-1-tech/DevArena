package com.devarena.arena.dto;

import com.devarena.progression.model.ActivityType;

import java.time.Instant;
import java.util.UUID;

public record PlayerActivityDto(
        UUID id,
        ActivityType activityType,
        String title,
        String description,
        int xpEarned,
        Instant createdAt
) {}

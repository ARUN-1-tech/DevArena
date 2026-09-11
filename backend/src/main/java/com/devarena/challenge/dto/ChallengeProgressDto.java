package com.devarena.challenge.dto;

import com.devarena.challenge.model.ChallengeProgressStatus;

import java.time.Instant;
import java.util.UUID;

public record ChallengeProgressDto(
        UUID challengeId,
        UUID userId,
        ChallengeProgressStatus status,
        Instant completedAt
) {}

package com.devarena.challenge.dto;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeProgressStatus;

import java.time.Instant;
import java.util.UUID;

public record ChallengeDetailDto(
        UUID id,
        String title,
        String slug,
        String description,
        ChallengeDifficulty difficulty,
        ChallengeCategory category,
        int xpReward,
        int estimatedMinutes,
        String tags,
        ChallengeProgressStatus progressStatus,
        Instant completedAt
) {}

package com.devarena.challenge.dto;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeProgressStatus;

import java.util.UUID;

public record ChallengeCardDto(
        UUID id,
        String title,
        String slug,
        ChallengeDifficulty difficulty,
        ChallengeCategory category,
        int xpReward,
        int estimatedMinutes,
        String tags,
        ChallengeProgressStatus progressStatus
) {}

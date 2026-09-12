package com.devarena.challenge.dto;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeProgressStatus;
import com.devarena.challenge.model.ProblemType;

import java.util.UUID;

public record ChallengeCardDto(
        UUID id,
        String title,
        String slug,
        ChallengeDifficulty difficulty,
        ChallengeCategory category,
        ProblemType problemType,
        int xpReward,
        int estimatedMinutes,
        String tags,
        String supportedLanguages,
        String sourceReference,
        ChallengeProgressStatus progressStatus
) {}

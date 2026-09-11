package com.devarena.challenge.dto;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeProgressStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;
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
        Instant completedAt,
        List<TestCaseSummaryDto> sampleTestCases,
        Map<String, String> starterTemplates
) {
    public ChallengeDetailDto(
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
    ) {
        this(id, title, slug, description, difficulty, category, xpReward, estimatedMinutes, tags, progressStatus, completedAt, List.of(), Map.of());
    }
}

package com.devarena.challenge.dto;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeProgressStatus;
import com.devarena.challenge.model.ProblemType;

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
        ProblemType problemType,
        int xpReward,
        int estimatedMinutes,
        int timeLimitSeconds,
        String tags,
        String options,
        String hints,
        String solutionApproach,
        String source,
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
            Instant completedAt,
            List<TestCaseSummaryDto> sampleTestCases,
            Map<String, String> starterTemplates
    ) {
        this(
                id, title, slug, description, difficulty, category, ProblemType.CODING,
                xpReward, estimatedMinutes, estimatedMinutes * 60, tags, null, null, null, null,
                progressStatus, completedAt, sampleTestCases, starterTemplates
        );
    }
}

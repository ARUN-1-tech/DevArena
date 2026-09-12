package com.devarena.challenge.dto;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ProblemType;

import java.util.List;
import java.util.Map;

public record ChallengeImportItemDto(
        String title,
        String slug,
        String description,
        ChallengeDifficulty difficulty,
        ChallengeCategory category,
        ProblemType problemType,
        Integer xpReward,
        Integer estimatedMinutes,
        Integer timeLimitSeconds,
        String tags,
        String options,
        String correctAnswer,
        String hints,
        String solutionApproach,
        String source,
        List<ImportTestCaseDto> testCases,
        Map<String, String> starterTemplates
) {
    public record ImportTestCaseDto(
            String input,
            String expectedOutput,
            Boolean hidden,
            Integer orderIndex,
            String explanation
    ) {}
}

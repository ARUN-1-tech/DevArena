package com.devarena.challenge.dto;

import com.devarena.progression.dto.XpRewardResult;

public record SubmitAnswerResponse(
        boolean correct,
        String submittedAnswer,
        String correctAnswer,
        String explanation,
        String message,
        XpRewardResult rewardResult
) {}

package com.devarena.battle.dto;

import com.devarena.submission.model.SubmissionStatus;

import java.util.UUID;

public record BattleSubmitResponse(
        SubmissionStatus status,
        int passedTests,
        int totalTests,
        long executionTimeMs,
        boolean battleFinished,
        UUID winnerId,
        String finishReason,
        String errorMessage
) {}

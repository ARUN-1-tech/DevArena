package com.devarena.submission.dto;

import com.devarena.execution.dto.TestCaseResultDto;
import com.devarena.progression.dto.XpRewardResult;
import com.devarena.submission.model.SubmissionStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubmitCodeResponse(
        UUID submissionId,
        SubmissionStatus status,
        int passedTests,
        int totalTests,
        long executionTimeMs,
        long memoryUsedBytes,
        String errorMessage,
        int xpEarned,
        boolean firstSolve,
        XpRewardResult xpResult,
        Instant createdAt,
        Instant completedAt,
        List<TestCaseResultDto> testResults
) {}

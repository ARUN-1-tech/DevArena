package com.devarena.submission.dto;

import com.devarena.execution.model.ExecutionLanguage;
import com.devarena.submission.model.SubmissionStatus;

import java.time.Instant;
import java.util.UUID;

public record SubmissionDetailDto(
        UUID id,
        UUID challengeId,
        String challengeTitle,
        ExecutionLanguage language,
        String sourceCode,
        SubmissionStatus status,
        int passedTests,
        int totalTests,
        long executionTimeMs,
        long memoryUsedBytes,
        String errorMessage,
        Instant createdAt,
        Instant completedAt
) {}

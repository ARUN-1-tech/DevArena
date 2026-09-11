package com.devarena.submission.dto;

import com.devarena.execution.model.ExecutionLanguage;
import com.devarena.submission.model.SubmissionStatus;

import java.time.Instant;
import java.util.UUID;

public record SubmissionSummaryDto(
        UUID id,
        UUID challengeId,
        ExecutionLanguage language,
        SubmissionStatus status,
        int passedTests,
        int totalTests,
        long executionTimeMs,
        Instant createdAt
) {}

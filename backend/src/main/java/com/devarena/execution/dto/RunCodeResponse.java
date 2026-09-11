package com.devarena.execution.dto;

import com.devarena.submission.model.SubmissionStatus;

import java.util.List;

public record RunCodeResponse(
        SubmissionStatus status,
        int passedTests,
        int totalTests,
        long executionTimeMs,
        long memoryUsedBytes,
        String stdout,
        String stderr,
        String errorMessage,
        List<TestCaseResultDto> testResults
) {}

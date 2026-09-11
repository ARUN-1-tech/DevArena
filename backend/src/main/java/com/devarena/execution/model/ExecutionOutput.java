package com.devarena.execution.model;

import com.devarena.submission.model.SubmissionStatus;

public record ExecutionOutput(
        SubmissionStatus status,
        String stdout,
        String stderr,
        long executionTimeMs,
        long memoryUsedBytes,
        int exitCode,
        String errorMessage
) {
    public static ExecutionOutput success(String stdout, long executionTimeMs, long memoryUsedBytes) {
        return new ExecutionOutput(SubmissionStatus.PASSED, stdout, "", executionTimeMs, memoryUsedBytes, 0, null);
    }

    public static ExecutionOutput failed(SubmissionStatus status, String stdout, String stderr, long executionTimeMs, int exitCode, String errorMessage) {
        return new ExecutionOutput(status, stdout, stderr, executionTimeMs, 0L, exitCode, errorMessage);
    }

    public static ExecutionOutput timeout(long timeoutMs) {
        return new ExecutionOutput(SubmissionStatus.TIME_LIMIT, "", "Execution timed out after " + timeoutMs + "ms", timeoutMs, 0L, -1, "Time Limit Exceeded");
    }

    public static ExecutionOutput error(SubmissionStatus status, String errorMessage) {
        return new ExecutionOutput(status, "", errorMessage, 0L, 0L, -1, errorMessage);
    }
}

package com.devarena.execution.dto;

import java.util.UUID;

public record TestCaseResultDto(
        UUID testCaseId,
        int orderIndex,
        String input,
        String expectedOutput,
        String actualOutput,
        boolean passed,
        boolean hidden,
        long executionTimeMs,
        String errorMessage
) {
    public static TestCaseResultDto publicCase(UUID id, int order, String input, String expected, String actual, boolean passed, long timeMs, String error) {
        return new TestCaseResultDto(id, order, input, expected, actual, passed, false, timeMs, error);
    }

    public static TestCaseResultDto hiddenCase(UUID id, int order, boolean passed, long timeMs, String error) {
        return new TestCaseResultDto(id, order, null, null, null, passed, true, timeMs, error);
    }
}

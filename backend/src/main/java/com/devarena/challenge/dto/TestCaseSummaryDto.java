package com.devarena.challenge.dto;

import java.util.UUID;

public record TestCaseSummaryDto(
        UUID id,
        int orderIndex,
        String input,
        String expectedOutput,
        String explanation
) {}

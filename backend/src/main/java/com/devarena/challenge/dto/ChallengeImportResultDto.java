package com.devarena.challenge.dto;

import java.util.List;

public record ChallengeImportResultDto(
        int totalProcessed,
        int createdCount,
        int skippedCount,
        int errorCount,
        List<String> createdTitles,
        List<String> skippedTitles,
        List<String> errors
) {}

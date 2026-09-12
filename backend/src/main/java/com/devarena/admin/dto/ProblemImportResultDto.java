package com.devarena.admin.dto;

import java.util.List;

/**
 * Result DTO returned after a bulk problem import operation.
 */
public record ProblemImportResultDto(
        int totalReceived,
        int imported,
        int skippedDuplicates,
        int failedValidation,
        List<String> errors
) {}

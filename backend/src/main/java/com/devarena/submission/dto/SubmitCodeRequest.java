package com.devarena.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SubmitCodeRequest(
        @NotNull(message = "Challenge ID is required")
        UUID challengeId,

        @NotBlank(message = "Language is required")
        String language,

        @NotBlank(message = "Source code cannot be empty")
        @Size(max = 65536, message = "Source code exceeds the 64 KB limit")
        String sourceCode
) {}

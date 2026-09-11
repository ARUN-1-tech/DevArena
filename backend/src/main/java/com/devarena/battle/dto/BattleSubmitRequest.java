package com.devarena.battle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BattleSubmitRequest(
        @NotBlank(message = "Language is required")
        String language,

        @NotBlank(message = "Source code cannot be empty")
        @Size(max = 65536, message = "Source code exceeds 64 KB limit")
        String sourceCode
) {}

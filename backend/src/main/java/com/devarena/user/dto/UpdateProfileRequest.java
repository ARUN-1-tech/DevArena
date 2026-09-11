package com.devarena.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 2, max = 50, message = "Display name must be between 2 and 50 characters")
        String displayName,

        String avatar,

        @Size(max = 255, message = "Bio cannot exceed 255 characters")
        String bio
) {}

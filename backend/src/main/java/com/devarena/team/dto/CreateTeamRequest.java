package com.devarena.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTeamRequest(
        @NotBlank(message = "Team name cannot be blank")
        @Size(min = 3, max = 50, message = "Team name must be between 3 and 50 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        String avatar,

        int maxMembers
) {}

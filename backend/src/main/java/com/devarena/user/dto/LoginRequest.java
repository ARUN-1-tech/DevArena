package com.devarena.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email or username cannot be blank")
        String email,

        @NotBlank(message = "Password cannot be blank")
        String password
) {}

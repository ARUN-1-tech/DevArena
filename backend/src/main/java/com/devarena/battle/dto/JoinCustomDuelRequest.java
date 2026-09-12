package com.devarena.battle.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinCustomDuelRequest(
        @NotBlank(message = "Room code is required")
        String roomCode
) {}

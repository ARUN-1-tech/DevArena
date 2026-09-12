package com.devarena.battle.dto;

import java.util.UUID;

public record CreateCustomDuelRequest(
        UUID challengeId,
        String difficulty,
        Integer durationSeconds
) {}

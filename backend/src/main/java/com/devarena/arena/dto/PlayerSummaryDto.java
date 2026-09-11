package com.devarena.arena.dto;

import java.util.UUID;

public record PlayerSummaryDto(
        UUID id,
        String username,
        String displayName,
        String avatar,
        String bio
) {}

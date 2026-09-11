package com.devarena.arena.dto;

public record ProgressionMilestoneDto(
        String title,
        String description,
        int requiredLevel,
        boolean unlocked
) {}

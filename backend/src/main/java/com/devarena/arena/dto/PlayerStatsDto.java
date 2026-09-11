package com.devarena.arena.dto;

public record PlayerStatsDto(
        int rating,
        int wins,
        int losses,
        int draws,
        int winStreak,
        int highestRating
) {}

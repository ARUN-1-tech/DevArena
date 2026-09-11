package com.devarena.battle.dto;

import com.devarena.battle.model.PlayerBattleStatus;

import java.util.UUID;

public record BattlePlayerDto(
        UUID userId,
        String username,
        String avatarUrl,
        int level,
        int rating,
        PlayerBattleStatus status,
        boolean ready
) {}

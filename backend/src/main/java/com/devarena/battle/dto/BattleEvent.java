package com.devarena.battle.dto;

import java.time.Instant;
import java.util.UUID;

public record BattleEvent(
        String type,
        UUID battleId,
        Object payload,
        Instant timestamp
) {
    public static BattleEvent of(String type, UUID battleId, Object payload) {
        return new BattleEvent(type, battleId, payload, Instant.now());
    }
}

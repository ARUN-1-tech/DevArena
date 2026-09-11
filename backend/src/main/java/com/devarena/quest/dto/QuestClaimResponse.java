package com.devarena.quest.dto;

import com.devarena.progression.dto.XpRewardResult;
import com.devarena.quest.model.PlayerQuestStatus;

import java.util.UUID;

public record QuestClaimResponse(
        UUID id,
        PlayerQuestStatus status,
        int xpEarned,
        XpRewardResult xpResult
) {}

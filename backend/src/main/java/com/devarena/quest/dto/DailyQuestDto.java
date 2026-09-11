package com.devarena.quest.dto;

import com.devarena.quest.model.PlayerQuestStatus;
import com.devarena.quest.model.QuestType;

import java.util.UUID;

public record DailyQuestDto(
        UUID id,
        UUID questId,
        String title,
        String description,
        QuestType questType,
        int currentCount,
        int targetCount,
        int xpReward,
        PlayerQuestStatus status,
        boolean completed,
        boolean claimed
) {}

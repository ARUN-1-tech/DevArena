package com.devarena.quest.controller;

import com.devarena.common.api.ApiResponse;
import com.devarena.quest.dto.DailyQuestDto;
import com.devarena.quest.dto.QuestClaimResponse;
import com.devarena.quest.service.DailyQuestService;
import com.devarena.security.DevArenaUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quests")
public class DailyQuestController {

    private final DailyQuestService dailyQuestService;

    public DailyQuestController(DailyQuestService dailyQuestService) {
        this.dailyQuestService = dailyQuestService;
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<DailyQuestDto>>> getDailyQuests(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        List<DailyQuestDto> quests = dailyQuestService.getDailyQuestsForPlayer(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok(quests));
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<ApiResponse<QuestClaimResponse>> claimQuest(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        QuestClaimResponse response = dailyQuestService.claimQuest(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Quest reward claimed successfully!", response));
    }
}

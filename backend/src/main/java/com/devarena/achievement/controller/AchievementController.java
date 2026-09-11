package com.devarena.achievement.controller;

import com.devarena.achievement.dto.AchievementDto;
import com.devarena.achievement.dto.PlayerAchievementDto;
import com.devarena.achievement.service.AchievementService;
import com.devarena.common.api.ApiResponse;
import com.devarena.security.DevArenaUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AchievementDto>>> getPublicAchievements() {
        List<AchievementDto> achievements = achievementService.getPublicAchievements();
        return ResponseEntity.ok(ApiResponse.ok("Achievements catalog retrieved", achievements));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<PlayerAchievementDto>>> getMyAchievements(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        List<PlayerAchievementDto> achievements = achievementService.getPlayerAchievements(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Player achievements retrieved", achievements));
    }
}

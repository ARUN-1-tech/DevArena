package com.devarena.leaderboard.controller;

import com.devarena.common.api.ApiResponse;
import com.devarena.leaderboard.dto.LeaderboardResponse;
import com.devarena.leaderboard.service.LeaderboardService;
import com.devarena.security.DevArenaUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<LeaderboardResponse>> getLeaderboard(
            @RequestParam(defaultValue = "global") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        UUID currentUserId = userDetails != null ? userDetails.getId() : null;
        LeaderboardResponse response = leaderboardService.getLeaderboard(type, page, size, currentUserId);
        return ResponseEntity.ok(ApiResponse.ok("Leaderboard retrieved successfully", response));
    }
}

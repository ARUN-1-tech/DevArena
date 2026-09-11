package com.devarena.analytics.controller;

import com.devarena.analytics.dto.PlayerAnalyticsResponse;
import com.devarena.analytics.service.AnalyticsService;
import com.devarena.common.api.ApiResponse;
import com.devarena.security.DevArenaUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PlayerAnalyticsResponse>> getMyAnalytics(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        PlayerAnalyticsResponse analytics = analyticsService.getPlayerAnalytics(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Player analytics retrieved successfully", analytics));
    }
}

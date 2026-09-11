package com.devarena.ai.controller;

import com.devarena.ai.dto.AiCoachRequest;
import com.devarena.ai.dto.AiCoachResponseDto;
import com.devarena.ai.dto.PersonalizedRecommendationDto;
import com.devarena.ai.service.AiCoachService;
import com.devarena.ai.service.RecommendationService;
import com.devarena.common.api.ApiResponse;
import com.devarena.security.DevArenaUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class AiCoachController {

    private final AiCoachService aiCoachService;
    private final RecommendationService recommendationService;

    public AiCoachController(AiCoachService aiCoachService, RecommendationService recommendationService) {
        this.aiCoachService = aiCoachService;
        this.recommendationService = recommendationService;
    }

    @PostMapping("/coach")
    public ResponseEntity<ApiResponse<AiCoachResponseDto>> askCoach(
            @AuthenticationPrincipal DevArenaUserDetails userDetails,
            @Valid @RequestBody AiCoachRequest request) {
        AiCoachResponseDto response = aiCoachService.processRequest(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<PersonalizedRecommendationDto>> getRecommendations(
            @AuthenticationPrincipal DevArenaUserDetails userDetails) {
        PersonalizedRecommendationDto recommendations = recommendationService.getRecommendations(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok(recommendations));
    }
}

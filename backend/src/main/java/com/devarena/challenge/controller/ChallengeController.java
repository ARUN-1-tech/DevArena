package com.devarena.challenge.controller;

import com.devarena.challenge.dto.ChallengeCardDto;
import com.devarena.challenge.dto.ChallengeDetailDto;
import com.devarena.challenge.dto.ChallengeProgressDto;
import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ProblemType;
import com.devarena.challenge.service.ChallengeService;
import com.devarena.common.api.ApiResponse;
import com.devarena.progression.dto.XpRewardResult;
import com.devarena.security.DevArenaUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ChallengeCardDto>>> getChallenges(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ChallengeDifficulty difficulty,
            @RequestParam(required = false) ChallengeCategory category,
            @RequestParam(required = false) ProblemType type,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        UUID userId = userDetails != null ? userDetails.getId() : null;

        Sort sortOrder = switch (sort) {
            case "xp_desc" -> Sort.by("xpReward").descending();
            case "xp_asc" -> Sort.by("xpReward").ascending();
            case "difficulty_asc" -> Sort.by("difficulty").ascending();
            case "difficulty_desc" -> Sort.by("difficulty").descending();
            case "title_asc" -> Sort.by("title").ascending();
            default -> Sort.by("createdAt").descending();
        };

        Page<ChallengeCardDto> result = challengeService.getChallenges(
                search, difficulty, category, type,
                PageRequest.of(page, size, sortOrder),
                userId
        );
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChallengeDetailDto>> getChallengeDetail(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        UUID userId = userDetails != null ? userDetails.getId() : null;
        ChallengeDetailDto detail = challengeService.getChallengeDetail(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<ChallengeProgressDto>> getChallengeProgress(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        ChallengeProgressDto progress = challengeService.getChallengeProgress(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok(progress));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<ChallengeProgressDto>> startChallenge(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        ChallengeProgressDto progress = challengeService.startChallenge(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Challenge started", progress));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<XpRewardResult>> completeChallenge(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        XpRewardResult result = challengeService.solveChallenge(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Challenge solved! XP awarded.", result));
    }
}

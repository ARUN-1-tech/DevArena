package com.devarena.battle.controller;

import com.devarena.battle.dto.*;
import com.devarena.battle.service.BattleService;
import com.devarena.common.api.ApiResponse;
import com.devarena.security.DevArenaUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/battles")
public class BattleController {

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BattleDetailResponse>> getBattle(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        BattleDetailResponse detail = battleService.getBattle(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<ApiResponse<BattleResultResponse>> getBattleResult(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        BattleResultResponse result = battleService.getBattleResult(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<BattleHistoryItemDto>>> getBattleHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        Page<BattleHistoryItemDto> history = battleService.getBattleHistory(
                userDetails.getId(),
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
        return ResponseEntity.ok(ApiResponse.ok(history));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<BattleSubmitResponse>> submitCode(
            @PathVariable UUID id,
            @Valid @RequestBody BattleSubmitRequest request,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        BattleSubmitResponse response = battleService.submitCode(id, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Battle code evaluated", response));
    }

    @PostMapping("/{id}/ready")
    public ResponseEntity<ApiResponse<BattleDetailResponse>> markReady(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        battleService.markPlayerReady(id, userDetails.getId());
        BattleDetailResponse detail = battleService.getBattle(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Player ready confirmed", detail));
    }

    @PostMapping("/{id}/forfeit")
    public ResponseEntity<ApiResponse<Void>> forfeitBattle(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        battleService.forfeitBattle(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Forfeited battle", null));
    }
}

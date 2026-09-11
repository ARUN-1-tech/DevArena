package com.devarena.matchmaking.controller;

import com.devarena.common.api.ApiResponse;
import com.devarena.matchmaking.dto.MatchmakingStatusResponse;
import com.devarena.matchmaking.service.MatchmakingService;
import com.devarena.security.DevArenaUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matchmaking")
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    public MatchmakingController(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<MatchmakingStatusResponse>> joinQueue(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        MatchmakingStatusResponse status = matchmakingService.joinQueue(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Joined matchmaking queue", status));
    }

    @DeleteMapping("/leave")
    public ResponseEntity<ApiResponse<Void>> leaveQueue(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        matchmakingService.leaveQueue(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Left matchmaking queue", null));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<MatchmakingStatusResponse>> getStatus(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        MatchmakingStatusResponse status = matchmakingService.getQueueStatus(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok(status));
    }
}

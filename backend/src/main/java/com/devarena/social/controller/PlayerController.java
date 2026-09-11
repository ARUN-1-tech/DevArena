package com.devarena.social.controller;

import com.devarena.common.api.ApiResponse;
import com.devarena.security.DevArenaUserDetails;
import com.devarena.social.dto.PlayerSearchResultDto;
import com.devarena.social.dto.PublicPlayerProfileDto;
import com.devarena.social.service.PlayerDiscoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final PlayerDiscoveryService playerDiscoveryService;

    public PlayerController(PlayerDiscoveryService playerDiscoveryService) {
        this.playerDiscoveryService = playerDiscoveryService;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PlayerSearchResultDto>>> searchPlayers(
            @RequestParam(name = "q", defaultValue = "") String query,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        UUID currentUserId = userDetails != null ? userDetails.getId() : null;
        List<PlayerSearchResultDto> results = playerDiscoveryService.searchPlayers(query, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<PublicPlayerProfileDto>> getPublicProfile(
            @PathVariable String username,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        UUID currentUserId = userDetails != null ? userDetails.getId() : null;
        PublicPlayerProfileDto profile = playerDiscoveryService.getPublicProfile(username, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
}

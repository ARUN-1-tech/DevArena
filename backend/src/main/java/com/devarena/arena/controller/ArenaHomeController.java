package com.devarena.arena.controller;

import com.devarena.arena.dto.ArenaHomeDto;
import com.devarena.arena.service.ArenaHomeService;
import com.devarena.common.api.ApiResponse;
import com.devarena.security.DevArenaUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/arena")
public class ArenaHomeController {

    private final ArenaHomeService arenaHomeService;

    public ArenaHomeController(ArenaHomeService arenaHomeService) {
        this.arenaHomeService = arenaHomeService;
    }

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<ArenaHomeDto>> getArenaHome(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        ArenaHomeDto data = arenaHomeService.getArenaHomeData(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}

package com.devarena.battle.controller;

import com.devarena.battle.dto.CreateCustomDuelRequest;
import com.devarena.battle.dto.CustomDuelRoomDto;
import com.devarena.battle.dto.JoinCustomDuelRequest;
import com.devarena.battle.service.CustomDuelService;
import com.devarena.common.api.ApiResponse;
import com.devarena.security.DevArenaUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/battles/custom")
public class CustomDuelController {

    private final CustomDuelService customDuelService;

    public CustomDuelController(CustomDuelService customDuelService) {
        this.customDuelService = customDuelService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CustomDuelRoomDto>> createRoom(
            @RequestBody(required = false) CreateCustomDuelRequest request,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        CustomDuelRoomDto room = customDuelService.createRoom(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Custom duel room created", room));
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<CustomDuelRoomDto>> joinRoom(
            @Valid @RequestBody JoinCustomDuelRequest request,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        CustomDuelRoomDto room = customDuelService.joinRoom(request.roomCode(), userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Joined custom duel room, entering arena", room));
    }

    @GetMapping("/room/{roomCode}")
    public ResponseEntity<ApiResponse<CustomDuelRoomDto>> getRoom(
            @PathVariable String roomCode
    ) {
        CustomDuelRoomDto room = customDuelService.getRoom(roomCode);
        return ResponseEntity.ok(ApiResponse.ok(room));
    }

    @PostMapping("/room/{roomCode}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelRoom(
            @PathVariable String roomCode,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        customDuelService.cancelRoom(roomCode, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Custom duel room cancelled", null));
    }
}

package com.devarena.social.controller;

import com.devarena.common.api.ApiResponse;
import com.devarena.security.DevArenaUserDetails;
import com.devarena.social.dto.FriendBattleInviteDto;
import com.devarena.social.dto.FriendDto;
import com.devarena.social.dto.FriendRequestDto;
import com.devarena.social.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FriendDto>>> getFriends(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        List<FriendDto> friends = friendService.getFriends(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(friends));
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<Map<String, List<FriendRequestDto>>>> getFriendRequests(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        Map<String, List<FriendRequestDto>> requests = friendService.getFriendRequests(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @PostMapping("/requests/{playerId}")
    public ResponseEntity<ApiResponse<FriendRequestDto>> sendFriendRequest(
            @PathVariable UUID playerId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        FriendRequestDto request = friendService.sendFriendRequest(userDetails.getId(), playerId);
        return ResponseEntity.ok(ApiResponse.success("Friend request sent", request));
    }

    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<FriendDto>> acceptFriendRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        FriendDto friend = friendService.acceptFriendRequest(requestId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Friend request accepted", friend));
    }

    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectFriendRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        friendService.rejectFriendRequest(requestId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Friend request declined", null));
    }

    @DeleteMapping("/{playerId}")
    public ResponseEntity<ApiResponse<Void>> removeFriend(
            @PathVariable UUID playerId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        friendService.removeFriend(userDetails.getId(), playerId);
        return ResponseEntity.ok(ApiResponse.success("Friend removed", null));
    }

    @PostMapping("/challenge/{friendId}")
    public ResponseEntity<ApiResponse<FriendBattleInviteDto>> challengeFriend(
            @PathVariable UUID friendId,
            @RequestParam(required = false) UUID challengeId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        FriendBattleInviteDto invite = friendService.sendBattleInvite(userDetails.getId(), friendId, challengeId);
        return ResponseEntity.ok(ApiResponse.success("Duel challenge sent", invite));
    }

    @PostMapping("/challenge/{inviteId}/accept")
    public ResponseEntity<ApiResponse<Map<String, Object>>> acceptChallenge(
            @PathVariable UUID inviteId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        Map<String, Object> result = friendService.acceptBattleInvite(inviteId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Duel accepted, joining battle room", result));
    }

    @GetMapping("/challenge/pending")
    public ResponseEntity<ApiResponse<java.util.List<FriendBattleInviteDto>>> getPendingChallenges(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        java.util.List<FriendBattleInviteDto> invites = friendService.getPendingBattleInvites(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok(invites));
    }

    @PostMapping("/challenge/{inviteId}/decline")
    public ResponseEntity<ApiResponse<Void>> declineChallenge(
            @PathVariable UUID inviteId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        friendService.declineBattleInvite(inviteId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Duel challenge declined", null));
    }

    @GetMapping("/challenge/{inviteId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getChallengeStatus(
            @PathVariable UUID inviteId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        Map<String, Object> status = friendService.getBattleInviteStatus(inviteId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok(status));
    }
}

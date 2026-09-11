package com.devarena.team.controller;

import com.devarena.common.api.ApiResponse;
import com.devarena.security.DevArenaUserDetails;
import com.devarena.team.dto.*;
import com.devarena.team.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TeamDto>> createTeam(
            @Valid @RequestBody CreateTeamRequest request,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        TeamDto team = teamService.createTeam(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Team created successfully", team));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TeamDto>> getMyTeam(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        TeamDto team = teamService.getMyTeam(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(team));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamDto>> getTeam(@PathVariable UUID id) {
        TeamDto team = teamService.getTeam(id);
        return ResponseEntity.ok(ApiResponse.success(team));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TeamDto>>> searchTeams(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<TeamDto> teams = teamService.searchTeams(query, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(teams));
    }

    @PostMapping("/{id}/invite/{playerId}")
    public ResponseEntity<ApiResponse<TeamInviteDto>> invitePlayer(
            @PathVariable UUID id,
            @PathVariable UUID playerId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        TeamInviteDto invite = teamService.invitePlayer(id, userDetails.getId(), playerId);
        return ResponseEntity.ok(ApiResponse.success("Team invitation sent", invite));
    }

    @PostMapping("/invites/{inviteId}/accept")
    public ResponseEntity<ApiResponse<TeamDto>> acceptInvite(
            @PathVariable UUID inviteId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        TeamDto team = teamService.acceptInvite(inviteId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Joined team successfully", team));
    }

    @PostMapping("/invites/{inviteId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectInvite(
            @PathVariable UUID inviteId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        teamService.rejectInvite(inviteId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Team invite declined", null));
    }

    @DeleteMapping("/{id}/members/{playerId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable UUID id,
            @PathVariable UUID playerId,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        teamService.removeMember(id, userDetails.getId(), playerId);
        return ResponseEntity.ok(ApiResponse.success("Member removed from team", null));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveTeam(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        teamService.leaveTeam(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Left team successfully", null));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<Page<TeamLeaderboardDto>>> getTeamLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<TeamLeaderboardDto> leaderboard = teamService.getTeamLeaderboard(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }
}

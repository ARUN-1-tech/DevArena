package com.devarena.admin.controller;

import com.devarena.admin.dto.*;
import com.devarena.admin.service.AdminAuditService;
import com.devarena.admin.service.AdminChallengeService;
import com.devarena.admin.service.AdminDashboardService;
import com.devarena.admin.service.AdminPlayerService;
import com.devarena.challenge.model.ChallengeStatus;
import com.devarena.common.api.ApiResponse;
import com.devarena.integrity.dto.IntegrityEventDto;
import com.devarena.integrity.service.IntegrityService;
import com.devarena.moderation.dto.ReportDto;
import com.devarena.moderation.dto.ResolveReportRequest;
import com.devarena.moderation.model.ReportStatus;
import com.devarena.moderation.service.ReportService;
import com.devarena.security.DevArenaUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminDashboardService dashboardService;
    private final AdminPlayerService playerService;
    private final AdminChallengeService challengeService;
    private final ReportService reportService;
    private final IntegrityService integrityService;
    private final AdminAuditService auditService;

    public AdminController(
            AdminDashboardService dashboardService,
            AdminPlayerService playerService,
            AdminChallengeService challengeService,
            ReportService reportService,
            IntegrityService integrityService,
            AdminAuditService auditService) {
        this.dashboardService = dashboardService;
        this.playerService = playerService;
        this.challengeService = challengeService;
        this.reportService = reportService;
        this.integrityService = integrityService;
        this.auditService = auditService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<AdminOverviewDto>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getOverview()));
    }

    @GetMapping("/players")
    public ResponseEntity<ApiResponse<Page<AdminPlayerDto>>> getPlayers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(playerService.getPlayers(search, pageable)));
    }

    @PostMapping("/players/{userId}/suspend")
    public ResponseEntity<ApiResponse<AdminPlayerDto>> suspendPlayer(
            @PathVariable UUID userId,
            @AuthenticationPrincipal DevArenaUserDetails adminDetails,
            @Valid @RequestBody SuspendPlayerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(playerService.suspendPlayer(userId, adminDetails.getId(), request)));
    }

    @PostMapping("/players/{userId}/restore")
    public ResponseEntity<ApiResponse<AdminPlayerDto>> restorePlayer(
            @PathVariable UUID userId,
            @AuthenticationPrincipal DevArenaUserDetails adminDetails) {
        return ResponseEntity.ok(ApiResponse.ok(playerService.restorePlayer(userId, adminDetails.getId())));
    }

    @GetMapping("/challenges")
    public ResponseEntity<ApiResponse<Page<AdminChallengeDto>>> getChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(challengeService.getAllChallenges(pageable)));
    }

    @PostMapping("/challenges")
    public ResponseEntity<ApiResponse<AdminChallengeDto>> createChallenge(
            @AuthenticationPrincipal DevArenaUserDetails adminDetails,
            @Valid @RequestBody UpsertChallengeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Challenge created", challengeService.createChallenge(adminDetails.getId(), request)));
    }

    @PutMapping("/challenges/{challengeId}")
    public ResponseEntity<ApiResponse<AdminChallengeDto>> updateChallenge(
            @PathVariable UUID challengeId,
            @AuthenticationPrincipal DevArenaUserDetails adminDetails,
            @Valid @RequestBody UpsertChallengeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(challengeService.updateChallenge(challengeId, adminDetails.getId(), request)));
    }

    @PatchMapping("/challenges/{challengeId}/status")
    public ResponseEntity<ApiResponse<AdminChallengeDto>> updateChallengeStatus(
            @PathVariable UUID challengeId,
            @AuthenticationPrincipal DevArenaUserDetails adminDetails,
            @RequestBody Map<String, String> body) {
        ChallengeStatus status = ChallengeStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(ApiResponse.ok(challengeService.updateStatus(challengeId, adminDetails.getId(), status)));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<Page<ReportDto>>> getReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(reportService.getReports(status, pageable)));
    }

    @PostMapping("/reports/{reportId}/resolve")
    public ResponseEntity<ApiResponse<ReportDto>> resolveReport(
            @PathVariable UUID reportId,
            @AuthenticationPrincipal DevArenaUserDetails adminDetails,
            @Valid @RequestBody ResolveReportRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.resolveReport(reportId, adminDetails.getId(), request)));
    }

    @GetMapping("/integrity")
    public ResponseEntity<ApiResponse<Page<IntegrityEventDto>>> getIntegrityEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(integrityService.getUnreviewedEvents(pageable)));
    }

    @PostMapping("/integrity/{eventId}/review")
    public ResponseEntity<ApiResponse<IntegrityEventDto>> reviewIntegrityEvent(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal DevArenaUserDetails adminDetails) {
        return ResponseEntity.ok(ApiResponse.ok(integrityService.reviewEvent(eventId, adminDetails.getId())));
    }

    @GetMapping("/audit")
    public ResponseEntity<ApiResponse<Page<AdminAuditDto>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(auditService.getAuditLogs(pageable)));
    }
}

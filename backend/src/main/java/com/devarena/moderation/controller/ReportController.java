package com.devarena.moderation.controller;

import com.devarena.common.api.ApiResponse;
import com.devarena.moderation.dto.CreateReportRequest;
import com.devarena.moderation.dto.ReportDto;
import com.devarena.moderation.service.ReportService;
import com.devarena.security.DevArenaUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReportDto>> createReport(
            @AuthenticationPrincipal DevArenaUserDetails userDetails,
            @Valid @RequestBody CreateReportRequest request) {
        ReportDto report = reportService.createReport(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Report submitted successfully", report));
    }
}

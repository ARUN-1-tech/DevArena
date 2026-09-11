package com.devarena.submission.controller;

import com.devarena.common.api.ApiResponse;
import com.devarena.security.DevArenaUserDetails;
import com.devarena.submission.dto.SubmissionDetailDto;
import com.devarena.submission.dto.SubmissionSummaryDto;
import com.devarena.submission.dto.SubmitCodeRequest;
import com.devarena.submission.dto.SubmitCodeResponse;
import com.devarena.submission.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/submissions")
    public ResponseEntity<ApiResponse<SubmitCodeResponse>> submitCode(
            @Valid @RequestBody SubmitCodeRequest request,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        SubmitCodeResponse response = submissionService.submitCode(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Submission evaluated", response));
    }

    @GetMapping("/submissions/{id}")
    public ResponseEntity<ApiResponse<SubmissionDetailDto>> getSubmission(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        SubmissionDetailDto detail = submissionService.getSubmission(userDetails.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }

    @GetMapping("/challenges/{id}/submissions")
    public ResponseEntity<ApiResponse<Page<SubmissionSummaryDto>>> getChallengeSubmissions(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        Page<SubmissionSummaryDto> submissions = submissionService.getChallengeSubmissions(
                userDetails.getId(),
                id,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
        return ResponseEntity.ok(ApiResponse.ok(submissions));
    }
}

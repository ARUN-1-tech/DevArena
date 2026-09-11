package com.devarena.execution.controller;

import com.devarena.common.api.ApiResponse;
import com.devarena.execution.dto.RunCodeRequest;
import com.devarena.execution.dto.RunCodeResponse;
import com.devarena.security.DevArenaUserDetails;
import com.devarena.submission.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/code")
public class CodeExecutionController {

    private final SubmissionService submissionService;

    public CodeExecutionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/run")
    public ResponseEntity<ApiResponse<RunCodeResponse>> runCode(
            @Valid @RequestBody RunCodeRequest request,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        RunCodeResponse response = submissionService.runCode(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Code evaluated successfully", response));
    }
}

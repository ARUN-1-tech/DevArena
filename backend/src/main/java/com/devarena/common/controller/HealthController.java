package com.devarena.common.controller;

import com.devarena.common.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Foundation controller providing health checks, status diagnostics,
 * and endpoint discovery for DevArena Module 01.
 */
@RestController
public class HealthController {

    @GetMapping({"/health", "/api/v1/health"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkHealth() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("service", "DevArena Backend");
        health.put("motto", "Code. Compete. Level Up.");
        health.put("module", "MODULE_01");
        health.put("serverTime", Instant.now().toString());

        return ResponseEntity.ok(ApiResponse.success("DevArena foundation is operational", health));
    }

    @GetMapping({"/status", "/api/v1/status"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("version", "1.0.0-SNAPSHOT");
        status.put("environment", "active");
        status.put("activeModules", List.of("FOUNDATION_M01"));
        status.put("futureModules", List.of(
                "USER_MANAGEMENT_M02",
                "AUTH_SECURITY_M03",
                "CHALLENGES_M04",
                "CODE_EXECUTION_M05",
                "BATTLES_WEBSOCKET_M06",
                "MATCHMAKING_M07",
                "GAMIFICATION_M08",
                "NOTIFICATIONS_M09",
                "ANALYTICS_M10"
        ));

        return ResponseEntity.ok(ApiResponse.success("System status loaded", status));
    }

    /**
     * Diagnostic endpoint for automated test verification of validation handling.
     */
    @PostMapping({"/test-validation", "/api/v1/test-validation"})
    public ResponseEntity<ApiResponse<String>> testValidation(@Valid @RequestBody ValidationSampleRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Validated successfully: " + request.name()));
    }

    public record ValidationSampleRequest(
            @NotBlank(message = "Field 'name' cannot be blank")
            @Size(min = 3, max = 50, message = "Field 'name' must be between 3 and 50 characters")
            String name
    ) {}
}

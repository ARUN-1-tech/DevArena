package com.devarena.admin.dto;

import jakarta.validation.constraints.NotBlank;

public class SuspendPlayerRequest {

    @NotBlank(message = "Reason is required")
    private String reason;

    public SuspendPlayerRequest() {}

    public SuspendPlayerRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

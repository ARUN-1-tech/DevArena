package com.devarena.moderation.dto;

import com.devarena.moderation.model.ReportReason;
import com.devarena.moderation.model.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateReportRequest {

    @NotNull(message = "targetType is required")
    private ReportTargetType targetType;

    @NotBlank(message = "targetId is required")
    private String targetId;

    @NotNull(message = "reason is required")
    private ReportReason reason;

    private String description;

    public CreateReportRequest() {}

    public CreateReportRequest(ReportTargetType targetType, String targetId, ReportReason reason, String description) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.description = description;
    }

    public ReportTargetType getTargetType() { return targetType; }
    public void setTargetType(ReportTargetType targetType) { this.targetType = targetType; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public ReportReason getReason() { return reason; }
    public void setReason(ReportReason reason) { this.reason = reason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

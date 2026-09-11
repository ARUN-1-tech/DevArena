package com.devarena.moderation.dto;

import com.devarena.moderation.model.ReportStatus;
import jakarta.validation.constraints.NotNull;

public class ResolveReportRequest {

    @NotNull(message = "status is required")
    private ReportStatus status;

    private String resolutionNotes;

    public ResolveReportRequest() {}

    public ResolveReportRequest(ReportStatus status, String resolutionNotes) {
        this.status = status;
        this.resolutionNotes = resolutionNotes;
    }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
}

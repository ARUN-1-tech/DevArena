package com.devarena.moderation.dto;

import com.devarena.moderation.model.ReportReason;
import com.devarena.moderation.model.ReportStatus;
import com.devarena.moderation.model.ReportTargetType;

import java.time.Instant;
import java.util.UUID;

public class ReportDto {

    private UUID id;
    private UUID reporterId;
    private String reporterUsername;
    private ReportTargetType targetType;
    private String targetId;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private String resolutionNotes;
    private Instant createdAt;
    private Instant resolvedAt;
    private String resolvedByUsername;

    public ReportDto() {}

    public ReportDto(UUID id, UUID reporterId, String reporterUsername, ReportTargetType targetType,
                     String targetId, ReportReason reason, String description, ReportStatus status,
                     String resolutionNotes, Instant createdAt, Instant resolvedAt, String resolvedByUsername) {
        this.id = id;
        this.reporterId = reporterId;
        this.reporterUsername = reporterUsername;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.description = description;
        this.status = status;
        this.resolutionNotes = resolutionNotes;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.resolvedByUsername = resolvedByUsername;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getReporterId() { return reporterId; }
    public void setReporterId(UUID reporterId) { this.reporterId = reporterId; }

    public String getReporterUsername() { return reporterUsername; }
    public void setReporterUsername(String reporterUsername) { this.reporterUsername = reporterUsername; }

    public ReportTargetType getTargetType() { return targetType; }
    public void setTargetType(ReportTargetType targetType) { this.targetType = targetType; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public ReportReason getReason() { return reason; }
    public void setReason(ReportReason reason) { this.reason = reason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getResolvedByUsername() { return resolvedByUsername; }
    public void setResolvedByUsername(String resolvedByUsername) { this.resolvedByUsername = resolvedByUsername; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private UUID reporterId;
        private String reporterUsername;
        private ReportTargetType targetType;
        private String targetId;
        private ReportReason reason;
        private String description;
        private ReportStatus status;
        private String resolutionNotes;
        private Instant createdAt;
        private Instant resolvedAt;
        private String resolvedByUsername;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder reporterId(UUID reporterId) { this.reporterId = reporterId; return this; }
        public Builder reporterUsername(String reporterUsername) { this.reporterUsername = reporterUsername; return this; }
        public Builder targetType(ReportTargetType targetType) { this.targetType = targetType; return this; }
        public Builder targetId(String targetId) { this.targetId = targetId; return this; }
        public Builder reason(ReportReason reason) { this.reason = reason; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder status(ReportStatus status) { this.status = status; return this; }
        public Builder resolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder resolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public Builder resolvedByUsername(String resolvedByUsername) { this.resolvedByUsername = resolvedByUsername; return this; }

        public ReportDto build() {
            return new ReportDto(id, reporterId, reporterUsername, targetType, targetId, reason, description, status, resolutionNotes, createdAt, resolvedAt, resolvedByUsername);
        }
    }
}

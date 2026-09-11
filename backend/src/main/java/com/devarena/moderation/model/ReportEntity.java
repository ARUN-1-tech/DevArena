package com.devarena.moderation.model;

import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reports")
public class ReportEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserEntity reporter;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 32)
    private ReportTargetType targetType;

    @Column(name = "target_id", nullable = false, length = 64)
    private String targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 64)
    private ReportReason reason;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReportStatus status;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id")
    private UserEntity resolvedBy;

    public ReportEntity() {}

    public ReportEntity(UserEntity reporter, ReportTargetType targetType, String targetId, ReportReason reason, String description, ReportStatus status) {
        this.reporter = reporter;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.description = description;
        this.status = status;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (status == null) {
            status = ReportStatus.OPEN;
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UserEntity getReporter() { return reporter; }
    public void setReporter(UserEntity reporter) { this.reporter = reporter; }

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

    public UserEntity getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(UserEntity resolvedBy) { this.resolvedBy = resolvedBy; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UserEntity reporter;
        private ReportTargetType targetType;
        private String targetId;
        private ReportReason reason;
        private String description;
        private ReportStatus status = ReportStatus.OPEN;

        public Builder reporter(UserEntity reporter) { this.reporter = reporter; return this; }
        public Builder targetType(ReportTargetType targetType) { this.targetType = targetType; return this; }
        public Builder targetId(String targetId) { this.targetId = targetId; return this; }
        public Builder reason(ReportReason reason) { this.reason = reason; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder status(ReportStatus status) { this.status = status; return this; }

        public ReportEntity build() {
            return new ReportEntity(reporter, targetType, targetId, reason, description, status);
        }
    }
}

package com.devarena.integrity.model;

import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "integrity_events")
public class IntegrityEventEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 64)
    private IntegrityEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private IntegritySeverity severity;

    @Column(name = "risk_score", nullable = false)
    private int riskScore;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "reviewed", nullable = false)
    private boolean reviewed;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public IntegrityEventEntity() {}

    public IntegrityEventEntity(UserEntity user, IntegrityEventType eventType, IntegritySeverity severity, int riskScore, String metadata, boolean reviewed) {
        this.user = user;
        this.eventType = eventType;
        this.severity = severity;
        this.riskScore = riskScore;
        this.metadata = metadata;
        this.reviewed = reviewed;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (riskScore <= 0) {
            riskScore = 10;
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public IntegrityEventType getEventType() { return eventType; }
    public void setEventType(IntegrityEventType eventType) { this.eventType = eventType; }

    public IntegritySeverity getSeverity() { return severity; }
    public void setSeverity(IntegritySeverity severity) { this.severity = severity; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public boolean isReviewed() { return reviewed; }
    public void setReviewed(boolean reviewed) { this.reviewed = reviewed; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UserEntity user;
        private IntegrityEventType eventType;
        private IntegritySeverity severity;
        private int riskScore = 10;
        private String metadata;
        private boolean reviewed = false;

        public Builder user(UserEntity user) { this.user = user; return this; }
        public Builder eventType(IntegrityEventType eventType) { this.eventType = eventType; return this; }
        public Builder severity(IntegritySeverity severity) { this.severity = severity; return this; }
        public Builder riskScore(int riskScore) { this.riskScore = riskScore; return this; }
        public Builder metadata(String metadata) { this.metadata = metadata; return this; }
        public Builder reviewed(boolean reviewed) { this.reviewed = reviewed; return this; }

        public IntegrityEventEntity build() {
            return new IntegrityEventEntity(user, eventType, severity, riskScore, metadata, reviewed);
        }
    }
}

package com.devarena.integrity.dto;

import com.devarena.integrity.model.IntegrityEventType;
import com.devarena.integrity.model.IntegritySeverity;

import java.time.Instant;
import java.util.UUID;

public class IntegrityEventDto {

    private UUID id;
    private UUID userId;
    private String username;
    private IntegrityEventType eventType;
    private IntegritySeverity severity;
    private int riskScore;
    private String metadata;
    private boolean reviewed;
    private Instant createdAt;

    public IntegrityEventDto() {}

    public IntegrityEventDto(UUID id, UUID userId, String username, IntegrityEventType eventType,
                             IntegritySeverity severity, int riskScore, String metadata,
                             boolean reviewed, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.eventType = eventType;
        this.severity = severity;
        this.riskScore = riskScore;
        this.metadata = metadata;
        this.reviewed = reviewed;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

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
        private UUID id;
        private UUID userId;
        private String username;
        private IntegrityEventType eventType;
        private IntegritySeverity severity;
        private int riskScore;
        private String metadata;
        private boolean reviewed;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder eventType(IntegrityEventType eventType) { this.eventType = eventType; return this; }
        public Builder severity(IntegritySeverity severity) { this.severity = severity; return this; }
        public Builder riskScore(int riskScore) { this.riskScore = riskScore; return this; }
        public Builder metadata(String metadata) { this.metadata = metadata; return this; }
        public Builder reviewed(boolean reviewed) { this.reviewed = reviewed; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public IntegrityEventDto build() {
            return new IntegrityEventDto(id, userId, username, eventType, severity, riskScore, metadata, reviewed, createdAt);
        }
    }
}

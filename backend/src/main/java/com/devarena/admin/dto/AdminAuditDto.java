package com.devarena.admin.dto;

import com.devarena.admin.model.AdminAuditAction;

import java.time.Instant;
import java.util.UUID;

public class AdminAuditDto {

    private UUID id;
    private UUID actorId;
    private String actorUsername;
    private AdminAuditAction action;
    private String targetType;
    private String targetId;
    private String metadata;
    private Instant createdAt;

    public AdminAuditDto() {}

    public AdminAuditDto(UUID id, UUID actorId, String actorUsername, AdminAuditAction action,
                         String targetType, String targetId, String metadata, Instant createdAt) {
        this.id = id;
        this.actorId = actorId;
        this.actorUsername = actorUsername;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.metadata = metadata;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getActorId() { return actorId; }
    public void setActorId(UUID actorId) { this.actorId = actorId; }

    public String getActorUsername() { return actorUsername; }
    public void setActorUsername(String actorUsername) { this.actorUsername = actorUsername; }

    public AdminAuditAction getAction() { return action; }
    public void setAction(AdminAuditAction action) { this.action = action; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private UUID actorId;
        private String actorUsername;
        private AdminAuditAction action;
        private String targetType;
        private String targetId;
        private String metadata;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder actorId(UUID actorId) { this.actorId = actorId; return this; }
        public Builder actorUsername(String actorUsername) { this.actorUsername = actorUsername; return this; }
        public Builder action(AdminAuditAction action) { this.action = action; return this; }
        public Builder targetType(String targetType) { this.targetType = targetType; return this; }
        public Builder targetId(String targetId) { this.targetId = targetId; return this; }
        public Builder metadata(String metadata) { this.metadata = metadata; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public AdminAuditDto build() {
            return new AdminAuditDto(id, actorId, actorUsername, action, targetType, targetId, metadata, createdAt);
        }
    }
}

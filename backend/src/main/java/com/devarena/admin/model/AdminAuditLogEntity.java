package com.devarena.admin.model;

import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admin_audit_logs")
public class AdminAuditLogEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private UserEntity actor;

    @Column(name = "actor_username", nullable = false, length = 100)
    private String actorUsername;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 64)
    private AdminAuditAction action;

    @Column(name = "target_type", nullable = false, length = 32)
    private String targetType;

    @Column(name = "target_id", nullable = false, length = 64)
    private String targetId;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public AdminAuditLogEntity() {}

    public AdminAuditLogEntity(UserEntity actor, String actorUsername, AdminAuditAction action, String targetType, String targetId, String metadata) {
        this.actor = actor;
        this.actorUsername = actorUsername;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.metadata = metadata;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UserEntity getActor() { return actor; }
    public void setActor(UserEntity actor) { this.actor = actor; }

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
        private UserEntity actor;
        private String actorUsername;
        private AdminAuditAction action;
        private String targetType;
        private String targetId;
        private String metadata;

        public Builder actor(UserEntity actor) { this.actor = actor; return this; }
        public Builder actorUsername(String actorUsername) { this.actorUsername = actorUsername; return this; }
        public Builder action(AdminAuditAction action) { this.action = action; return this; }
        public Builder targetType(String targetType) { this.targetType = targetType; return this; }
        public Builder targetId(String targetId) { this.targetId = targetId; return this; }
        public Builder metadata(String metadata) { this.metadata = metadata; return this; }

        public AdminAuditLogEntity build() {
            return new AdminAuditLogEntity(actor, actorUsername, action, targetType, targetId, metadata);
        }
    }
}

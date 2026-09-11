package com.devarena.admin.service;

import com.devarena.admin.dto.AdminAuditDto;
import com.devarena.admin.model.AdminAuditAction;
import com.devarena.admin.model.AdminAuditLogEntity;
import com.devarena.admin.repository.AdminAuditLogRepository;
import com.devarena.user.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AdminAuditService {

    private final AdminAuditLogRepository auditLogRepository;

    public AdminAuditService(AdminAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void logAction(UserEntity actor, AdminAuditAction action, String targetType, String targetId, String metadata) {
        AdminAuditLogEntity log = AdminAuditLogEntity.builder()
                .actor(actor)
                .actorUsername(actor != null ? actor.getUsername() : "SYSTEM")
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .metadata(metadata)
                .build();
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AdminAuditDto> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(log -> AdminAuditDto.builder()
                        .id(log.getId())
                        .actorId(log.getActor() != null ? log.getActor().getId() : null)
                        .actorUsername(log.getActorUsername())
                        .action(log.getAction())
                        .targetType(log.getTargetType())
                        .targetId(log.getTargetId())
                        .metadata(log.getMetadata())
                        .createdAt(log.getCreatedAt())
                        .build());
    }
}

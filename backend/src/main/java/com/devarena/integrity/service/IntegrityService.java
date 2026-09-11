package com.devarena.integrity.service;

import com.devarena.admin.model.AdminAuditAction;
import com.devarena.admin.service.AdminAuditService;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.integrity.dto.IntegrityEventDto;
import com.devarena.integrity.model.IntegrityEventEntity;
import com.devarena.integrity.model.IntegrityEventType;
import com.devarena.integrity.model.IntegritySeverity;
import com.devarena.integrity.repository.IntegrityEventRepository;
import com.devarena.user.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IntegrityService {

    private final IntegrityEventRepository integrityEventRepository;
    private final AdminAuditService adminAuditService;
    private final com.devarena.user.repository.UserRepository userRepository;

    public IntegrityService(
            IntegrityEventRepository integrityEventRepository,
            AdminAuditService adminAuditService,
            com.devarena.user.repository.UserRepository userRepository) {
        this.integrityEventRepository = integrityEventRepository;
        this.adminAuditService = adminAuditService;
        this.userRepository = userRepository;
    }

    @Transactional
    public IntegrityEventEntity recordEvent(UserEntity user, IntegrityEventType type, IntegritySeverity severity, int riskScore, String metadata) {
        IntegrityEventEntity event = IntegrityEventEntity.builder()
                .user(user)
                .eventType(type)
                .severity(severity)
                .riskScore(riskScore)
                .metadata(metadata)
                .reviewed(false)
                .build();
        return integrityEventRepository.save(event);
    }

    @Transactional
    public void validateSubmissionTiming(UserEntity user, UUID challengeId, ChallengeDifficulty difficulty, long elapsedMs) {
        if (elapsedMs < 5000 && (difficulty == ChallengeDifficulty.MEDIUM || difficulty == ChallengeDifficulty.HARD)) {
            recordEvent(
                    user,
                    IntegrityEventType.IMPOSSIBLE_TIMING,
                    IntegritySeverity.HIGH,
                    40,
                    String.format("Unrealistic solve speed: %d ms for %s challenge (%s)", elapsedMs, difficulty, challengeId)
            );
        }
    }

    @Transactional
    public void validateRapidRunSpam(UserEntity user, int runsInShortWindow) {
        if (runsInShortWindow > 25) {
            recordEvent(
                    user,
                    IntegrityEventType.RAPID_EXECUTION,
                    IntegritySeverity.MEDIUM,
                    20,
                    String.format("Excessive rapid code execution attempts (%d executions in window)", runsInShortWindow)
            );
        }
    }

    @Transactional(readOnly = true)
    public int getUserRiskScore(UUID userId) {
        return integrityEventRepository.calculateActiveRiskScore(userId);
    }

    @Transactional(readOnly = true)
    public Page<IntegrityEventDto> getUnreviewedEvents(Pageable pageable) {
        return integrityEventRepository.findByReviewedOrderByCreatedAtDesc(false, pageable)
                .map(this::mapToDto);
    }

    @Transactional
    public IntegrityEventDto reviewEvent(UUID eventId, UUID adminId) {
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));
        return reviewEvent(eventId, admin);
    }

    @Transactional
    public IntegrityEventDto reviewEvent(UUID eventId, UserEntity admin) {
        IntegrityEventEntity event = integrityEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Integrity event not found: " + eventId));

        event.setReviewed(true);
        IntegrityEventEntity updated = integrityEventRepository.save(event);

        adminAuditService.logAction(
                admin,
                AdminAuditAction.INTEGRITY_EVENT_REVIEWED,
                "INTEGRITY_EVENT",
                eventId.toString(),
                "Reviewed event for user: " + event.getUser().getUsername() + ", type: " + event.getEventType()
        );

        return mapToDto(updated);
    }

    public long countHighRiskAlerts() {
        return integrityEventRepository.countBySeverity(IntegritySeverity.HIGH) +
                integrityEventRepository.countBySeverity(IntegritySeverity.CRITICAL);
    }

    private IntegrityEventDto mapToDto(IntegrityEventEntity entity) {
        return IntegrityEventDto.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .username(entity.getUser() != null ? entity.getUser().getUsername() : "UNKNOWN")
                .eventType(entity.getEventType())
                .severity(entity.getSeverity())
                .riskScore(entity.getRiskScore())
                .metadata(entity.getMetadata())
                .reviewed(entity.isReviewed())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

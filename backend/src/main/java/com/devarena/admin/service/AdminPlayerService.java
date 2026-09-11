package com.devarena.admin.service;

import com.devarena.admin.dto.AdminPlayerDto;
import com.devarena.admin.dto.SuspendPlayerRequest;
import com.devarena.admin.model.AdminAuditAction;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.integrity.service.IntegrityService;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AdminPlayerService {

    private final UserRepository userRepository;
    private final IntegrityService integrityService;
    private final AdminAuditService adminAuditService;

    public AdminPlayerService(
            UserRepository userRepository,
            IntegrityService integrityService,
            AdminAuditService adminAuditService) {
        this.userRepository = userRepository;
        this.integrityService = integrityService;
        this.adminAuditService = adminAuditService;
    }

    @Transactional(readOnly = true)
    public Page<AdminPlayerDto> getPlayers(String search, Pageable pageable) {
        Page<UserEntity> users;
        if (search != null && !search.isBlank()) {
            users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search.trim(), search.trim(), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(this::mapToDto);
    }

    @Transactional
    public AdminPlayerDto suspendPlayer(UUID userId, UUID adminId, SuspendPlayerRequest request) {
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));
        return suspendPlayer(userId, admin, request);
    }

    @Transactional
    public AdminPlayerDto suspendPlayer(UUID userId, UserEntity admin, SuspendPlayerRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        user.setAccountNonLocked(false);
        UserEntity saved = userRepository.save(user);

        adminAuditService.logAction(
                admin,
                AdminAuditAction.PLAYER_SUSPENDED,
                "USER",
                userId.toString(),
                "Reason: " + request.getReason()
        );

        return mapToDto(saved);
    }

    @Transactional
    public AdminPlayerDto restorePlayer(UUID userId, UUID adminId) {
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));
        return restorePlayer(userId, admin);
    }

    @Transactional
    public AdminPlayerDto restorePlayer(UUID userId, UserEntity admin) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        user.setAccountNonLocked(true);
        UserEntity saved = userRepository.save(user);

        adminAuditService.logAction(
                admin,
                AdminAuditAction.PLAYER_RESTORED,
                "USER",
                userId.toString(),
                "Account restored by admin"
        );

        return mapToDto(saved);
    }

    private AdminPlayerDto mapToDto(UserEntity user) {
        int xp = user.getProgression() != null ? user.getProgression().getTotalXp() : 0;
        int level = user.getProgression() != null ? user.getProgression().getLevel() : 1;
        int mmr = user.getStats() != null ? user.getStats().getRating() : 1000;
        int riskScore = integrityService.getUserRiskScore(user.getId());

        return AdminPlayerDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .enabled(user.isEnabled())
                .accountNonLocked(user.isAccountNonLocked())
                .xp(xp)
                .level(level)
                .mmr(mmr)
                .riskScore(riskScore)
                .createdAt(user.getCreatedAt())
                .build();
    }
}

package com.devarena.notification.service;

import com.devarena.common.exception.DevArenaException;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.notification.dto.NotificationDto;
import com.devarena.notification.model.NotificationEntity;
import com.devarena.notification.model.NotificationType;
import com.devarena.notification.repository.NotificationRepository;
import com.devarena.user.model.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(
            NotificationRepository notificationRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public NotificationDto createNotification(
            UserEntity recipient,
            NotificationType type,
            String title,
            String message,
            String referenceType,
            String referenceId
    ) {
        NotificationEntity entity = new NotificationEntity(
                recipient,
                type,
                title,
                message,
                referenceType,
                referenceId
        );
        entity = notificationRepository.save(entity);

        NotificationDto dto = toDto(entity);

        // Push real-time notification via STOMP WebSocket
        try {
            messagingTemplate.convertAndSendToUser(recipient.getUsername(), "/queue/notifications", dto);
            messagingTemplate.convertAndSend("/topic/notifications." + recipient.getId(), dto);
        } catch (Exception e) {
            log.warn("Failed to deliver WebSocket notification to {}: {}", recipient.getUsername(), e.getMessage());
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotifications(UUID recipientId, Pageable pageable) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID recipientId) {
        return notificationRepository.countByRecipientIdAndReadFalse(recipientId);
    }

    @Transactional
    public NotificationDto markAsRead(UUID notificationId, UUID recipientId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        if (!notification.getRecipient().getId().equals(recipientId)) {
            throw new DevArenaException("You are not authorized to access this notification", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }

        notification.setRead(true);
        notification = notificationRepository.save(notification);
        return toDto(notification);
    }

    @Transactional
    public void markAllAsRead(UUID recipientId) {
        notificationRepository.markAllAsRead(recipientId);
    }

    private NotificationDto toDto(NotificationEntity entity) {
        return new NotificationDto(
                entity.getId(),
                entity.getType(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getReferenceType(),
                entity.getReferenceId(),
                entity.isRead(),
                entity.getCreatedAt()
        );
    }
}

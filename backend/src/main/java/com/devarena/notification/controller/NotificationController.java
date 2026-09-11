package com.devarena.notification.controller;

import com.devarena.common.api.ApiResponse;
import com.devarena.notification.dto.NotificationDto;
import com.devarena.notification.dto.UnreadCountDto;
import com.devarena.notification.service.NotificationService;
import com.devarena.security.DevArenaUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationDto>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        Page<NotificationDto> notifications = notificationService.getNotifications(userDetails.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountDto>> getUnreadCount(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        long count = notificationService.getUnreadCount(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(new UnreadCountDto(count)));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        NotificationDto notification = notificationService.markAsRead(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", notification));
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        notificationService.markAllAsRead(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}

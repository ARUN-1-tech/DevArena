package com.devarena;

import com.devarena.common.exception.DevArenaException;
import com.devarena.notification.dto.NotificationDto;
import com.devarena.notification.model.NotificationType;
import com.devarena.notification.service.NotificationService;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should create, retrieve, and mark notifications as read with authorization check")
    void testNotificationLifecycle() {
        UserEntity u1 = userRepository.save(new UserEntity("notif_alice", "notif_alice@test.com", "hash"));
        UserEntity u2 = userRepository.save(new UserEntity("notif_bob", "notif_bob@test.com", "hash"));

        // 1. Create notification
        NotificationDto n1 = notificationService.createNotification(
                u1,
                NotificationType.FRIEND_REQUEST,
                "Friend Request",
                "Bob sent you a friend request",
                "FRIEND_REQUEST",
                "123"
        );
        assertThat(n1).isNotNull();
        assertThat(n1.read()).isFalse();

        // 2. Check unread count
        assertThat(notificationService.getUnreadCount(u1.getId())).isEqualTo(1);
        assertThat(notificationService.getUnreadCount(u2.getId())).isEqualTo(0);

        // 3. User 2 cannot mark User 1's notification as read
        assertThatThrownBy(() -> notificationService.markAsRead(n1.id(), u2.getId()))
                .isInstanceOf(DevArenaException.class);

        // 4. User 1 marks as read
        NotificationDto read = notificationService.markAsRead(n1.id(), u1.getId());
        assertThat(read.read()).isTrue();
        assertThat(notificationService.getUnreadCount(u1.getId())).isEqualTo(0);

        // 5. Mark all as read
        notificationService.createNotification(u1, NotificationType.SYSTEM, "Sys1", "Msg1", null, null);
        notificationService.createNotification(u1, NotificationType.LEVEL_UP, "Level Up", "Msg2", null, null);
        assertThat(notificationService.getUnreadCount(u1.getId())).isEqualTo(2);

        notificationService.markAllAsRead(u1.getId());
        assertThat(notificationService.getUnreadCount(u1.getId())).isEqualTo(0);
    }
}

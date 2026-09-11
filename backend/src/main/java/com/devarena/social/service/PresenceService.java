package com.devarena.social.service;

import com.devarena.security.DevArenaUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    private static final Logger log = LoggerFactory.getLogger(PresenceService.class);
    private static final String REDIS_PREFIX = "devarena:presence:";
    private static final Duration TTL = Duration.ofMinutes(2);

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<UUID, Long> localPresence = new ConcurrentHashMap<>();

    public PresenceService(
            @Autowired(required = false) StringRedisTemplate redisTemplate,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    public void setOnline(UUID userId, String username) {
        if (userId == null) return;
        localPresence.put(userId, System.currentTimeMillis() + TTL.toMillis());
        if (redisTemplate != null) {
            try {
                redisTemplate.opsForValue().set(REDIS_PREFIX + userId, "ONLINE", TTL);
            } catch (Exception e) {
                log.debug("Redis presence set failed, relying on local state: {}", e.getMessage());
            }
        }
        broadcastPresence(userId, username, true);
    }

    public void setOffline(UUID userId, String username) {
        if (userId == null) return;
        localPresence.remove(userId);
        if (redisTemplate != null) {
            try {
                redisTemplate.delete(REDIS_PREFIX + userId);
            } catch (Exception e) {
                log.debug("Redis presence delete failed: {}", e.getMessage());
            }
        }
        broadcastPresence(userId, username, false);
    }

    public boolean isOnline(UUID userId) {
        if (userId == null) return false;
        if (redisTemplate != null) {
            try {
                Boolean hasKey = redisTemplate.hasKey(REDIS_PREFIX + userId);
                if (hasKey != null && hasKey) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        Long expiresAt = localPresence.get(userId);
        if (expiresAt != null && expiresAt > System.currentTimeMillis()) {
            return true;
        }
        if (expiresAt != null) {
            localPresence.remove(userId);
        }
        return false;
    }

    public Map<UUID, Boolean> getOnlineStatuses(Collection<UUID> userIds) {
        Map<UUID, Boolean> map = new ConcurrentHashMap<>();
        for (UUID id : userIds) {
            map.put(id, isOnline(id));
        }
        return map;
    }

    private void broadcastPresence(UUID userId, String username, boolean online) {
        try {
            Map<String, Object> payload = Map.of(
                    "userId", userId.toString(),
                    "username", username != null ? username : "",
                    "online", online
            );
            messagingTemplate.convertAndSend("/topic/presence", payload);
        } catch (Exception e) {
            log.debug("Could not broadcast presence: {}", e.getMessage());
        }
    }

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() instanceof Authentication auth) {
            if (auth.getPrincipal() instanceof DevArenaUserDetails details) {
                setOnline(details.getId(), details.getUsername());
            }
        }
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() instanceof Authentication auth) {
            if (auth.getPrincipal() instanceof DevArenaUserDetails details) {
                setOffline(details.getId(), details.getUsername());
            }
        }
    }
}

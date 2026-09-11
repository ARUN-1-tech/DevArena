package com.devarena.social.service;

import com.devarena.common.exception.DevArenaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SocialRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(SocialRateLimiter.class);

    private final StringRedisTemplate redisTemplate;
    private final Map<String, InMemoryCounter> inMemoryStore = new ConcurrentHashMap<>();

    public SocialRateLimiter(@Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void checkLimit(String action, UUID userId, int maxPerMinute) {
        String actionKey = action + ":" + userId.toString();

        if (redisTemplate != null) {
            try {
                String redisKey = "devarena:ratelimit:social:" + actionKey;
                Long count = redisTemplate.opsForValue().increment(redisKey);
                if (count != null && count == 1) {
                    redisTemplate.expire(redisKey, Duration.ofMinutes(1));
                }
                if (count != null && count > maxPerMinute) {
                    throw new DevArenaException(
                            "Too many " + action + " attempts. Please slow down and try again in a minute.",
                            HttpStatus.TOO_MANY_REQUESTS,
                            "RATE_LIMIT_EXCEEDED"
                    );
                }
                return;
            } catch (DevArenaException de) {
                throw de;
            } catch (Exception e) {
                log.debug("Redis rate limiter fallback to in-memory: {}", e.getMessage());
            }
        }

        // In-memory fallback
        long now = System.currentTimeMillis();
        InMemoryCounter counter = inMemoryStore.compute(actionKey, (k, existing) -> {
            if (existing == null || existing.windowStart + 60_000L < now) {
                return new InMemoryCounter(now, new AtomicInteger(1));
            }
            existing.count.incrementAndGet();
            return existing;
        });

        if (counter.count.get() > maxPerMinute) {
            throw new DevArenaException(
                    "Too many " + action + " attempts. Please slow down and try again in a minute.",
                    HttpStatus.TOO_MANY_REQUESTS,
                    "RATE_LIMIT_EXCEEDED"
            );
        }
    }

    private static final class InMemoryCounter {
        final long windowStart;
        final AtomicInteger count;

        InMemoryCounter(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}

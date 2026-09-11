package com.devarena.ai.service;

import com.devarena.common.exception.DevArenaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AiRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(AiRateLimiter.class);
    private static final int DAILY_LIMIT = 50;

    private final StringRedisTemplate redisTemplate;
    private final Map<String, AtomicInteger> inMemoryDailyStore = new ConcurrentHashMap<>();

    public AiRateLimiter(@Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public int checkAndIncrement(UUID userId) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String key = "devarena:ai:quota:" + userId + ":" + today;

        if (redisTemplate != null) {
            try {
                Long count = redisTemplate.opsForValue().increment(key);
                if (count != null && count == 1) {
                    redisTemplate.expire(key, Duration.ofDays(1));
                }
                if (count != null && count > DAILY_LIMIT) {
                    throw new DevArenaException(
                            "Daily AI Coach quota of " + DAILY_LIMIT + " queries exceeded. Please return tomorrow!",
                            HttpStatus.TOO_MANY_REQUESTS,
                            "AI_QUOTA_EXCEEDED"
                    );
                }
                return (int) Math.max(0, DAILY_LIMIT - (count != null ? count : 0));
            } catch (DevArenaException de) {
                throw de;
            } catch (Exception e) {
                log.debug("Redis AI rate limiter fallback to in-memory: {}", e.getMessage());
            }
        }

        // In-memory fallback
        AtomicInteger counter = inMemoryDailyStore.computeIfAbsent(key, k -> new AtomicInteger(0));
        int current = counter.incrementAndGet();
        if (current > DAILY_LIMIT) {
            throw new DevArenaException(
                    "Daily AI Coach quota of " + DAILY_LIMIT + " queries exceeded. Please return tomorrow!",
                    HttpStatus.TOO_MANY_REQUESTS,
                    "AI_QUOTA_EXCEEDED"
            );
        }
        return Math.max(0, DAILY_LIMIT - current);
    }
}

package com.devarena.execution.service;

import com.devarena.common.exception.DevArenaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ExecutionRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(ExecutionRateLimiter.class);

    @Value("${devarena.execution.rate-limit.run-per-minute:20}")
    private int runPerMinute;

    @Value("${devarena.execution.rate-limit.submit-per-minute:10}")
    private int submitPerMinute;

    private final StringRedisTemplate redisTemplate;
    private final Map<String, InMemoryCounter> inMemoryStore = new ConcurrentHashMap<>();

    public ExecutionRateLimiter(@Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void checkRateLimit(UUID userId, boolean isSubmission) {
        int maxAllowed = isSubmission ? submitPerMinute : runPerMinute;
        String actionKey = (isSubmission ? "sub:" : "run:") + userId.toString();

        if (redisTemplate != null) {
            try {
                String redisKey = "devarena:ratelimit:" + actionKey;
                Long count = redisTemplate.opsForValue().increment(redisKey);
                if (count != null && count == 1) {
                    redisTemplate.expire(redisKey, Duration.ofMinutes(1));
                }
                if (count != null && count > maxAllowed) {
                    throw new DevArenaException(
                            "Rate limit exceeded. Please wait a moment before " + (isSubmission ? "submitting" : "running") + " code again.",
                            HttpStatus.TOO_MANY_REQUESTS,
                            "RATE_LIMIT_EXCEEDED"
                    );
                }
                return;
            } catch (DevArenaException de) {
                throw de;
            } catch (Exception e) {
                log.warn("Redis rate limiter unavailable, falling back to in-memory counter: {}", e.getMessage());
            }
        }

        // In-memory fallback
        long now = System.currentTimeMillis();
        InMemoryCounter counter = inMemoryStore.compute(actionKey, (k, existing) -> {
            if (existing == null || now - existing.windowStart > 60_000) {
                return new InMemoryCounter(now, new AtomicInteger(1));
            }
            existing.count.incrementAndGet();
            return existing;
        });

        if (counter.count.get() > maxAllowed) {
            throw new DevArenaException(
                    "Rate limit exceeded. Please wait a moment before " + (isSubmission ? "submitting" : "running") + " code again.",
                    HttpStatus.TOO_MANY_REQUESTS,
                    "RATE_LIMIT_EXCEEDED"
            );
        }
    }

    private static class InMemoryCounter {
        final long windowStart;
        final AtomicInteger count;

        InMemoryCounter(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}

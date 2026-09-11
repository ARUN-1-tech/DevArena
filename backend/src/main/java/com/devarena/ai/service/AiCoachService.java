package com.devarena.ai.service;

import com.devarena.ai.dto.AiCoachRequest;
import com.devarena.ai.dto.AiCoachResponseDto;
import com.devarena.ai.model.AiCoachRequestType;
import com.devarena.ai.model.AiUsageRecordEntity;
import com.devarena.ai.repository.AiUsageRecordRepository;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AiCoachService {

    private final AiProvider aiProvider;
    private final AiRateLimiter aiRateLimiter;
    private final AiUsageRecordRepository aiUsageRecordRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final Map<String, AtomicInteger> inMemoryHintLevels = new ConcurrentHashMap<>();

    public AiCoachService(
            AiProvider aiProvider,
            AiRateLimiter aiRateLimiter,
            AiUsageRecordRepository aiUsageRecordRepository,
            ChallengeRepository challengeRepository,
            UserRepository userRepository,
            @Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.aiProvider = aiProvider;
        this.aiRateLimiter = aiRateLimiter;
        this.aiUsageRecordRepository = aiUsageRecordRepository;
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public AiCoachResponseDto processRequest(UUID userId, AiCoachRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return processRequest(user, request);
    }

    @Transactional
    public AiCoachResponseDto processRequest(UserEntity user, AiCoachRequest request) {
        int remainingQueries = aiRateLimiter.checkAndIncrement(user.getId());

        ChallengeEntity challenge = null;
        if (request.getChallengeId() != null) {
            challenge = challengeRepository.findById(request.getChallengeId())
                    .orElse(null);
        }

        // Sanitize code snippet (strip potential sensitive data)
        String sanitizedCode = sanitizeCode(request.getCurrentCode());
        request.setCurrentCode(sanitizedCode);

        // Determine hint level for user on this challenge
        int currentHintLevel = getHintLevel(user.getId(), request.getChallengeId());

        AiCoachResponseDto response = aiProvider.generateResponse(request, challenge, user.getUsername(), currentHintLevel);
        response.setRemainingDailyQueries(remainingQueries);

        if (request.getRequestType() == AiCoachRequestType.HINT) {
            incrementHintLevel(user.getId(), request.getChallengeId(), response.getHintLevel());
        }

        // Record AI query in database
        AiUsageRecordEntity record = AiUsageRecordEntity.builder()
                .user(user)
                .challenge(challenge)
                .requestType(request.getRequestType())
                .tokensUsed(estimateTokens(request, response))
                .build();
        aiUsageRecordRepository.save(record);

        return response;
    }

    private int getHintLevel(UUID userId, UUID challengeId) {
        if (challengeId == null) return 1;
        String key = "ai:hint:" + userId + ":" + challengeId;
        if (redisTemplate != null) {
            try {
                String val = redisTemplate.opsForValue().get(key);
                if (val != null) {
                    return Integer.parseInt(val);
                }
            } catch (Exception ignored) {}
        }
        return inMemoryHintLevels.computeIfAbsent(key, k -> new AtomicInteger(1)).get();
    }

    private void incrementHintLevel(UUID userId, UUID challengeId, int nextLevel) {
        if (challengeId == null) return;
        String key = "ai:hint:" + userId + ":" + challengeId;
        if (redisTemplate != null) {
            try {
                redisTemplate.opsForValue().set(key, String.valueOf(nextLevel), Duration.ofHours(6));
                return;
            } catch (Exception ignored) {}
        }
        inMemoryHintLevels.put(key, new AtomicInteger(nextLevel));
    }

    private String sanitizeCode(String code) {
        if (code == null) return "";
        // Mask passwords, jwt tokens, or private secrets if mistakenly pasted
        return code.replaceAll("(?i)(password|secret|token|api_key|apikey)\\s*=\\s*[\"'][^\"']+[\"']", "$1 = \"[REDACTED]\"");
    }

    private int estimateTokens(AiCoachRequest req, AiCoachResponseDto res) {
        int codeLen = req.getCurrentCode() != null ? req.getCurrentCode().length() : 0;
        int replyLen = res.getReply() != null ? res.getReply().length() : 0;
        return (codeLen + replyLen) / 4 + 10;
    }
}

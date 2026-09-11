package com.devarena.matchmaking.service;

import com.devarena.battle.dto.BattleEvent;
import com.devarena.battle.model.BattleEntity;
import com.devarena.battle.model.BattleStatus;
import com.devarena.battle.repository.BattleRepository;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.common.exception.BadRequestException;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.matchmaking.dto.MatchmakingQueueItem;
import com.devarena.matchmaking.dto.MatchmakingStatusResponse;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatchmakingService {

    private static final Logger log = LoggerFactory.getLogger(MatchmakingService.class);

    private static final int INITIAL_SEARCH_RADIUS = 150;
    private static final int MAX_SEARCH_RADIUS = 600;
    private static final int RADIUS_EXPANSION_STEP = 50;
    private static final int EXPANSION_INTERVAL_SECONDS = 5;

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final BattleRepository battleRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Optional<StringRedisTemplate> redisTemplate;

    // Fast, thread-safe in-memory matchmaking queue (works standalone or when Redis is unavailable)
    private final Map<UUID, MatchmakingQueueItem> inMemoryQueue = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> matchedBattles = new ConcurrentHashMap<>();
    private final Object matchLock = new Object();

    @Autowired
    public MatchmakingService(
            UserRepository userRepository,
            ChallengeRepository challengeRepository,
            BattleRepository battleRepository,
            SimpMessagingTemplate messagingTemplate,
            Optional<StringRedisTemplate> redisTemplate
    ) {
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.battleRepository = battleRepository;
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
    }

    public MatchmakingStatusResponse joinQueue(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // 1. Check if user already has an active battle
        List<BattleEntity> active = battleRepository.findActiveBattlesByPlayer(
                userId,
                List.of(BattleStatus.WAITING, BattleStatus.READY, BattleStatus.IN_PROGRESS)
        );
        if (!active.isEmpty()) {
            BattleEntity existing = active.get(0);
            return new MatchmakingStatusResponse(false, 0, 0, getPlayerRating(user), existing.getId());
        }

        // 2. Check if already queued
        if (inMemoryQueue.containsKey(userId)) {
            MatchmakingQueueItem item = inMemoryQueue.get(userId);
            int waitSec = (int) Duration.between(item.queuedAt(), Instant.now()).getSeconds();
            int radius = calculateSearchRadius(waitSec);
            return new MatchmakingStatusResponse(true, waitSec, radius, item.rating(), matchedBattles.get(userId));
        }

        int rating = getPlayerRating(user);
        int level = user.getProgression() != null ? user.getProgression().getLevel() : 1;
        String avatar = (user.getProfile() != null && user.getProfile().getAvatar() != null)
                ? user.getProfile().getAvatar()
                : "avatar-1";

        MatchmakingQueueItem queueItem = new MatchmakingQueueItem(
                userId,
                user.getUsername(),
                avatar,
                rating,
                level,
                Instant.now()
        );

        inMemoryQueue.put(userId, queueItem);
        log.info("Player {} ({}) joined matchmaking queue with MMR {}", user.getUsername(), userId, rating);

        // Attempt immediate matching tick
        runMatchingCycle();

        return new MatchmakingStatusResponse(true, 0, INITIAL_SEARCH_RADIUS, rating, matchedBattles.get(userId));
    }

    public void leaveQueue(UUID userId) {
        inMemoryQueue.remove(userId);
        matchedBattles.remove(userId);
        log.info("Player {} removed from matchmaking queue", userId);
    }

    public MatchmakingStatusResponse getQueueStatus(UUID userId) {
        UUID matched = matchedBattles.get(userId);
        if (matched != null) {
            return new MatchmakingStatusResponse(false, 0, 0, 0, matched);
        }

        MatchmakingQueueItem item = inMemoryQueue.get(userId);
        if (item == null) {
            return new MatchmakingStatusResponse(false, 0, 0, 0, null);
        }

        int waitSec = (int) Duration.between(item.queuedAt(), Instant.now()).getSeconds();
        int radius = calculateSearchRadius(waitSec);
        return new MatchmakingStatusResponse(true, waitSec, radius, item.rating(), null);
    }

    @Scheduled(fixedDelay = 1500)
    public void scheduledMatchmakingCycle() {
        runMatchingCycle();
    }

    @Transactional
    public void runMatchingCycle() {
        synchronized (matchLock) {
            if (inMemoryQueue.size() < 2) {
                return;
            }

            List<MatchmakingQueueItem> candidates = new ArrayList<>(inMemoryQueue.values());
            // Sort by queuedAt ascending (oldest waiters get priority)
            candidates.sort(Comparator.comparing(MatchmakingQueueItem::queuedAt));

            Set<UUID> matchedInCycle = new HashSet<>();

            for (int i = 0; i < candidates.size(); i++) {
                MatchmakingQueueItem p1 = candidates.get(i);
                if (matchedInCycle.contains(p1.userId())) continue;

                int waitSec1 = (int) Duration.between(p1.queuedAt(), Instant.now()).getSeconds();
                int radius1 = calculateSearchRadius(waitSec1);

                MatchmakingQueueItem bestMatch = null;
                int bestDiff = Integer.MAX_VALUE;

                for (int j = i + 1; j < candidates.size(); j++) {
                    MatchmakingQueueItem p2 = candidates.get(j);
                    if (matchedInCycle.contains(p2.userId())) continue;
                    if (p1.userId().equals(p2.userId())) continue; // Never match player with themselves

                    int waitSec2 = (int) Duration.between(p2.queuedAt(), Instant.now()).getSeconds();
                    int radius2 = calculateSearchRadius(waitSec2);
                    int allowedRadius = Math.max(radius1, radius2);

                    int ratingDiff = Math.abs(p1.rating() - p2.rating());
                    if (ratingDiff <= allowedRadius && ratingDiff < bestDiff) {
                        bestDiff = ratingDiff;
                        bestMatch = p2;
                    }
                }

                if (bestMatch != null) {
                    matchedInCycle.add(p1.userId());
                    matchedInCycle.add(bestMatch.userId());

                    inMemoryQueue.remove(p1.userId());
                    inMemoryQueue.remove(bestMatch.userId());

                    createAndNotifyMatch(p1, bestMatch);
                }
            }
        }
    }

    private void createAndNotifyMatch(MatchmakingQueueItem p1, MatchmakingQueueItem p2) {
        try {
            UserEntity user1 = userRepository.findById(p1.userId()).orElse(null);
            UserEntity user2 = userRepository.findById(p2.userId()).orElse(null);

            if (user1 == null || user2 == null) {
                log.warn("Could not find user entities for matched pair: {} & {}", p1.userId(), p2.userId());
                return;
            }

            // Select random suitable published challenge (Easy or Medium preferred for 1v1)
            List<ChallengeEntity> pool = challengeRepository.findAll().stream()
                    .filter(c -> c.getDifficulty() == ChallengeDifficulty.EASY || c.getDifficulty() == ChallengeDifficulty.MEDIUM)
                    .toList();

            ChallengeEntity selectedChallenge = pool.isEmpty()
                    ? challengeRepository.findAll().get(0)
                    : pool.get(new Random().nextInt(pool.size()));

            BattleEntity battle = new BattleEntity(user1, user2, selectedChallenge, 900); // 15 minutes
            battle.setStatus(BattleStatus.WAITING);
            battle = battleRepository.save(battle);

            matchedBattles.put(p1.userId(), battle.getId());
            matchedBattles.put(p2.userId(), battle.getId());

            log.info("Created 1v1 Battle {} between {} and {} for challenge '{}'",
                    battle.getId(), user1.getUsername(), user2.getUsername(), selectedChallenge.getTitle());

            // Notify both players through WebSocket
            Map<String, Object> eventPayload = Map.of(
                    "battleId", battle.getId().toString(),
                    "challengeTitle", selectedChallenge.getTitle(),
                    "difficulty", selectedChallenge.getDifficulty().name(),
                    "player1", Map.of("username", user1.getUsername(), "rating", p1.rating(), "level", p1.level()),
                    "player2", Map.of("username", user2.getUsername(), "rating", p2.rating(), "level", p2.level())
            );

            BattleEvent event = BattleEvent.of("MATCH_FOUND", battle.getId(), eventPayload);

            // Send point-to-point and topic fallback
            messagingTemplate.convertAndSendToUser(user1.getUsername(), "/queue/match", event);
            messagingTemplate.convertAndSendToUser(user2.getUsername(), "/queue/match", event);
            messagingTemplate.convertAndSend("/topic/match." + user1.getId(), event);
            messagingTemplate.convertAndSend("/topic/match." + user2.getId(), event);

        } catch (Exception e) {
            log.error("Failed to create battle from matched pair", e);
        }
    }

    private int calculateSearchRadius(int waitSeconds) {
        int expansions = waitSeconds / EXPANSION_INTERVAL_SECONDS;
        return Math.min(MAX_SEARCH_RADIUS, INITIAL_SEARCH_RADIUS + (expansions * RADIUS_EXPANSION_STEP));
    }

    private int getPlayerRating(UserEntity user) {
        return user.getStats() != null && user.getStats().getRating() > 0
                ? user.getStats().getRating()
                : 1000;
    }
}

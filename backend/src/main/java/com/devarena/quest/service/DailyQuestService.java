package com.devarena.quest.service;

import com.devarena.common.exception.BadRequestException;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.progression.dto.XpRewardResult;
import com.devarena.progression.model.ActivityType;
import com.devarena.progression.service.XPService;
import com.devarena.quest.dto.DailyQuestDto;
import com.devarena.quest.dto.QuestClaimResponse;
import com.devarena.quest.model.*;
import com.devarena.quest.repository.DailyQuestRepository;
import com.devarena.quest.repository.PlayerDailyQuestRepository;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerProgressionRepository;
import com.devarena.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DailyQuestService {

    private final DailyQuestRepository dailyQuestRepository;
    private final PlayerDailyQuestRepository playerDailyQuestRepository;
    private final UserRepository userRepository;
    private final PlayerProgressionRepository progressionRepository;
    private final XPService xpService;

    public DailyQuestService(
            DailyQuestRepository dailyQuestRepository,
            PlayerDailyQuestRepository playerDailyQuestRepository,
            UserRepository userRepository,
            PlayerProgressionRepository progressionRepository,
            XPService xpService) {
        this.dailyQuestRepository = dailyQuestRepository;
        this.playerDailyQuestRepository = playerDailyQuestRepository;
        this.userRepository = userRepository;
        this.progressionRepository = progressionRepository;
        this.xpService = xpService;
    }

    @Transactional
    public List<DailyQuestDto> getDailyQuestsForPlayer(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        ensureDailyQuestsExist(today);

        List<PlayerDailyQuestEntity> playerQuests = playerDailyQuestRepository.findByUserId(userId);
        List<PlayerDailyQuestEntity> todayPlayerQuests = playerQuests.stream()
                .filter(pq -> pq.getQuest().getActiveDate().equals(today))
                .collect(Collectors.toList());

        if (todayPlayerQuests.isEmpty()) {
            List<DailyQuestEntity> pool = dailyQuestRepository.findByActiveDateAndStatus(today, QuestStatus.ACTIVE);
            if (pool.isEmpty()) {
                pool = dailyQuestRepository.findByStatus(QuestStatus.ACTIVE);
            }

            for (DailyQuestEntity q : pool) {
                // Check if already assigned
                if (playerDailyQuestRepository.findByUserIdAndQuestId(userId, q.getId()).isEmpty()) {
                    PlayerDailyQuestEntity pq = new PlayerDailyQuestEntity(user, q, 0, q.getTargetCount(), PlayerQuestStatus.IN_PROGRESS);
                    todayPlayerQuests.add(playerDailyQuestRepository.save(pq));
                }
            }
        }

        return todayPlayerQuests.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public QuestClaimResponse claimQuest(UUID playerQuestId, UUID userId) {
        PlayerDailyQuestEntity playerQuest = playerDailyQuestRepository.findByIdAndUserId(playerQuestId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Daily quest not found for player: " + playerQuestId));

        if (playerQuest.getStatus() == PlayerQuestStatus.CLAIMED) {
            throw new BadRequestException("Quest reward has already been claimed.");
        }

        // Auto-complete if count matches
        if (playerQuest.getCurrentCount() >= playerQuest.getTargetCount() && playerQuest.getStatus() == PlayerQuestStatus.IN_PROGRESS) {
            playerQuest.setStatus(PlayerQuestStatus.COMPLETED);
            playerQuest.setCompletedAt(Instant.now());
        }

        if (playerQuest.getStatus() != PlayerQuestStatus.COMPLETED) {
            throw new BadRequestException("Quest requirements are not yet satisfied (" +
                    playerQuest.getCurrentCount() + "/" + playerQuest.getTargetCount() + ").");
        }

        int reward = playerQuest.getQuest().getXpReward();
        XpRewardResult xpResult = xpService.awardXP(
                userId,
                reward,
                ActivityType.QUEST_COMPLETED,
                "Completed: " + playerQuest.getQuest().getTitle(),
                "Claimed daily quest reward of " + reward + " XP"
        );

        playerQuest.setStatus(PlayerQuestStatus.CLAIMED);
        playerQuest.setClaimedAt(Instant.now());
        playerDailyQuestRepository.save(playerQuest);

        // Update player's quests_completed count
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getProgression() != null) {
            PlayerProgressionEntity p = user.getProgression();
            p.setQuestsCompleted(p.getQuestsCompleted() + 1);
            progressionRepository.save(p);
        }

        return new QuestClaimResponse(playerQuest.getId(), PlayerQuestStatus.CLAIMED, reward, xpResult);
    }

    @Transactional
    public void incrementQuestProgress(UUID userId, QuestType questType, int amount) {
        List<PlayerDailyQuestEntity> playerQuests = playerDailyQuestRepository.findByUserId(userId);
        for (PlayerDailyQuestEntity pq : playerQuests) {
            if (pq.getStatus() == PlayerQuestStatus.IN_PROGRESS && pq.getQuest().getQuestType() == questType) {
                pq.setCurrentCount(Math.min(pq.getTargetCount(), pq.getCurrentCount() + amount));
                playerDailyQuestRepository.save(pq);
            }
        }
    }

    private void ensureDailyQuestsExist(LocalDate date) {
        List<DailyQuestEntity> existing = dailyQuestRepository.findByActiveDateAndStatus(date, QuestStatus.ACTIVE);
        if (existing.isEmpty()) {
            List<DailyQuestEntity> templates = List.of(
                    new DailyQuestEntity("Solve 1 Challenge", "Complete any algorithmic kata in the practice archive.", QuestType.COMPLETE_CHALLENGE, 1, 100, date),
                    new DailyQuestEntity("Practice 2 Easy Katas", "Complete 2 Easy difficulty algorithmic problems.", QuestType.COMPLETE_EASY, 2, 150, date),
                    new DailyQuestEntity("Earn 200 XP", "Gain 200 XP through problem-solving and activities.", QuestType.EARN_XP, 200, 100, date)
            );
            dailyQuestRepository.saveAll(templates);
        }
    }

    private DailyQuestDto toDto(PlayerDailyQuestEntity pq) {
        DailyQuestEntity q = pq.getQuest();
        boolean completed = pq.getStatus() == PlayerQuestStatus.COMPLETED || pq.getStatus() == PlayerQuestStatus.CLAIMED || pq.getCurrentCount() >= pq.getTargetCount();
        boolean claimed = pq.getStatus() == PlayerQuestStatus.CLAIMED;
        return new DailyQuestDto(
                pq.getId(),
                q.getId(),
                q.getTitle(),
                q.getDescription(),
                q.getQuestType(),
                pq.getCurrentCount(),
                pq.getTargetCount(),
                q.getXpReward(),
                pq.getStatus(),
                completed,
                claimed
        );
    }
}

package com.devarena.achievement.service;

import com.devarena.achievement.dto.AchievementDto;
import com.devarena.achievement.dto.PlayerAchievementDto;
import com.devarena.achievement.model.AchievementEntity;
import com.devarena.achievement.model.PlayerAchievementEntity;
import com.devarena.achievement.repository.AchievementRepository;
import com.devarena.achievement.repository.PlayerAchievementRepository;
import com.devarena.progression.model.ActivityType;
import com.devarena.progression.service.XPService;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.PlayerStatsEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerProgressionRepository;
import com.devarena.user.repository.PlayerStatsRepository;
import com.devarena.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    private static final Logger log = LoggerFactory.getLogger(AchievementService.class);

    private final AchievementRepository achievementRepository;
    private final PlayerAchievementRepository playerAchievementRepository;
    private final UserRepository userRepository;
    private final PlayerProgressionRepository progressionRepository;
    private final PlayerStatsRepository statsRepository;
    private final XPService xpService;

    public AchievementService(
            AchievementRepository achievementRepository,
            PlayerAchievementRepository playerAchievementRepository,
            UserRepository userRepository,
            PlayerProgressionRepository progressionRepository,
            PlayerStatsRepository statsRepository,
            XPService xpService
    ) {
        this.achievementRepository = achievementRepository;
        this.playerAchievementRepository = playerAchievementRepository;
        this.userRepository = userRepository;
        this.progressionRepository = progressionRepository;
        this.statsRepository = statsRepository;
        this.xpService = xpService;
    }

    @Transactional(readOnly = true)
    public List<AchievementDto> getPublicAchievements() {
        return achievementRepository.findByIsActiveTrue().stream()
                .filter(a -> !a.isSecret())
                .map(AchievementDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlayerAchievementDto> getPlayerAchievements(UUID userId) {
        List<AchievementEntity> allAchievements = achievementRepository.findByIsActiveTrue();
        Map<UUID, PlayerAchievementEntity> unlockedMap = playerAchievementRepository.findByUserIdOrderByUnlockedAtDesc(userId)
                .stream()
                .collect(Collectors.toMap(pa -> pa.getAchievement().getId(), pa -> pa, (a, b) -> a));

        PlayerProgressionEntity progression = progressionRepository.findByUserId(userId).orElse(null);
        PlayerStatsEntity stats = statsRepository.findByUserId(userId).orElse(null);

        int challengesSolved = progression != null ? progression.getChallengesSolved() : 0;
        int totalXp = progression != null ? progression.getTotalXp() : 0;
        int dailyStreak = progression != null ? progression.getCurrentStreak() : 0;
        int battlesWon = stats != null ? stats.getWins() : 0;
        int winStreak = stats != null ? stats.getWinStreak() : 0;
        int rating = stats != null ? stats.getRating() : 1000;

        List<PlayerAchievementDto> result = new ArrayList<>();
        for (AchievementEntity a : allAchievements) {
            boolean unlocked = unlockedMap.containsKey(a.getId());
            PlayerAchievementEntity pa = unlockedMap.get(a.getId());

            // If secret and locked, hide details
            if (a.isSecret() && !unlocked) {
                continue;
            }

            int currentVal = 0;
            switch (a.getRequirementType()) {
                case CHALLENGES_SOLVED -> currentVal = challengesSolved;
                case BATTLES_WON -> currentVal = battlesWon;
                case BATTLE_STREAK -> currentVal = winStreak;
                case TOTAL_XP -> currentVal = totalXp;
                case RATING_THRESHOLD -> currentVal = rating;
                case DAILY_STREAK -> currentVal = dailyStreak;
            }

            int target = a.getRequirementValue();
            int progressPercent = unlocked ? 100 : Math.min(100, (int) Math.round(((double) currentVal / target) * 100.0));

            result.add(new PlayerAchievementDto(
                    a.getId(),
                    a.getCode(),
                    a.getName(),
                    a.getDescription(),
                    a.getIcon(),
                    a.getCategory(),
                    a.getRarity(),
                    a.getXpReward(),
                    unlocked,
                    pa != null ? pa.getUnlockedAt() : null,
                    unlocked ? target : Math.min(currentVal, target),
                    target,
                    progressPercent
            ));
        }

        // Sort: unlocked first by date desc, then by progress percentage desc
        result.sort((a, b) -> {
            if (a.unlocked() && !b.unlocked()) return -1;
            if (!a.unlocked() && b.unlocked()) return 1;
            if (a.unlocked() && b.unlocked()) {
                if (a.unlockedAt() != null && b.unlockedAt() != null) {
                    return b.unlockedAt().compareTo(a.unlockedAt());
                }
                return 0;
            }
            return Integer.compare(b.progressPercentage(), a.progressPercentage());
        });

        return result;
    }

    @Transactional
    public List<AchievementDto> evaluateAndUnlock(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        List<AchievementEntity> allAchievements = achievementRepository.findByIsActiveTrue();
        Set<UUID> alreadyUnlocked = new HashSet<>(playerAchievementRepository.findUnlockedAchievementIdsByUserId(userId));

        PlayerProgressionEntity progression = progressionRepository.findByUserId(userId).orElse(null);
        PlayerStatsEntity stats = statsRepository.findByUserId(userId).orElse(null);

        int challengesSolved = progression != null ? progression.getChallengesSolved() : 0;
        int totalXp = progression != null ? progression.getTotalXp() : 0;
        int dailyStreak = progression != null ? progression.getCurrentStreak() : 0;
        int battlesWon = stats != null ? stats.getWins() : 0;
        int winStreak = stats != null ? stats.getWinStreak() : 0;
        int rating = stats != null ? stats.getRating() : 1000;

        List<AchievementDto> newlyUnlocked = new ArrayList<>();

        for (AchievementEntity a : allAchievements) {
            if (alreadyUnlocked.contains(a.getId())) {
                continue;
            }

            boolean qualifies = false;
            switch (a.getRequirementType()) {
                case CHALLENGES_SOLVED -> qualifies = (challengesSolved >= a.getRequirementValue());
                case BATTLES_WON -> qualifies = (battlesWon >= a.getRequirementValue());
                case BATTLE_STREAK -> qualifies = (winStreak >= a.getRequirementValue());
                case TOTAL_XP -> qualifies = (totalXp >= a.getRequirementValue());
                case RATING_THRESHOLD -> qualifies = (rating >= a.getRequirementValue());
                case DAILY_STREAK -> qualifies = (dailyStreak >= a.getRequirementValue());
            }

            if (qualifies) {
                log.info("Player {} qualified for achievement: {} ({})", userId, a.getName(), a.getCode());
                PlayerAchievementEntity pa = new PlayerAchievementEntity(user, a, a.getXpReward());
                playerAchievementRepository.save(pa);
                alreadyUnlocked.add(a.getId());

                // Award XP for achievement
                if (a.getXpReward() > 0) {
                    try {
                        xpService.awardXP(
                                userId,
                                a.getXpReward(),
                                ActivityType.ACHIEVEMENT_UNLOCKED,
                                "Achievement Unlocked: " + a.getName(),
                                a.getDescription()
                        );
                    } catch (Exception ex) {
                        log.warn("Could not award XP for achievement {}: {}", a.getCode(), ex.getMessage());
                    }
                }

                newlyUnlocked.add(AchievementDto.fromEntity(a));
            }
        }

        return newlyUnlocked;
    }
}

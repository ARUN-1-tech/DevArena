package com.devarena.arena.service;

import com.devarena.arena.dto.*;
import com.devarena.challenge.dto.ChallengeCardDto;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.service.ChallengeService;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.progression.dto.PlayerProgressionDto;
import com.devarena.progression.model.PlayerActivityEntity;
import com.devarena.progression.repository.PlayerActivityRepository;
import com.devarena.progression.service.ProgressionService;
import com.devarena.quest.dto.DailyQuestDto;
import com.devarena.quest.service.DailyQuestService;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.PlayerStatsEntity;
import com.devarena.user.model.ProfileEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArenaHomeService {

    private final UserRepository userRepository;
    private final ProgressionService progressionService;
    private final DailyQuestService dailyQuestService;
    private final ChallengeService challengeService;
    private final PlayerActivityRepository activityRepository;

    public ArenaHomeService(
            UserRepository userRepository,
            ProgressionService progressionService,
            DailyQuestService dailyQuestService,
            ChallengeService challengeService,
            PlayerActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.progressionService = progressionService;
        this.dailyQuestService = dailyQuestService;
        this.challengeService = challengeService;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public ArenaHomeDto getArenaHomeData(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Player profile summary
        ProfileEntity profile = user.getProfile();
        PlayerSummaryDto playerDto = new PlayerSummaryDto(
                user.getId(),
                user.getUsername(),
                profile != null ? profile.getDisplayName() : user.getUsername(),
                profile != null ? profile.getAvatar() : "avatar-1",
                profile != null ? profile.getBio() : null
        );

        // Progression
        PlayerProgressionEntity p = user.getProgression();
        int level = p != null ? p.getLevel() : 1;
        int currentXp = p != null ? p.getCurrentXp() : 0;
        int xpToNext = p != null && p.getXpToNextLevel() > 0 ? p.getXpToNextLevel() : progressionService.calculateXpForNextLevel(level);
        int xpPercentage = Math.min(100, Math.round(((float) currentXp / (float) xpToNext) * 100));

        PlayerProgressionDto progDto = new PlayerProgressionDto(
                level,
                currentXp,
                xpToNext,
                p != null ? p.getTotalXp() : 0,
                p != null ? p.getCurrentStreak() : 0,
                p != null ? p.getLongestStreak() : 0,
                p != null ? p.getChallengesSolved() : 0,
                p != null ? p.getQuestsCompleted() : 0,
                xpPercentage
        );

        // Stats
        PlayerStatsEntity s = user.getStats();
        PlayerStatsDto statsDto = new PlayerStatsDto(
                s != null ? s.getRating() : 1000,
                s != null ? s.getWins() : 0,
                s != null ? s.getLosses() : 0,
                s != null ? s.getDraws() : 0,
                s != null ? s.getWinStreak() : 0,
                s != null ? s.getHighestRating() : 1000
        );

        // Daily Quests
        List<DailyQuestDto> quests = dailyQuestService.getDailyQuestsForPlayer(userId);

        // Recommended Challenges (appropriate for level)
        ChallengeDifficulty targetDiff = level <= 2 ? ChallengeDifficulty.EASY :
                level <= 5 ? ChallengeDifficulty.MEDIUM : ChallengeDifficulty.HARD;
        List<ChallengeCardDto> recommended = challengeService.getChallenges(
                null, targetDiff, null, null, PageRequest.of(0, 3), userId
        ).getContent();
        if (recommended.isEmpty()) {
            recommended = challengeService.getChallenges(
                    null, null, null, null, PageRequest.of(0, 3), userId
            ).getContent();
        }

        // Recent Activity
        List<PlayerActivityEntity> activities = activityRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
        List<PlayerActivityDto> activityDtos = activities.stream().map(a -> new PlayerActivityDto(
                a.getId(),
                a.getActivityType(),
                a.getTitle(),
                a.getDescription(),
                a.getXpEarned(),
                a.getCreatedAt()
        )).collect(Collectors.toList());

        // Next Milestone
        int nextMilestoneLevel = level < 2 ? 2 : level < 3 ? 3 : level + 1;
        ProgressionMilestoneDto milestone = new ProgressionMilestoneDto(
                nextMilestoneLevel == 2 ? "Ranked 1v1 Duels" :
                nextMilestoneLevel == 3 ? "Skill Tree Specializations" :
                "Level " + nextMilestoneLevel + " Grandmaster Mastery",
                "Unlocks advanced arena features and prestige badges at Level " + nextMilestoneLevel,
                nextMilestoneLevel,
                level >= nextMilestoneLevel
        );

        return new ArenaHomeDto(
                playerDto,
                progDto,
                statsDto,
                quests,
                recommended,
                activityDtos,
                milestone
        );
    }
}

package com.devarena.social.service;

import com.devarena.achievement.dto.PlayerAchievementDto;
import com.devarena.achievement.service.AchievementService;
import com.devarena.challenge.model.ChallengeProgressStatus;
import com.devarena.challenge.repository.PlayerChallengeRepository;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.progression.model.PlayerActivityEntity;
import com.devarena.progression.repository.PlayerActivityRepository;
import com.devarena.skill.dto.PlayerSkillDto;
import com.devarena.skill.service.SkillProgressionService;
import com.devarena.social.dto.PlayerSearchResultDto;
import com.devarena.social.dto.PublicPlayerProfileDto;
import com.devarena.social.model.FriendRequestStatus;
import com.devarena.social.repository.FriendRequestRepository;
import com.devarena.social.repository.FriendshipRepository;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.PlayerStatsEntity;
import com.devarena.user.model.ProfileEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerProgressionRepository;
import com.devarena.user.repository.PlayerStatsRepository;
import com.devarena.user.repository.ProfileRepository;
import com.devarena.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PlayerDiscoveryService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final PlayerProgressionRepository playerProgressionRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final PresenceService presenceService;
    private final PlayerChallengeRepository playerChallengeRepository;
    private final SkillProgressionService skillProgressionService;
    private final AchievementService achievementService;
    private final PlayerActivityRepository playerActivityRepository;

    public PlayerDiscoveryService(
            ProfileRepository profileRepository,
            UserRepository userRepository,
            PlayerStatsRepository playerStatsRepository,
            PlayerProgressionRepository playerProgressionRepository,
            FriendshipRepository friendshipRepository,
            FriendRequestRepository friendRequestRepository,
            PresenceService presenceService,
            PlayerChallengeRepository playerChallengeRepository,
            SkillProgressionService skillProgressionService,
            AchievementService achievementService,
            PlayerActivityRepository playerActivityRepository
    ) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.playerProgressionRepository = playerProgressionRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.presenceService = presenceService;
        this.playerChallengeRepository = playerChallengeRepository;
        this.skillProgressionService = skillProgressionService;
        this.achievementService = achievementService;
        this.playerActivityRepository = playerActivityRepository;
    }

    @Transactional(readOnly = true)
    public List<PlayerSearchResultDto> searchPlayers(String query, UUID currentUserId) {
        if (query == null || query.trim().length() < 1) {
            return Collections.emptyList();
        }

        List<ProfileEntity> profiles = profileRepository.searchProfiles(query.trim(), PageRequest.of(0, 20));
        List<PlayerSearchResultDto> results = new ArrayList<>();

        for (ProfileEntity profile : profiles) {
            UUID targetUserId = profile.getUser().getId();
            if (targetUserId.equals(currentUserId)) {
                continue; // Skip searching self
            }

            PlayerStatsEntity stats = playerStatsRepository.findByUserId(targetUserId).orElse(null);
            PlayerProgressionEntity progression = playerProgressionRepository.findByUserId(targetUserId).orElse(null);

            int rating = stats != null ? stats.getRating() : 1000;
            int level = progression != null ? progression.getLevel() : 1;
            String badge = computeRankBadge(rating);
            boolean online = presenceService.isOnline(targetUserId);

            boolean isFriend = currentUserId != null && friendshipRepository.areFriends(currentUserId, targetUserId);
            boolean hasPending = false;
            if (currentUserId != null && !isFriend) {
                hasPending = friendRequestRepository.findPendingBetweenUsers(
                        currentUserId, targetUserId, FriendRequestStatus.PENDING
                ).isPresent();
            }

            // Top skills highlights
            List<String> highlights = new ArrayList<>();
            try {
                List<PlayerSkillDto> skills = skillProgressionService.getPlayerSkills(targetUserId);
                skills.stream()
                        .filter(s -> s.currentLevel() > 0)
                        .limit(2)
                        .forEach(s -> highlights.add(s.name() + " Lvl " + s.currentLevel()));
            } catch (Exception ignored) {
            }

            results.add(new PlayerSearchResultDto(
                    targetUserId,
                    profile.getUsername(),
                    profile.getDisplayName(),
                    profile.getAvatar(),
                    level,
                    rating,
                    badge,
                    online,
                    isFriend,
                    hasPending,
                    highlights
            ));
        }

        return results;
    }

    @Transactional(readOnly = true)
    public PublicPlayerProfileDto getPublicProfile(String username, UUID currentUserId) {
        ProfileEntity profile = profileRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with username: " + username));

        UUID targetUserId = profile.getUser().getId();
        PlayerStatsEntity stats = playerStatsRepository.findByUserId(targetUserId).orElse(null);
        PlayerProgressionEntity progression = playerProgressionRepository.findByUserId(targetUserId).orElse(null);

        int rating = stats != null ? stats.getRating() : 1000;
        int level = progression != null ? progression.getLevel() : 1;
        int totalXp = progression != null ? progression.getTotalXp() : 0;
        int wins = stats != null ? stats.getWins() : 0;
        int losses = stats != null ? stats.getLosses() : 0;
        int draws = stats != null ? stats.getDraws() : 0;
        int totalBattles = wins + losses + draws;
        double winRate = totalBattles > 0 ? Math.round((double) wins / totalBattles * 1000.0) / 10.0 : 0.0;
        int streak = stats != null ? stats.getWinStreak() : 0;

        int solvedCount = (int) playerChallengeRepository.countByUserIdAndStatus(targetUserId, ChallengeProgressStatus.SOLVED);
        boolean online = presenceService.isOnline(targetUserId);

        boolean isFriend = currentUserId != null && friendshipRepository.areFriends(currentUserId, targetUserId);
        boolean hasPending = false;
        if (currentUserId != null && !isFriend && !currentUserId.equals(targetUserId)) {
            hasPending = friendRequestRepository.findPendingBetweenUsers(
                    currentUserId, targetUserId, FriendRequestStatus.PENDING
            ).isPresent();
        }

        // Top skills
        List<PublicPlayerProfileDto.PublicSkillDto> topSkills = new ArrayList<>();
        try {
            List<PlayerSkillDto> skills = skillProgressionService.getPlayerSkills(targetUserId);
            skills.stream()
                    .sorted((a, b) -> Integer.compare(b.currentLevel(), a.currentLevel()))
                    .limit(5)
                    .forEach(s -> topSkills.add(new PublicPlayerProfileDto.PublicSkillDto(
                            s.code(),
                            s.name(),
                            s.category().name(),
                            s.icon(),
                            s.currentLevel(),
                            s.masteryPercentage()
                    )));
        } catch (Exception ignored) {
        }

        // Unlocked achievements
        List<PublicPlayerProfileDto.PublicAchievementDto> unlockedAchievements = new ArrayList<>();
        try {
            List<PlayerAchievementDto> achievements = achievementService.getPlayerAchievements(targetUserId);
            achievements.stream()
                    .filter(PlayerAchievementDto::unlocked)
                    .forEach(a -> unlockedAchievements.add(new PublicPlayerProfileDto.PublicAchievementDto(
                            a.code(),
                            a.name(),
                            a.description(),
                            a.icon(),
                            a.rarity().name(),
                            a.unlockedAt()
                    )));
        } catch (Exception ignored) {
        }

        // Recent public activity
        List<PublicPlayerProfileDto.PublicActivityDto> activities = new ArrayList<>();
        try {
            List<PlayerActivityEntity> rawActivities = playerActivityRepository.findTop10ByUserIdOrderByCreatedAtDesc(targetUserId);
            rawActivities.forEach(act -> activities.add(new PublicPlayerProfileDto.PublicActivityDto(
                    act.getActivityType().name(),
                    act.getDescription(),
                    act.getCreatedAt()
            )));
        } catch (Exception ignored) {
        }

        // Calculate approximate rank
        int rank = 1;
        try {
            List<PlayerStatsEntity> higherRatings = playerStatsRepository.findAll().stream()
                    .filter(s -> s.getRating() > rating)
                    .toList();
            rank = higherRatings.size() + 1;
        } catch (Exception ignored) {
        }

        return new PublicPlayerProfileDto(
                targetUserId,
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getAvatar(),
                profile.getBio(),
                level,
                rating,
                rank,
                totalXp,
                solvedCount,
                wins,
                losses,
                winRate,
                streak,
                topSkills,
                unlockedAchievements,
                activities,
                isFriend,
                hasPending,
                online
        );
    }

    private String computeRankBadge(int rating) {
        if (rating >= 2400) return "Grandmaster";
        if (rating >= 2000) return "Master";
        if (rating >= 1600) return "Diamond";
        if (rating >= 1300) return "Gold";
        if (rating >= 1100) return "Silver";
        return "Bronze";
    }
}

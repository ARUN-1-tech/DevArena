package com.devarena.analytics.service;

import com.devarena.analytics.dto.PlayerAnalyticsResponse;
import com.devarena.battle.model.BattleEntity;
import com.devarena.battle.repository.BattleRepository;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeProgressStatus;
import com.devarena.challenge.model.PlayerChallengeEntity;
import com.devarena.challenge.repository.PlayerChallengeRepository;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.progression.model.PlayerActivityEntity;
import com.devarena.progression.repository.PlayerActivityRepository;
import com.devarena.skill.model.PlayerSkillEntity;
import com.devarena.skill.repository.PlayerSkillRepository;
import com.devarena.submission.model.SubmissionStatus;
import com.devarena.submission.repository.SubmissionRepository;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.PlayerStatsEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerProgressionRepository;
import com.devarena.user.repository.PlayerStatsRepository;
import com.devarena.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final UserRepository userRepository;
    private final PlayerProgressionRepository progressionRepository;
    private final PlayerStatsRepository statsRepository;
    private final PlayerChallengeRepository playerChallengeRepository;
    private final SubmissionRepository submissionRepository;
    private final BattleRepository battleRepository;
    private final PlayerSkillRepository playerSkillRepository;
    private final PlayerActivityRepository activityRepository;

    public AnalyticsService(
            UserRepository userRepository,
            PlayerProgressionRepository progressionRepository,
            PlayerStatsRepository statsRepository,
            PlayerChallengeRepository playerChallengeRepository,
            SubmissionRepository submissionRepository,
            BattleRepository battleRepository,
            PlayerSkillRepository playerSkillRepository,
            PlayerActivityRepository activityRepository
    ) {
        this.userRepository = userRepository;
        this.progressionRepository = progressionRepository;
        this.statsRepository = statsRepository;
        this.playerChallengeRepository = playerChallengeRepository;
        this.submissionRepository = submissionRepository;
        this.battleRepository = battleRepository;
        this.playerSkillRepository = playerSkillRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public PlayerAnalyticsResponse getPlayerAnalytics(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        PlayerProgressionEntity progression = user.getProgression() != null ? user.getProgression() :
                progressionRepository.findByUserId(userId).orElse(null);
        PlayerStatsEntity stats = user.getStats() != null ? user.getStats() :
                statsRepository.findByUserId(userId).orElse(null);

        // 1. Overview KPIs
        int currentLevel = progression != null ? progression.getLevel() : 1;
        int totalXp = progression != null ? progression.getTotalXp() : 0;
        int currentMmr = stats != null ? stats.getRating() : 1000;
        int currentStreak = progression != null ? progression.getCurrentStreak() : 0;
        int longestStreak = progression != null ? progression.getLongestStreak() : 0;

        // 2. Challenge & Submission Metrics
        long totalChallengesAttempted = playerChallengeRepository.countByUserId(userId);
        int totalChallengesSolved = progression != null ? progression.getChallengesSolved() :
                (int) playerChallengeRepository.countByUserIdAndStatus(userId, ChallengeProgressStatus.SOLVED);
        double challengeSolveRate = totalChallengesAttempted > 0
                ? Math.round(((double) totalChallengesSolved / totalChallengesAttempted) * 1000.0) / 10.0
                : 0.0;

        long totalSubmissions = submissionRepository.countByUserId(userId);
        long successfulSubmissions = submissionRepository.countByUserIdAndStatus(userId, SubmissionStatus.PASSED);
        double submissionSuccessRate = totalSubmissions > 0
                ? Math.round(((double) successfulSubmissions / totalSubmissions) * 1000.0) / 10.0
                : 0.0;

        // Difficulty & Category Distributions
        List<PlayerChallengeEntity> solvedChallenges = playerChallengeRepository.findByUserId(userId).stream()
                .filter(pc -> pc.getStatus() == ChallengeProgressStatus.SOLVED)
                .toList();

        Map<String, Integer> difficultyDistribution = new HashMap<>();
        difficultyDistribution.put("EASY", 0);
        difficultyDistribution.put("MEDIUM", 0);
        difficultyDistribution.put("HARD", 0);

        Map<String, Integer> categoryDistribution = new HashMap<>();

        for (PlayerChallengeEntity pc : solvedChallenges) {
            String diff = pc.getChallenge().getDifficulty().name();
            difficultyDistribution.put(diff, difficultyDistribution.getOrDefault(diff, 0) + 1);

            String cat = pc.getChallenge().getCategory().name();
            categoryDistribution.put(cat, categoryDistribution.getOrDefault(cat, 0) + 1);
        }

        // 3. Battle Performance
        int battleWins = stats != null ? stats.getWins() : 0;
        int battleLosses = stats != null ? stats.getLosses() : 0;
        int battleDraws = stats != null ? stats.getDraws() : 0;
        int totalBattles = battleWins + battleLosses;
        double battleWinRate = totalBattles > 0
                ? Math.round(((double) battleWins / totalBattles) * 1000.0) / 10.0
                : 0.0;

        // 4. MMR 30-Day Change
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        List<BattleEntity> recentMonthBattles = battleRepository.findCompletedBattlesByPlayer(userId, PageRequest.of(0, 50))
                .getContent().stream()
                .filter(b -> b.getEndedAt() != null && b.getEndedAt().isAfter(thirtyDaysAgo))
                .toList();

        int mmrChange30Days = 0;
        for (BattleEntity b : recentMonthBattles) {
            boolean isP1 = b.getPlayer1().getId().equals(userId);
            mmrChange30Days += isP1 ? b.getPlayer1RatingDelta() : b.getPlayer2RatingDelta();
        }

        // 5. Skill Mastery
        List<PlayerSkillEntity> playerSkills = playerSkillRepository.findTopSkillsByUserId(userId);
        String strongestSkill = playerSkills.isEmpty() ? "Arrays & Strings" : playerSkills.get(0).getSkill().getName();
        String weakestSkill = playerSkills.isEmpty() ? "Dynamic Programming" :
                playerSkills.get(playerSkills.size() - 1).getSkill().getName();

        List<PlayerAnalyticsResponse.SkillProgressSummaryDto> topSkills = playerSkills.stream()
                .limit(3)
                .map(ps -> new PlayerAnalyticsResponse.SkillProgressSummaryDto(
                        ps.getSkill().getName(),
                        ps.getSkill().getCategory().name(),
                        ps.getSkill().getIcon(),
                        ps.getCurrentLevel(),
                        ps.getMasteryPercentage()
                ))
                .collect(Collectors.toList());

        // 6. Time Series: Last 14 Days Daily XP Trend
        Instant fourteenDaysAgo = Instant.now().minus(14, ChronoUnit.DAYS);
        List<PlayerActivityEntity> activities = activityRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(a -> a.getCreatedAt().isAfter(fourteenDaysAgo))
                .toList();

        Map<String, Integer> xpByDate = new HashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

        // Initialize last 14 days with 0
        LocalDate today = LocalDate.now();
        for (int i = 13; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            xpByDate.put(day.format(fmt), 0);
        }

        for (PlayerActivityEntity a : activities) {
            LocalDate date = a.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
            String key = date.format(fmt);
            if (xpByDate.containsKey(key)) {
                xpByDate.put(key, xpByDate.get(key) + a.getXpEarned());
            }
        }

        List<PlayerAnalyticsResponse.DailyXpTrendDto> dailyXpTrend = xpByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new PlayerAnalyticsResponse.DailyXpTrendDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // 7. Recent Battles (Top 5)
        List<PlayerAnalyticsResponse.RecentBattleSummaryDto> recentBattles = battleRepository
                .findCompletedBattlesByPlayer(userId, PageRequest.of(0, 5))
                .getContent().stream()
                .map(b -> {
                    boolean isP1 = b.getPlayer1().getId().equals(userId);
                    UserEntity opponent = isP1 ? b.getPlayer2() : b.getPlayer1();
                    int delta = isP1 ? b.getPlayer1RatingDelta() : b.getPlayer2RatingDelta();
                    int xp = isP1 ? b.getPlayer1XpDelta() : b.getPlayer2XpDelta();
                    String outcome = (b.getWinner() == null) ? "DRAW" :
                            b.getWinner().getId().equals(userId) ? "WIN" : "LOSS";

                    String dateStr = b.getEndedAt() != null ? b.getEndedAt().toString() : b.getCreatedAt().toString();
                    return new PlayerAnalyticsResponse.RecentBattleSummaryDto(
                            opponent != null ? opponent.getUsername() : "Opponent",
                            outcome,
                            delta,
                            xp,
                            b.getChallenge() != null ? b.getChallenge().getTitle() : "Duel",
                            dateStr
                    );
                })
                .collect(Collectors.toList());

        return new PlayerAnalyticsResponse(
                currentLevel,
                totalXp,
                currentMmr,
                mmrChange30Days,
                currentStreak,
                longestStreak,
                totalChallengesAttempted,
                totalChallengesSolved,
                challengeSolveRate,
                totalSubmissions,
                successfulSubmissions,
                submissionSuccessRate,
                difficultyDistribution,
                categoryDistribution,
                battleWins,
                battleLosses,
                battleDraws,
                battleWinRate,
                strongestSkill,
                weakestSkill,
                topSkills,
                dailyXpTrend,
                recentBattles
        );
    }
}

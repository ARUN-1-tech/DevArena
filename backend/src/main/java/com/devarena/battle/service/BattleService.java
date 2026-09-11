package com.devarena.battle.service;

import com.devarena.battle.dto.*;
import com.devarena.battle.model.BattleEntity;
import com.devarena.battle.model.BattleStatus;
import com.devarena.battle.model.PlayerBattleStatus;
import com.devarena.battle.repository.BattleRepository;
import com.devarena.challenge.dto.ChallengeDetailDto;
import com.devarena.challenge.dto.TestCaseSummaryDto;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.model.ChallengeStarterCodeEntity;
import com.devarena.challenge.model.ChallengeTestCaseEntity;
import com.devarena.challenge.repository.ChallengeStarterCodeRepository;
import com.devarena.challenge.repository.ChallengeTestCaseRepository;
import com.devarena.common.exception.BadRequestException;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.execution.model.ExecutionLanguage;
import com.devarena.execution.service.CodeExecutionService;
import com.devarena.progression.model.ActivityType;
import com.devarena.progression.service.XPService;
import com.devarena.submission.model.SubmissionEntity;
import com.devarena.submission.model.SubmissionStatus;
import com.devarena.submission.repository.SubmissionRepository;
import com.devarena.user.model.PlayerStatsEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerStatsRepository;
import com.devarena.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BattleService {

    private static final Logger log = LoggerFactory.getLogger(BattleService.class);

    private static final int XP_WIN = 200;
    private static final int XP_DRAW = 75;
    private static final int XP_LOSS = 25;
    private static final int XP_FORFEIT = 0;

    private final BattleRepository battleRepository;
    private final UserRepository userRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final ChallengeStarterCodeRepository starterCodeRepository;
    private final ChallengeTestCaseRepository testCaseRepository;
    private final SubmissionRepository submissionRepository;
    private final CodeExecutionService codeExecutionService;
    private final RatingService ratingService;
    private final XPService xpService;
    private final SimpMessagingTemplate messagingTemplate;
    private final com.devarena.skill.service.SkillProgressionService skillProgressionService;
    private final com.devarena.achievement.service.AchievementService achievementService;

    // Track ready players per battle in memory
    private final Map<UUID, Set<UUID>> readyPlayers = new ConcurrentHashMap<>();

    public BattleService(
            BattleRepository battleRepository,
            UserRepository userRepository,
            PlayerStatsRepository playerStatsRepository,
            ChallengeStarterCodeRepository starterCodeRepository,
            ChallengeTestCaseRepository testCaseRepository,
            SubmissionRepository submissionRepository,
            CodeExecutionService codeExecutionService,
            RatingService ratingService,
            XPService xpService,
            SimpMessagingTemplate messagingTemplate,
            com.devarena.skill.service.SkillProgressionService skillProgressionService,
            com.devarena.achievement.service.AchievementService achievementService
    ) {
        this.battleRepository = battleRepository;
        this.userRepository = userRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.starterCodeRepository = starterCodeRepository;
        this.testCaseRepository = testCaseRepository;
        this.submissionRepository = submissionRepository;
        this.codeExecutionService = codeExecutionService;
        this.ratingService = ratingService;
        this.xpService = xpService;
        this.messagingTemplate = messagingTemplate;
        this.skillProgressionService = skillProgressionService;
        this.achievementService = achievementService;
    }

    @Transactional(readOnly = true)
    public BattleDetailResponse getBattle(UUID battleId, UUID userId) {
        BattleEntity battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new ResourceNotFoundException("Battle not found: " + battleId));

        validateParticipant(battle, userId);

        ChallengeEntity challenge = battle.getChallenge();
        ChallengeDetailDto challengeDto = buildChallengeDto(challenge);

        boolean isPlayer1 = battle.getPlayer1().getId().equals(userId);
        Set<UUID> readySet = readyPlayers.getOrDefault(battleId, Collections.emptySet());

        BattlePlayerDto p1 = new BattlePlayerDto(
                battle.getPlayer1().getId(),
                battle.getPlayer1().getUsername(),
                getAvatar(battle.getPlayer1()),
                getPlayerLevel(battle.getPlayer1()),
                getPlayerRating(battle.getPlayer1()),
                battle.getPlayer1Status(),
                readySet.contains(battle.getPlayer1().getId())
        );

        BattlePlayerDto p2 = new BattlePlayerDto(
                battle.getPlayer2().getId(),
                battle.getPlayer2().getUsername(),
                getAvatar(battle.getPlayer2()),
                getPlayerLevel(battle.getPlayer2()),
                getPlayerRating(battle.getPlayer2()),
                battle.getPlayer2Status(),
                readySet.contains(battle.getPlayer2().getId())
        );

        return new BattleDetailResponse(
                battle.getId(),
                battle.getStatus(),
                p1,
                p2,
                challengeDto,
                battle.getDurationSeconds(),
                battle.getStartedAt(),
                battle.getEndedAt(),
                battle.getWinner() != null ? battle.getWinner().getId() : null,
                battle.getFinishReason(),
                isPlayer1
        );
    }

    @Transactional
    public void markPlayerReady(UUID battleId, UUID userId) {
        BattleEntity battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new ResourceNotFoundException("Battle not found: " + battleId));

        validateParticipant(battle, userId);

        Set<UUID> readySet = readyPlayers.computeIfAbsent(battleId, k -> ConcurrentHashMap.newKeySet());
        readySet.add(userId);

        log.info("Player {} is ready in battle {}", userId, battleId);

        // Broadcast player ready state
        messagingTemplate.convertAndSend(
                "/topic/battle." + battleId,
                BattleEvent.of("PLAYER_STATUS", battleId, Map.of(
                        "userId", userId.toString(),
                        "status", "READY",
                        "readyCount", readySet.size()
                ))
        );

        // If both players ready and battle is WAITING, start countdown
        if (readySet.contains(battle.getPlayer1().getId()) && readySet.contains(battle.getPlayer2().getId())) {
            if (battle.getStatus() == BattleStatus.WAITING || battle.getStatus() == BattleStatus.READY) {
                battle.setStatus(BattleStatus.IN_PROGRESS);
                battle.setStartedAt(Instant.now());
                battleRepository.save(battle);

                log.info("Battle {} started! Countdown initiated.", battleId);

                messagingTemplate.convertAndSend(
                        "/topic/battle." + battleId,
                        BattleEvent.of("BATTLE_STARTED", battleId, Map.of(
                                "startedAt", battle.getStartedAt().toString(),
                                "durationSeconds", battle.getDurationSeconds()
                        ))
                );
            }
        }
    }

    public void broadcastCodingActivity(UUID battleId, UUID userId) {
        messagingTemplate.convertAndSend(
                "/topic/battle." + battleId,
                BattleEvent.of("PLAYER_STATUS", battleId, Map.of(
                        "userId", userId.toString(),
                        "status", "CODING"
                ))
        );
    }

    @Transactional
    public BattleSubmitResponse submitCode(UUID battleId, UUID userId, BattleSubmitRequest request) {
        BattleEntity battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new ResourceNotFoundException("Battle not found: " + battleId));

        validateParticipant(battle, userId);

        if (battle.getStatus() == BattleStatus.COMPLETED) {
            return new BattleSubmitResponse(
                    SubmissionStatus.FAILED,
                    0, 0, 0L,
                    true,
                    battle.getWinner() != null ? battle.getWinner().getId() : null,
                    battle.getFinishReason(),
                    "Battle has already finished"
            );
        }

        if (battle.getStatus() != BattleStatus.IN_PROGRESS) {
            throw new BadRequestException("Battle is not currently in progress.");
        }

        // Notify opponent that player submitted
        messagingTemplate.convertAndSend(
                "/topic/battle." + battleId,
                BattleEvent.of("PLAYER_SUBMITTED", battleId, Map.of(
                        "userId", userId.toString()
                ))
        );

        // Execute against all test cases for this challenge
        ExecutionLanguage lang;
        try {
            lang = ExecutionLanguage.valueOf(request.language().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Unsupported language: " + request.language());
        }

        List<ChallengeTestCaseEntity> allCases = testCaseRepository.findByChallengeIdOrderByOrderIndexAsc(battle.getChallenge().getId());
        CodeExecutionService.ExecutionBatchResult execResult = codeExecutionService.executeTestCases(lang, request.sourceCode(), allCases);

        // Save submission audit record
        UserEntity user = userRepository.findById(userId).orElseThrow();
        SubmissionEntity sub = new SubmissionEntity(user, battle.getChallenge(), lang, request.sourceCode());
        sub.setStatus(execResult.status());
        sub.setPassedTests(execResult.passedTests());
        sub.setTotalTests(execResult.totalTests());
        sub.setExecutionTimeMs(execResult.executionTimeMs());
        sub.setMemoryUsedBytes(execResult.memoryUsedBytes());
        sub.setErrorMessage(execResult.errorMessage());
        sub.setCompletedAt(Instant.now());
        sub = submissionRepository.save(sub);

        boolean passedAll = execResult.status() == SubmissionStatus.PASSED
                && execResult.passedTests() == execResult.totalTests()
                && execResult.totalTests() > 0;

        if (!passedAll) {
            return new BattleSubmitResponse(
                    execResult.status(),
                    execResult.passedTests(),
                    execResult.totalTests(),
                    execResult.executionTimeMs(),
                    false,
                    null,
                    null,
                    execResult.errorMessage()
            );
        }

        // Execution passed! Acquire pessimistic lock to guarantee single authoritative winner
        BattleEntity lockedBattle = battleRepository.findByIdWithLock(battleId).orElse(battle);
        if (lockedBattle.getStatus() == BattleStatus.IN_PROGRESS) {
            lockedBattle.setStatus(BattleStatus.COMPLETED);
            lockedBattle.setWinner(user);
            lockedBattle.setEndedAt(Instant.now());
            lockedBattle.setFinishReason("SOLVED");

            boolean isPlayer1 = lockedBattle.getPlayer1().getId().equals(userId);
            if (isPlayer1) {
                lockedBattle.setPlayer1Status(PlayerBattleStatus.PASSED);
                lockedBattle.setPlayer1Submission(sub);
            } else {
                lockedBattle.setPlayer2Status(PlayerBattleStatus.PASSED);
                lockedBattle.setPlayer2Submission(sub);
            }

            // Calculate Elo rating changes
            applyRatingAndXp(lockedBattle, isPlayer1 ? 1.0 : 0.0);
            battleRepository.save(lockedBattle);

            log.info("Battle {} won by {} via full test suite pass!", battleId, user.getUsername());

            messagingTemplate.convertAndSend(
                    "/topic/battle." + battleId,
                    BattleEvent.of("BATTLE_FINISHED", battleId, Map.of(
                            "winnerId", user.getId().toString(),
                            "winnerUsername", user.getUsername(),
                            "finishReason", "SOLVED"
                    ))
            );

            return new BattleSubmitResponse(
                    SubmissionStatus.PASSED,
                    execResult.passedTests(),
                    execResult.totalTests(),
                    execResult.executionTimeMs(),
                    true,
                    user.getId(),
                    "SOLVED",
                    null
            );
        }

        return new BattleSubmitResponse(
                SubmissionStatus.PASSED,
                execResult.passedTests(),
                execResult.totalTests(),
                execResult.executionTimeMs(),
                true,
                lockedBattle.getWinner() != null ? lockedBattle.getWinner().getId() : null,
                lockedBattle.getFinishReason(),
                null
        );
    }

    @Transactional
    public void forfeitBattle(UUID battleId, UUID userId) {
        BattleEntity battle = battleRepository.findByIdWithLock(battleId)
                .orElseThrow(() -> new ResourceNotFoundException("Battle not found: " + battleId));

        validateParticipant(battle, userId);

        if (battle.getStatus() == BattleStatus.COMPLETED || battle.getStatus() == BattleStatus.CANCELLED) {
            return;
        }

        boolean isPlayer1 = battle.getPlayer1().getId().equals(userId);
        UserEntity winner = isPlayer1 ? battle.getPlayer2() : battle.getPlayer1();

        battle.setStatus(BattleStatus.COMPLETED);
        battle.setWinner(winner);
        battle.setEndedAt(Instant.now());
        battle.setFinishReason("FORFEIT");

        if (isPlayer1) {
            battle.setPlayer1Status(PlayerBattleStatus.FORFEITED);
        } else {
            battle.setPlayer2Status(PlayerBattleStatus.FORFEITED);
        }

        applyRatingAndXp(battle, isPlayer1 ? 0.0 : 1.0);
        battleRepository.save(battle);

        log.info("Player {} forfeited battle {}. Winner: {}", userId, battleId, winner.getUsername());

        messagingTemplate.convertAndSend(
                "/topic/battle." + battleId,
                BattleEvent.of("BATTLE_FINISHED", battleId, Map.of(
                        "winnerId", winner.getId().toString(),
                        "winnerUsername", winner.getUsername(),
                        "finishReason", "FORFEIT"
                ))
        );
    }

    @Transactional(readOnly = true)
    public BattleResultResponse getBattleResult(UUID battleId, UUID userId) {
        BattleEntity battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new ResourceNotFoundException("Battle not found: " + battleId));

        validateParticipant(battle, userId);

        boolean isPlayer1 = battle.getPlayer1().getId().equals(userId);
        UserEntity me = isPlayer1 ? battle.getPlayer1() : battle.getPlayer2();
        UserEntity opponent = isPlayer1 ? battle.getPlayer2() : battle.getPlayer1();

        String outcome;
        if (battle.getWinner() == null) {
            outcome = "DRAW";
        } else if (battle.getWinner().getId().equals(userId)) {
            outcome = "WIN";
        } else {
            outcome = "LOSS";
        }

        int myXp = isPlayer1 ? battle.getPlayer1XpDelta() : battle.getPlayer2XpDelta();
        int myDelta = isPlayer1 ? battle.getPlayer1RatingDelta() : battle.getPlayer2RatingDelta();
        int oppDelta = isPlayer1 ? battle.getPlayer2RatingDelta() : battle.getPlayer1RatingDelta();

        int duration = battle.getStartedAt() != null && battle.getEndedAt() != null
                ? (int) Duration.between(battle.getStartedAt(), battle.getEndedAt()).getSeconds()
                : 0;

        return new BattleResultResponse(
                battle.getId(),
                battle.getStatus(),
                outcome,
                battle.getWinner() != null ? battle.getWinner().getId() : null,
                battle.getFinishReason(),
                duration,
                myXp,
                myDelta,
                getPlayerRating(me),
                opponent.getUsername(),
                getAvatar(opponent),
                getPlayerRating(opponent),
                oppDelta,
                battle.getChallenge().getTitle(),
                battle.getEndedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<BattleHistoryItemDto> getBattleHistory(UUID userId, Pageable pageable) {
        return battleRepository.findCompletedBattlesByPlayer(userId, pageable)
                .map(battle -> {
                    boolean isPlayer1 = battle.getPlayer1().getId().equals(userId);
                    UserEntity opponent = isPlayer1 ? battle.getPlayer2() : battle.getPlayer1();

                    String outcome;
                    if (battle.getWinner() == null) {
                        outcome = "DRAW";
                    } else if (battle.getWinner().getId().equals(userId)) {
                        outcome = "WIN";
                    } else {
                        outcome = "LOSS";
                    }

                    int myDelta = isPlayer1 ? battle.getPlayer1RatingDelta() : battle.getPlayer2RatingDelta();
                    int myXp = isPlayer1 ? battle.getPlayer1XpDelta() : battle.getPlayer2XpDelta();

                    return new BattleHistoryItemDto(
                            battle.getId(),
                            battle.getChallenge().getTitle(),
                            battle.getChallenge().getDifficulty(),
                            opponent.getUsername(),
                            getAvatar(opponent),
                            outcome,
                            myDelta,
                            myXp,
                            battle.getEndedAt() != null ? battle.getEndedAt() : battle.getCreatedAt()
                    );
                });
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void checkBattleTimeouts() {
        Instant cutoff = Instant.now().minusSeconds(900); // 15 minutes
        List<BattleEntity> expired = battleRepository.findExpiredInProgressBattles(cutoff);

        for (BattleEntity battle : expired) {
            log.info("Battle {} expired due to timer limit. Finishing as DRAW.", battle.getId());
            battle.setStatus(BattleStatus.COMPLETED);
            battle.setWinner(null);
            battle.setEndedAt(Instant.now());
            battle.setFinishReason("TIME_EXPIRED");

            applyRatingAndXp(battle, 0.5); // Draw
            battleRepository.save(battle);

            messagingTemplate.convertAndSend(
                    "/topic/battle." + battle.getId(),
                    BattleEvent.of("BATTLE_FINISHED", battle.getId(), Map.of(
                            "finishReason", "TIME_EXPIRED",
                            "outcome", "DRAW"
                    ))
            );
        }
    }

    private void applyRatingAndXp(BattleEntity battle, double player1Score) {
        UserEntity p1 = battle.getPlayer1();
        UserEntity p2 = battle.getPlayer2();

        int r1 = getPlayerRating(p1);
        int r2 = getPlayerRating(p2);

        RatingService.RatingResult elo = ratingService.calculateElo(r1, r2, player1Score);

        battle.setPlayer1RatingDelta(elo.player1Delta());
        battle.setPlayer2RatingDelta(elo.player2Delta());

        // Update player record and stats (wins, losses, win streak, rating)
        boolean p1Win = (player1Score == 1.0);
        boolean p2Win = (player1Score == 0.0);
        boolean isDraw = (player1Score == 0.5);

        updatePlayerRecord(p1, elo.player1NewRating(), p1Win, p2Win, isDraw);
        updatePlayerRecord(p2, elo.player2NewRating(), p2Win, p1Win, isDraw);

        // Award XP
        int xp1, xp2;
        if (p1Win) {
            xp1 = XP_WIN;
            xp2 = battle.getPlayer2Status() == PlayerBattleStatus.FORFEITED ? XP_FORFEIT : XP_LOSS;
            xpService.awardXP(p1.getId(), xp1, ActivityType.BATTLE_VICTORY, "1v1 Battle Victory", "Won 1v1 battle against " + p2.getUsername());
            if (xp2 > 0) xpService.awardXP(p2.getId(), xp2, ActivityType.BATTLE_PARTICIPATION, "Battle Participation", "Completed 1v1 battle");
        } else if (p2Win) {
            xp1 = battle.getPlayer1Status() == PlayerBattleStatus.FORFEITED ? XP_FORFEIT : XP_LOSS;
            xp2 = XP_WIN;
            if (xp1 > 0) xpService.awardXP(p1.getId(), xp1, ActivityType.BATTLE_PARTICIPATION, "Battle Participation", "Completed 1v1 battle");
            xpService.awardXP(p2.getId(), xp2, ActivityType.BATTLE_VICTORY, "1v1 Battle Victory", "Won 1v1 battle against " + p1.getUsername());
        } else {
            xp1 = XP_DRAW;
            xp2 = XP_DRAW;
            xpService.awardXP(p1.getId(), xp1, ActivityType.BATTLE_DRAW, "1v1 Battle Draw", "Fought to a draw against " + p2.getUsername());
            xpService.awardXP(p2.getId(), xp2, ActivityType.BATTLE_DRAW, "1v1 Battle Draw", "Fought to a draw against " + p1.getUsername());
        }

        battle.setPlayer1XpDelta(xp1);
        battle.setPlayer2XpDelta(xp2);

        // Award skill XP based on battle challenge category
        if (battle.getChallenge() != null) {
            try {
                skillProgressionService.awardSkillXp(p1.getId(), battle.getChallenge().getCategory(), xp1);
                skillProgressionService.awardSkillXp(p2.getId(), battle.getChallenge().getCategory(), xp2);
            } catch (Exception ex) {
                log.warn("Failed to award skill XP for battle: {}", ex.getMessage());
            }
        }

        // Evaluate achievements for both participants
        try {
            achievementService.evaluateAndUnlock(p1.getId());
            achievementService.evaluateAndUnlock(p2.getId());
        } catch (Exception ex) {
            log.warn("Failed to evaluate achievements for battle: {}", ex.getMessage());
        }
    }

    private void updatePlayerRecord(UserEntity user, int newRating, boolean isWin, boolean isLoss, boolean isDraw) {
        PlayerStatsEntity stats = user.getStats();
        if (stats == null) {
            stats = playerStatsRepository.findByUserId(user.getId())
                    .orElseGet(() -> new PlayerStatsEntity(user));
            user.setStats(stats);
        }
        stats.setRating(newRating);
        if (newRating > stats.getHighestRating()) {
            stats.setHighestRating(newRating);
        }
        if (isWin) {
            stats.setWins(stats.getWins() + 1);
            stats.setWinStreak(stats.getWinStreak() + 1);
        } else if (isLoss) {
            stats.setLosses(stats.getLosses() + 1);
            stats.setWinStreak(0);
        } else if (isDraw) {
            stats.setDraws(stats.getDraws() + 1);
        }
        playerStatsRepository.save(stats);
    }

    private void validateParticipant(BattleEntity battle, UUID userId) {
        if (!battle.getPlayer1().getId().equals(userId) && !battle.getPlayer2().getId().equals(userId)) {
            throw new BadRequestException("You are not an authorized participant in this battle.");
        }
    }

    private ChallengeDetailDto buildChallengeDto(ChallengeEntity challenge) {
        Map<String, String> starters = starterCodeRepository.findByChallengeId(challenge.getId())
                .stream()
                .collect(Collectors.toMap(sc -> sc.getLanguage().name(), ChallengeStarterCodeEntity::getStarterCode, (a, b) -> a));

        List<TestCaseSummaryDto> sampleCases = testCaseRepository.findByChallengeIdAndHiddenFalseOrderByOrderIndexAsc(challenge.getId())
                .stream()
                .map(tc -> new TestCaseSummaryDto(tc.getId(), tc.getOrderIndex(), tc.getInput(), tc.getExpectedOutput(), tc.getExplanation()))
                .toList();

        return new ChallengeDetailDto(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getSlug(),
                challenge.getDescription(),
                challenge.getDifficulty(),
                challenge.getCategory(),
                challenge.getXpReward(),
                challenge.getEstimatedMinutes(),
                challenge.getTags(),
                null,
                null,
                sampleCases,
                starters
        );
    }

    private String getAvatar(UserEntity user) {
        return (user.getProfile() != null && user.getProfile().getAvatar() != null)
                ? user.getProfile().getAvatar()
                : "avatar-1";
    }

    private int getPlayerLevel(UserEntity user) {
        return user.getProgression() != null ? user.getProgression().getLevel() : 1;
    }

    private int getPlayerRating(UserEntity user) {
        return user.getStats() != null && user.getStats().getRating() > 0
                ? user.getStats().getRating()
                : 1000;
    }
}

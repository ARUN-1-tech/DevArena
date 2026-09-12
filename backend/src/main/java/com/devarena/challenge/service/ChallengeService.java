package com.devarena.challenge.service;

import com.devarena.challenge.dto.*;
import com.devarena.challenge.model.*;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.challenge.repository.ChallengeStarterCodeRepository;
import com.devarena.challenge.repository.ChallengeTestCaseRepository;
import com.devarena.challenge.repository.PlayerChallengeRepository;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.progression.dto.XpRewardResult;
import com.devarena.progression.model.ActivityType;
import com.devarena.progression.service.XPService;
import com.devarena.quest.service.DailyQuestService;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerProgressionRepository;
import com.devarena.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final PlayerChallengeRepository playerChallengeRepository;
    private final UserRepository userRepository;
    private final PlayerProgressionRepository progressionRepository;
    private final XPService xpService;
    private final DailyQuestService dailyQuestService;
    private final ChallengeTestCaseRepository testCaseRepository;
    private final ChallengeStarterCodeRepository starterCodeRepository;

    public ChallengeService(
            ChallengeRepository challengeRepository,
            PlayerChallengeRepository playerChallengeRepository,
            UserRepository userRepository,
            PlayerProgressionRepository progressionRepository,
            XPService xpService,
            DailyQuestService dailyQuestService,
            ChallengeTestCaseRepository testCaseRepository,
            ChallengeStarterCodeRepository starterCodeRepository) {
        this.challengeRepository = challengeRepository;
        this.playerChallengeRepository = playerChallengeRepository;
        this.userRepository = userRepository;
        this.progressionRepository = progressionRepository;
        this.xpService = xpService;
        this.dailyQuestService = dailyQuestService;
        this.testCaseRepository = testCaseRepository;
        this.starterCodeRepository = starterCodeRepository;
    }

    public Page<ChallengeCardDto> getChallenges(String search, ChallengeDifficulty difficulty, ChallengeCategory category, Pageable pageable, UUID userId) {
        return getChallenges(search, difficulty, category, null, pageable, userId);
    }

    public Page<ChallengeCardDto> getChallenges(
            String search,
            ChallengeDifficulty difficulty,
            ChallengeCategory category,
            ProblemType problemType,
            Pageable pageable,
            UUID userId
    ) {
        Page<ChallengeEntity> entityPage = challengeRepository.searchChallenges(
                ChallengeStatus.PUBLISHED,
                difficulty,
                category,
                problemType,
                (search != null && !search.isBlank()) ? search.trim() : null,
                pageable
        );

        Map<UUID, ChallengeProgressStatus> progressMap = new HashMap<>();
        if (userId != null) {
            List<PlayerChallengeEntity> progresses = playerChallengeRepository.findByUserId(userId);
            for (PlayerChallengeEntity p : progresses) {
                progressMap.put(p.getChallenge().getId(), p.getStatus());
            }
        }

        List<ChallengeCardDto> cards = entityPage.getContent().stream().map(c -> new ChallengeCardDto(
                c.getId(),
                c.getTitle(),
                c.getSlug(),
                c.getDifficulty(),
                c.getCategory(),
                c.getProblemType(),
                c.getXpReward(),
                c.getEstimatedMinutes(),
                c.getTags(),
                progressMap.getOrDefault(c.getId(), ChallengeProgressStatus.NOT_STARTED),
                c.getOptions(),
                c.getSource()
        )).collect(Collectors.toList());

        return new PageImpl<>(cards, pageable, entityPage.getTotalElements());
    }

    public ChallengeCountStatsDto getChallengeStats(UUID userId) {
        long total = challengeRepository.countByStatus(ChallengeStatus.PUBLISHED);
        long solvedCount = 0;
        if (userId != null) {
            solvedCount = playerChallengeRepository.countByUserIdAndStatus(userId, ChallengeProgressStatus.SOLVED);
        }

        Map<String, Long> byDifficulty = new LinkedHashMap<>();
        for (ChallengeDifficulty diff : ChallengeDifficulty.values()) {
            byDifficulty.put(diff.name(), challengeRepository.countByDifficultyAndStatus(diff, ChallengeStatus.PUBLISHED));
        }

        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (ChallengeCategory cat : ChallengeCategory.values()) {
            long count = challengeRepository.countByCategoryAndStatus(cat, ChallengeStatus.PUBLISHED);
            if (count > 0) {
                byCategory.put(cat.name(), count);
            }
        }

        Map<String, Long> byProblemType = new LinkedHashMap<>();
        for (ProblemType type : ProblemType.values()) {
            long count = challengeRepository.countByProblemTypeAndStatus(type, ChallengeStatus.PUBLISHED);
            if (count > 0) {
                byProblemType.put(type.name(), count);
            }
        }

        return new ChallengeCountStatsDto(total, solvedCount, byDifficulty, byCategory, byProblemType);
    }

    public ChallengeDetailDto getChallengeDetail(UUID challengeId, UUID userId) {
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + challengeId));

        ChallengeProgressStatus progressStatus = ChallengeProgressStatus.NOT_STARTED;
        Instant completedAt = null;

        if (userId != null) {
            Optional<PlayerChallengeEntity> playerChallenge = playerChallengeRepository.findByUserIdAndChallengeId(userId, challengeId);
            if (playerChallenge.isPresent()) {
                progressStatus = playerChallenge.get().getStatus();
                completedAt = playerChallenge.get().getCompletedAt();
            }
        }

        List<TestCaseSummaryDto> sampleCases = testCaseRepository
                .findByChallengeIdAndHiddenFalseOrderByOrderIndexAsc(challenge.getId())
                .stream()
                .map(tc -> new TestCaseSummaryDto(
                        tc.getId(),
                        tc.getOrderIndex(),
                        tc.getInput(),
                        tc.getExpectedOutput(),
                        tc.getExplanation()
                ))
                .toList();

        Map<String, String> starterMap = new HashMap<>();
        starterCodeRepository.findByChallengeId(challenge.getId()).forEach(sc ->
                starterMap.put(sc.getLanguage().name(), sc.getStarterCode())
        );

        return new ChallengeDetailDto(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getSlug(),
                challenge.getDescription(),
                challenge.getDifficulty(),
                challenge.getCategory(),
                challenge.getProblemType(),
                challenge.getXpReward(),
                challenge.getEstimatedMinutes(),
                challenge.getTimeLimitSeconds(),
                challenge.getTags(),
                challenge.getOptions(),
                challenge.getHints(),
                challenge.getSolutionApproach(),
                challenge.getSource(),
                progressStatus,
                completedAt,
                sampleCases,
                starterMap
        );
    }

    public ChallengeProgressDto getChallengeProgress(UUID challengeId, UUID userId) {
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + challengeId));

        PlayerChallengeEntity progress = playerChallengeRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElse(null);

        return new ChallengeProgressDto(
                challenge.getId(),
                userId,
                progress != null ? progress.getStatus() : ChallengeProgressStatus.NOT_STARTED,
                progress != null ? progress.getAttempts() : 0,
                progress != null ? progress.getBestResult() : null,
                progress != null ? progress.getLastSubmissionAt() : null,
                progress != null ? progress.getCompletedAt() : null
        );
    }

    @Transactional
    public ChallengeProgressDto startChallenge(UUID challengeId, UUID userId) {
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + challengeId));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        PlayerChallengeEntity progress = playerChallengeRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseGet(() -> new PlayerChallengeEntity(user, challenge, ChallengeProgressStatus.NOT_STARTED));

        if (progress.getStatus() == ChallengeProgressStatus.NOT_STARTED) {
            progress.setStatus(ChallengeProgressStatus.ATTEMPTED);
            playerChallengeRepository.save(progress);
        }

        return new ChallengeProgressDto(
                challenge.getId(),
                userId,
                progress.getStatus(),
                progress.getAttempts(),
                progress.getBestResult(),
                progress.getLastSubmissionAt(),
                progress.getCompletedAt()
        );
    }

    @Transactional
    public SubmitAnswerResponse submitNonCodingAnswer(UUID challengeId, String submittedAnswer, UUID userId) {
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + challengeId));

        String correctAnswer = challenge.getCorrectAnswer();
        boolean isCorrect = false;

        if (correctAnswer != null && submittedAnswer != null) {
            isCorrect = correctAnswer.trim().equalsIgnoreCase(submittedAnswer.trim());
        } else if (challenge.getProblemType() == ProblemType.GENERAL) {
            isCorrect = true; // General conceptual task completion
        }

        XpRewardResult rewardResult = null;
        if (isCorrect) {
            rewardResult = solveChallenge(challengeId, userId);
        }

        String explanation = challenge.getSolutionApproach();
        if (explanation == null || explanation.isBlank()) {
            explanation = challenge.getHints();
        }

        String message = isCorrect ? "Correct answer! XP awarded." : "Incorrect answer. Check the explanation and try again!";
        return new SubmitAnswerResponse(isCorrect, submittedAnswer, correctAnswer, explanation, message, rewardResult);
    }

    @Transactional
    public XpRewardResult solveChallenge(UUID challengeId, UUID userId) {
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + challengeId));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        PlayerChallengeEntity progress = playerChallengeRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseGet(() -> new PlayerChallengeEntity(user, challenge, ChallengeProgressStatus.NOT_STARTED));

        boolean wasSolved = (progress.getStatus() == ChallengeProgressStatus.SOLVED);
        progress.setStatus(ChallengeProgressStatus.SOLVED);
        if (progress.getCompletedAt() == null) {
            progress.setCompletedAt(Instant.now());
        }
        playerChallengeRepository.save(progress);

        if (!wasSolved) {
            PlayerProgressionEntity progression = user.getProgression();
            if (progression != null) {
                progression.setChallengesSolved(progression.getChallengesSolved() + 1);
                progressionRepository.save(progression);
            }

            // Advance matching daily quests for player
            dailyQuestService.incrementQuestProgress(userId, com.devarena.quest.model.QuestType.COMPLETE_CHALLENGE, 1);
            dailyQuestService.incrementQuestProgress(userId, com.devarena.quest.model.QuestType.EARN_XP, challenge.getXpReward());
            if (challenge.getDifficulty() == ChallengeDifficulty.EASY) {
                dailyQuestService.incrementQuestProgress(userId, com.devarena.quest.model.QuestType.COMPLETE_EASY, 1);
            }

            return xpService.awardXP(
                    userId,
                    challenge.getXpReward(),
                    ActivityType.CHALLENGE_SOLVED,
                    "Solved " + challenge.getTitle(),
                    "Completed " + challenge.getDifficulty() + " challenge in " + challenge.getCategory()
            );
        }

        // Already solved: no duplicate XP
        PlayerProgressionEntity p = user.getProgression();
        return new XpRewardResult(
                p != null ? p.getCurrentXp() : 0,
                p != null ? p.getCurrentXp() : 0,
                p != null ? p.getLevel() : 1,
                p != null ? p.getLevel() : 1,
                p != null ? p.getTotalXp() : 0,
                0,
                p != null ? p.getXpToNextLevel() : 100,
                false
        );
    }
}

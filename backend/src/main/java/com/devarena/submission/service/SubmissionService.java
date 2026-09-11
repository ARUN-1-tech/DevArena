package com.devarena.submission.service;

import com.devarena.challenge.dto.ChallengeProgressDto;
import com.devarena.challenge.model.*;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.challenge.repository.ChallengeTestCaseRepository;
import com.devarena.challenge.repository.PlayerChallengeRepository;
import com.devarena.challenge.service.ChallengeService;
import com.devarena.common.exception.BadRequestException;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.execution.dto.RunCodeRequest;
import com.devarena.execution.dto.RunCodeResponse;
import com.devarena.execution.model.ExecutionLanguage;
import com.devarena.execution.service.CodeExecutionService;
import com.devarena.execution.service.ExecutionRateLimiter;
import com.devarena.progression.dto.XpRewardResult;
import com.devarena.submission.dto.*;
import com.devarena.submission.model.SubmissionEntity;
import com.devarena.submission.model.SubmissionStatus;
import com.devarena.submission.repository.SubmissionRepository;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionService.class);

    private final ChallengeRepository challengeRepository;
    private final ChallengeTestCaseRepository testCaseRepository;
    private final PlayerChallengeRepository playerChallengeRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CodeExecutionService codeExecutionService;
    private final ChallengeService challengeService;
    private final ExecutionRateLimiter rateLimiter;
    private final com.devarena.skill.service.SkillProgressionService skillProgressionService;
    private final com.devarena.achievement.service.AchievementService achievementService;

    public SubmissionService(
            ChallengeRepository challengeRepository,
            ChallengeTestCaseRepository testCaseRepository,
            PlayerChallengeRepository playerChallengeRepository,
            SubmissionRepository submissionRepository,
            UserRepository userRepository,
            CodeExecutionService codeExecutionService,
            ChallengeService challengeService,
            ExecutionRateLimiter rateLimiter,
            com.devarena.skill.service.SkillProgressionService skillProgressionService,
            com.devarena.achievement.service.AchievementService achievementService) {
        this.challengeRepository = challengeRepository;
        this.testCaseRepository = testCaseRepository;
        this.playerChallengeRepository = playerChallengeRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.codeExecutionService = codeExecutionService;
        this.challengeService = challengeService;
        this.rateLimiter = rateLimiter;
        this.skillProgressionService = skillProgressionService;
        this.achievementService = achievementService;
    }

    public RunCodeResponse runCode(UUID userId, RunCodeRequest request) {
        rateLimiter.checkRateLimit(userId, false);

        ChallengeEntity challenge = challengeRepository.findById(request.challengeId())
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + request.challengeId()));

        ExecutionLanguage language;
        try {
            language = ExecutionLanguage.fromString(request.language());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }

        List<ChallengeTestCaseEntity> visibleTestCases = testCaseRepository
                .findByChallengeIdAndHiddenFalseOrderByOrderIndexAsc(challenge.getId());

        if (visibleTestCases.isEmpty()) {
            throw new BadRequestException("No visible test cases configured for this challenge.");
        }

        CodeExecutionService.ExecutionBatchResult batchResult = codeExecutionService.executeTestCases(
                language,
                request.sourceCode(),
                visibleTestCases
        );

        return new RunCodeResponse(
                batchResult.status(),
                batchResult.passedTests(),
                batchResult.totalTests(),
                batchResult.executionTimeMs(),
                batchResult.memoryUsedBytes(),
                batchResult.stdout(),
                batchResult.stderr(),
                batchResult.errorMessage(),
                batchResult.testResults()
        );
    }

    @Transactional
    public SubmitCodeResponse submitCode(UUID userId, SubmitCodeRequest request) {
        rateLimiter.checkRateLimit(userId, true);

        ChallengeEntity challenge = challengeRepository.findById(request.challengeId())
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + request.challengeId()));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        ExecutionLanguage language;
        try {
            language = ExecutionLanguage.fromString(request.language());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }

        List<ChallengeTestCaseEntity> allTestCases = testCaseRepository
                .findByChallengeIdOrderByOrderIndexAsc(challenge.getId());

        if (allTestCases.isEmpty()) {
            throw new BadRequestException("No test cases configured for this challenge.");
        }

        CodeExecutionService.ExecutionBatchResult batchResult = codeExecutionService.executeTestCases(
                language,
                request.sourceCode(),
                allTestCases
        );

        Instant completedAt = Instant.now();

        // 1. Persist submission
        SubmissionEntity submission = new SubmissionEntity(user, challenge, language, request.sourceCode());
        submission.setStatus(batchResult.status());
        submission.setPassedTests(batchResult.passedTests());
        submission.setTotalTests(batchResult.totalTests());
        submission.setExecutionTimeMs(batchResult.executionTimeMs());
        submission.setMemoryUsedBytes(batchResult.memoryUsedBytes());
        submission.setErrorMessage(batchResult.errorMessage());
        submission.setCompletedAt(completedAt);
        submission = submissionRepository.save(submission);

        // 2. Update player challenge progress
        int xpEarned = 0;
        boolean firstSolve = false;
        XpRewardResult xpResult = null;

        List<com.devarena.achievement.dto.AchievementDto> unlockedAchievements = Collections.emptyList();
        if (batchResult.status() == SubmissionStatus.PASSED) {
            xpResult = challengeService.solveChallenge(challenge.getId(), userId);
            xpEarned = xpResult.xpEarned();
            firstSolve = (xpEarned > 0);

            PlayerChallengeEntity playerChallenge = playerChallengeRepository.findByUserIdAndChallengeId(userId, challenge.getId())
                    .orElseGet(() -> new PlayerChallengeEntity(user, challenge, ChallengeProgressStatus.SOLVED));
            playerChallenge.setAttempts(playerChallenge.getAttempts() + 1);
            playerChallenge.setBestResult("PASSED");
            playerChallenge.setLastSubmissionAt(completedAt);
            playerChallengeRepository.save(playerChallenge);

            // Award skill XP based on challenge category
            skillProgressionService.awardSkillXp(userId, challenge.getCategory(), challenge.getXpReward());

            // Evaluate automatic achievement unlocks
            unlockedAchievements = achievementService.evaluateAndUnlock(userId);
        } else {
            PlayerChallengeEntity playerChallenge = playerChallengeRepository.findByUserIdAndChallengeId(userId, challenge.getId())
                    .orElseGet(() -> new PlayerChallengeEntity(user, challenge, ChallengeProgressStatus.NOT_STARTED));
            playerChallenge.setAttempts(playerChallenge.getAttempts() + 1);
            playerChallenge.setLastSubmissionAt(completedAt);
            if (playerChallenge.getBestResult() == null || !"PASSED".equals(playerChallenge.getBestResult())) {
                playerChallenge.setBestResult(batchResult.status().name());
            }
            if (playerChallenge.getStatus() == ChallengeProgressStatus.NOT_STARTED) {
                playerChallenge.setStatus(ChallengeProgressStatus.ATTEMPTED);
            }
            playerChallengeRepository.save(playerChallenge);
        }

        return new SubmitCodeResponse(
                submission.getId(),
                submission.getStatus(),
                submission.getPassedTests(),
                submission.getTotalTests(),
                submission.getExecutionTimeMs(),
                submission.getMemoryUsedBytes(),
                submission.getErrorMessage(),
                xpEarned,
                firstSolve,
                xpResult,
                submission.getCreatedAt(),
                submission.getCompletedAt(),
                batchResult.testResults(),
                unlockedAchievements
        );
    }

    public SubmissionDetailDto getSubmission(UUID userId, UUID submissionId) {
        SubmissionEntity s = submissionRepository.findByIdAndUserId(submissionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found or unauthorized"));

        return new SubmissionDetailDto(
                s.getId(),
                s.getChallenge().getId(),
                s.getChallenge().getTitle(),
                s.getLanguage(),
                s.getSourceCode(),
                s.getStatus(),
                s.getPassedTests(),
                s.getTotalTests(),
                s.getExecutionTimeMs(),
                s.getMemoryUsedBytes(),
                s.getErrorMessage(),
                s.getCreatedAt(),
                s.getCompletedAt()
        );
    }

    public Page<SubmissionSummaryDto> getChallengeSubmissions(UUID userId, UUID challengeId, Pageable pageable) {
        Page<SubmissionEntity> submissions = submissionRepository.findByUserIdAndChallengeIdOrderByCreatedAtDesc(userId, challengeId, pageable);
        return submissions.map(s -> new SubmissionSummaryDto(
                s.getId(),
                s.getChallenge().getId(),
                s.getLanguage(),
                s.getStatus(),
                s.getPassedTests(),
                s.getTotalTests(),
                s.getExecutionTimeMs(),
                s.getCreatedAt()
        ));
    }

    public ChallengeProgressDto getChallengeProgress(UUID userId, UUID challengeId) {
        PlayerChallengeEntity p = playerChallengeRepository.findByUserIdAndChallengeId(userId, challengeId).orElse(null);
        ChallengeProgressStatus status = p != null ? p.getStatus() : ChallengeProgressStatus.NOT_STARTED;
        int attempts = p != null ? p.getAttempts() : 0;
        String bestResult = p != null ? p.getBestResult() : null;
        Instant lastSubmission = p != null ? p.getLastSubmissionAt() : null;
        Instant solvedDate = p != null ? p.getCompletedAt() : null;

        return new ChallengeProgressDto(challengeId, userId, status, attempts, bestResult, lastSubmission, solvedDate);
    }
}

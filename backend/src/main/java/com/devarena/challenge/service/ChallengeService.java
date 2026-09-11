package com.devarena.challenge.service;

import com.devarena.challenge.dto.ChallengeCardDto;
import com.devarena.challenge.dto.ChallengeDetailDto;
import com.devarena.challenge.dto.ChallengeProgressDto;
import com.devarena.challenge.model.*;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.challenge.repository.PlayerChallengeRepository;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.progression.dto.XpRewardResult;
import com.devarena.progression.model.ActivityType;
import com.devarena.progression.service.XPService;
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
    private final com.devarena.quest.service.DailyQuestService dailyQuestService;

    public ChallengeService(
            ChallengeRepository challengeRepository,
            PlayerChallengeRepository playerChallengeRepository,
            UserRepository userRepository,
            PlayerProgressionRepository progressionRepository,
            XPService xpService,
            com.devarena.quest.service.DailyQuestService dailyQuestService) {
        this.challengeRepository = challengeRepository;
        this.playerChallengeRepository = playerChallengeRepository;
        this.userRepository = userRepository;
        this.progressionRepository = progressionRepository;
        this.xpService = xpService;
        this.dailyQuestService = dailyQuestService;
    }

    public Page<ChallengeCardDto> getChallenges(String search, ChallengeDifficulty difficulty, ChallengeCategory category, Pageable pageable, UUID userId) {
        Page<ChallengeEntity> entityPage = challengeRepository.searchChallenges(
                ChallengeStatus.PUBLISHED,
                difficulty,
                category,
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
                c.getXpReward(),
                c.getEstimatedMinutes(),
                c.getTags(),
                progressMap.getOrDefault(c.getId(), ChallengeProgressStatus.NOT_STARTED)
        )).collect(Collectors.toList());

        return new PageImpl<>(cards, pageable, entityPage.getTotalElements());
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
                progressStatus,
                completedAt
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

        return new ChallengeProgressDto(challenge.getId(), userId, progress.getStatus(), progress.getCompletedAt());
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

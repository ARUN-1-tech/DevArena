package com.devarena.ai.service;

import com.devarena.ai.dto.PersonalizedRecommendationDto;
import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.model.ChallengeStatus;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.skill.model.PlayerSkillEntity;
import com.devarena.skill.repository.PlayerSkillRepository;
import com.devarena.submission.model.SubmissionStatus;
import com.devarena.submission.repository.SubmissionRepository;
import com.devarena.user.model.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final ChallengeRepository challengeRepository;
    private final PlayerSkillRepository playerSkillRepository;
    private final SubmissionRepository submissionRepository;
    private final com.devarena.user.repository.UserRepository userRepository;

    public RecommendationService(
            ChallengeRepository challengeRepository,
            PlayerSkillRepository playerSkillRepository,
            SubmissionRepository submissionRepository,
            com.devarena.user.repository.UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.playerSkillRepository = playerSkillRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PersonalizedRecommendationDto getRecommendations(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new com.devarena.common.exception.ResourceNotFoundException("User not found: " + userId));
        return getRecommendations(user);
    }

    @Transactional(readOnly = true)
    public PersonalizedRecommendationDto getRecommendations(UserEntity user) {
        List<PlayerSkillEntity> skills = playerSkillRepository.findByUserId(user.getId());
        List<ChallengeEntity> allChallenges = challengeRepository.findAll().stream()
                .filter(c -> c.getStatus() == ChallengeStatus.PUBLISHED)
                .collect(Collectors.toList());

        // Find weak skill or lowest mastery
        String weakSkill = "Algorithms & Data Structures";
        if (!skills.isEmpty()) {
            // Sort ascending by mastery percentage
            skills.sort(Comparator.comparingInt(PlayerSkillEntity::getMasteryPercentage));
            weakSkill = skills.get(0).getSkill().getName();
        }

        // Filter out challenges already successfully solved by user
        List<ChallengeEntity> unsolved = allChallenges.stream()
                .filter(c -> submissionRepository.countByUserIdAndChallengeIdAndStatus(user.getId(), c.getId(), SubmissionStatus.PASSED) == 0)
                .collect(Collectors.toList());

        if (unsolved.isEmpty()) {
            unsolved = allChallenges; // if solved all, recommend from all
        }

        // Pick up to 3 challenges
        List<PersonalizedRecommendationDto.RecommendedChallengeDto> recommendedItems = unsolved.stream()
                .limit(3)
                .map(c -> PersonalizedRecommendationDto.RecommendedChallengeDto.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .slug(c.getSlug())
                        .difficulty(c.getDifficulty() != null ? c.getDifficulty().name() : "MEDIUM")
                        .category(c.getCategory() != null ? c.getCategory().name() : "ALGORITHMS")
                        .xpReward(c.getXpReward())
                        .build())
                .collect(Collectors.toList());

        String reason = String.format(
                "Focusing on %s will yield the highest MMR and XP growth based on your recent performance profile.",
                weakSkill
        );

        return PersonalizedRecommendationDto.builder()
                .weakSkill(weakSkill)
                .reason(reason)
                .recommendedChallenges(recommendedItems)
                .build();
    }
}

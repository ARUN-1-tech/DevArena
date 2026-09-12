package com.devarena.skill.service;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.skill.dto.PlayerSkillDto;
import com.devarena.skill.dto.SkillDto;
import com.devarena.skill.model.PlayerSkillEntity;
import com.devarena.skill.model.SkillEntity;
import com.devarena.skill.repository.PlayerSkillRepository;
import com.devarena.skill.repository.SkillRepository;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillProgressionService {

    private static final Logger log = LoggerFactory.getLogger(SkillProgressionService.class);

    private final SkillRepository skillRepository;
    private final PlayerSkillRepository playerSkillRepository;
    private final UserRepository userRepository;

    public SkillProgressionService(
            SkillRepository skillRepository,
            PlayerSkillRepository playerSkillRepository,
            UserRepository userRepository
    ) {
        this.skillRepository = skillRepository;
        this.playerSkillRepository = playerSkillRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getAllSkills() {
        return skillRepository.findAllByOrderByCategoryAscOrderIndexAsc().stream()
                .map(SkillDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlayerSkillDto> getPlayerSkills(UUID userId) {
        List<SkillEntity> allSkills = skillRepository.findAllByOrderByCategoryAscOrderIndexAsc();
        Map<UUID, PlayerSkillEntity> playerSkillMap = playerSkillRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(ps -> ps.getSkill().getId(), ps -> ps, (a, b) -> a));

        List<PlayerSkillDto> result = new ArrayList<>();

        for (SkillEntity skill : allSkills) {
            PlayerSkillEntity ps = playerSkillMap.get(skill.getId());

            // Check if unlocked (either already initialized or has no prerequisite or prerequisite is unlocked)
            boolean unlocked = true;
            if (skill.getPrerequisite() != null) {
                PlayerSkillEntity prereqPs = playerSkillMap.get(skill.getPrerequisite().getId());
                unlocked = (prereqPs != null && prereqPs.getCurrentLevel() >= 1);
            }

            int currentLevel = ps != null ? ps.getCurrentLevel() : (unlocked ? 1 : 0);
            int currentXp = ps != null ? ps.getCurrentXp() : 0;
            int xpToNext = calculateXpToNextLevel(currentLevel);
            int mastery = ps != null ? ps.getMasteryPercentage() : (unlocked ? 20 : 0);

            result.add(new PlayerSkillDto(
                    ps != null ? ps.getId() : null,
                    skill.getId(),
                    skill.getCode(),
                    skill.getName(),
                    skill.getCategory(),
                    skill.getDescription(),
                    skill.getIcon(),
                    currentLevel,
                    skill.getMaxLevel(),
                    currentXp,
                    xpToNext,
                    mastery,
                    unlocked,
                    skill.getPrerequisite() != null ? skill.getPrerequisite().getId() : null,
                    skill.getPrerequisite() != null ? skill.getPrerequisite().getName() : null
            ));
        }

        return result;
    }

    @Transactional
    public void awardSkillXp(UUID userId, ChallengeCategory challengeCategory, int xpReward) {
        if (xpReward <= 0) return;

        String skillCode = mapCategoryToSkillCode(challengeCategory);
        awardSkillXpByCode(userId, skillCode, xpReward);
    }

    @Transactional
    public void awardSkillXpByCode(UUID userId, String skillCode, int xpReward) {
        if (xpReward <= 0) return;

        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        Optional<SkillEntity> skillOpt = skillRepository.findByCode(skillCode);
        if (skillOpt.isEmpty()) {
            log.warn("Skill with code {} not found. Skipping skill XP award.", skillCode);
            return;
        }

        SkillEntity skill = skillOpt.get();
        PlayerSkillEntity playerSkill = playerSkillRepository.findByUserIdAndSkillId(userId, skill.getId())
                .orElseGet(() -> {
                    PlayerSkillEntity ps = new PlayerSkillEntity(user, skill);
                    return ps;
                });

        int newXp = playerSkill.getCurrentXp() + xpReward;
        playerSkill.setCurrentXp(newXp);

        // Calculate level from XP
        int newLevel = calculateLevelFromXp(newXp, skill.getMaxLevel());
        playerSkill.setCurrentLevel(newLevel);

        // Mastery: level / maxLevel * 100
        int mastery = Math.min(100, Math.max(20, (int) Math.round(((double) newLevel / skill.getMaxLevel()) * 100.0)));
        playerSkill.setMasteryPercentage(mastery);
        playerSkill.setUpdatedAt(Instant.now());

        playerSkillRepository.save(playerSkill);
        log.info("Player {} awarded {} XP for skill {}. New level: {}, mastery: {}%",
                userId, xpReward, skill.getCode(), newLevel, mastery);
    }

    public static int calculateLevelFromXp(int xp, int maxLevel) {
        if (xp >= 1000) return Math.min(maxLevel, 5);
        if (xp >= 500) return Math.min(maxLevel, 4);
        if (xp >= 250) return Math.min(maxLevel, 3);
        if (xp >= 100) return Math.min(maxLevel, 2);
        return 1;
    }

    public static int calculateXpToNextLevel(int currentLevel) {
        return switch (currentLevel) {
            case 1 -> 100;
            case 2 -> 250;
            case 3 -> 500;
            case 4 -> 1000;
            default -> 1000;
        };
    }

    public static String mapCategoryToSkillCode(ChallengeCategory category) {
        if (category == null) return "arrays";
        return switch (category) {
            case ARRAYS, STRINGS, BIT_MANIPULATION -> "arrays";
            case LINKED_LIST, STACK_QUEUE -> "linked-lists";
            case TREES -> "trees";
            case GRAPHS -> "graphs";
            case DYNAMIC_PROGRAMMING, GREEDY -> "dynamic-programming";
            case DATABASE, SQL, DBMS -> "sql-fundamentals";
            case BINARY_SEARCH, BACKTRACKING, HEAPS_PRIORITY_QUEUES, ALGORITHMS, DEBUGGING -> "searching";
            default -> "arrays";
        };
    }
}

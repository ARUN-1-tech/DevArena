package com.devarena.progression.service;

import com.devarena.common.exception.BadRequestException;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.progression.dto.XpRewardResult;
import com.devarena.progression.model.ActivityType;
import com.devarena.progression.model.PlayerActivityEntity;
import com.devarena.progression.repository.PlayerActivityRepository;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerProgressionRepository;
import com.devarena.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class XPService {

    private final UserRepository userRepository;
    private final PlayerProgressionRepository progressionRepository;
    private final PlayerActivityRepository activityRepository;
    private final ProgressionService progressionService;

    public XPService(
            UserRepository userRepository,
            PlayerProgressionRepository progressionRepository,
            PlayerActivityRepository activityRepository,
            ProgressionService progressionService) {
        this.userRepository = userRepository;
        this.progressionRepository = progressionRepository;
        this.activityRepository = activityRepository;
        this.progressionService = progressionService;
    }

    @Transactional
    public XpRewardResult awardXP(UUID userId, int amount, ActivityType activityType, String title, String description) {
        if (amount <= 0) {
            throw new BadRequestException("XP reward amount must be strictly positive.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        PlayerProgressionEntity progression = user.getProgression();
        if (progression == null) {
            progression = progressionRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        PlayerProgressionEntity p = new PlayerProgressionEntity(user);
                        user.setProgression(p);
                        return progressionRepository.save(p);
                    });
        }

        int prevXp = progression.getCurrentXp();
        int prevLevel = progression.getLevel();

        // Update total XP and streak
        progression.setTotalXp(progression.getTotalXp() + amount);
        progressionService.updateStreak(progression);

        // Level-up calculation loop
        int xp = prevXp + amount;
        int level = prevLevel;
        int reqXp = progression.getXpToNextLevel();
        if (reqXp <= 0) {
            reqXp = progressionService.calculateXpForNextLevel(level);
        }

        while (xp >= reqXp) {
            xp -= reqXp;
            level++;
            reqXp = progressionService.calculateXpForNextLevel(level);
        }

        boolean leveledUp = (level > prevLevel);
        progression.setLevel(level);
        progression.setCurrentXp(xp);
        progression.setXpToNextLevel(reqXp);

        progressionRepository.save(progression);

        // Persist activity log
        PlayerActivityEntity activity = new PlayerActivityEntity(user, activityType, title, description, amount);
        activityRepository.save(activity);

        if (leveledUp) {
            PlayerActivityEntity levelActivity = new PlayerActivityEntity(
                    user,
                    ActivityType.LEVEL_UP,
                    "Reached Level " + level + "!",
                    "Advanced from Level " + prevLevel + " to Level " + level,
                    0
            );
            activityRepository.save(levelActivity);
        }

        return new XpRewardResult(
                prevXp,
                xp,
                prevLevel,
                level,
                progression.getTotalXp(),
                amount,
                reqXp,
                leveledUp
        );
    }
}

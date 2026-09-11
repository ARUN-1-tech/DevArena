package com.devarena.progression.service;

import com.devarena.user.model.PlayerProgressionEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class ProgressionService {

    public static final int BASE_XP = 100;

    /**
     * Calculates required XP to advance from current level to next level.
     * Level 1 -> 100 XP
     * Level 2 -> 200 XP
     * Level 3 -> 300 XP
     * Formula: level * 100
     */
    public int calculateXpForNextLevel(int level) {
        if (level < 1) level = 1;
        return level * BASE_XP;
    }

    /**
     * Updates player daily activity streak based on UTC calendar day.
     * Same day: no change
     * Consecutive day: +1 streak
     * Missed day: resets to 1
     */
    public void updateStreak(PlayerProgressionEntity progression) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate lastActivity = progression.getLastActivityDate();

        if (lastActivity == null) {
            progression.setCurrentStreak(1);
            progression.setLongestStreak(Math.max(progression.getLongestStreak(), 1));
            progression.setLastActivityDate(today);
            return;
        }

        if (lastActivity.equals(today)) {
            // Already active today
            return;
        }

        if (lastActivity.equals(today.minusDays(1))) {
            // Consecutive active day
            int newStreak = progression.getCurrentStreak() + 1;
            progression.setCurrentStreak(newStreak);
            progression.setLongestStreak(Math.max(progression.getLongestStreak(), newStreak));
        } else {
            // Streak broken
            progression.setCurrentStreak(1);
        }

        progression.setLastActivityDate(today);
    }
}

package com.devarena;

import com.devarena.progression.service.ProgressionService;
import com.devarena.user.model.PlayerProgressionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class ProgressionServiceTest {

    private ProgressionService progressionService;

    @BeforeEach
    void setUp() {
        progressionService = new ProgressionService();
    }

    @Test
    @DisplayName("Level XP formula scales linearly: level * 100")
    void testXpCalculation() {
        assertEquals(100, progressionService.calculateXpForNextLevel(1));
        assertEquals(200, progressionService.calculateXpForNextLevel(2));
        assertEquals(300, progressionService.calculateXpForNextLevel(3));
        assertEquals(1000, progressionService.calculateXpForNextLevel(10));
    }

    @Test
    @DisplayName("Streak increases on consecutive day and preserves longest streak")
    void testStreakUpdateConsecutiveDay() {
        PlayerProgressionEntity progression = new PlayerProgressionEntity();
        LocalDate yesterday = LocalDate.now(ZoneOffset.UTC).minusDays(1);
        progression.setCurrentStreak(2);
        progression.setLongestStreak(5);
        progression.setLastActivityDate(yesterday);

        progressionService.updateStreak(progression);

        assertEquals(3, progression.getCurrentStreak());
        assertEquals(5, progression.getLongestStreak());
        assertEquals(LocalDate.now(ZoneOffset.UTC), progression.getLastActivityDate());
    }

    @Test
    @DisplayName("Streak does not increase on the same active day")
    void testStreakUpdateSameDay() {
        PlayerProgressionEntity progression = new PlayerProgressionEntity();
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        progression.setCurrentStreak(3);
        progression.setLongestStreak(3);
        progression.setLastActivityDate(today);

        progressionService.updateStreak(progression);

        assertEquals(3, progression.getCurrentStreak());
        assertEquals(3, progression.getLongestStreak());
    }

    @Test
    @DisplayName("Streak resets to 1 when a day is missed")
    void testStreakResetAfterMissedDay() {
        PlayerProgressionEntity progression = new PlayerProgressionEntity();
        LocalDate threeDaysAgo = LocalDate.now(ZoneOffset.UTC).minusDays(3);
        progression.setCurrentStreak(10);
        progression.setLongestStreak(10);
        progression.setLastActivityDate(threeDaysAgo);

        progressionService.updateStreak(progression);

        assertEquals(1, progression.getCurrentStreak());
        assertEquals(10, progression.getLongestStreak());
    }
}
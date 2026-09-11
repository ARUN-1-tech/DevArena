package com.devarena;

import com.devarena.analytics.dto.PlayerAnalyticsResponse;
import com.devarena.analytics.service.AnalyticsService;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.PlayerStatsEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerProgressionRepository;
import com.devarena.user.repository.PlayerStatsRepository;
import com.devarena.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AnalyticsServiceTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerProgressionRepository progressionRepository;

    @Autowired
    private PlayerStatsRepository statsRepository;

    @Test
    @DisplayName("Should return valid analytics for newly registered player with zero activity")
    void testZeroActivityPlayerAnalytics() {
        UserEntity user = new UserEntity("newbie", "newbie@devarena.io", "pass");
        userRepository.save(user);

        PlayerStatsEntity stats = new PlayerStatsEntity(user);
        statsRepository.save(stats);

        PlayerProgressionEntity prog = new PlayerProgressionEntity(user);
        progressionRepository.save(prog);

        PlayerAnalyticsResponse res = analyticsService.getPlayerAnalytics(user.getId());

        assertThat(res).isNotNull();
        assertThat(res.currentLevel()).isEqualTo(1);
        assertThat(res.totalXp()).isEqualTo(0);
        assertThat(res.currentMmr()).isEqualTo(1000);
        assertThat(res.battleWinRate()).isEqualTo(0.0);
        assertThat(res.challengeSolveRate()).isEqualTo(0.0);
        assertThat(res.difficultyDistribution()).containsKeys("EASY", "MEDIUM", "HARD");
        assertThat(res.dailyXpTrend()).hasSize(14);
    }
}

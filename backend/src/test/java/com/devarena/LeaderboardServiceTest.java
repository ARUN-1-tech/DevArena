package com.devarena;

import com.devarena.leaderboard.dto.LeaderboardResponse;
import com.devarena.leaderboard.service.LeaderboardService;
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
class LeaderboardServiceTest {

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerStatsRepository statsRepository;

    @Autowired
    private PlayerProgressionRepository progressionRepository;

    @Test
    @DisplayName("Should return global leaderboard ordered by MMR desc with tie-breaking")
    void testGlobalLeaderboardOrder() {
        // Setup two users with different MMR
        UserEntity highMmr = new UserEntity("pro_coder", "pro@devarena.io", "pass");
        userRepository.save(highMmr);
        PlayerStatsEntity statsHigh = new PlayerStatsEntity(highMmr);
        statsHigh.setRating(1500);
        statsRepository.save(statsHigh);

        UserEntity lowMmr = new UserEntity("junior_dev", "junior@devarena.io", "pass");
        userRepository.save(lowMmr);
        PlayerStatsEntity statsLow = new PlayerStatsEntity(lowMmr);
        statsLow.setRating(1100);
        statsRepository.save(statsLow);

        LeaderboardResponse res = leaderboardService.getLeaderboard("global", 0, 10, highMmr.getId());

        assertThat(res).isNotNull();
        assertThat(res.rankings()).isNotEmpty();
        assertThat(res.rankings().get(0).rating()).isGreaterThanOrEqualTo(res.rankings().get(1).rating());
        assertThat(res.myRank()).isNotNull();
        assertThat(res.myRank().userId()).isEqualTo(highMmr.getId());
    }

    @Test
    @DisplayName("Should correctly handle pagination and page limits")
    void testLeaderboardPagination() {
        LeaderboardResponse res = leaderboardService.getLeaderboard("global", 0, 2, null);
        assertThat(res.size()).isEqualTo(2);
        assertThat(res.rankings().size()).isLessThanOrEqualTo(2);
    }
}

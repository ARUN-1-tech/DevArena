package com.devarena;

import com.devarena.achievement.dto.AchievementDto;
import com.devarena.achievement.dto.PlayerAchievementDto;
import com.devarena.achievement.service.AchievementService;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerProgressionRepository;
import com.devarena.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AchievementServiceTest {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerProgressionRepository progressionRepository;

    @Test
    @DisplayName("Should automatically unlock FIRST_BLOOD when player solves first challenge")
    void testUnlockFirstBlood() {
        UserEntity user = new UserEntity("achiever", "achiever@devarena.io", "pass");
        userRepository.save(user);

        PlayerProgressionEntity prog = new PlayerProgressionEntity(user);
        prog.setChallengesSolved(1);
        prog.setTotalXp(100);
        progressionRepository.save(prog);

        List<AchievementDto> unlocked = achievementService.evaluateAndUnlock(user.getId());

        assertThat(unlocked).isNotEmpty();
        assertThat(unlocked.stream().anyMatch(a -> "FIRST_BLOOD".equals(a.code()))).isTrue();

        // Second evaluation should not duplicate unlock
        List<AchievementDto> secondPass = achievementService.evaluateAndUnlock(user.getId());
        assertThat(secondPass.stream().anyMatch(a -> "FIRST_BLOOD".equals(a.code()))).isFalse();

        // Check player achievement list
        List<PlayerAchievementDto> playerAchievements = achievementService.getPlayerAchievements(user.getId());
        assertThat(playerAchievements).isNotEmpty();
        PlayerAchievementDto firstBlood = playerAchievements.stream()
                .filter(a -> "FIRST_BLOOD".equals(a.code()))
                .findFirst()
                .orElse(null);

        assertThat(firstBlood).isNotNull();
        assertThat(firstBlood.unlocked()).isTrue();
        assertThat(firstBlood.progressPercentage()).isEqualTo(100);
    }
}

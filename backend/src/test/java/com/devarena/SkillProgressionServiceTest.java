package com.devarena;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.skill.dto.PlayerSkillDto;
import com.devarena.skill.service.SkillProgressionService;
import com.devarena.user.model.UserEntity;
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
class SkillProgressionServiceTest {

    @Autowired
    private SkillProgressionService skillProgressionService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should correctly calculate skill level and XP boundaries")
    void testLevelCalculation() {
        assertThat(SkillProgressionService.calculateLevelFromXp(0, 5)).isEqualTo(1);
        assertThat(SkillProgressionService.calculateLevelFromXp(120, 5)).isEqualTo(2);
        assertThat(SkillProgressionService.calculateLevelFromXp(280, 5)).isEqualTo(3);
        assertThat(SkillProgressionService.calculateLevelFromXp(600, 5)).isEqualTo(4);
        assertThat(SkillProgressionService.calculateLevelFromXp(1200, 5)).isEqualTo(5);
    }

    @Test
    @DisplayName("Should map challenge categories to corresponding skill codes")
    void testCategoryMapping() {
        assertThat(SkillProgressionService.mapCategoryToSkillCode(ChallengeCategory.ARRAYS)).isEqualTo("arrays");
        assertThat(SkillProgressionService.mapCategoryToSkillCode(ChallengeCategory.DYNAMIC_PROGRAMMING)).isEqualTo("dynamic-programming");
        assertThat(SkillProgressionService.mapCategoryToSkillCode(ChallengeCategory.ALGORITHMS)).isEqualTo("searching");
        assertThat(SkillProgressionService.mapCategoryToSkillCode(ChallengeCategory.TREES)).isEqualTo("trees");
    }

    @Test
    @DisplayName("Should award skill XP and level up player skill")
    void testAwardSkillXp() {
        UserEntity user = new UserEntity("skill_user", "skill@devarena.io", "pass");
        userRepository.save(user);

        // Award 300 XP in Arrays
        skillProgressionService.awardSkillXp(user.getId(), ChallengeCategory.ARRAYS, 300);

        List<PlayerSkillDto> skills = skillProgressionService.getPlayerSkills(user.getId());
        assertThat(skills).isNotEmpty();

        PlayerSkillDto arraySkill = skills.stream()
                .filter(s -> "arrays".equals(s.code()))
                .findFirst()
                .orElse(null);

        assertThat(arraySkill).isNotNull();
        assertThat(arraySkill.currentXp()).isEqualTo(300);
        assertThat(arraySkill.currentLevel()).isEqualTo(3);
        assertThat(arraySkill.masteryPercentage()).isGreaterThanOrEqualTo(60);
    }
}

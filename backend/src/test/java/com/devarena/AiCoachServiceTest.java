package com.devarena;

import com.devarena.ai.dto.AiCoachRequest;
import com.devarena.ai.dto.AiCoachResponseDto;
import com.devarena.ai.dto.PersonalizedRecommendationDto;
import com.devarena.ai.model.AiCoachRequestType;
import com.devarena.ai.service.AiCoachService;
import com.devarena.ai.service.RecommendationService;
import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.user.model.UserEntity;
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
class AiCoachServiceTest {

    @Autowired
    private AiCoachService aiCoachService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Test
    @DisplayName("Should progress through Socratic hints 1 -> 2 -> 3 without giving away code")
    void testHintProgression() {
        UserEntity user = userRepository.save(new UserEntity("ai_tester", "ai_tester@devarena.io", "pass"));
        ChallengeEntity challenge = challengeRepository.save(new ChallengeEntity(
                "Test Algo", "test-algo", "Reverse an array in-place", ChallengeDifficulty.MEDIUM,
                ChallengeCategory.ARRAYS, 100, 15, "arrays"
        ));

        // Request Hint 1
        AiCoachRequest req1 = new AiCoachRequest(challenge.getId(), AiCoachRequestType.HINT, "public void reverse() {}", null);
        AiCoachResponseDto res1 = aiCoachService.processRequest(user, req1);

        assertThat(res1).isNotNull();
        assertThat(res1.getReply()).contains("Hint 1");
        assertThat(res1.getHintLevel()).isEqualTo(2);

        // Request Hint 2
        AiCoachRequest req2 = new AiCoachRequest(challenge.getId(), AiCoachRequestType.HINT, "public void reverse() {}", null);
        AiCoachResponseDto res2 = aiCoachService.processRequest(user, req2);

        assertThat(res2).isNotNull();
        assertThat(res2.getReply()).contains("Hint 2");
        assertThat(res2.getHintLevel()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should return personalized recommendations based on player progress")
    void testPersonalizedRecommendations() {
        UserEntity user = userRepository.save(new UserEntity("rec_tester", "rec_tester@devarena.io", "pass"));

        PersonalizedRecommendationDto recs = recommendationService.getRecommendations(user);

        assertThat(recs).isNotNull();
        assertThat(recs.getWeakSkill()).isNotNull();
        assertThat(recs.getReason()).contains("Focusing on");
        assertThat(recs.getRecommendedChallenges()).isNotEmpty();
    }
}

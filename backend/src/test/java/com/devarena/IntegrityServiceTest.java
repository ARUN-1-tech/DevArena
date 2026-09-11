package com.devarena;

import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.integrity.dto.IntegrityEventDto;
import com.devarena.integrity.model.IntegrityEventType;
import com.devarena.integrity.service.IntegrityService;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IntegrityServiceTest {

    @Autowired
    private IntegrityService integrityService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should detect impossible solve timing (< 5s on Medium/Hard) and record high severity event")
    void testImpossibleTimingDetection() {
        UserEntity cheater = userRepository.save(new UserEntity("speedy_bot", "bot@devarena.io", "pass"));

        integrityService.validateSubmissionTiming(cheater, UUID.randomUUID(), ChallengeDifficulty.HARD, 2500);

        int riskScore = integrityService.getUserRiskScore(cheater.getId());
        assertThat(riskScore).isGreaterThanOrEqualTo(40);

        Page<IntegrityEventDto> unreviewed = integrityService.getUnreviewedEvents(PageRequest.of(0, 10));
        assertThat(unreviewed.getContent().stream().anyMatch(e -> e.getEventType() == IntegrityEventType.IMPOSSIBLE_TIMING)).isTrue();
    }

    @Test
    @DisplayName("Should flag rapid execution spam")
    void testRapidExecutionSpam() {
        UserEntity spammer = userRepository.save(new UserEntity("spammer_dev", "spammer@devarena.io", "pass"));

        integrityService.validateRapidRunSpam(spammer, 30);

        int riskScore = integrityService.getUserRiskScore(spammer.getId());
        assertThat(riskScore).isGreaterThanOrEqualTo(20);
    }
}

package com.devarena;

import com.devarena.battle.dto.BattleDetailResponse;
import com.devarena.battle.dto.BattleResultResponse;
import com.devarena.battle.dto.BattleSubmitRequest;
import com.devarena.battle.dto.BattleSubmitResponse;
import com.devarena.battle.model.BattleEntity;
import com.devarena.battle.model.BattleStatus;
import com.devarena.battle.repository.BattleRepository;
import com.devarena.battle.service.BattleService;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.matchmaking.dto.MatchmakingStatusResponse;
import com.devarena.matchmaking.service.MatchmakingService;
import com.devarena.submission.model.SubmissionStatus;
import com.devarena.user.model.PlayerStatsEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerStatsRepository;
import com.devarena.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BattleIntegrationTest {

    @Autowired
    private MatchmakingService matchmakingService;

    @Autowired
    private BattleService battleService;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerStatsRepository playerStatsRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private com.devarena.challenge.repository.ChallengeStarterCodeRepository starterCodeRepository;

    private UserEntity userA;
    private UserEntity userB;
    private ChallengeEntity challenge;

    @BeforeEach
    void setUp() {
        userA = userRepository.findByUsernameIgnoreCase("Arun")
                .orElseGet(() -> userRepository.save(new UserEntity("Arun", "arun@devarena.io", "hashedPass")));
        userB = userRepository.findByUsernameIgnoreCase("Bob")
                .orElseGet(() -> userRepository.save(new UserEntity("Bob", "bob@devarena.io", "hashedPass")));

        if (userA.getStats() == null) {
            PlayerStatsEntity sA = new PlayerStatsEntity(userA);
            userA.setStats(playerStatsRepository.save(sA));
        }
        if (userB.getStats() == null) {
            PlayerStatsEntity sB = new PlayerStatsEntity(userB);
            userB.setStats(playerStatsRepository.save(sB));
        }

        challenge = challengeRepository.findAll().stream().findFirst().orElseThrow();
    }

    @Test
    @DisplayName("Matchmaking: Queue join, leave, and pair two players")
    void testMatchmakingPairing() {
        // Player A joins
        MatchmakingStatusResponse statusA = matchmakingService.joinQueue(userA.getId());
        assertThat(statusA.inQueue()).isTrue();

        // Player B joins
        MatchmakingStatusResponse statusB = matchmakingService.joinQueue(userB.getId());

        // Check if matched
        MatchmakingStatusResponse checkA = matchmakingService.getQueueStatus(userA.getId());
        MatchmakingStatusResponse checkB = matchmakingService.getQueueStatus(userB.getId());

        assertThat(checkA.matchedBattleId()).isNotNull();
        assertThat(checkB.matchedBattleId()).isNotNull();
        assertThat(checkA.matchedBattleId()).isEqualTo(checkB.matchedBattleId());
    }

    @Test
    @DisplayName("Battle lifecycle: Ready check, Submit code, Winner determination, Elo & XP awards")
    void testBattleLifecycle() {
        BattleEntity battle = new BattleEntity(userA, userB, challenge, 900);
        battle = battleRepository.save(battle);
        UUID battleId = battle.getId();

        // 1. Get battle detail
        BattleDetailResponse detail = battleService.getBattle(battleId, userA.getId());
        assertThat(detail.status()).isEqualTo(BattleStatus.WAITING);
        assertThat(detail.challenge().title()).isEqualTo(challenge.getTitle());

        // 2. Both players ready -> starts battle
        battleService.markPlayerReady(battleId, userA.getId());
        battleService.markPlayerReady(battleId, userB.getId());

        BattleEntity started = battleRepository.findById(battleId).orElseThrow();
        assertThat(started.getStatus()).isEqualTo(BattleStatus.IN_PROGRESS);
        assertThat(started.getStartedAt()).isNotNull();

        // 3. User A submits correct solution
        String solutionCode = starterCodeRepository.findByChallengeIdAndLanguage(challenge.getId(), com.devarena.execution.model.ExecutionLanguage.JAVA)
                .map(com.devarena.challenge.model.ChallengeStarterCodeEntity::getStarterCode)
                .orElse("""
                    import java.util.*;
                    public class Solution {
                        public static void main(String[] args) {
                            Scanner sc = new Scanner(System.in);
                            if (sc.hasNextLine()) System.out.println(sc.nextLine().trim());
                        }
                    }
                """);

        BattleSubmitResponse subRes = battleService.submitCode(
                battleId,
                userA.getId(),
                new BattleSubmitRequest("JAVA", solutionCode)
        );

        assertThat(subRes.battleFinished()).isTrue();
        assertThat(subRes.winnerId()).isEqualTo(userA.getId());

        // 4. Verify result endpoint
        BattleResultResponse resultA = battleService.getBattleResult(battleId, userA.getId());
        assertThat(resultA.outcome()).isEqualTo("WIN");
        assertThat(resultA.ratingDelta()).isGreaterThan(0);
        assertThat(resultA.xpEarned()).isEqualTo(200);

        BattleResultResponse resultB = battleService.getBattleResult(battleId, userB.getId());
        assertThat(resultB.outcome()).isEqualTo("LOSS");
        assertThat(resultB.ratingDelta()).isLessThan(0);
    }

    @Test
    @DisplayName("Forfeit battle grants victory to opponent")
    void testForfeitBattle() {
        BattleEntity battle = new BattleEntity(userA, userB, challenge, 900);
        battle.setStatus(BattleStatus.IN_PROGRESS);
        battle = battleRepository.save(battle);

        battleService.forfeitBattle(battle.getId(), userA.getId());

        BattleEntity finished = battleRepository.findById(battle.getId()).orElseThrow();
        assertThat(finished.getStatus()).isEqualTo(BattleStatus.COMPLETED);
        assertThat(finished.getWinner().getId()).isEqualTo(userB.getId());
        assertThat(finished.getFinishReason()).isEqualTo("FORFEIT");
    }
}

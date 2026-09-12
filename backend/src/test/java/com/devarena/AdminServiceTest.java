package com.devarena;

import com.devarena.admin.dto.*;
import com.devarena.admin.service.AdminAuditService;
import com.devarena.admin.service.AdminChallengeService;
import com.devarena.admin.service.AdminDashboardService;
import com.devarena.admin.service.AdminPlayerService;
import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeStatus;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminServiceTest {

    @Autowired
    private AdminDashboardService dashboardService;

    @Autowired
    private AdminPlayerService playerService;

    @Autowired
    private AdminChallengeService challengeService;

    @Autowired
    private AdminAuditService auditService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should retrieve platform KPI overview and audit logs correctly")
    void testAdminOverviewAndAudit() {
        AdminOverviewDto overview = dashboardService.getOverview();
        assertThat(overview).isNotNull();
        assertThat(overview.getTotalUsers()).isGreaterThanOrEqualTo(0);

        Page<AdminAuditDto> logs = auditService.getAuditLogs(PageRequest.of(0, 10));
        assertThat(logs).isNotNull();
    }

    @Test
    @DisplayName("Should suspend player and record audit trail")
    void testSuspendAndRestorePlayer() {
        UserEntity admin = userRepository.save(new UserEntity("admin_test", "adm@devarena.io", "pass"));
        UserEntity offender = userRepository.save(new UserEntity("offender_test", "off@devarena.io", "pass"));

        AdminPlayerDto suspended = playerService.suspendPlayer(offender.getId(), admin, new SuspendPlayerRequest("Violating TOS"));
        assertThat(suspended.isAccountNonLocked()).isFalse();

        AdminPlayerDto restored = playerService.restorePlayer(offender.getId(), admin);
        assertThat(restored.isAccountNonLocked()).isTrue();
    }

    @Test
    @DisplayName("Should create challenge and manage lifecycle")
    void testChallengeLifecycle() {
        UserEntity admin = userRepository.save(new UserEntity("admin_author", "author@devarena.io", "pass"));

        UpsertChallengeRequest req = new UpsertChallengeRequest(
                "Custom Dynamic Problem",
                "custom-dynamic-problem",
                "Solve this dynamic problem efficiently.",
                ChallengeDifficulty.HARD,
                ChallengeCategory.DYNAMIC_PROGRAMMING,
                200,
                25,
                "dp,math",
                ChallengeStatus.DRAFT,
                List.of(new UpsertChallengeRequest.TestCaseItem("input1", "output1", false, 1, "test case 1"))
        );

        AdminChallengeDto created = challengeService.createChallenge(admin, req);
        assertThat(created).isNotNull();
        assertThat(created.getStatus()).isEqualTo(ChallengeStatus.DRAFT);
        assertThat(created.getTestCaseCount()).isEqualTo(1);

        AdminChallengeDto published = challengeService.updateStatus(created.getId(), admin, ChallengeStatus.PUBLISHED);
        assertThat(published.getStatus()).isEqualTo(ChallengeStatus.PUBLISHED);
    }

    @Test
    @DisplayName("Should bulk import challenges, prevent duplicates, and report results")
    void testBulkImportChallenges() {
        UserEntity admin = userRepository.save(new UserEntity("admin_importer", "importer@devarena.io", "pass"));

        com.devarena.challenge.dto.ChallengeImportItemDto item1 = new com.devarena.challenge.dto.ChallengeImportItemDto(
                "Unique Bulk Problem 1",
                "unique-bulk-problem-1",
                "Description for problem 1",
                ChallengeDifficulty.EASY,
                ChallengeCategory.ARRAYS,
                com.devarena.challenge.model.ProblemType.CODING,
                50,
                15,
                900,
                "array,test",
                null,
                null,
                "Hint 1",
                "Approach 1",
                "Rising Brain Sheet",
                List.of(new com.devarena.challenge.dto.ChallengeImportItemDto.ImportTestCaseDto("1,2", "3", false, 1, "test")),
                null
        );

        com.devarena.challenge.dto.ChallengeImportItemDto item2Duplicate = new com.devarena.challenge.dto.ChallengeImportItemDto(
                "Unique Bulk Problem 1", // Duplicate
                "unique-bulk-problem-1",
                "Duplicate description",
                ChallengeDifficulty.EASY,
                ChallengeCategory.ARRAYS,
                com.devarena.challenge.model.ProblemType.CODING,
                50,
                15,
                900,
                "array,test",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        com.devarena.challenge.dto.ChallengeImportResultDto result = challengeService.bulkImportChallenges(
                admin.getId(), List.of(item1, item2Duplicate)
        );

        assertThat(result).isNotNull();
        assertThat(result.totalProcessed()).isEqualTo(2);
        assertThat(result.createdCount()).isEqualTo(1);
        assertThat(result.skippedCount()).isEqualTo(1);
        assertThat(result.createdTitles()).contains("Unique Bulk Problem 1");
    }
}

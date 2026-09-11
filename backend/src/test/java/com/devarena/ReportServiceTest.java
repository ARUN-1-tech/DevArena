package com.devarena;

import com.devarena.moderation.dto.CreateReportRequest;
import com.devarena.moderation.dto.ReportDto;
import com.devarena.moderation.dto.ResolveReportRequest;
import com.devarena.moderation.model.ReportReason;
import com.devarena.moderation.model.ReportStatus;
import com.devarena.moderation.model.ReportTargetType;
import com.devarena.moderation.service.ReportService;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should file report and resolve it through admin workflow")
    void testCreateAndResolveReport() {
        UserEntity reporter = userRepository.save(new UserEntity("reporter1", "rep1@devarena.io", "pass"));
        UserEntity admin = userRepository.save(new UserEntity("moderator1", "mod1@devarena.io", "pass"));

        CreateReportRequest req = new CreateReportRequest(
                ReportTargetType.PLAYER,
                "bad_actor_99",
                ReportReason.HARASSMENT,
                "Used abusive language in match lobby."
        );

        ReportDto created = reportService.createReport(reporter, req);
        assertThat(created).isNotNull();
        assertThat(created.getStatus()).isEqualTo(ReportStatus.OPEN);
        assertThat(created.getReporterUsername()).isEqualTo("reporter1");

        Page<ReportDto> openReports = reportService.getReports(ReportStatus.OPEN, PageRequest.of(0, 10));
        assertThat(openReports.getContent()).isNotEmpty();

        // Admin resolves report
        ResolveReportRequest resolveReq = new ResolveReportRequest(ReportStatus.RESOLVED, "Player issued warning.");
        ReportDto resolved = reportService.resolveReport(created.getId(), admin, resolveReq);

        assertThat(resolved.getStatus()).isEqualTo(ReportStatus.RESOLVED);
        assertThat(resolved.getResolvedByUsername()).isEqualTo("moderator1");
        assertThat(resolved.getResolutionNotes()).isEqualTo("Player issued warning.");
    }
}

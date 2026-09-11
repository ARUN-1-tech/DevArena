package com.devarena.admin.service;

import com.devarena.admin.dto.AdminOverviewDto;
import com.devarena.ai.repository.AiUsageRecordRepository;
import com.devarena.battle.repository.BattleRepository;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.integrity.service.IntegrityService;
import com.devarena.moderation.service.ReportService;
import com.devarena.submission.repository.SubmissionRepository;
import com.devarena.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final BattleRepository battleRepository;
    private final ReportService reportService;
    private final IntegrityService integrityService;
    private final AiUsageRecordRepository aiUsageRecordRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            ChallengeRepository challengeRepository,
            SubmissionRepository submissionRepository,
            BattleRepository battleRepository,
            ReportService reportService,
            IntegrityService integrityService,
            AiUsageRecordRepository aiUsageRecordRepository) {
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
        this.battleRepository = battleRepository;
        this.reportService = reportService;
        this.integrityService = integrityService;
        this.aiUsageRecordRepository = aiUsageRecordRepository;
    }

    @Transactional(readOnly = true)
    public AdminOverviewDto getOverview() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabledTrue();
        long totalChallenges = challengeRepository.count();
        long totalSubmissions = submissionRepository.count();
        long totalBattles = battleRepository.count();
        long openReports = reportService.countOpenReports();
        long highRiskAlerts = integrityService.countHighRiskAlerts();

        Instant startOfToday = Instant.now().truncatedTo(ChronoUnit.DAYS);
        long totalAiQueriesToday = aiUsageRecordRepository.countSince(startOfToday);

        return AdminOverviewDto.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalChallenges(totalChallenges)
                .totalSubmissions(totalSubmissions)
                .totalBattles(totalBattles)
                .openReports(openReports)
                .highRiskIntegrityAlerts(highRiskAlerts)
                .totalAiQueriesToday(totalAiQueriesToday)
                .build();
    }
}

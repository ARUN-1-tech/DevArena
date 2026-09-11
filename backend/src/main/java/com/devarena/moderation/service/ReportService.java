package com.devarena.moderation.service;

import com.devarena.admin.model.AdminAuditAction;
import com.devarena.admin.service.AdminAuditService;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.moderation.dto.CreateReportRequest;
import com.devarena.moderation.dto.ReportDto;
import com.devarena.moderation.dto.ResolveReportRequest;
import com.devarena.moderation.model.ReportEntity;
import com.devarena.moderation.model.ReportStatus;
import com.devarena.moderation.repository.ReportRepository;
import com.devarena.user.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final AdminAuditService adminAuditService;
    private final com.devarena.user.repository.UserRepository userRepository;

    public ReportService(
            ReportRepository reportRepository,
            AdminAuditService adminAuditService,
            com.devarena.user.repository.UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.adminAuditService = adminAuditService;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReportDto createReport(UUID reporterId, CreateReportRequest request) {
        UserEntity reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reporterId));
        return createReport(reporter, request);
    }

    @Transactional
    public ReportDto createReport(UserEntity reporter, CreateReportRequest request) {
        ReportEntity entity = ReportEntity.builder()
                .reporter(reporter)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .reason(request.getReason())
                .description(request.getDescription())
                .status(ReportStatus.OPEN)
                .build();

        ReportEntity saved = reportRepository.save(entity);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReportDto> getReports(ReportStatus status, Pageable pageable) {
        if (status != null) {
            return reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable).map(this::mapToDto);
        }
        return reportRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional
    public ReportDto resolveReport(UUID reportId, UUID adminId, ResolveReportRequest request) {
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));
        return resolveReport(reportId, admin, request);
    }

    @Transactional
    public ReportDto resolveReport(UUID reportId, UserEntity admin, ResolveReportRequest request) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));

        report.setStatus(request.getStatus());
        report.setResolutionNotes(request.getResolutionNotes());
        report.setResolvedBy(admin);
        report.setResolvedAt(Instant.now());

        ReportEntity updated = reportRepository.save(report);

        AdminAuditAction action = request.getStatus() == ReportStatus.RESOLVED 
                ? AdminAuditAction.REPORT_RESOLVED 
                : AdminAuditAction.REPORT_DISMISSED;

        adminAuditService.logAction(
                admin,
                action,
                "REPORT",
                reportId.toString(),
                "Target: " + report.getTargetType() + " " + report.getTargetId() + ", Notes: " + request.getResolutionNotes()
        );

        return mapToDto(updated);
    }

    public long countOpenReports() {
        return reportRepository.countByStatus(ReportStatus.OPEN);
    }

    private ReportDto mapToDto(ReportEntity entity) {
        return ReportDto.builder()
                .id(entity.getId())
                .reporterId(entity.getReporter() != null ? entity.getReporter().getId() : null)
                .reporterUsername(entity.getReporter() != null ? entity.getReporter().getUsername() : "UNKNOWN")
                .targetType(entity.getTargetType())
                .targetId(entity.getTargetId())
                .reason(entity.getReason())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .resolutionNotes(entity.getResolutionNotes())
                .createdAt(entity.getCreatedAt())
                .resolvedAt(entity.getResolvedAt())
                .resolvedByUsername(entity.getResolvedBy() != null ? entity.getResolvedBy().getUsername() : null)
                .build();
    }
}

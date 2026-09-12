package com.devarena.admin.service;

import com.devarena.admin.dto.AdminChallengeDto;
import com.devarena.admin.dto.ProblemImportDto;
import com.devarena.admin.dto.ProblemImportResultDto;
import com.devarena.admin.dto.UpsertChallengeRequest;
import com.devarena.admin.model.AdminAuditAction;
import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.model.ChallengeStatus;
import com.devarena.challenge.model.ChallengeTestCaseEntity;
import com.devarena.challenge.model.ProblemType;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.challenge.repository.ChallengeTestCaseRepository;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.user.model.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AdminChallengeService {

    private static final Logger log = LoggerFactory.getLogger(AdminChallengeService.class);

    private final ChallengeRepository challengeRepository;
    private final ChallengeTestCaseRepository testCaseRepository;
    private final AdminAuditService adminAuditService;
    private final com.devarena.user.repository.UserRepository userRepository;

    public AdminChallengeService(
            ChallengeRepository challengeRepository,
            ChallengeTestCaseRepository testCaseRepository,
            AdminAuditService adminAuditService,
            com.devarena.user.repository.UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.testCaseRepository = testCaseRepository;
        this.adminAuditService = adminAuditService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<AdminChallengeDto> getAllChallenges(Pageable pageable) {
        return challengeRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional
    public AdminChallengeDto createChallenge(UUID adminId, UpsertChallengeRequest request) {
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));
        return createChallenge(admin, request);
    }

    @Transactional
    public AdminChallengeDto createChallenge(UserEntity admin, UpsertChallengeRequest request) {
        ChallengeEntity challenge = new ChallengeEntity();
        challenge.setTitle(request.getTitle());
        challenge.setSlug(request.getSlug().toLowerCase().replaceAll("[^a-z0-9-]", "-"));
        challenge.setDescription(request.getDescription());
        challenge.setDifficulty(request.getDifficulty());
        challenge.setCategory(request.getCategory());
        challenge.setXpReward(request.getXpReward() > 0 ? request.getXpReward() : 100);
        challenge.setEstimatedMinutes(request.getEstimatedMinutes() > 0 ? request.getEstimatedMinutes() : 15);
        challenge.setTags(request.getTags());
        challenge.setStatus(request.getStatus() != null ? request.getStatus() : ChallengeStatus.DRAFT);

        ChallengeEntity saved = challengeRepository.save(challenge);

        if (request.getTestCases() != null && !request.getTestCases().isEmpty()) {
            for (UpsertChallengeRequest.TestCaseItem item : request.getTestCases()) {
                ChallengeTestCaseEntity tc = new ChallengeTestCaseEntity(
                        saved,
                        item.getInput(),
                        item.getExpectedOutput(),
                        item.isHidden(),
                        item.getOrderIndex(),
                        item.getExplanation()
                );
                testCaseRepository.save(tc);
            }
        }

        adminAuditService.logAction(
                admin,
                AdminAuditAction.CHALLENGE_CREATED,
                "CHALLENGE",
                saved.getId().toString(),
                "Title: " + saved.getTitle() + ", Status: " + saved.getStatus()
        );

        return mapToDto(saved);
    }

    @Transactional
    public AdminChallengeDto updateChallenge(UUID challengeId, UUID adminId, UpsertChallengeRequest request) {
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));
        return updateChallenge(challengeId, admin, request);
    }

    @Transactional
    public AdminChallengeDto updateChallenge(UUID challengeId, UserEntity admin, UpsertChallengeRequest request) {
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + challengeId));

        challenge.setTitle(request.getTitle());
        challenge.setSlug(request.getSlug().toLowerCase().replaceAll("[^a-z0-9-]", "-"));
        challenge.setDescription(request.getDescription());
        challenge.setDifficulty(request.getDifficulty());
        challenge.setCategory(request.getCategory());
        challenge.setXpReward(request.getXpReward());
        challenge.setEstimatedMinutes(request.getEstimatedMinutes());
        challenge.setTags(request.getTags());
        if (request.getStatus() != null) {
            challenge.setStatus(request.getStatus());
        }

        ChallengeEntity saved = challengeRepository.save(challenge);

        if (request.getTestCases() != null) {
            List<ChallengeTestCaseEntity> existing = testCaseRepository.findByChallengeIdOrderByOrderIndexAsc(challengeId);
            testCaseRepository.deleteAll(existing);

            for (UpsertChallengeRequest.TestCaseItem item : request.getTestCases()) {
                ChallengeTestCaseEntity tc = new ChallengeTestCaseEntity(
                        saved,
                        item.getInput(),
                        item.getExpectedOutput(),
                        item.isHidden(),
                        item.getOrderIndex(),
                        item.getExplanation()
                );
                testCaseRepository.save(tc);
            }
        }

        adminAuditService.logAction(
                admin,
                AdminAuditAction.CHALLENGE_UPDATED,
                "CHALLENGE",
                saved.getId().toString(),
                "Updated details for challenge: " + saved.getTitle()
        );

        return mapToDto(saved);
    }

    @Transactional
    public AdminChallengeDto updateStatus(UUID challengeId, UUID adminId, ChallengeStatus status) {
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));
        return updateStatus(challengeId, admin, status);
    }

    @Transactional
    public AdminChallengeDto updateStatus(UUID challengeId, UserEntity admin, ChallengeStatus status) {
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + challengeId));

        challenge.setStatus(status);
        ChallengeEntity saved = challengeRepository.save(challenge);

        AdminAuditAction action = status == ChallengeStatus.PUBLISHED 
                ? AdminAuditAction.CHALLENGE_PUBLISHED 
                : AdminAuditAction.CHALLENGE_ARCHIVED;

        adminAuditService.logAction(
                admin,
                action,
                "CHALLENGE",
                saved.getId().toString(),
                "Status changed to " + status
        );

        return mapToDto(saved);
    }

    private AdminChallengeDto mapToDto(ChallengeEntity entity) {
        long testCaseCount = testCaseRepository.countByChallengeId(entity.getId());
        return AdminChallengeDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .difficulty(entity.getDifficulty())
                .category(entity.getCategory())
                .xpReward(entity.getXpReward())
                .estimatedMinutes(entity.getEstimatedMinutes())
                .tags(entity.getTags())
                .status(entity.getStatus())
                .testCaseCount((int) testCaseCount)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Transactional
    public ProblemImportResultDto importProblems(List<ProblemImportDto> problems, UUID adminId) {
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));

        int imported = 0;
        int skipped = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();

        for (ProblemImportDto dto : problems) {
            try {
                // Validate required fields
                if (dto.getTitle() == null || dto.getTitle().isBlank()) {
                    errors.add("Skipped: missing title");
                    failed++;
                    continue;
                }
                if (dto.getDescription() == null || dto.getDescription().isBlank()) {
                    errors.add("Skipped '" + dto.getTitle() + "': missing description");
                    failed++;
                    continue;
                }
                if (dto.getDifficulty() == null) {
                    errors.add("Skipped '" + dto.getTitle() + "': missing difficulty");
                    failed++;
                    continue;
                }
                if (dto.getCategory() == null) {
                    errors.add("Skipped '" + dto.getTitle() + "': missing category");
                    failed++;
                    continue;
                }

                // Compute slug
                String slug = dto.getSlug() != null && !dto.getSlug().isBlank()
                        ? dto.getSlug().toLowerCase().replaceAll("[^a-z0-9-]", "-")
                        : dto.getTitle().toLowerCase().trim().replaceAll("[^a-z0-9]+", "-");

                // Duplicate check by slug or title
                if (challengeRepository.existsBySlug(slug)) {
                    log.debug("Skipping duplicate slug: {}", slug);
                    skipped++;
                    continue;
                }
                if (challengeRepository.existsByTitle(dto.getTitle())) {
                    log.debug("Skipping duplicate title: {}", dto.getTitle());
                    skipped++;
                    continue;
                }

                // Build entity
                ChallengeEntity challenge = new ChallengeEntity(
                        dto.getTitle(),
                        slug,
                        dto.getDescription(),
                        dto.getDifficulty(),
                        dto.getCategory(),
                        dto.getProblemType() != null ? dto.getProblemType() : ProblemType.CODING,
                        dto.getXpReward() > 0 ? dto.getXpReward() : 100,
                        dto.getEstimatedMinutes() > 0 ? dto.getEstimatedMinutes() : 15,
                        dto.getTags(),
                        dto.getSupportedLanguages() != null ? dto.getSupportedLanguages() : "JAVA,PYTHON,JAVASCRIPT",
                        dto.getSourceReference()
                );
                challenge.setStatus(dto.getStatus() != null ? dto.getStatus() : ChallengeStatus.PUBLISHED);

                ChallengeEntity saved = challengeRepository.save(challenge);

                // Save test cases if provided
                if (dto.getTestCases() != null) {
                    for (ProblemImportDto.TestCaseImportItem tc : dto.getTestCases()) {
                        ChallengeTestCaseEntity testCase = new ChallengeTestCaseEntity(
                                saved,
                                tc.getInput() != null ? tc.getInput() : "",
                                tc.getExpectedOutput() != null ? tc.getExpectedOutput() : "",
                                tc.isHidden(),
                                tc.getOrderIndex(),
                                tc.getExplanation()
                        );
                        testCaseRepository.save(testCase);
                    }
                }

                imported++;
            } catch (Exception ex) {
                log.error("Failed to import problem '{}': {}", dto.getTitle(), ex.getMessage());
                errors.add("Error importing '" + dto.getTitle() + "': " + ex.getMessage());
                failed++;
            }
        }

        if (imported > 0) {
            adminAuditService.logAction(
                    admin,
                    AdminAuditAction.CHALLENGE_CREATED,
                    "PROBLEM_IMPORT",
                    null,
                    "Bulk imported " + imported + " problems. Skipped: " + skipped + ", Failed: " + failed
            );
        }

        log.info("Problem import completed: imported={}, skipped={}, failed={}", imported, skipped, failed);
        return new ProblemImportResultDto(problems.size(), imported, skipped, failed, errors);
    }
}


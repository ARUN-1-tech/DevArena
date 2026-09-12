package com.devarena.admin.service;

import com.devarena.admin.dto.AdminChallengeDto;
import com.devarena.admin.dto.UpsertChallengeRequest;
import com.devarena.admin.model.AdminAuditAction;
import com.devarena.challenge.dto.ChallengeImportItemDto;
import com.devarena.challenge.dto.ChallengeImportResultDto;
import com.devarena.challenge.model.*;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.challenge.repository.ChallengeStarterCodeRepository;
import com.devarena.challenge.repository.ChallengeTestCaseRepository;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.execution.model.ExecutionLanguage;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdminChallengeService {

    private static final Logger log = LoggerFactory.getLogger(AdminChallengeService.class);

    private final ChallengeRepository challengeRepository;
    private final ChallengeTestCaseRepository testCaseRepository;
    private final ChallengeStarterCodeRepository starterCodeRepository;
    private final AdminAuditService adminAuditService;
    private final UserRepository userRepository;

    public AdminChallengeService(
            ChallengeRepository challengeRepository,
            ChallengeTestCaseRepository testCaseRepository,
            ChallengeStarterCodeRepository starterCodeRepository,
            AdminAuditService adminAuditService,
            UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.testCaseRepository = testCaseRepository;
        this.starterCodeRepository = starterCodeRepository;
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
    public ChallengeImportResultDto bulkImportChallenges(UUID adminId, List<ChallengeImportItemDto> items) {
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));

        int total = items != null ? items.size() : 0;
        int created = 0;
        int skipped = 0;
        int errorsCount = 0;
        List<String> createdTitles = new ArrayList<>();
        List<String> skippedTitles = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        if (items == null || items.isEmpty()) {
            return new ChallengeImportResultDto(0, 0, 0, 0, createdTitles, skippedTitles, errorMessages);
        }

        for (int i = 0; i < items.size(); i++) {
            ChallengeImportItemDto item = items.get(i);
            try {
                if (item.title() == null || item.title().isBlank()) {
                    errorsCount++;
                    errorMessages.add("Record #" + (i + 1) + ": Missing title");
                    continue;
                }

                String slug = item.slug();
                if (slug == null || slug.isBlank()) {
                    slug = item.title().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
                } else {
                    slug = slug.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
                }

                // Check duplicate by slug or title
                if (challengeRepository.existsBySlugIgnoreCase(slug) || challengeRepository.existsByTitleIgnoreCase(item.title())) {
                    skipped++;
                    skippedTitles.add(item.title() + " (Already exists)");
                    continue;
                }

                ChallengeDifficulty diff = item.difficulty() != null ? item.difficulty() : ChallengeDifficulty.MEDIUM;
                ChallengeCategory cat = item.category() != null ? item.category() : ChallengeCategory.ALGORITHMS;
                ProblemType pType = item.problemType() != null ? item.problemType() : ProblemType.CODING;
                int xp = (item.xpReward() != null && item.xpReward() > 0) ? item.xpReward() : 100;
                int estMins = (item.estimatedMinutes() != null && item.estimatedMinutes() > 0) ? item.estimatedMinutes() : 20;
                int timeLimit = (item.timeLimitSeconds() != null && item.timeLimitSeconds() > 0) ? item.timeLimitSeconds() : estMins * 60;
                String desc = (item.description() != null && !item.description().isBlank()) ? item.description() : item.title();

                ChallengeEntity challenge = new ChallengeEntity();
                challenge.setTitle(item.title());
                challenge.setSlug(slug);
                challenge.setDescription(desc);
                challenge.setDifficulty(diff);
                challenge.setCategory(cat);
                challenge.setProblemType(pType);
                challenge.setXpReward(xp);
                challenge.setEstimatedMinutes(estMins);
                challenge.setTimeLimitSeconds(timeLimit);
                challenge.setTags(item.tags());
                challenge.setOptions(item.options());
                challenge.setCorrectAnswer(item.correctAnswer());
                challenge.setHints(item.hints());
                challenge.setSolutionApproach(item.solutionApproach());
                challenge.setSource(item.source() != null ? item.source() : "Rising Brain Problem Archive");
                challenge.setStatus(ChallengeStatus.PUBLISHED);

                ChallengeEntity saved = challengeRepository.save(challenge);

                // Save test cases if any
                if (item.testCases() != null && !item.testCases().isEmpty()) {
                    int order = 1;
                    for (ChallengeImportItemDto.ImportTestCaseDto tcDto : item.testCases()) {
                        ChallengeTestCaseEntity tc = new ChallengeTestCaseEntity(
                                saved,
                                tcDto.input() != null ? tcDto.input() : "",
                                tcDto.expectedOutput() != null ? tcDto.expectedOutput() : "",
                                tcDto.hidden() != null ? tcDto.hidden() : false,
                                tcDto.orderIndex() != null ? tcDto.orderIndex() : order++,
                                tcDto.explanation()
                        );
                        testCaseRepository.save(tc);
                    }
                }

                // Save starter templates if any
                if (item.starterTemplates() != null && !item.starterTemplates().isEmpty()) {
                    for (Map.Entry<String, String> entry : item.starterTemplates().entrySet()) {
                        try {
                            ExecutionLanguage lang = ExecutionLanguage.valueOf(entry.getKey().toUpperCase());
                            ChallengeStarterCodeEntity sc = new ChallengeStarterCodeEntity(saved, lang, entry.getValue());
                            starterCodeRepository.save(sc);
                        } catch (Exception ignored) {}
                    }
                }

                created++;
                createdTitles.add(saved.getTitle());
            } catch (Exception ex) {
                errorsCount++;
                errorMessages.add("Record #" + (i + 1) + " ('" + item.title() + "'): " + ex.getMessage());
                log.warn("Failed to import challenge record #{}: {}", i, ex.getMessage());
            }
        }

        adminAuditService.logAction(
                admin,
                AdminAuditAction.CHALLENGE_CREATED,
                "CHALLENGE_BULK_IMPORT",
                "IMPORT",
                "Bulk imported " + created + " challenges, skipped " + skipped + ", errors: " + errorsCount
        );

        return new ChallengeImportResultDto(total, created, skipped, errorsCount, createdTitles, skippedTitles, errorMessages);
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
}

package com.devarena.common.data;

import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.model.ChallengeStarterCodeEntity;
import com.devarena.challenge.model.ChallengeTestCaseEntity;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.challenge.repository.ChallengeStarterCodeRepository;
import com.devarena.challenge.repository.ChallengeTestCaseRepository;
import com.devarena.common.data.catalog.ChallengeCatalogRegistry;
import com.devarena.common.data.catalog.ChallengeProblemDef;
import com.devarena.common.data.catalog.TestCaseDef;
import com.devarena.execution.model.ExecutionLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Component
public class ChallengeDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(ChallengeDataSeeder.class);

    private final ChallengeRepository challengeRepository;
    private final ChallengeStarterCodeRepository starterCodeRepository;
    private final ChallengeTestCaseRepository testCaseRepository;
    private final TransactionTemplate transactionTemplate;

    public ChallengeDataSeeder(
            ChallengeRepository challengeRepository,
            ChallengeStarterCodeRepository starterCodeRepository,
            ChallengeTestCaseRepository testCaseRepository,
            PlatformTransactionManager transactionManager) {
        this.challengeRepository = challengeRepository;
        this.starterCodeRepository = starterCodeRepository;
        this.testCaseRepository = testCaseRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void seedStarterCodesAndTestCasesIfEmpty() {
        List<ChallengeEntity> allChallenges = challengeRepository.findAll();
        int updatedCount = 0;

        for (ChallengeEntity challenge : allChallenges) {
            try {
                Boolean updated = transactionTemplate.execute(status -> {
                    List<ChallengeTestCaseEntity> existingCases = testCaseRepository.findByChallengeIdOrderByOrderIndexAsc(challenge.getId());
                    boolean needsTestCases = existingCases.isEmpty() || hasPlaceholderTestCases(existingCases);

                    seedOrUpdateChallenge(challenge, existingCases, needsTestCases);
                    return true;
                });

                if (Boolean.TRUE.equals(updated)) {
                    updatedCount++;
                }
            } catch (Exception ex) {
                log.warn("Could not update challenge data for {}: {}", challenge.getSlug(), ex.getMessage());
            }
        }

        if (updatedCount > 0) {
            log.info("Successfully populated/updated clean starter codes and test cases for {} challenges.", updatedCount);
        }
    }

    private boolean hasPlaceholderTestCases(List<ChallengeTestCaseEntity> cases) {
        for (ChallengeTestCaseEntity tc : cases) {
            String input = tc.getInput();
            String output = tc.getExpectedOutput();
            if (input != null && (input.equalsIgnoreCase("Sample Input 1")
                    || input.equalsIgnoreCase("Sample Input 2")
                    || input.equalsIgnoreCase("test input 1")
                    || input.equalsIgnoreCase("hidden verification 2")
                    || input.equalsIgnoreCase("test input"))) {
                return true;
            }
            if (output != null && (output.equalsIgnoreCase("Sample Output 1")
                    || output.equalsIgnoreCase("Sample Output 2")
                    || output.equalsIgnoreCase("test input 1")
                    || output.equalsIgnoreCase("hidden verification 2")
                    || output.equalsIgnoreCase("test input"))) {
                return true;
            }
        }
        return false;
    }

    public void seedOrUpdateChallenge(ChallengeEntity c, List<ChallengeTestCaseEntity> existingCases, boolean needsTestCases) {
        ChallengeProblemDef def = ChallengeCatalogRegistry.getProblem(c);

        // Safe in-place update or insert for starter codes (clean skeletons without solution body)
        saveOrUpdateStarter(c, ExecutionLanguage.JAVA, def.javaStarter().trim());
        saveOrUpdateStarter(c, ExecutionLanguage.PYTHON, def.pythonStarter().trim());
        saveOrUpdateStarter(c, ExecutionLanguage.JAVASCRIPT, def.jsStarter().trim());

        if (needsTestCases) {
            if (!existingCases.isEmpty()) {
                testCaseRepository.deleteAll(existingCases);
                testCaseRepository.flush();
            }

            // Save test cases
            for (TestCaseDef tc : def.testCases()) {
                testCaseRepository.save(new ChallengeTestCaseEntity(
                        c,
                        tc.input(),
                        tc.expectedOutput(),
                        tc.hidden(),
                        tc.orderIndex(),
                        tc.explanation()
                ));
            }
            testCaseRepository.flush();
        }
    }

    private void saveOrUpdateStarter(ChallengeEntity c, ExecutionLanguage lang, String code) {
        Optional<ChallengeStarterCodeEntity> existing = starterCodeRepository.findByChallengeIdAndLanguage(c.getId(), lang);
        if (existing.isPresent()) {
            ChallengeStarterCodeEntity entity = existing.get();
            if (!code.equals(entity.getStarterCode())) {
                entity.setStarterCode(code);
                starterCodeRepository.save(entity);
            }
        } else {
            starterCodeRepository.save(new ChallengeStarterCodeEntity(c, lang, code));
        }
    }
}


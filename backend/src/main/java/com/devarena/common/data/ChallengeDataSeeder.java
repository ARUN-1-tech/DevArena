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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class ChallengeDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(ChallengeDataSeeder.class);

    private final ChallengeRepository challengeRepository;
    private final ChallengeStarterCodeRepository starterCodeRepository;
    private final ChallengeTestCaseRepository testCaseRepository;

    public ChallengeDataSeeder(
            ChallengeRepository challengeRepository,
            ChallengeStarterCodeRepository starterCodeRepository,
            ChallengeTestCaseRepository testCaseRepository) {
        this.challengeRepository = challengeRepository;
        this.starterCodeRepository = starterCodeRepository;
        this.testCaseRepository = testCaseRepository;
    }

    @Transactional
    public void seedStarterCodesAndTestCasesIfEmpty() {
        List<ChallengeEntity> allChallenges = challengeRepository.findAll();
        int updatedCount = 0;

        for (ChallengeEntity challenge : allChallenges) {
            try {
                List<ChallengeTestCaseEntity> existingCases = testCaseRepository.findByChallengeIdOrderByOrderIndexAsc(challenge.getId());
                boolean needsSeedingOrUpdate = existingCases.isEmpty() || hasPlaceholderTestCases(existingCases);

                if (needsSeedingOrUpdate) {
                    seedOrUpdateChallenge(challenge, existingCases);
                    updatedCount++;
                }
            } catch (Exception ex) {
                log.warn("Could not update challenge data for {}: {}", challenge.getSlug(), ex.getMessage());
            }
        }

        if (updatedCount > 0) {
            log.info("Successfully populated/updated comprehensive test cases and starter codes for {} challenges.", updatedCount);
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

    @Transactional
    public void seedOrUpdateChallenge(ChallengeEntity c, List<ChallengeTestCaseEntity> existingCases) {
        if (!existingCases.isEmpty()) {
            testCaseRepository.deleteAllInBatch(existingCases);
        }

        ChallengeProblemDef def = ChallengeCatalogRegistry.getProblem(c);

        // Safe in-place update or insert for starter codes (avoids unique constraint violation)
        saveOrUpdateStarter(c, ExecutionLanguage.JAVA, def.javaStarter().trim());
        saveOrUpdateStarter(c, ExecutionLanguage.PYTHON, def.pythonStarter().trim());
        saveOrUpdateStarter(c, ExecutionLanguage.JAVASCRIPT, def.jsStarter().trim());

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
    }

    private void saveOrUpdateStarter(ChallengeEntity c, ExecutionLanguage lang, String code) {
        Optional<ChallengeStarterCodeEntity> existing = starterCodeRepository.findByChallengeIdAndLanguage(c.getId(), lang);
        if (existing.isPresent()) {
            ChallengeStarterCodeEntity entity = existing.get();
            entity.setStarterCode(code);
            starterCodeRepository.save(entity);
        } else {
            starterCodeRepository.save(new ChallengeStarterCodeEntity(c, lang, code));
        }
    }
}

package com.devarena.execution.service;

import com.devarena.challenge.model.ChallengeTestCaseEntity;
import com.devarena.execution.dto.TestCaseResultDto;
import com.devarena.execution.model.ExecutionLanguage;
import com.devarena.execution.model.ExecutionOutput;
import com.devarena.submission.model.SubmissionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeExecutionService {

    private static final Logger log = LoggerFactory.getLogger(CodeExecutionService.class);

    private final List<CodeExecutor> executors;

    @Value("${devarena.execution.timeout-ms:5000}")
    private long defaultTimeoutMs;

    public CodeExecutionService(List<CodeExecutor> executors) {
        this.executors = executors;
    }

    public CodeExecutor getActiveExecutor() {
        for (CodeExecutor executor : executors) {
            if (executor instanceof MockCodeExecutor && executor.isAvailable()) {
                return executor;
            }
        }
        for (CodeExecutor executor : executors) {
            if (executor instanceof DockerCodeExecutor && executor.isAvailable()) {
                return executor;
            }
        }
        for (CodeExecutor executor : executors) {
            if (executor instanceof ProcessSandboxCodeExecutor && executor.isAvailable()) {
                return executor;
            }
        }
        // Fallback
        return executors.stream().filter(CodeExecutor::isAvailable).findFirst()
                .orElse(new ProcessSandboxCodeExecutor());
    }

    public ExecutionBatchResult executeTestCases(
            ExecutionLanguage language,
            String sourceCode,
            List<ChallengeTestCaseEntity> testCases
    ) {
        CodeExecutor executor = getActiveExecutor();
        log.info("Executing {} test cases for language {} using executor {}", testCases.size(), language, executor.getName());

        List<TestCaseResultDto> results = new ArrayList<>();
        int passedCount = 0;
        long totalExecutionTime = 0L;
        long maxMemoryUsed = 0L;
        SubmissionStatus overallStatus = SubmissionStatus.PASSED;
        String firstErrorMessage = null;
        String combinedStdout = "";
        String combinedStderr = "";

        for (int i = 0; i < testCases.size(); i++) {
            ChallengeTestCaseEntity tc = testCases.get(i);
            ExecutionOutput output = executor.execute(language, sourceCode, tc.getInput(), defaultTimeoutMs);

            totalExecutionTime += output.executionTimeMs();
            maxMemoryUsed = Math.max(maxMemoryUsed, output.memoryUsedBytes());

            if (i == 0 || !output.stdout().isBlank()) {
                combinedStdout = output.stdout();
            }
            if (!output.stderr().isBlank()) {
                combinedStderr = output.stderr();
            }

            // If compilation error, fail entire run immediately
            if (output.status() == SubmissionStatus.COMPILATION_ERROR) {
                overallStatus = SubmissionStatus.COMPILATION_ERROR;
                firstErrorMessage = output.errorMessage();
                results.add(buildTestCaseResult(tc, output, false, output.errorMessage()));
                break;
            }

            if (output.status() == SubmissionStatus.TIME_LIMIT) {
                overallStatus = SubmissionStatus.TIME_LIMIT;
                firstErrorMessage = "Time Limit Exceeded (" + defaultTimeoutMs + "ms)";
                results.add(buildTestCaseResult(tc, output, false, firstErrorMessage));
                break;
            }

            if (output.status() == SubmissionStatus.RUNTIME_ERROR) {
                if (overallStatus == SubmissionStatus.PASSED) {
                    overallStatus = SubmissionStatus.RUNTIME_ERROR;
                    firstErrorMessage = output.errorMessage();
                }
                results.add(buildTestCaseResult(tc, output, false, output.errorMessage()));
                continue;
            }

            boolean passed = isOutputMatching(output.stdout(), tc.getExpectedOutput());
            if (passed) {
                passedCount++;
                results.add(buildTestCaseResult(tc, output, true, null));
            } else {
                if (overallStatus == SubmissionStatus.PASSED) {
                    overallStatus = SubmissionStatus.FAILED;
                    firstErrorMessage = "Output mismatch on test case " + (i + 1);
                }
                results.add(buildTestCaseResult(tc, output, false, "Output did not match expected value"));
            }
        }

        return new ExecutionBatchResult(
                overallStatus,
                passedCount,
                testCases.size(),
                totalExecutionTime,
                maxMemoryUsed,
                combinedStdout,
                combinedStderr,
                firstErrorMessage,
                results
        );
    }

    private TestCaseResultDto buildTestCaseResult(ChallengeTestCaseEntity tc, ExecutionOutput output, boolean passed, String error) {
        if (tc.isHidden()) {
            return TestCaseResultDto.hiddenCase(
                    tc.getId(),
                    tc.getOrderIndex(),
                    passed,
                    output.executionTimeMs(),
                    error
            );
        } else {
            return TestCaseResultDto.publicCase(
                    tc.getId(),
                    tc.getOrderIndex(),
                    tc.getInput(),
                    tc.getExpectedOutput(),
                    output.stdout() != null ? output.stdout().trim() : "",
                    passed,
                    output.executionTimeMs(),
                    error
            );
        }
    }

    private boolean isOutputMatching(String actual, String expected) {
        if (actual == null || expected == null) return false;
        String normalizedActual = actual.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
        String normalizedExpected = expected.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
        return normalizedActual.equals(normalizedExpected);
    }

    public record ExecutionBatchResult(
            SubmissionStatus status,
            int passedTests,
            int totalTests,
            long executionTimeMs,
            long memoryUsedBytes,
            String stdout,
            String stderr,
            String errorMessage,
            List<TestCaseResultDto> testResults
    ) {}
}

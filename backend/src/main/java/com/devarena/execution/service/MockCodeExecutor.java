package com.devarena.execution.service;

import com.devarena.execution.model.ExecutionLanguage;
import com.devarena.execution.model.ExecutionOutput;
import com.devarena.submission.model.SubmissionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MockCodeExecutor implements CodeExecutor {

    @Value("${devarena.execution.mock.enabled:false}")
    private boolean mockEnabled;

    @Override
    public String getName() {
        return "DeterministicMockExecutor";
    }

    @Override
    public boolean isAvailable() {
        return mockEnabled;
    }

    @Override
    public ExecutionOutput execute(ExecutionLanguage language, String sourceCode, String stdin, long timeoutMs) {
        if (sourceCode.contains("// FAIL_COMPILE")) {
            return ExecutionOutput.failed(SubmissionStatus.COMPILATION_ERROR, "", "Syntax error at line 1", 10L, 1, "Compilation error");
        }
        if (sourceCode.contains("// TIMEOUT")) {
            return ExecutionOutput.timeout(timeoutMs);
        }
        if (sourceCode.contains("// RUNTIME_ERROR")) {
            return ExecutionOutput.failed(SubmissionStatus.RUNTIME_ERROR, "", "Exception in thread 'main' java.lang.NullPointerException", 15L, 1, "Runtime Exception");
        }

        // Deterministic mock: if solution contains "// RETURN: xyz", return that, else return default mock answer based on stdin
        if (sourceCode.contains("// RETURN:")) {
            int idx = sourceCode.indexOf("// RETURN:") + 10;
            String ret = sourceCode.substring(idx).trim();
            if (ret.contains("\n")) {
                ret = ret.substring(0, ret.indexOf("\n")).trim();
            }
            return ExecutionOutput.success(ret, 25L, 1024L);
        }

        // Return empty or echo back
        return ExecutionOutput.success("mock-output", 20L, 1024L);
    }
}

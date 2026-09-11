package com.devarena.execution.service;

import com.devarena.execution.model.ExecutionLanguage;
import com.devarena.execution.model.ExecutionOutput;

public interface CodeExecutor {
    ExecutionOutput execute(ExecutionLanguage language, String sourceCode, String stdin, long timeoutMs);
    boolean isAvailable();
    String getName();
}

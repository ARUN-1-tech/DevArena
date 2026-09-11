package com.devarena.submission.model;

public enum SubmissionStatus {
    QUEUED("Queued in execution pipeline"),
    RUNNING("Running sandbox evaluation"),
    PASSED("All tests passed"),
    FAILED("Test case output mismatch"),
    TIME_LIMIT("Time Limit Exceeded"),
    MEMORY_LIMIT("Memory Limit Exceeded"),
    RUNTIME_ERROR("Runtime Error occurred"),
    COMPILATION_ERROR("Compilation failed"),
    SYSTEM_ERROR("Execution sandbox error");

    private final String description;

    SubmissionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this != QUEUED && this != RUNNING;
    }

    public boolean isSuccessful() {
        return this == PASSED;
    }
}

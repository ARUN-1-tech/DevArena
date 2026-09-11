package com.devarena.execution.model;

public enum ExecutionLanguage {
    JAVA("java", ".java", "Java"),
    PYTHON("python", ".py", "Python"),
    JAVASCRIPT("javascript", ".js", "JavaScript");

    private final String monacoId;
    private final String extension;
    private final String displayName;

    ExecutionLanguage(String monacoId, String extension, String displayName) {
        this.monacoId = monacoId;
        this.extension = extension;
        this.displayName = displayName;
    }

    public String getMonacoId() {
        return monacoId;
    }

    public String getExtension() {
        return extension;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ExecutionLanguage fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
        String normalized = value.trim().toUpperCase();
        return switch (normalized) {
            case "JAVA" -> JAVA;
            case "PYTHON", "PY" -> PYTHON;
            case "JAVASCRIPT", "JS", "NODE" -> JAVASCRIPT;
            default -> throw new IllegalArgumentException("Unsupported execution language: " + value);
        };
    }
}

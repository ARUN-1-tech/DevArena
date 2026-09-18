package com.devarena.common.data.catalog;

public record TestCaseDef(
        String input,
        String expectedOutput,
        boolean hidden,
        int orderIndex,
        String explanation
) {
    public static TestCaseDef publicCase(int orderIndex, String input, String expectedOutput, String explanation) {
        return new TestCaseDef(input, expectedOutput, false, orderIndex, explanation);
    }

    public static TestCaseDef hiddenCase(int orderIndex, String input, String expectedOutput, String explanation) {
        return new TestCaseDef(input, expectedOutput, true, orderIndex, explanation);
    }
}

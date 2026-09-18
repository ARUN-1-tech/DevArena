package com.devarena.common.data.catalog;

import java.util.List;

public record ChallengeProblemDef(
        String slug,
        String javaStarter,
        String pythonStarter,
        String jsStarter,
        List<TestCaseDef> testCases
) {
}

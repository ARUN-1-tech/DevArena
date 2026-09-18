package com.devarena.common.data.catalog;

import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.model.ProblemType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallengeCatalogRegistry {

    private static final Map<String, ChallengeProblemDef> REGISTRY = new HashMap<>();

    static {
        REGISTRY.putAll(ArrayCatalog.getDefinitions());
        REGISTRY.putAll(StringCatalog.getDefinitions());
        REGISTRY.putAll(TwoPointerSlidingWindowCatalog.getDefinitions());
        REGISTRY.putAll(LinkedListCatalog.getDefinitions());
        REGISTRY.putAll(StackQueueCatalog.getDefinitions());
        REGISTRY.putAll(TreeCatalog.getDefinitions());
        REGISTRY.putAll(GraphCatalog.getDefinitions());
        REGISTRY.putAll(DynamicProgrammingCatalog.getDefinitions());
        REGISTRY.putAll(BinarySearchCatalog.getDefinitions());
        REGISTRY.putAll(BacktrackingCatalog.getDefinitions());
        REGISTRY.putAll(HeapCatalog.getDefinitions());
        REGISTRY.putAll(BitMathGreedyCatalog.getDefinitions());
        REGISTRY.putAll(SqlMcqAptitudeCatalog.getDefinitions());
    }

    public static ChallengeProblemDef getProblem(ChallengeEntity challenge) {
        String slug = challenge.getSlug();
        if (REGISTRY.containsKey(slug)) {
            return REGISTRY.get(slug);
        }
        return generateFallback(challenge);
    }

    private static ChallengeProblemDef generateFallback(ChallengeEntity c) {
        String title = c.getTitle();
        String slug = c.getSlug();

        if (c.getProblemType() == ProblemType.SQL) {
            String sqlStarter = "-- Write your SQL query for " + title + "\n";
            return new ChallengeProblemDef(
                    slug,
                    sqlStarter,
                    "# " + sqlStarter,
                    "// " + sqlStarter,
                    List.of(
                            TestCaseDef.publicCase(1, "Table Input Data", "Expected Result Query Output", "Standard query execution test case."),
                            TestCaseDef.hiddenCase(2, "Edge Case Records", "Expected Edge Query Output", "Hidden validation query execution test case.")
                    )
            );
        }

        if (c.getProblemType() == ProblemType.MCQ || c.getProblemType() == ProblemType.PUZZLE || c.getProblemType() == ProblemType.APTITUDE || c.getProblemType() == ProblemType.INTERVIEW) {
            String ans = c.getCorrectAnswer() != null ? c.getCorrectAnswer() : "A";
            return new ChallengeProblemDef(
                    slug,
                    "// Multiple choice / conceptual challenge: " + title,
                    "# Multiple choice / conceptual challenge: " + title,
                    "// Multiple choice / conceptual challenge: " + title,
                    List.of(
                            TestCaseDef.publicCase(1, "Option Selection", ans, "Correct conceptual answer evaluation.")
                    )
            );
        }

        // Standard Algorithm Coding Kata Fallback
        String javaStarter = """
import java.util.*;

public class Solution {
    // Solve %s
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        // TODO: Implement solution logic for %s
        System.out.println(line);
    }
}
""".formatted(title, title);

        String pythonStarter = """
import sys

def solve():
    line = sys.stdin.read().strip()
    # TODO: Implement solution logic for %s
    print(line)

if __name__ == "__main__":
    solve()
""".formatted(title);

        String jsStarter = """
const fs = require('fs');

function solve() {
    const input = fs.readFileSync(0, 'utf-8').trim();
    // TODO: Implement solution logic for %s
    console.log(input);
}

solve();
""".formatted(title);

        return new ChallengeProblemDef(
                slug,
                javaStarter,
                pythonStarter,
                jsStarter,
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3", "[1, 2, 3]", "Public sample verification for " + title),
                        TestCaseDef.hiddenCase(2, "4,5,6", "[4, 5, 6]", "Hidden verification for " + title)
                )
        );
    }
}

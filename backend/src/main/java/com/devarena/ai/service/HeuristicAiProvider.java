package com.devarena.ai.service;

import com.devarena.ai.dto.AiCoachRequest;
import com.devarena.ai.dto.AiCoachResponseDto;
import com.devarena.challenge.model.ChallengeEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HeuristicAiProvider implements AiProvider {

    @Override
    public AiCoachResponseDto generateResponse(AiCoachRequest request, ChallengeEntity challenge, String username, int hintLevel) {
        String title = challenge != null ? challenge.getTitle() : "Problem";
        String category = challenge != null && challenge.getCategory() != null ? challenge.getCategory().name() : "ALGORITHMS";
        String difficulty = challenge != null && challenge.getDifficulty() != null ? challenge.getDifficulty().name() : "MEDIUM";
        String code = request.getCurrentCode() != null ? request.getCurrentCode().trim() : "";

        String reply;
        List<String> suggestedFollowUps = new ArrayList<>();
        int nextHintLevel = hintLevel;

        switch (request.getRequestType()) {
            case HINT -> {
                if (hintLevel <= 1) {
                    reply = buildHintLevel1(title, category, difficulty);
                    nextHintLevel = 2;
                    suggestedFollowUps.add("Need more specifics on data structures?");
                    suggestedFollowUps.add("What should the time complexity be?");
                } else if (hintLevel == 2) {
                    reply = buildHintLevel2(title, category, difficulty);
                    nextHintLevel = 3;
                    suggestedFollowUps.add("Can you walk me through edge cases?");
                    suggestedFollowUps.add("How do I structure the loop condition?");
                } else {
                    reply = buildHintLevel3(title, category, difficulty);
                    nextHintLevel = 3;
                    suggestedFollowUps.add("Explain the optimal time complexity.");
                    suggestedFollowUps.add("Help me debug my current approach.");
                }
            }
            case EXPLAIN -> {
                reply = buildExplainResponse(challenge);
                suggestedFollowUps.add("Give me a conceptual hint.");
                suggestedFollowUps.add("What is the optimal complexity target?");
            }
            case DEBUG -> {
                reply = buildDebugResponse(title, code);
                suggestedFollowUps.add("What edge cases could break this?");
                suggestedFollowUps.add("Give me a hint for the next step.");
            }
            case COMPLEXITY -> {
                reply = buildComplexityResponse(title, category, difficulty);
                suggestedFollowUps.add("How can I avoid extra space?");
                suggestedFollowUps.add("Give me an algorithmic hint.");
            }
            case SOLUTION_EXPLANATION -> {
                reply = buildSolutionExplanation(title, category, difficulty);
                suggestedFollowUps.add("What are the key trade-offs in this approach?");
                suggestedFollowUps.add("How does this compare to a brute force solution?");
            }
            default -> {
                reply = "I'm your DevArena AI Coach. I'm here to guide your problem-solving without spoiling the solution. Ask me for a hint, an explanation of the problem, complexity analysis, or debugging assistance!";
                suggestedFollowUps.add("Give me a hint.");
                suggestedFollowUps.add("Explain the problem.");
            }
        }

        return AiCoachResponseDto.builder()
                .reply(reply)
                .hintLevel(nextHintLevel)
                .suggestedFollowUps(suggestedFollowUps)
                .remainingDailyQueries(50) // overridden by AiCoachService
                .build();
    }

    private String buildHintLevel1(String title, String category, String difficulty) {
        return String.format(
                "### 💡 Hint 1: Conceptual Strategy for **%s**\n\n" +
                "Before writing code, consider the core transformation required:\n" +
                "- Identify what invariants or properties remain constant throughout the input data.\n" +
                "- Does processing the items sequentially, sorting beforehand, or grouping elements simplify the state?\n" +
                "- **Socratic reflection:** What is the naive brute-force approach, and what repetitive work is it doing that we could eliminate?",
                title
        );
    }

    private String buildHintLevel2(String title, String category, String difficulty) {
        return String.format(
                "### 🔍 Hint 2: Data Structure & State for **%s**\n\n" +
                "To optimize beyond brute force for this %s (%s) challenge:\n" +
                "- Consider which data structure grants O(1) lookups or tracks running extremes (e.g. HashMap/Set, Monotonic Stack, or Two Pointers).\n" +
                "- If working with sequences, can you maintain running pointers (`left`, `right`) or an accumulator variable instead of nested loops?\n" +
                "- Think about what minimal information you need to record at step `i` to make the optimal choice for step `i+1`.",
                title, category, difficulty
        );
    }

    private String buildHintLevel3(String title, String category, String difficulty) {
        return String.format(
                "### 🎯 Hint 3: Edge Cases & Structural Pseudo-Logic for **%s**\n\n" +
                "You're almost there! Pay close attention to boundary conditions:\n" +
                "- **Edge cases:** Empty input (`length == 0`), single element, duplicate values, or negative numbers.\n" +
                "- **Loop bounds:** Ensure boundary indices (`< len` vs `<= len - 1`) don't throw OutOfBounds or miss the final element.\n" +
                "- **Structure:** Initialize your state variables -> loop through input -> check condition/update running result -> return formatted answer.",
                title
        );
    }

    private String buildExplainResponse(ChallengeEntity challenge) {
        if (challenge == null) {
            return "This challenge tests algorithmic reasoning and data structure selection. Analyze input constraints carefully before coding.";
        }
        return String.format(
                "### 📖 Problem Breakdown: **%s**\n\n" +
                "**Category:** %s | **Difficulty:** %s | **XP Reward:** %d XP\n\n" +
                "**Summary:**\n%s\n\n" +
                "**Key Objectives:**\n" +
                "1. Accurately transform the given input matching all stated constraints.\n" +
                "2. Maintain optimal time complexity suitable for large test cases.\n" +
                "3. Ensure clean handling of boundary conditions (e.g. minimum/maximum limits).",
                challenge.getTitle(),
                challenge.getCategory(),
                challenge.getDifficulty(),
                challenge.getXpReward(),
                challenge.getDescription()
        );
    }

    private String buildDebugResponse(String title, String code) {
        if (code == null || code.isBlank()) {
            return "No code was provided for review. Write your solution in the Code Lab editor and ask again for debugging assistance!";
        }

        StringBuilder debugAdvice = new StringBuilder();
        debugAdvice.append(String.format("### 🛠️ Debugging Assessment for **%s**\n\n", title));

        if (!code.contains("return")) {
            debugAdvice.append("- ⚠️ **Missing Return Statement:** Ensure your method returns the expected output value.\n");
        }
        if (code.contains("while") && !code.contains("++") && !code.contains("+=") && !code.contains("--") && !code.contains("-=")) {
            debugAdvice.append("- ⚠️ **Potential Infinite Loop:** A `while` loop was detected without an obvious counter modification.\n");
        }
        if (code.contains("[i]") && !code.contains("length") && !code.contains("size")) {
            debugAdvice.append("- ⚠️ **Array Bounds Check:** Ensure loop terminating condition guards against index out-of-bounds.\n");
        }
        if (code.contains("==") && (code.contains("String") || code.contains("str"))) {
            debugAdvice.append("- ⚠️ **Object Equality:** Remember to use `.equals()` rather than `==` for object/string comparisons.\n");
        }

        debugAdvice.append("\n**Recommended Debugging Steps:**\n" +
                "1. Trace through with the smallest sample test case line by line.\n" +
                "2. Check if variables are properly reset between iterations.\n" +
                "3. Verify that empty or null inputs do not trigger unhandled exceptions.");

        return debugAdvice.toString();
    }

    private String buildComplexityResponse(String title, String category, String difficulty) {
        String targetTime;
        String targetSpace;

        if ("HARD".equalsIgnoreCase(difficulty)) {
            targetTime = "O(N log N) or O(N)";
            targetSpace = "O(N) or O(1)";
        } else if ("EASY".equalsIgnoreCase(difficulty)) {
            targetTime = "O(N)";
            targetSpace = "O(1) auxiliary";
        } else {
            targetTime = "O(N) or O(N log N)";
            targetSpace = "O(N) with Hash/Heap or O(1) in-place";
        }

        return String.format(
                "### ⏱️ Complexity Targets for **%s**\n\n" +
                "- **Target Time Complexity:** `%s` — Brute force approaches (like O(N²)) will typically exceed runtime limits on hidden stress tests.\n" +
                "- **Target Space Complexity:** `%s` — Aim to avoid unnecessary object allocations inside hot loops.\n\n" +
                "**Why this matters:** In competitive 1v1 battles and evaluation tests, efficient memory layout and linear scan patterns minimize execution overhead.",
                title, targetTime, targetSpace
        );
    }

    private String buildSolutionExplanation(String title, String category, String difficulty) {
        return String.format(
                "### 🧠 Algorithmic Walkthrough for **%s**\n\n" +
                "1. **State Initialization:** Define clear variables to track the intermediate results.\n" +
                "2. **Single-Pass Invariant:** Traverse the dataset once, updating your tracking structures in constant O(1) time per item.\n" +
                "3. **Resolution:** When the scan concludes, synthesize the tracked state into the target output type.\n" +
                "4. **Verification:** Validate against all edge cases before final submission to maximize your battle/challenge score.",
                title
        );
    }
}

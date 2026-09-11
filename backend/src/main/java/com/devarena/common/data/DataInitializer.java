package com.devarena.common.data;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.quest.model.DailyQuestEntity;
import com.devarena.quest.model.QuestType;
import com.devarena.quest.repository.DailyQuestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ChallengeRepository challengeRepository;
    private final DailyQuestRepository dailyQuestRepository;
    private final com.devarena.user.repository.UserRepository userRepository;
    private final com.devarena.user.service.AuthService authService;

    public DataInitializer(
            ChallengeRepository challengeRepository,
            DailyQuestRepository dailyQuestRepository,
            com.devarena.user.repository.UserRepository userRepository,
            com.devarena.user.service.AuthService authService) {
        this.challengeRepository = challengeRepository;
        this.dailyQuestRepository = dailyQuestRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        seedChallengesIfEmpty();
        seedDailyQuestsIfEmpty();
        seedDefaultUserIfEmpty();
    }

    private void seedDefaultUserIfEmpty() {
        if (userRepository.findByUsernameIgnoreCase("Arun").isPresent()) {
            return;
        }

        try {
            log.info("Seeding default developer player account (Arun)...");
            authService.register(new com.devarena.user.dto.RegisterRequest(
                    "arun@devarena.io",
                    "Password123",
                    "Arun",
                    "Arun"
            ));
            log.info("Default developer player account seeded successfully.");
        } catch (Exception ex) {
            log.warn("Could not seed default developer account: {}", ex.getMessage());
        }
    }

    private void seedChallengesIfEmpty() {
        if (challengeRepository.count() > 0) {
            return;
        }

        log.info("Seeding initial DevArena coding challenges catalog...");

        List<ChallengeEntity> challenges = List.of(
                // 5 EASY
                new ChallengeEntity(
                        "Two Sum",
                        "two-sum",
                        "Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice.\n\n### Example 1:\n```\nInput: nums = [2,7,11,15], target = 9\nOutput: [0,1]\nExplanation: Because nums[0] + nums[1] == 9, we return [0, 1].\n```\n\n### Constraints:\n- 2 <= nums.length <= 10^4\n- -10^9 <= nums[i] <= 10^9\n- Only one valid answer exists.",
                        ChallengeDifficulty.EASY,
                        ChallengeCategory.ARRAYS,
                        50,
                        15,
                        "array,hash-table"
                ),
                new ChallengeEntity(
                        "Valid Palindrome",
                        "valid-palindrome",
                        "A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward.\n\n### Example 1:\n```\nInput: s = \"A man, a plan, a canal: Panama\"\nOutput: true\n```\n\n### Constraints:\n- 1 <= s.length <= 2 * 10^5\n- `s` consists only of printable ASCII characters.",
                        ChallengeDifficulty.EASY,
                        ChallengeCategory.STRINGS,
                        50,
                        10,
                        "string,two-pointers"
                ),
                new ChallengeEntity(
                        "Reverse Linked List",
                        "reverse-linked-list",
                        "Given the `head` of a singly linked list, reverse the list, and return the reversed list.\n\n### Example 1:\n```\nInput: head = [1,2,3,4,5]\nOutput: [5,4,3,2,1]\n```\n\n### Constraints:\n- Number of nodes is in range [0, 5000].\n- -5000 <= Node.val <= 5000",
                        ChallengeDifficulty.EASY,
                        ChallengeCategory.LINKED_LIST,
                        60,
                        15,
                        "linked-list,recursion"
                ),
                new ChallengeEntity(
                        "Valid Parentheses",
                        "valid-parentheses",
                        "Given a string `s` containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.\n\nAn input string is valid if open brackets are closed by the same type of brackets and in the correct order.\n\n### Example 1:\n```\nInput: s = \"()[]{}\"\nOutput: true\n```",
                        ChallengeDifficulty.EASY,
                        ChallengeCategory.STACK_QUEUE,
                        60,
                        15,
                        "stack,string"
                ),
                new ChallengeEntity(
                        "Maximum Depth of Binary Tree",
                        "maximum-depth-of-binary-tree",
                        "Given the `root` of a binary tree, return its maximum depth.\n\nA binary tree's maximum depth is the number of nodes along the longest path from the root node down to the farthest leaf node.",
                        ChallengeDifficulty.EASY,
                        ChallengeCategory.TREES,
                        70,
                        15,
                        "tree,dfs,binary-tree"
                ),

                // 5 MEDIUM
                new ChallengeEntity(
                        "Longest Substring Without Repeating Characters",
                        "longest-substring-without-repeating-characters",
                        "Given a string `s`, find the length of the longest substring without repeating characters.\n\n### Example 1:\n```\nInput: s = \"abcabcbb\"\nOutput: 3\nExplanation: The answer is \"abc\", with the length of 3.\n```",
                        ChallengeDifficulty.MEDIUM,
                        ChallengeCategory.STRINGS,
                        120,
                        25,
                        "sliding-window,hash-table,string"
                ),
                new ChallengeEntity(
                        "Container With Most Water",
                        "container-with-most-water",
                        "You are given an integer array `height` of length `n`. Find two lines that together with the x-axis form a container, such that the container contains the most water.\n\nReturn the maximum amount of water a container can store.",
                        ChallengeDifficulty.MEDIUM,
                        ChallengeCategory.ARRAYS,
                        130,
                        25,
                        "two-pointers,array,greedy"
                ),
                new ChallengeEntity(
                        "Number of Islands",
                        "number-of-islands",
                        "Given an `m x n` 2D binary grid which represents a map of '1's (land) and '0's (water), return the number of islands.\n\nAn island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically.",
                        ChallengeDifficulty.MEDIUM,
                        ChallengeCategory.GRAPHS,
                        150,
                        30,
                        "graph,dfs,bfs,matrix"
                ),
                new ChallengeEntity(
                        "Coin Change",
                        "coin-change",
                        "You are given an integer array `coins` representing coins of different denominations and an integer `amount` representing a total amount of money.\n\nReturn the fewest number of coins that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return `-1`.",
                        ChallengeDifficulty.MEDIUM,
                        ChallengeCategory.DYNAMIC_PROGRAMMING,
                        160,
                        30,
                        "dynamic-programming,bfs"
                ),
                new ChallengeEntity(
                        "LRU Cache Design",
                        "lru-cache-design",
                        "Design a data structure that follows the constraints of a Least Recently Used (LRU) cache.\n\nImplement the `LRUCache` class with `get` and `put` in O(1) average time complexity.",
                        ChallengeDifficulty.MEDIUM,
                        ChallengeCategory.STACK_QUEUE,
                        175,
                        35,
                        "hash-table,linked-list,design"
                ),

                // 3 HARD
                new ChallengeEntity(
                        "Merge k Sorted Lists",
                        "merge-k-sorted-lists",
                        "You are given an array of `k` linked-lists `lists`, each linked-list is sorted in ascending order.\n\nMerge all the linked-lists into one sorted linked-list and return it.",
                        ChallengeDifficulty.HARD,
                        ChallengeCategory.LINKED_LIST,
                        250,
                        45,
                        "linked-list,divide-and-conquer,heap"
                ),
                new ChallengeEntity(
                        "Trapping Rain Water",
                        "trapping-rain-water",
                        "Given `n` non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.",
                        ChallengeDifficulty.HARD,
                        ChallengeCategory.ARRAYS,
                        280,
                        45,
                        "two-pointers,dynamic-programming,stack"
                ),
                new ChallengeEntity(
                        "Word Ladder II",
                        "word-ladder-ii",
                        "A transformation sequence from word `beginWord` to word `endWord` using a dictionary `wordList` is a sequence of words `beginWord -> s1 -> s2 -> ... -> sk` such that every adjacent pair differs by a single letter.\n\nReturn all the shortest transformation sequences.",
                        ChallengeDifficulty.HARD,
                        ChallengeCategory.GRAPHS,
                        300,
                        50,
                        "graph,bfs,backtracking"
                ),

                // 2 EXPERT
                new ChallengeEntity(
                        "Median of Two Sorted Arrays",
                        "median-of-two-sorted-arrays",
                        "Given two sorted arrays `nums1` and `nums2` of size `m` and `n` respectively, return the median of the two sorted arrays.\n\nThe overall run time complexity should be O(log (m+n)).",
                        ChallengeDifficulty.EXPERT,
                        ChallengeCategory.ALGORITHMS,
                        400,
                        60,
                        "binary-search,divide-and-conquer,array"
                ),
                new ChallengeEntity(
                        "Burst Balloons",
                        "burst-balloons",
                        "You are given `n` balloons, indexed from `0` to `n - 1`. Each balloon is painted with a number on it represented by an array `nums`. You are asked to burst all the balloons.\n\nIf you burst the `i`th balloon, you will get `nums[i - 1] * nums[i] * nums[i + 1]` coins. Return the maximum coins you can collect.",
                        ChallengeDifficulty.EXPERT,
                        ChallengeCategory.DYNAMIC_PROGRAMMING,
                        450,
                        60,
                        "dynamic-programming,divide-and-conquer"
                )
        );

        challengeRepository.saveAll(challenges);
        log.info("Successfully seeded {} coding challenges.", challenges.size());
    }

    private void seedDailyQuestsIfEmpty() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        if (!dailyQuestRepository.findByActiveDateAndStatus(today, com.devarena.quest.model.QuestStatus.ACTIVE).isEmpty()) {
            return;
        }

        log.info("Seeding daily quests for date: {}", today);
        List<DailyQuestEntity> quests = List.of(
                new DailyQuestEntity("Solve 1 Challenge", "Complete any algorithmic kata in the practice archive.", QuestType.COMPLETE_CHALLENGE, 1, 100, today),
                new DailyQuestEntity("Practice 2 Easy Katas", "Complete 2 Easy difficulty algorithmic problems.", QuestType.COMPLETE_EASY, 2, 150, today),
                new DailyQuestEntity("Earn 200 XP", "Gain 200 XP through problem-solving and activities.", QuestType.EARN_XP, 200, 100, today)
        );
        dailyQuestRepository.saveAll(quests);
    }
}

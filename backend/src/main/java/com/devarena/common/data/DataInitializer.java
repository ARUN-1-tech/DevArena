package com.devarena.common.data;

import com.devarena.challenge.model.ChallengeCategory;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.model.ProblemType;
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
    private final ChallengeDataSeeder challengeDataSeeder;

    private final com.devarena.achievement.repository.AchievementRepository achievementRepository;
    private final com.devarena.skill.repository.SkillRepository skillRepository;

    public DataInitializer(
            ChallengeRepository challengeRepository,
            DailyQuestRepository dailyQuestRepository,
            com.devarena.user.repository.UserRepository userRepository,
            com.devarena.user.service.AuthService authService,
            ChallengeDataSeeder challengeDataSeeder,
            com.devarena.achievement.repository.AchievementRepository achievementRepository,
            com.devarena.skill.repository.SkillRepository skillRepository) {
        this.challengeRepository = challengeRepository;
        this.dailyQuestRepository = dailyQuestRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.challengeDataSeeder = challengeDataSeeder;
        this.achievementRepository = achievementRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public void run(String... args) {
        seedChallengesIfEmpty();
        seedDailyQuestsIfEmpty();
        seedDefaultUserIfEmpty();
        seedAdminUserIfEmpty();
        challengeDataSeeder.seedStarterCodesAndTestCasesIfEmpty();
        seedAchievementsIfEmpty();
        seedSkillsIfEmpty();
        seedRisingBrainProblemsIfNeeded();
    }

    private void seedAdminUserIfEmpty() {
        if (userRepository.findByUsernameIgnoreCase("Admin").isPresent() || userRepository.findByEmailIgnoreCase("admin@devarena.io").isPresent()) {
            return;
        }

        try {
            log.info("Seeding default administrator account (admin@devarena.io)...");
            com.devarena.user.dto.AuthResponse authResp = authService.register(new com.devarena.user.dto.RegisterRequest(
                    "admin@devarena.io",
                    "Password123",
                    "Admin",
                    "Admin"
            ));
            userRepository.findById(authResp.user().id()).ifPresent(adminUser -> {
                adminUser.getRoles().add(com.devarena.security.UserRole.ROLE_ADMIN);
                userRepository.save(adminUser);
            });
            log.info("Default administrator account seeded with ROLE_ADMIN successfully.");
        } catch (Exception ex) {
            log.warn("Could not seed default administrator account: {}", ex.getMessage());
        }
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
                ),

                // Additional popular competitive programming katas
                new ChallengeEntity(
                        "Binary Search",
                        "binary-search",
                        "Given an array of integers `nums` which is sorted in ascending order, and an integer `target`, write a function to search `target` in `nums`. If `target` exists, then return its index. Otherwise, return `-1`.\n\nYou must write an algorithm with `O(log n)` runtime complexity.\n\n### Example 1:\n```\nInput: nums = [-1,0,3,5,9,12], target = 9\nOutput: 4\nExplanation: 9 exists in nums and its index is 4\n```",
                        ChallengeDifficulty.EASY,
                        ChallengeCategory.ALGORITHMS,
                        50,
                        10,
                        "binary-search,array"
                ),
                new ChallengeEntity(
                        "Climbing Stairs",
                        "climbing-stairs",
                        "You are climbing a staircase. It takes `n` steps to reach the top. Each time you can either climb `1` or `2` steps. In how many distinct ways can you climb to the top?\n\n### Example 1:\n```\nInput: n = 3\nOutput: 3\nExplanation: There are three ways to climb to the top:\n1. 1 step + 1 step + 1 step\n2. 1 step + 2 steps\n3. 2 steps + 1 step\n```",
                        ChallengeDifficulty.EASY,
                        ChallengeCategory.DYNAMIC_PROGRAMMING,
                        50,
                        10,
                        "dynamic-programming,math"
                ),
                new ChallengeEntity(
                        "Maximum Subarray",
                        "maximum-subarray",
                        "Given an integer array `nums`, find the subarray with the largest sum, and return its sum.\n\n### Example 1:\n```\nInput: nums = [-2,1,-3,4,-1,2,1,-5,4]\nOutput: 6\nExplanation: The subarray [4,-1,2,1] has the largest sum 6.\n```",
                        ChallengeDifficulty.MEDIUM,
                        ChallengeCategory.ARRAYS,
                        120,
                        20,
                        "array,dynamic-programming,divide-and-conquer"
                ),
                new ChallengeEntity(
                        "Rotate Image",
                        "rotate-image",
                        "You are given an `n x n` 2D matrix representing an image, rotate the image by 90 degrees (clockwise) in-place.\n\n### Example 1:\n```\nInput: matrix = [[1,2,3],[4,5,6],[7,8,9]]\nOutput: [[7,4,1],[8,5,2],[9,6,3]]\n```",
                        ChallengeDifficulty.MEDIUM,
                        ChallengeCategory.ARRAYS,
                        140,
                        25,
                        "array,math,matrix"
                ),
                new ChallengeEntity(
                        "House Robber",
                        "house-robber",
                        "You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed, the only constraint stopping you from robbing each of them is that adjacent houses have security systems connected and it will automatically contact the police if two adjacent houses were broken into on the same night.\n\nGiven an integer array `nums` representing the amount of money of each house, return the maximum amount of money you can rob tonight without alerting the police.\n\n### Example 1:\n```\nInput: nums = [1,2,3,1]\nOutput: 4\nExplanation: Rob house 1 (money = 1) and then rob house 3 (money = 3). Total amount you can rob = 1 + 3 = 4.\n```",
                        ChallengeDifficulty.MEDIUM,
                        ChallengeCategory.DYNAMIC_PROGRAMMING,
                        130,
                        20,
                        "dynamic-programming,array"
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
                new DailyQuestEntity("Earn 200 XP", "Gain 200 XP through problem-solving and activities.", QuestType.EARN_XP, 200, 100, today),
                new DailyQuestEntity("Arena Warrior", "Compete and achieve victory in a 1v1 Battle Arena match.", QuestType.WIN_BATTLE, 1, 250, today),
                new DailyQuestEntity("Practice 3 Katas", "Solve 3 competitive programming challenges.", QuestType.PRACTICE_PROBLEMS, 3, 200, today)
        );
        dailyQuestRepository.saveAll(quests);
    }

    private void seedAchievementsIfEmpty() {
        if (achievementRepository.count() > 0) {
            return;
        }

        log.info("Seeding initial DevArena achievements catalog...");
        List<com.devarena.achievement.model.AchievementEntity> list = List.of(
                new com.devarena.achievement.model.AchievementEntity(
                        "FIRST_BLOOD", "First Blood", "Solve your very first algorithmic coding challenge.",
                        "Swords", com.devarena.achievement.model.AchievementCategory.CHALLENGE,
                        com.devarena.achievement.model.RequirementType.CHALLENGES_SOLVED, 1, 100,
                        com.devarena.achievement.model.AchievementRarity.COMMON, false
                ),
                new com.devarena.achievement.model.AchievementEntity(
                        "CODE_WARRIOR", "Code Warrior", "Successfully solve 5 coding challenges.",
                        "Zap", com.devarena.achievement.model.AchievementCategory.CHALLENGE,
                        com.devarena.achievement.model.RequirementType.CHALLENGES_SOLVED, 5, 250,
                        com.devarena.achievement.model.AchievementRarity.RARE, false
                ),
                new com.devarena.achievement.model.AchievementEntity(
                        "CENTURY", "Century Master", "Master the craft by solving 20 challenges.",
                        "Trophy", com.devarena.achievement.model.AchievementCategory.CHALLENGE,
                        com.devarena.achievement.model.RequirementType.CHALLENGES_SOLVED, 20, 1000,
                        com.devarena.achievement.model.AchievementRarity.LEGENDARY, false
                ),
                new com.devarena.achievement.model.AchievementEntity(
                        "ARENA_STARTER", "Arena Starter", "Win your first competitive 1v1 Battle Arena match.",
                        "Flame", com.devarena.achievement.model.AchievementCategory.BATTLE,
                        com.devarena.achievement.model.RequirementType.BATTLES_WON, 1, 150,
                        com.devarena.achievement.model.AchievementRarity.COMMON, false
                ),
                new com.devarena.achievement.model.AchievementEntity(
                        "DUELIST", "Seasoned Duelist", "Triumph in 5 ranked 1v1 coding duels.",
                        "Swords", com.devarena.achievement.model.AchievementCategory.BATTLE,
                        com.devarena.achievement.model.RequirementType.BATTLES_WON, 5, 300,
                        com.devarena.achievement.model.AchievementRarity.RARE, false
                ),
                new com.devarena.achievement.model.AchievementEntity(
                        "WIN_STREAK", "Unbroken Streak", "Achieve a win streak of 3 consecutive arena battles.",
                        "Zap", com.devarena.achievement.model.AchievementCategory.BATTLE,
                        com.devarena.achievement.model.RequirementType.BATTLE_STREAK, 3, 500,
                        com.devarena.achievement.model.AchievementRarity.EPIC, false
                ),
                new com.devarena.achievement.model.AchievementEntity(
                        "XP_HUNTER", "XP Hunter", "Amass a career total of 2,000 XP.",
                        "Sparkles", com.devarena.achievement.model.AchievementCategory.MASTERY,
                        com.devarena.achievement.model.RequirementType.TOTAL_XP, 2000, 250,
                        com.devarena.achievement.model.AchievementRarity.RARE, false
                ),
                new com.devarena.achievement.model.AchievementEntity(
                        "UNSTOPPABLE", "Unstoppable", "Maintain an active coding streak of 7 days.",
                        "Flame", com.devarena.achievement.model.AchievementCategory.STREAK,
                        com.devarena.achievement.model.RequirementType.DAILY_STREAK, 7, 500,
                        com.devarena.achievement.model.AchievementRarity.EPIC, false
                ),
                new com.devarena.achievement.model.AchievementEntity(
                        "MASTER_DUELIST", "Grandmaster Duelist", "Attain a competitive MMR of 1200 or higher.",
                        "Shield", com.devarena.achievement.model.AchievementCategory.BATTLE,
                        com.devarena.achievement.model.RequirementType.RATING_THRESHOLD, 1200, 750,
                        com.devarena.achievement.model.AchievementRarity.LEGENDARY, false
                )
        );
        achievementRepository.saveAll(list);
        log.info("Successfully seeded {} achievements.", list.size());
    }

    private void seedSkillsIfEmpty() {
        if (skillRepository.count() > 0) {
            return;
        }

        log.info("Seeding initial DevArena skills hierarchy...");

        // 1. Root skills (no prerequisites)
        com.devarena.skill.model.SkillEntity arrays = skillRepository.save(new com.devarena.skill.model.SkillEntity(
                "arrays", "Arrays & Strings", com.devarena.skill.model.SkillCategory.DATA_STRUCTURES,
                "Core contiguous memory sequences, sliding window, and string manipulation.", "Layers", 5, null, 1
        ));

        com.devarena.skill.model.SkillEntity searching = skillRepository.save(new com.devarena.skill.model.SkillEntity(
                "searching", "Binary Search & Pointers", com.devarena.skill.model.SkillCategory.ALGORITHMS,
                "Logarithmic divide-and-conquer search and two-pointer windowing strategies.", "Search", 5, null, 1
        ));

        com.devarena.skill.model.SkillEntity sql = skillRepository.save(new com.devarena.skill.model.SkillEntity(
                "sql-fundamentals", "SQL & Relational DB", com.devarena.skill.model.SkillCategory.DATABASE,
                "Structured queries, joins, aggregations, filtering, and normalization.", "Database", 5, null, 1
        ));

        com.devarena.skill.model.SkillEntity rest = skillRepository.save(new com.devarena.skill.model.SkillEntity(
                "rest-apis", "REST APIs & Protocols", com.devarena.skill.model.SkillCategory.WEB_DEVELOPMENT,
                "HTTP methods, status codes, payload structures, and client contracts.", "Globe", 5, null, 1
        ));

        // 2. Second-tier skills
        com.devarena.skill.model.SkillEntity hashMaps = skillRepository.save(new com.devarena.skill.model.SkillEntity(
                "hash-maps", "Hash Maps & Sets", com.devarena.skill.model.SkillCategory.DATA_STRUCTURES,
                "Constant-time lookup structures, collisions, and key-value mapping.", "Database", 5, arrays, 2
        ));

        com.devarena.skill.model.SkillEntity sorting = skillRepository.save(new com.devarena.skill.model.SkillEntity(
                "sorting", "Sorting & Merging", com.devarena.skill.model.SkillCategory.ALGORITHMS,
                "Comparison-based sorts, partition strategies, and fast divide routines.", "ArrowUpDown", 5, searching, 2
        ));

        // 3. Third-tier skills
        com.devarena.skill.model.SkillEntity linkedLists = skillRepository.save(new com.devarena.skill.model.SkillEntity(
                "linked-lists", "Linked Lists & Stacks", com.devarena.skill.model.SkillCategory.DATA_STRUCTURES,
                "Linear pointer-based nodes, LIFO stacks, and FIFO queues.", "GitBranch", 5, hashMaps, 3
        ));

        com.devarena.skill.model.SkillEntity trees = skillRepository.save(new com.devarena.skill.model.SkillEntity(
                "trees", "Binary Trees & BSTs", com.devarena.skill.model.SkillCategory.DATA_STRUCTURES,
                "Hierarchical nodes, recursive traversals, and balanced trees.", "Workflow", 5, hashMaps, 4
        ));

        com.devarena.skill.model.SkillEntity dp = skillRepository.save(new com.devarena.skill.model.SkillEntity(
                "dynamic-programming", "Dynamic Programming", com.devarena.skill.model.SkillCategory.ALGORITHMS,
                "Optimal substructure, overlapping subproblems, memoization and tabulation.", "Cpu", 5, sorting, 3
        ));

        // 4. Fourth-tier skills
        com.devarena.skill.model.SkillEntity graphs = skillRepository.save(new com.devarena.skill.model.SkillEntity(
                "graphs", "Graphs & Topologies", com.devarena.skill.model.SkillCategory.DATA_STRUCTURES,
                "Adjacency structures, BFS/DFS traversals, and shortest path algorithms.", "Share2", 5, trees, 5
        ));

        log.info("Successfully seeded 10 skills in the mastery matrix.");
    }

    /**
     * Seeds a rich set of Rising Brain DSA Sheet problems covering all problem types.
     * Only seeds titles that don't already exist — safe to run repeatedly.
     */
    private void seedRisingBrainProblemsIfNeeded() {
        // Only seed if total problems are less than 30 (we have ~20 initial)
        if (challengeRepository.count() >= 30) {
            return;
        }

        log.info("Seeding Rising Brain DSA Sheet problems into the complete problem archive...");

        List<ChallengeEntity> problems = new java.util.ArrayList<>();

        // ── TWO POINTERS ──────────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "Three Sum", "three-sum",
            "Given an integer array `nums`, return all the triplets `[nums[i], nums[j], nums[k]]` such that `i != j`, `i != k`, `j != k`, and `nums[i] + nums[j] + nums[k] == 0`.\n\nNotice that the solution set must not contain duplicate triplets.\n\n### Example:\n```\nInput: nums = [-1,0,1,2,-1,-4]\nOutput: [[-1,-1,2],[-1,0,1]]\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.TWO_POINTERS, ProblemType.CODING,
            150, 30, "two-pointers,array,sorting", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Move Zeroes", "move-zeroes",
            "Given an integer array `nums`, move all `0`s to the end of it while maintaining the relative order of the non-zero elements.\n\nNote that you must do this in-place without making a copy of the array.\n\n### Example:\n```\nInput: nums = [0,1,0,3,12]\nOutput: [1,3,12,0,0]\n```",
            ChallengeDifficulty.EASY, ChallengeCategory.TWO_POINTERS, ProblemType.CODING,
            60, 15, "two-pointers,array", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Squares of a Sorted Array", "squares-of-sorted-array",
            "Given an integer array `nums` sorted in non-decreasing order, return an array of the squares of each number sorted in non-decreasing order.\n\n### Example:\n```\nInput: nums = [-4,-1,0,3,10]\nOutput: [0,1,9,16,100]\n```",
            ChallengeDifficulty.EASY, ChallengeCategory.TWO_POINTERS, ProblemType.CODING,
            60, 15, "two-pointers,array,sorting", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        // ── SLIDING WINDOW ────────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "Maximum Average Subarray I", "maximum-average-subarray",
            "You are given an integer array `nums` consisting of `n` elements, and an integer `k`. Find a contiguous subarray whose length is equal to `k` that has the maximum average value and return this value.\n\n### Example:\n```\nInput: nums = [1,12,-5,-6,50,3], k = 4\nOutput: 12.75000\n```",
            ChallengeDifficulty.EASY, ChallengeCategory.SLIDING_WINDOW, ProblemType.CODING,
            70, 15, "sliding-window,array", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Minimum Size Subarray Sum", "minimum-size-subarray-sum",
            "Given an array of positive integers `nums` and a positive integer `target`, return the minimal length of a subarray whose sum is greater than or equal to `target`. If there is no such subarray, return `0` instead.\n\n### Example:\n```\nInput: target = 7, nums = [2,3,1,2,4,3]\nOutput: 2\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.SLIDING_WINDOW, ProblemType.CODING,
            140, 25, "sliding-window,array,binary-search", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Longest Repeating Character Replacement", "longest-repeating-character-replacement",
            "You are given a string `s` and an integer `k`. You can choose any character of the string and change it to any other uppercase English character. You can perform this operation at most `k` times.\n\nReturn the length of the longest substring containing the same letter you can get after performing the above operations.\n\n### Example:\n```\nInput: s = \"ABAB\", k = 2\nOutput: 4\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.SLIDING_WINDOW, ProblemType.CODING,
            160, 30, "sliding-window,string,hash-table", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        // ── BINARY SEARCH ─────────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "Search in Rotated Sorted Array", "search-in-rotated-sorted-array",
            "There is an integer array `nums` sorted in ascending order (with distinct values). Prior to being passed to your function, `nums` is possibly rotated at an unknown pivot index `k`.\n\nGiven the array `nums` after the possible rotation and an integer `target`, return the index of `target` if it is in nums, or `-1` if it is not in nums.\n\n### Example:\n```\nInput: nums = [4,5,6,7,0,1,2], target = 0\nOutput: 4\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.BINARY_SEARCH, ProblemType.CODING,
            150, 25, "binary-search,array", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Find Minimum in Rotated Sorted Array", "find-minimum-in-rotated-sorted-array",
            "Suppose an array of length `n` sorted in ascending order is rotated between 1 and `n` times. Given the sorted rotated array `nums` of unique elements, return the minimum element of this array.\n\n### Example:\n```\nInput: nums = [3,4,5,1,2]\nOutput: 1\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.BINARY_SEARCH, ProblemType.CODING,
            130, 20, "binary-search,array", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Koko Eating Bananas", "koko-eating-bananas",
            "Koko loves to eat bananas. There are `n` piles of bananas, the `i`th pile has `piles[i]` bananas. Koko can decide her bananas-per-hour eating speed of `k`. Each hour, she chooses some pile of bananas and eats `k` bananas from that pile.\n\nReturn the minimum integer `k` such that she can eat all the bananas within `h` hours.\n\n### Example:\n```\nInput: piles = [3,6,7,11], h = 8\nOutput: 4\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.BINARY_SEARCH, ProblemType.CODING,
            160, 30, "binary-search,array", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        // ── HEAPS / PRIORITY QUEUES ───────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "Kth Largest Element in an Array", "kth-largest-element-in-array",
            "Given an integer array `nums` and an integer `k`, return the `k`th largest element in the array.\n\nNote that it is the `k`th largest element in the sorted order, not the `k`th distinct element.\n\n### Example:\n```\nInput: nums = [3,2,1,5,6,4], k = 2\nOutput: 5\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.HEAPS, ProblemType.CODING,
            150, 25, "heap,priority-queue,sorting,quickselect", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Find Median from Data Stream", "find-median-from-data-stream",
            "The median is the middle value in an ordered integer list. If the size of the list is even, there is no middle value, and the median is the mean of the two middle values.\n\nImplement the `MedianFinder` class with `addNum` and `findMedian` methods.\n\n### Example:\n```\naddNum(1), addNum(2), findMedian() = 1.5\naddNum(3), findMedian() = 2.0\n```",
            ChallengeDifficulty.HARD, ChallengeCategory.HEAPS, ProblemType.CODING,
            280, 45, "heap,priority-queue,design,data-stream", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        // ── BACKTRACKING ──────────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "Permutations", "permutations",
            "Given an array `nums` of distinct integers, return all the possible permutations. You can return the answer in any order.\n\n### Example:\n```\nInput: nums = [1,2,3]\nOutput: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.BACKTRACKING, ProblemType.CODING,
            150, 30, "backtracking,recursion,array", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Subsets", "subsets",
            "Given an integer array `nums` of unique elements, return all possible subsets (the power set). The solution set must not contain duplicate subsets. Return the solution in any order.\n\n### Example:\n```\nInput: nums = [1,2,3]\nOutput: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.BACKTRACKING, ProblemType.CODING,
            140, 25, "backtracking,recursion,bit-manipulation", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "N-Queens", "n-queens",
            "The n-queens puzzle is the problem of placing `n` queens on an `n x n` chessboard such that no two queens attack each other.\n\nGiven an integer `n`, return all distinct solutions to the n-queens puzzle.\n\n### Example:\n```\nInput: n = 4\nOutput: [[\".Q..\",\"...Q\",\"Q...\",\"..Q.\"],\n         [\"..Q.\",\"Q...\",\"...Q\",\".Q..\"]]\n```",
            ChallengeDifficulty.HARD, ChallengeCategory.BACKTRACKING, ProblemType.CODING,
            300, 50, "backtracking,array,constraint-satisfaction", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        // ── GREEDY ────────────────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "Jump Game", "jump-game",
            "You are given an integer array `nums`. You are initially positioned at the array's first index, and each element in the array represents your maximum jump length at that position.\n\nReturn `true` if you can reach the last index, or `false` otherwise.\n\n### Example:\n```\nInput: nums = [2,3,1,1,4]\nOutput: true\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.GREEDY, ProblemType.CODING,
            130, 20, "greedy,array,dynamic-programming", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Meeting Rooms II", "meeting-rooms-ii",
            "Given an array of meeting time intervals `intervals` where `intervals[i] = [starti, endi]`, return the minimum number of conference rooms required.\n\n### Example:\n```\nInput: intervals = [[0,30],[5,10],[15,20]]\nOutput: 2\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.GREEDY, ProblemType.CODING,
            160, 30, "greedy,heap,sorting,intervals", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        // ── BIT MANIPULATION ─────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "Single Number", "single-number",
            "Given a non-empty array of integers `nums`, every element appears twice except for one. Find that single one.\n\nYou must implement a solution with a linear runtime complexity and use only constant extra space.\n\n### Example:\n```\nInput: nums = [4,1,2,1,2]\nOutput: 4\n```",
            ChallengeDifficulty.EASY, ChallengeCategory.BIT_MANIPULATION, ProblemType.CODING,
            60, 10, "bit-manipulation,xor,array", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Count Bits", "count-bits",
            "Given an integer `n`, return an array `ans` of length `n + 1` such that for each `i` (0 <= i <= n), `ans[i]` is the number of 1's in the binary representation of `i`.\n\n### Example:\n```\nInput: n = 5\nOutput: [0,1,1,2,1,2]\n```",
            ChallengeDifficulty.EASY, ChallengeCategory.BIT_MANIPULATION, ProblemType.CODING,
            70, 15, "bit-manipulation,dynamic-programming", "JAVA,PYTHON,JAVASCRIPT,CPP", "Rising Brain DSA Sheet"));

        // ── SQL / DATABASE ─────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "Find Customers Who Never Order", "customers-who-never-order",
            "Given a `Customers` table and an `Orders` table, find all customers who never ordered anything.\n\n### Schema:\n```sql\nCustomers: id INT, name VARCHAR\nOrders: id INT, customerId INT\n```\n\n### Expected Output:\nReturn the `name` column of customers with no matching orders.\n\n### Hint: Use LEFT JOIN or NOT IN subquery.",
            ChallengeDifficulty.EASY, ChallengeCategory.SQL_DB, ProblemType.SQL,
            60, 10, "sql,joins,subquery", "SQL", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Rank Scores", "rank-scores",
            "Write a SQL query to rank the scores. The ranking should be calculated according to the following rules: The scores should be ranked from the highest to the lowest. If there is a tie between two scores, both should have the same ranking. After a tie, the next ranking number should be the next consecutive integer value.\n\n### Schema:\n```sql\nScores: id INT, score DECIMAL\n```\n\n### Example Output:\n```\nscore | rank\n3.50  | 1\n3.65  | 1 ← Tied!\n4.00  | 2\n```",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.SQL_DB, ProblemType.SQL,
            130, 20, "sql,window-functions,rank,dense_rank", "SQL", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Department Top Three Salaries", "department-top-three-salaries",
            "Write a SQL query to find employees who have a high salary in each of the departments. A company's executives are interested in seeing who earns the most money in each of the departments. A high earner in a department is an employee who has a salary in the top three unique salaries for that department.\n\n### Schema:\n```sql\nEmployee: id, name, salary, departmentId\nDepartment: id, name\n```",
            ChallengeDifficulty.HARD, ChallengeCategory.SQL_DB, ProblemType.SQL,
            250, 40, "sql,window-functions,subquery,dense_rank", "SQL", "Rising Brain DSA Sheet"));

        // ── OPERATING SYSTEMS ─────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "What is a Deadlock?", "what-is-a-deadlock",
            "A deadlock in an operating system occurs when two or more processes are waiting for each other to release resources, and none of them can proceed.\n\n**Question:** Which of the following conditions is NOT necessary for a deadlock to occur?\n\nA) Mutual Exclusion\nB) Hold and Wait\nC) No Preemption\nD) Aging\n\nChoose the correct answer and explain the four Coffman conditions for deadlock.",
            ChallengeDifficulty.EASY, ChallengeCategory.OPERATING_SYSTEMS, ProblemType.MCQ,
            50, 10, "os,deadlock,concurrency,mcq", "N/A", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Process Scheduling: Round Robin", "round-robin-scheduling",
            "Consider 4 processes P1, P2, P3, P4 with burst times 6, 4, 2, 5 respectively. If a Round Robin scheduling algorithm with time quantum = 2 is used:\n\n1. Draw the Gantt chart.\n2. Calculate the average waiting time.\n3. Calculate the average turnaround time.\n\n**Show your step-by-step solution.**",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.OPERATING_SYSTEMS, ProblemType.APTITUDE,
            130, 20, "os,scheduling,round-robin,aptitude", "N/A", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Virtual Memory and Page Faults", "virtual-memory-page-faults",
            "A process references the following pages in order: 1, 2, 3, 4, 2, 1, 5, 6, 2, 1, 2, 3, 7, 6, 3, 2, 1, 2, 3, 6.\n\nWith a physical memory capacity of 3 frames and using the **LRU page replacement algorithm**:\n\n1. Count the total number of page faults.\n2. Show the state of frames after each reference.",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.OPERATING_SYSTEMS, ProblemType.APTITUDE,
            160, 30, "os,virtual-memory,page-replacement,lru,aptitude", "N/A", "Rising Brain DSA Sheet"));

        // ── NETWORKING ─────────────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "What Happens When You Type a URL?", "what-happens-when-you-type-url",
            "Describe in detail the complete sequence of events that occurs when a user types `https://www.google.com` in a browser and presses Enter.\n\nYour explanation should cover:\n- DNS resolution process\n- TCP connection (three-way handshake)\n- TLS/HTTPS handshake\n- HTTP request/response cycle\n- Browser rendering\n\nThis is a classic system design / networking interview question.",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.NETWORKING, ProblemType.INTERVIEW,
            120, 20, "networking,http,tcp,dns,tls,interview", "N/A", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "TCP vs UDP", "tcp-vs-udp",
            "**Multiple Choice:** Which of the following statements about TCP and UDP is FALSE?\n\nA) TCP is connection-oriented; UDP is connectionless.\nB) TCP guarantees delivery and ordering; UDP does not.\nC) UDP is used by DNS for initial queries because of lower overhead.\nD) TCP uses a two-way handshake to establish a connection.\n\nExplain the key differences between TCP and UDP and give real-world use cases for each.",
            ChallengeDifficulty.EASY, ChallengeCategory.NETWORKING, ProblemType.MCQ,
            50, 10, "networking,tcp,udp,protocols,mcq", "N/A", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Subnet Calculation", "subnet-calculation",
            "An organization is assigned the IP address block `192.168.10.0/24`.\n\nThey need to divide this into **5 subnets** where the largest subnet should accommodate at least **50 hosts**.\n\n1. What subnet mask should be used?\n2. How many subnets can be created?\n3. How many usable hosts per subnet?\n4. List the network address, first host, last host, and broadcast address for the first 3 subnets.",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.NETWORKING, ProblemType.APTITUDE,
            150, 25, "networking,subnetting,cidr,ip-addressing,aptitude", "N/A", "Rising Brain DSA Sheet"));

        // ── PUZZLES ────────────────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "8 Balls, One Heavy — Find It in 2 Weighings", "eight-balls-two-weighings",
            "You have 8 identical-looking balls. One of them is heavier than the rest. You have a balance scale and are allowed only **2 weighings**.\n\n**Question:** How do you determine which ball is the heaviest in exactly 2 weighings?\n\nDescribe your strategy step by step.",
            ChallengeDifficulty.EASY, ChallengeCategory.PUZZLES, ProblemType.PUZZLE,
            70, 15, "puzzle,logic,divide-and-conquer,reasoning", "N/A", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Water Jug Problem", "water-jug-problem",
            "You have a 3-liter jug and a 5-liter jug. You need to measure exactly **4 liters** of water.\n\nYou can:\n- Fill either jug completely from a tap\n- Empty either jug\n- Pour water from one jug into the other\n\n**Question:** Describe the sequence of steps to obtain exactly 4 liters.",
            ChallengeDifficulty.EASY, ChallengeCategory.PUZZLES, ProblemType.PUZZLE,
            60, 10, "puzzle,logic,state-machine,reasoning", "N/A", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Egg Drop Problem", "egg-drop-problem",
            "You are given `n` floors and `k` eggs. You need to determine the **minimum number of trials** needed to find the critical floor (the highest floor from which an egg can be dropped without breaking).\n\n### Classic Case:\n- `n = 100` floors, `k = 2` eggs\n\n**Question:** What is the minimum number of trials in the worst case? Derive the formula.\n\n### Extended:\nSolve the general case using dynamic programming.",
            ChallengeDifficulty.HARD, ChallengeCategory.PUZZLES, ProblemType.PUZZLE,
            280, 45, "puzzle,dynamic-programming,binary-search,reasoning", "JAVA,PYTHON,JAVASCRIPT", "Rising Brain DSA Sheet"));

        // ── INTERVIEW PROBLEMS ────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "Design a URL Shortener", "design-url-shortener",
            "Design a URL shortening service like bit.ly.\n\n**Requirements:**\n- Given a long URL, generate a short unique URL (e.g., bit.ly/abc123)\n- Redirect users from short URL to original URL\n- Handle 100M URLs, 10:1 read:write ratio\n- Short URLs should expire after 1 year\n\n**Discuss:**\n1. API design\n2. Database schema\n3. Hashing algorithm (Base62 encoding)\n4. Scalability and caching strategy\n5. Analytics (click tracking)",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.INTERVIEW, ProblemType.INTERVIEW,
            200, 40, "system-design,hashing,scalability,caching,interview", "N/A", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "OOP: Design a Parking Lot", "design-parking-lot",
            "Design an object-oriented parking lot system.\n\n**Requirements:**\n- Multiple floors with different spot sizes: SMALL, MEDIUM, LARGE\n- Vehicles: Motorcycle, Car, Bus\n- Park and unpark vehicles\n- Track available spots\n- Generate a ticket on parking\n\n**Implement using clean OOP principles:**\n- Identify all classes, interfaces, and relationships\n- Implement core methods: `park()`, `unpark()`, `getAvailableSpots()`",
            ChallengeDifficulty.MEDIUM, ChallengeCategory.INTERVIEW, ProblemType.INTERVIEW,
            200, 40, "oop,system-design,low-level-design,interview", "JAVA,PYTHON", "Rising Brain DSA Sheet"));

        // ── APTITUDE ──────────────────────────────────────────────────────────
        addIfNew(problems, new ChallengeEntity(
            "Train Speed and Time", "train-speed-time",
            "A train traveling at 60 km/h crosses a 200-metre-long bridge in 30 seconds.\n\n**Question:** What is the length of the train?\n\n**Hint:** Speed = Distance / Time. The distance the train covers is its own length plus the bridge length.",
            ChallengeDifficulty.EASY, ChallengeCategory.APTITUDE, ProblemType.APTITUDE,
            40, 5, "aptitude,speed-time-distance,reasoning", "N/A", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Probability: Dice Throw", "probability-dice-throw",
            "Two fair dice are thrown simultaneously.\n\n**Questions:**\n1. What is the probability that the sum of the two dice equals 7?\n2. What is the probability of getting at least one 6?\n3. What is the probability that both dice show even numbers?\n\n**Show your work using sample space enumeration.**",
            ChallengeDifficulty.EASY, ChallengeCategory.APTITUDE, ProblemType.APTITUDE,
            50, 10, "aptitude,probability,combinatorics,reasoning", "N/A", "Rising Brain DSA Sheet"));

        addIfNew(problems, new ChallengeEntity(
            "Work and Time Problem", "work-and-time",
            "A can complete a task in 12 days. B can complete the same task in 18 days.\n\n**Questions:**\n1. How many days will A and B together take to complete the task?\n2. If they work together for 4 days, how much of the task is completed?\n3. If A leaves after 4 days, how many more days will B need to finish the remaining work?",
            ChallengeDifficulty.EASY, ChallengeCategory.APTITUDE, ProblemType.APTITUDE,
            40, 5, "aptitude,work-time,fractions,reasoning", "N/A", "Rising Brain DSA Sheet"));

        // Save all new problems
        if (!problems.isEmpty()) {
            challengeRepository.saveAll(problems);
            log.info("Rising Brain DSA Sheet: seeded {} new problems into the archive.", problems.size());
        } else {
            log.info("Rising Brain DSA Sheet: all problems already exist, nothing to seed.");
        }
    }

    private void addIfNew(List<ChallengeEntity> list, ChallengeEntity entity) {
        if (!challengeRepository.existsByTitle(entity.getTitle()) &&
            !challengeRepository.existsBySlug(entity.getSlug())) {
            list.add(entity);
        }
    }
}


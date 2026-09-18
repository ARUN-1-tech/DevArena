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
    private final ChallengeDataSeeder challengeDataSeeder;
    private final RisingBrainDatasetSeeder risingBrainDatasetSeeder;
    private final com.devarena.achievement.repository.AchievementRepository achievementRepository;
    private final com.devarena.skill.repository.SkillRepository skillRepository;

    public DataInitializer(
            ChallengeRepository challengeRepository,
            DailyQuestRepository dailyQuestRepository,
            com.devarena.user.repository.UserRepository userRepository,
            com.devarena.user.service.AuthService authService,
            ChallengeDataSeeder challengeDataSeeder,
            RisingBrainDatasetSeeder risingBrainDatasetSeeder,
            com.devarena.achievement.repository.AchievementRepository achievementRepository,
            com.devarena.skill.repository.SkillRepository skillRepository) {
        this.challengeRepository = challengeRepository;
        this.dailyQuestRepository = dailyQuestRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.challengeDataSeeder = challengeDataSeeder;
        this.risingBrainDatasetSeeder = risingBrainDatasetSeeder;
        this.achievementRepository = achievementRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public void run(String... args) {
        try { seedChallengesIfEmpty(); } catch (Exception e) { log.warn("seedChallenges error: {}", e.getMessage()); }
        try { risingBrainDatasetSeeder.seedRisingBrainDatasetIfMissing(); } catch (Exception e) { log.warn("risingBrainSeeder error: {}", e.getMessage()); }
        try { challengeDataSeeder.seedStarterCodesAndTestCasesIfEmpty(); } catch (Exception e) { log.warn("challengeDataSeeder error: {}", e.getMessage()); }
        try { seedDailyQuestsIfEmpty(); } catch (Exception e) { log.warn("seedDailyQuests error: {}", e.getMessage()); }
        try { seedDefaultUserIfEmpty(); } catch (Exception e) { log.warn("seedDefaultUser error: {}", e.getMessage()); }
        try { seedAdminUserIfEmpty(); } catch (Exception e) { log.warn("seedAdminUser error: {}", e.getMessage()); }
        try { seedAchievementsIfEmpty(); } catch (Exception e) { log.warn("seedAchievements error: {}", e.getMessage()); }
        try { seedSkillsIfEmpty(); } catch (Exception e) { log.warn("seedSkills error: {}", e.getMessage()); }
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
}

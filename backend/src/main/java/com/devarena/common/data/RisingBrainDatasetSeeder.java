package com.devarena.common.data;

import com.devarena.challenge.model.*;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.challenge.repository.ChallengeStarterCodeRepository;
import com.devarena.challenge.repository.ChallengeTestCaseRepository;
import com.devarena.execution.model.ExecutionLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class RisingBrainDatasetSeeder {

    private static final Logger log = LoggerFactory.getLogger(RisingBrainDatasetSeeder.class);

    private final ChallengeRepository challengeRepository;
    private final ChallengeStarterCodeRepository starterCodeRepository;
    private final ChallengeTestCaseRepository testCaseRepository;

    public RisingBrainDatasetSeeder(
            ChallengeRepository challengeRepository,
            ChallengeStarterCodeRepository starterCodeRepository,
            ChallengeTestCaseRepository testCaseRepository) {
        this.challengeRepository = challengeRepository;
        this.starterCodeRepository = starterCodeRepository;
        this.testCaseRepository = testCaseRepository;
    }

    @Transactional
    public void seedRisingBrainDatasetIfMissing() {
        long currentCount = challengeRepository.count();
        if (currentCount >= 100) {
            log.info("Problem Archive already seeded with {} problems. Skipping full dataset initialization.", currentCount);
            return;
        }

        log.info("Seeding Rising Brain Complete Problem Archive (Target: 110+ classic DSA, SQL, OS, & Aptitude challenges)...");

        List<ChallengeEntity> dataset = createCompleteProblemDataset();
        int seededCount = 0;

        for (ChallengeEntity challenge : dataset) {
            if (!challengeRepository.existsBySlugIgnoreCase(challenge.getSlug())) {
                ChallengeEntity saved = challengeRepository.save(challenge);
                seedChallengeAssets(saved);
                seededCount++;
            }
        }

        log.info("Successfully seeded {} new problems. Total available challenges: {}", seededCount, challengeRepository.count());
    }

    private void seedChallengeAssets(ChallengeEntity c) {
        com.devarena.common.data.catalog.ChallengeProblemDef def = com.devarena.common.data.catalog.ChallengeCatalogRegistry.getProblem(c);

        // Provide starter templates for all 3 supported languages
        starterCodeRepository.save(new ChallengeStarterCodeEntity(c, ExecutionLanguage.JAVA, def.javaStarter().trim()));
        starterCodeRepository.save(new ChallengeStarterCodeEntity(c, ExecutionLanguage.PYTHON, def.pythonStarter().trim()));
        starterCodeRepository.save(new ChallengeStarterCodeEntity(c, ExecutionLanguage.JAVASCRIPT, def.jsStarter().trim()));

        // Provide sample & hidden test cases
        for (com.devarena.common.data.catalog.TestCaseDef tc : def.testCases()) {
            testCaseRepository.save(new ChallengeTestCaseEntity(c, tc.input(), tc.expectedOutput(), tc.hidden(), tc.orderIndex(), tc.explanation()));
        }
    }

    private List<ChallengeEntity> createCompleteProblemDataset() {
        List<ChallengeEntity> list = new ArrayList<>();

        // ==========================================
        // 1. ARRAYS & HASHING
        // ==========================================
        list.add(new ChallengeEntity("Contains Duplicate", "contains-duplicate",
                "Given an integer array `nums`, return `true` if any value appears at least twice in the array, and return `false` if every element is distinct.\n\n### Example 1:\n```\nInput: nums = [1,2,3,1]\nOutput: true\n```",
                ChallengeDifficulty.EASY, ChallengeCategory.ARRAYS, 40, 10, "array,hash-table"));

        list.add(new ChallengeEntity("Best Time to Buy and Sell Stock", "best-time-to-buy-and-sell-stock",
                "You are given an array `prices` where `prices[i]` is the price of a given stock on the `i`th day. You want to maximize your profit by choosing a single day to buy one stock and choosing a different day in the future to sell that stock.\n\n### Example 1:\n```\nInput: prices = [7,1,5,3,6,4]\nOutput: 5\n```",
                ChallengeDifficulty.EASY, ChallengeCategory.ARRAYS, 50, 15, "array,dynamic-programming"));

        list.add(new ChallengeEntity("Product of Array Except Self", "product-of-array-except-self",
                "Given an integer array `nums`, return an array `answer` such that `answer[i]` is equal to the product of all the elements of `nums` except `nums[i]`.\n\nYou must write an algorithm that runs in `O(n)` time and without using the division operation.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 110, 20, "array,prefix-sum"));

        list.add(new ChallengeEntity("Maximum Subarray", "maximum-subarray",
                "Given an integer array `nums`, find the subarray with the largest sum, and return its sum (Kadane's Algorithm).\n\n### Example 1:\n```\nInput: nums = [-2,1,-3,4,-1,2,1,-5,4]\nOutput: 6\n```",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 100, 20, "array,divide-and-conquer,dynamic-programming"));

        list.add(new ChallengeEntity("3Sum", "3sum",
                "Given an integer array nums, return all the triplets `[nums[i], nums[j], nums[k]]` such that `i != j`, `i != k`, and `j != k`, and `nums[i] + nums[j] + nums[k] == 0`.\n\nNotice that the solution set must not contain duplicate triplets.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 130, 25, "array,two-pointers,sorting"));

        list.add(new ChallengeEntity("Trapping Rain Water", "trapping-rain-water",
                "Given `n` non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.",
                ChallengeDifficulty.HARD, ChallengeCategory.ARRAYS, 200, 35, "array,two-pointers,dynamic-programming,stack"));

        list.add(new ChallengeEntity("Merge Intervals", "merge-intervals",
                "Given an array of `intervals` where `intervals[i] = [starti, endi]`, merge all overlapping intervals, and return an array of the non-overlapping intervals that cover all the intervals in the input.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 120, 25, "array,sorting"));

        list.add(new ChallengeEntity("Insert Interval", "insert-interval",
                "You are given an array of non-overlapping intervals `intervals` sorted in ascending order by `starti`. Insert `newInterval` into `intervals` such that `intervals` is still sorted and non-overlapping.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 125, 25, "array"));

        list.add(new ChallengeEntity("Set Matrix Zeroes", "set-matrix-zeroes",
                "Given an `m x n` integer matrix `matrix`, if an element is 0, set its entire row and column to 0's. You must do it in place.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 115, 25, "array,matrix"));

        list.add(new ChallengeEntity("Spiral Matrix", "spiral-matrix",
                "Given an `m x n` matrix, return all elements of the matrix in spiral order.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 110, 20, "array,matrix,simulation"));

        list.add(new ChallengeEntity("Rotate Image", "rotate-image",
                "You are given an `n x n` 2D matrix representing an image, rotate the image by 90 degrees (clockwise) in-place.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 120, 20, "array,math,matrix"));

        list.add(new ChallengeEntity("Subarray Sum Equals K", "subarray-sum-equals-k",
                "Given an array of integers `nums` and an integer `k`, return the total number of subarrays whose sum equals to `k`.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 130, 25, "array,hash-table,prefix-sum"));

        list.add(new ChallengeEntity("Longest Consecutive Sequence", "longest-consecutive-sequence",
                "Given an unsorted array of integers `nums`, return the length of the longest consecutive elements sequence in O(n) time.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 135, 25, "array,hash-table,union-find"));

        list.add(new ChallengeEntity("Find All Numbers Disappeared in an Array", "find-all-numbers-disappeared-in-an-array",
                "Given an array `nums` of `n` integers where `nums[i]` is in the range `[1, n]`, return an array of all the integers in the range `[1, n]` that do not appear in `nums`.",
                ChallengeDifficulty.EASY, ChallengeCategory.ARRAYS, 55, 15, "array,hash-table"));

        // ==========================================
        // 2. STRINGS & TWO POINTERS
        // ==========================================
        list.add(new ChallengeEntity("Valid Anagram", "valid-anagram",
                "Given two strings `s` and `t`, return `true` if `t` is an anagram of `s`, and `false` otherwise.\n\n### Example:\n```\nInput: s = \"anagram\", t = \"nagaram\"\nOutput: true\n```",
                ChallengeDifficulty.EASY, ChallengeCategory.STRINGS, 45, 10, "string,hash-table,sorting"));

        list.add(new ChallengeEntity("Group Anagrams", "group-anagrams",
                "Given an array of strings `strs`, group the anagrams together. You can return the answer in any order.\n\n### Example:\n```\nInput: strs = [\"eat\",\"tea\",\"tan\",\"ate\",\"nat\",\"bat\"]\nOutput: [[\"bat\"],[\"nat\",\"tan\"],[\"ate\",\"eat\",\"tea\"]]\n```",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.STRINGS, 110, 20, "string,hash-table,sorting"));

        list.add(new ChallengeEntity("Longest Repeating Character Replacement", "longest-repeating-character-replacement",
                "You are given a string `s` and an integer `k`. You can choose any character of the string and change it to any other uppercase English character at most `k` times. Return the length of the longest substring containing the same letter you can get after performing the above operations.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.STRINGS, 140, 25, "string,sliding-window,hash-table"));

        list.add(new ChallengeEntity("Longest Palindromic Substring", "longest-palindromic-substring",
                "Given a string `s`, return the longest palindromic substring in `s`.\n\n### Example:\n```\nInput: s = \"babad\"\nOutput: \"bab\"\n```",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.STRINGS, 130, 25, "string,dynamic-programming,two-pointers"));

        list.add(new ChallengeEntity("Palindromic Substrings", "palindromic-substrings",
                "Given a string `s`, return the number of palindromic substrings in it.\n\nA string is a palindrome when it reads the same backward as forward.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.STRINGS, 120, 20, "string,dynamic-programming,two-pointers"));

        list.add(new ChallengeEntity("Minimum Window Substring", "minimum-window-substring",
                "Given two strings `s` and `t` of lengths `m` and `n` respectively, return the minimum window substring of `s` such that every character in `t` (including duplicates) is included in the window.",
                ChallengeDifficulty.HARD, ChallengeCategory.STRINGS, 210, 35, "string,sliding-window,hash-table"));

        list.add(new ChallengeEntity("String to Integer (atoi)", "string-to-integer-atoi",
                "Implement the `myAtoi(string s)` function, which converts a string to a 32-bit signed integer (similar to C/C++'s `atoi` function).",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.STRINGS, 100, 20, "string"));

        list.add(new ChallengeEntity("Longest Common Prefix", "longest-common-prefix",
                "Write a function to find the longest common prefix string amongst an array of strings. If there is no common prefix, return an empty string `\"\"`.",
                ChallengeDifficulty.EASY, ChallengeCategory.STRINGS, 45, 10, "string,trie"));

        list.add(new ChallengeEntity("Reverse Words in a String", "reverse-words-in-a-string",
                "Given an input string `s`, reverse the order of the words. A word is defined as a sequence of non-space characters. The words in `s` will be separated by at least one space.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.STRINGS, 95, 15, "string,two-pointers"));

        // ==========================================
        // 3. TWO POINTERS & SLIDING WINDOW
        // ==========================================
        list.add(new ChallengeEntity("Two Sum II - Input Array Is Sorted", "two-sum-ii-input-array-is-sorted",
                "Given a 1-indexed array of integers `numbers` that is already sorted in non-decreasing order, find two numbers such that they add up to a specific `target` number.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 90, 15, "array,two-pointers,binary-search"));

        list.add(new ChallengeEntity("3Sum Closest", "3sum-closest",
                "Given an integer array `nums` of length `n` and an integer `target`, find three integers in `nums` such that the sum is closest to `target`. Return the sum of the three integers.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 115, 20, "array,two-pointers,sorting"));

        list.add(new ChallengeEntity("4Sum", "4sum",
                "Given an array `nums` of `n` integers, return an array of all the unique quadruplets `[nums[a], nums[b], nums[c], nums[d]]` such that `a, b, c, and d` are distinct and sum to `target`.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 140, 30, "array,two-pointers,sorting"));

        list.add(new ChallengeEntity("Sliding Window Maximum", "sliding-window-maximum",
                "You are given an array of integers `nums`, there is a sliding window of size `k` which is moving from the very left of the array to the very right. Return the max sliding window.",
                ChallengeDifficulty.HARD, ChallengeCategory.STACK_QUEUE, 220, 35, "array,queue,sliding-window,heap,monotonic-queue"));

        list.add(new ChallengeEntity("Minimum Size Subarray Sum", "minimum-size-subarray-sum",
                "Given an array of positive integers `nums` and a positive integer `target`, return the minimal length of a subarray whose sum is greater than or equal to `target`. If there is no such subarray, return 0.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.ARRAYS, 110, 20, "array,binary-search,sliding-window,prefix-sum"));

        // ==========================================
        // 4. LINKED LISTS
        // ==========================================
        list.add(new ChallengeEntity("Merge Two Sorted Lists", "merge-two-sorted-lists",
                "You are given the heads of two sorted linked lists `list1` and `list2`. Merge the two lists into one sorted list. The list should be made by splicing together the nodes of the first two lists.",
                ChallengeDifficulty.EASY, ChallengeCategory.LINKED_LIST, 50, 15, "linked-list,recursion"));

        list.add(new ChallengeEntity("Linked List Cycle", "linked-list-cycle",
                "Given `head`, the head of a linked list, determine if the linked list has a cycle in it using Floyd's Tortoise and Hare algorithm.",
                ChallengeDifficulty.EASY, ChallengeCategory.LINKED_LIST, 55, 10, "linked-list,two-pointers"));

        list.add(new ChallengeEntity("Linked List Cycle II", "linked-list-cycle-ii",
                "Given the `head` of a linked list, return the node where the cycle begins. If there is no cycle, return `null`.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.LINKED_LIST, 110, 20, "linked-list,two-pointers"));

        list.add(new ChallengeEntity("Remove Nth Node From End of List", "remove-nth-node-from-end-of-list",
                "Given the `head` of a linked list, remove the `n`th node from the end of the list and return its head in one pass.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.LINKED_LIST, 100, 20, "linked-list,two-pointers"));

        list.add(new ChallengeEntity("Reorder List", "reorder-list",
                "You are given the head of a singly linked-list: `L0 → L1 → … → Ln - 1 → Ln`. Reorder the list to be: `L0 → Ln → L1 → Ln - 1 → L2 → Ln - 2 → …` in-place.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.LINKED_LIST, 125, 25, "linked-list,two-pointers,stack"));

        list.add(new ChallengeEntity("Merge k Sorted Lists", "merge-k-sorted-lists",
                "You are given an array of `k` linked-lists lists, each linked-list is sorted in ascending order. Merge all the linked-lists into one sorted linked-list and return it.",
                ChallengeDifficulty.HARD, ChallengeCategory.LINKED_LIST, 230, 35, "linked-list,divide-and-conquer,heap"));

        list.add(new ChallengeEntity("Add Two Numbers", "add-two-numbers",
                "You are given two non-empty linked lists representing two non-negative integers. The digits are stored in reverse order, and each of their nodes contains a single digit. Add the two numbers and return the sum as a linked list.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.LINKED_LIST, 105, 20, "linked-list,math,recursion"));

        list.add(new ChallengeEntity("Palindrome Linked List", "palindrome-linked-list",
                "Given the `head` of a singly linked list, return `true` if it is a palindrome or `false` otherwise in O(n) time and O(1) space.",
                ChallengeDifficulty.EASY, ChallengeCategory.LINKED_LIST, 60, 15, "linked-list,two-pointers,stack"));

        list.add(new ChallengeEntity("Intersection of Two Linked Lists", "intersection-of-two-linked-lists",
                "Given the heads of two singly linked-lists `headA` and `headB`, return the node at which the two lists intersect. If the two linked lists have no intersection at all, return `null`.",
                ChallengeDifficulty.EASY, ChallengeCategory.LINKED_LIST, 65, 15, "linked-list,two-pointers,hash-table"));

        // ==========================================
        // 5. STACKS & QUEUES
        // ==========================================
        list.add(new ChallengeEntity("Min Stack", "min-stack",
                "Design a stack that supports push, pop, top, and retrieving the minimum element in constant time `O(1)`.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.STACK_QUEUE, 95, 15, "stack,design"));

        list.add(new ChallengeEntity("Evaluate Reverse Polish Notation", "evaluate-reverse-polish-notation",
                "You are given an array of strings `tokens` that represents an arithmetic expression in a Reverse Polish Notation. Evaluate the expression and return an integer that represents the evaluation of the expression.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.STACK_QUEUE, 100, 20, "array,math,stack"));

        list.add(new ChallengeEntity("Daily Temperatures", "daily-temperatures",
                "Given an array of integers `temperatures` represents the daily temperatures, return an array `answer` such that `answer[i]` is the number of days you have to wait after the `i`th day to get a warmer temperature.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.STACK_QUEUE, 115, 20, "array,stack,monotonic-stack"));

        list.add(new ChallengeEntity("Largest Rectangle in Histogram", "largest-rectangle-in-histogram",
                "Given an array of integers `heights` representing the histogram's bar height where the width of each bar is 1, return the area of the largest rectangle in the histogram.",
                ChallengeDifficulty.HARD, ChallengeCategory.STACK_QUEUE, 240, 40, "array,stack,monotonic-stack"));

        list.add(new ChallengeEntity("Implement Queue using Stacks", "implement-queue-using-stacks",
                "Implement a first in first out (FIFO) queue using only two stacks. The implemented queue should support all the functions of a normal queue (`push`, `peek`, `pop`, and `empty`).",
                ChallengeDifficulty.EASY, ChallengeCategory.STACK_QUEUE, 50, 10, "stack,design,queue"));

        list.add(new ChallengeEntity("Basic Calculator", "basic-calculator",
                "Given a string `s` representing a valid expression, implement a basic calculator to evaluate it, and return the result of the evaluation. `s` consists of digits, `'+'`, `'-'`, `'('`, `')'`, and `' '`.",
                ChallengeDifficulty.HARD, ChallengeCategory.STACK_QUEUE, 220, 35, "math,string,stack,recursion"));

        list.add(new ChallengeEntity("Asteroid Collision", "asteroid-collision",
                "We are given an array `asteroids` of integers representing asteroids in a row. Find out the state of the asteroids after all collisions.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.STACK_QUEUE, 110, 20, "array,stack,simulation"));

        list.add(new ChallengeEntity("Next Greater Element I", "next-greater-element-i",
                "The next greater element of some element `x` in an array is the first greater element that is to the right of `x` in the same array. Return an array `ans` of length `nums1.length` such that `ans[i]` is the next greater element as described above.",
                ChallengeDifficulty.EASY, ChallengeCategory.STACK_QUEUE, 60, 15, "array,hash-table,stack,monotonic-stack"));

        list.add(new ChallengeEntity("Online Stock Span", "online-stock-span",
                "Design an algorithm that collects daily price quotes for some stock and returns the span of that stock's price for the current day.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.STACK_QUEUE, 120, 20, "stack,design,monotonic-stack,data-stream"));

        // ==========================================
        // 6. TREES & BINARY SEARCH TREES
        // ==========================================
        list.add(new ChallengeEntity("Same Tree", "same-tree",
                "Given the roots of two binary trees `p` and `q`, write a function to check if they are the same or not.",
                ChallengeDifficulty.EASY, ChallengeCategory.TREES, 45, 10, "tree,depth-first-search,breadth-first-search,binary-tree"));

        list.add(new ChallengeEntity("Invert Binary Tree", "invert-binary-tree",
                "Given the `root` of a binary tree, invert the tree, and return its root.",
                ChallengeDifficulty.EASY, ChallengeCategory.TREES, 45, 10, "tree,depth-first-search,breadth-first-search,binary-tree"));

        list.add(new ChallengeEntity("Binary Tree Maximum Path Sum", "binary-tree-maximum-path-sum",
                "A path in a binary tree is a sequence of nodes where each pair of adjacent nodes in the sequence has an edge connecting them. Return the maximum path sum of any non-empty path.",
                ChallengeDifficulty.HARD, ChallengeCategory.TREES, 220, 35, "dynamic-programming,tree,depth-first-search,binary-tree"));

        list.add(new ChallengeEntity("Binary Tree Level Order Traversal", "binary-tree-level-order-traversal",
                "Given the `root` of a binary tree, return the level order traversal of its nodes' values. (i.e., from left to right, level by level).",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.TREES, 105, 20, "tree,breadth-first-search,binary-tree"));

        list.add(new ChallengeEntity("Validate Binary Search Tree", "validate-binary-search-tree",
                "Given the `root` of a binary tree, determine if it is a valid binary search tree (BST).",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.TREES, 115, 20, "tree,depth-first-search,binary-search-tree,binary-tree"));

        list.add(new ChallengeEntity("Kth Smallest Element in a BST", "kth-smallest-element-in-a-bst",
                "Given the `root` of a binary search tree, and an integer `k`, return the `k`th smallest value (1-indexed) of all the values of the nodes in the tree.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.TREES, 110, 20, "tree,depth-first-search,binary-search-tree,binary-tree"));

        list.add(new ChallengeEntity("Lowest Common Ancestor of a BST", "lowest-common-ancestor-of-a-bst",
                "Given a binary search tree (BST), find the lowest common ancestor (LCA) node of two given nodes in the BST.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.TREES, 100, 15, "tree,depth-first-search,binary-search-tree,binary-tree"));

        list.add(new ChallengeEntity("Diameter of Binary Tree", "diameter-of-binary-tree",
                "Given the `root` of a binary tree, return the length of the diameter of the tree. The diameter of a binary tree is the length of the longest path between any two nodes in a tree.",
                ChallengeDifficulty.EASY, ChallengeCategory.TREES, 60, 15, "tree,depth-first-search,binary-tree"));

        list.add(new ChallengeEntity("Balanced Binary Tree", "balanced-binary-tree",
                "Given a binary tree, determine if it is height-balanced (a binary tree in which the left and right subtrees of every node differ in height by no more than 1).",
                ChallengeDifficulty.EASY, ChallengeCategory.TREES, 55, 15, "tree,depth-first-search,binary-tree"));

        list.add(new ChallengeEntity("Construct Binary Tree from Preorder and Inorder Traversal", "construct-binary-tree-from-preorder-and-inorder-traversal",
                "Given two integer arrays `preorder` and `inorder` where `preorder` is the preorder traversal of a binary tree and `inorder` is the inorder traversal of the same tree, construct and return the binary tree.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.TREES, 135, 25, "array,hash-table,divide-and-conquer,tree,binary-tree"));

        list.add(new ChallengeEntity("Serialize and Deserialize Binary Tree", "serialize-and-deserialize-binary-tree",
                "Design an algorithm to serialize and deserialize a binary tree into a string format and reconstruct the tree from string.",
                ChallengeDifficulty.HARD, ChallengeCategory.TREES, 240, 40, "string,tree,depth-first-search,breadth-first-search,design,binary-tree"));

        // ==========================================
        // 7. GRAPHS & DISJOINT SET
        // ==========================================
        list.add(new ChallengeEntity("Clone Graph", "clone-graph",
                "Given a reference of a node in a connected undirected graph. Return a deep copy (clone) of the graph.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.GRAPHS, 120, 20, "hash-table,depth-first-search,breadth-first-search,graph"));

        list.add(new ChallengeEntity("Pacific Atlantic Water Flow", "pacific-atlantic-water-flow",
                "There is an `m x n` rectangular island that borders both the Pacific Ocean and Atlantic Ocean. Return a 2D list of grid coordinates where rain water can flow to both oceans.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.GRAPHS, 140, 25, "array,depth-first-search,breadth-first-search,matrix"));

        list.add(new ChallengeEntity("Course Schedule", "course-schedule",
                "There are a total of `numCourses` courses you have to take, labeled from `0` to `numCourses - 1`. You are given an array `prerequisites` where `prerequisites[i] = [ai, bi]`. Return `true` if you can finish all courses.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.GRAPHS, 135, 25, "depth-first-search,breadth-first-search,graph,topological-sort"));

        list.add(new ChallengeEntity("Course Schedule II", "course-schedule-ii",
                "Return the ordering of courses you should take to finish all courses. If there are many valid answers, return any of them. If it is impossible to finish all courses, return an empty array.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.GRAPHS, 145, 25, "depth-first-search,breadth-first-search,graph,topological-sort"));

        list.add(new ChallengeEntity("Number of Connected Components in an Undirected Graph", "number-of-connected-components-in-an-undirected-graph",
                "You have a graph of `n` nodes. You are given an integer `n` and an array `edges` where `edges[i] = [ai, bi]`. Return the number of connected components in the graph.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.GRAPHS, 115, 20, "depth-first-search,breadth-first-search,union-find,graph"));

        list.add(new ChallengeEntity("Graph Valid Tree", "graph-valid-tree",
                "Given `n` nodes labeled from `0` to `n - 1` and a list of undirected edges, write a function to check whether these edges make up a valid tree.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.GRAPHS, 125, 20, "depth-first-search,breadth-first-search,union-find,graph"));

        list.add(new ChallengeEntity("Word Ladder", "word-ladder",
                "A transformation sequence from word `beginWord` to word `endWord` using a dictionary `wordList` is a sequence of words `beginWord -> s1 -> s2 -> ... -> sk` such that every adjacent pair of words differs by a single letter. Return the number of words in the shortest transformation sequence.",
                ChallengeDifficulty.HARD, ChallengeCategory.GRAPHS, 230, 35, "hash-table,string,breadth-first-search"));

        list.add(new ChallengeEntity("Rotting Oranges", "rotting-oranges",
                "You are given an `m x n` grid where each cell can have one of three values: 0 (empty), 1 (fresh orange), 2 (rotten orange). Return the minimum number of minutes that must elapse until no cell has a fresh orange.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.GRAPHS, 110, 20, "array,breadth-first-search,matrix"));

        list.add(new ChallengeEntity("Network Delay Time", "network-delay-time",
                "You are given a network of `n` nodes, labeled from `1` to `n`. You are also given `times`, a list of travel times as directed edges. Return the minimum time it takes for all the `n` nodes to receive the signal using Dijkstra's Algorithm.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.GRAPHS, 150, 30, "depth-first-search,breadth-first-search,graph,heap,shortest-path"));

        // ==========================================
        // 8. DYNAMIC PROGRAMMING
        // ==========================================
        list.add(new ChallengeEntity("Longest Increasing Subsequence", "longest-increasing-subsequence",
                "Given an integer array `nums`, return the length of the longest strictly increasing subsequence in O(n log n) time.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 130, 25, "array,binary-search,dynamic-programming"));

        list.add(new ChallengeEntity("Longest Common Subsequence", "longest-common-subsequence",
                "Given two strings `text1` and `text2`, return the length of their longest common subsequence. If there is no common subsequence, return 0.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 125, 25, "string,dynamic-programming"));

        list.add(new ChallengeEntity("Word Break", "word-break",
                "Given a string `s` and a dictionary of strings `wordDict`, return `true` if `s` can be segmented into a space-separated sequence of one or more dictionary words.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 135, 25, "array,hash-table,string,dynamic-programming,trie,memoization"));

        list.add(new ChallengeEntity("Combination Sum IV", "combination-sum-iv",
                "Given an array of distinct integers `nums` and a target integer `target`, return the number of possible combinations that add up to `target`.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 120, 20, "array,dynamic-programming,memoization"));

        list.add(new ChallengeEntity("House Robber", "house-robber",
                "You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed. Determine the maximum amount of money you can rob tonight without alerting the police.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 100, 20, "array,dynamic-programming"));

        list.add(new ChallengeEntity("House Robber II", "house-robber-ii",
                "You are a professional robber planning to rob houses along a street where all houses at this place are arranged in a circle. Return the maximum amount of money you can rob tonight without alerting the police.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 120, 20, "array,dynamic-programming"));

        list.add(new ChallengeEntity("Decode Ways", "decode-ways",
                "A message containing letters from A-Z can be encoded into numbers using 'A' -> \"1\", 'B' -> \"2\", ... 'Z' -> \"26\". Given a string `s` containing only digits, return the number of ways to decode it.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 130, 25, "string,dynamic-programming"));

        list.add(new ChallengeEntity("Unique Paths", "unique-paths",
                "There is a robot on an `m x n` grid. The robot can only move either down or right at any point in time. Return the number of possible unique paths that the robot can take to reach the bottom-right corner.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 100, 15, "math,dynamic-programming,combinatorics"));

        list.add(new ChallengeEntity("Jump Game", "jump-game",
                "You are given an integer array `nums`. You are initially positioned at the array's first index, and each element in the array represents your maximum jump length at that position. Return `true` if you can reach the last index.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 110, 20, "array,dynamic-programming,greedy"));

        list.add(new ChallengeEntity("Jump Game II", "jump-game-ii",
                "Return the minimum number of jumps to reach `nums[n - 1]`. The test cases are generated such that you can reach `nums[n - 1]`.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 125, 20, "array,dynamic-programming,greedy"));

        list.add(new ChallengeEntity("Partition Equal Subset Sum", "partition-equal-subset-sum",
                "Given an integer array `nums`, return `true` if you can partition the array into two subsets such that the sum of the elements in both subsets is equal or `false` otherwise.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 135, 25, "array,dynamic-programming"));

        list.add(new ChallengeEntity("Edit Distance", "edit-distance",
                "Given two strings `word1` and `word2`, return the minimum number of operations required to convert `word1` to `word2` (insert, delete, replace character).",
                ChallengeDifficulty.HARD, ChallengeCategory.DYNAMIC_PROGRAMMING, 220, 35, "string,dynamic-programming"));

        list.add(new ChallengeEntity("Maximal Square", "maximal-square",
                "Given an `m x n` binary matrix filled with 0's and 1's, find the largest square containing only 1's and return its area.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DYNAMIC_PROGRAMMING, 140, 25, "array,dynamic-programming,matrix"));

        // ==========================================
        // 9. BINARY SEARCH
        // ==========================================
        list.add(new ChallengeEntity("Search in Rotated Sorted Array", "search-in-rotated-sorted-array",
                "Given the array `nums` after the possible rotation and an integer `target`, return the index of `target` if it is in `nums`, or `-1` if it is not in `nums` in O(log n) runtime.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BINARY_SEARCH, 115, 20, "array,binary-search"));

        list.add(new ChallengeEntity("Find Minimum in Rotated Sorted Array", "find-minimum-in-rotated-sorted-array",
                "Given the sorted rotated array `nums` of unique elements, return the minimum element of this array in O(log n) time.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BINARY_SEARCH, 110, 15, "array,binary-search"));

        list.add(new ChallengeEntity("Search a 2D Matrix", "search-a-2d-matrix",
                "You are given an `m x n` integer matrix `matrix` with the two properties: each row is sorted, and the first integer of each row is greater than the last integer of the previous row. Return `true` if `target` is in `matrix` in O(log(m * n)).",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BINARY_SEARCH, 95, 15, "array,binary-search,matrix"));

        list.add(new ChallengeEntity("Koko Eating Bananas", "koko-eating-bananas",
                "Koko loves to eat bananas. There are `n` piles of bananas. Return the minimum integer `k` such that she can eat all the bananas within `h` hours.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BINARY_SEARCH, 130, 25, "array,binary-search"));

        list.add(new ChallengeEntity("Capacity To Ship Packages Within D Days", "capacity-to-ship-packages-within-d-days",
                "Return the least weight capacity of the ship that will result in all the packages on the conveyor belt being shipped within `days` days.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BINARY_SEARCH, 135, 25, "array,binary-search"));

        list.add(new ChallengeEntity("Median of Two Sorted Arrays", "median-of-two-sorted-arrays",
                "Given two sorted arrays `nums1` and `nums2` of size `m` and `n` respectively, return the median of the two sorted arrays in O(log (m+n)) runtime.",
                ChallengeDifficulty.HARD, ChallengeCategory.BINARY_SEARCH, 250, 45, "array,binary-search,divide-and-conquer"));

        list.add(new ChallengeEntity("Find First and Last Position of Element in Sorted Array", "find-first-and-last-position-of-element-in-sorted-array",
                "Given an array of integers `nums` sorted in non-decreasing order, find the starting and ending position of a given `target` value in O(log n) time.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BINARY_SEARCH, 100, 15, "array,binary-search"));

        // ==========================================
        // 10. BACKTRACKING & RECURSION
        // ==========================================
        list.add(new ChallengeEntity("Subsets", "subsets",
                "Given an integer array `nums` of unique elements, return all possible subsets (the power set). The solution set must not contain duplicate subsets.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BACKTRACKING, 110, 20, "array,backtracking,bit-manipulation"));

        list.add(new ChallengeEntity("Subsets II", "subsets-ii",
                "Given an integer array `nums` that may contain duplicates, return all possible subsets (the power set). The solution set must not contain duplicate subsets.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BACKTRACKING, 120, 20, "array,backtracking,bit-manipulation"));

        list.add(new ChallengeEntity("Permutations", "permutations",
                "Given an array `nums` of distinct integers, return all the possible permutations. You can return the answer in any order.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BACKTRACKING, 110, 20, "array,backtracking"));

        list.add(new ChallengeEntity("Combination Sum", "combination-sum",
                "Given an array of distinct integers `candidates` and a target integer `target`, return a list of all unique combinations of `candidates` where the chosen numbers sum to `target`.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BACKTRACKING, 125, 25, "array,backtracking"));

        list.add(new ChallengeEntity("Palindrome Partitioning", "palindrome-partitioning",
                "Given a string `s`, partition `s` such that every substring of the partition is a palindrome. Return all possible palindrome partitioning of `s`.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BACKTRACKING, 130, 25, "string,dynamic-programming,backtracking"));

        list.add(new ChallengeEntity("Letter Combinations of a Phone Number", "letter-combinations-of-a-phone-number",
                "Given a string containing digits from 2-9 inclusive, return all possible letter combinations that the number could represent according to telephone keypad mapping.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BACKTRACKING, 100, 15, "hash-table,string,backtracking"));

        list.add(new ChallengeEntity("N-Queens", "n-queens",
                "The n-queens puzzle is the problem of placing `n` queens on an `n x n` chessboard such that no two queens attack each other. Return all distinct solutions to the n-queens puzzle.",
                ChallengeDifficulty.HARD, ChallengeCategory.BACKTRACKING, 240, 40, "array,backtracking"));

        list.add(new ChallengeEntity("Word Search", "word-search",
                "Given an `m x n` grid of characters `board` and a string `word`, return `true` if `word` exists in the grid.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.BACKTRACKING, 130, 25, "array,string,backtracking,matrix"));

        // ==========================================
        // 11. HEAPS & PRIORITY QUEUES
        // ==========================================
        list.add(new ChallengeEntity("Kth Largest Element in an Array", "kth-largest-element-in-an-array",
                "Given an integer array `nums` and an integer `k`, return the `k`th largest element in the array in O(n) average time (QuickSelect or Min-Heap).",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.HEAPS_PRIORITY_QUEUES, 110, 20, "array,divide-and-conquer,sorting,heap,quickselect"));

        list.add(new ChallengeEntity("Top K Frequent Elements", "top-k-frequent-elements",
                "Given an integer array `nums` and an integer `k`, return the `k` most frequent elements in O(n log k) time.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.HEAPS_PRIORITY_QUEUES, 115, 20, "array,hash-table,divide-and-conquer,sorting,heap,bucket-sort,counting,quickselect"));

        list.add(new ChallengeEntity("Find Median from Data Stream", "find-median-from-data-stream",
                "The median is the middle value in an ordered integer list. Design a data structure that supports adding integer numbers from the data stream and finding the median in O(1) or O(log n).",
                ChallengeDifficulty.HARD, ChallengeCategory.HEAPS_PRIORITY_QUEUES, 240, 35, "two-pointers,design,sorting,heap,data-stream"));

        list.add(new ChallengeEntity("Task Scheduler", "task-scheduler",
                "Given a characters array `tasks`, representing the tasks a CPU needs to do, and a cooling interval `n`, return the least number of units of times that the CPU will take to finish all the given tasks.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.HEAPS_PRIORITY_QUEUES, 130, 25, "array,hash-table,greedy,sorting,heap,counting"));

        list.add(new ChallengeEntity("K Closest Points to Origin", "k-closest-points-to-origin",
                "Given an array of `points` where `points[i] = [xi, yi]` represents a point on the X-Y plane and an integer `k`, return the `k` closest points to the origin `(0, 0)`.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.HEAPS_PRIORITY_QUEUES, 100, 20, "array,math,divide-and-conquer,geometry,sorting,heap,quickselect"));

        // ==========================================
        // 12. BIT MANIPULATION & MATH
        // ==========================================
        list.add(new ChallengeEntity("Single Number", "single-number",
                "Given a non-empty array of integers `nums`, every element appears twice except for one. Find that single one using XOR bitwise operator in O(n) time and O(1) space.",
                ChallengeDifficulty.EASY, ChallengeCategory.BIT_MANIPULATION, 45, 10, "array,bit-manipulation"));

        list.add(new ChallengeEntity("Number of 1 Bits", "number-of-1-bits",
                "Write a function that takes the binary representation of a positive integer and returns the number of set bits it has (also known as the Hamming weight).",
                ChallengeDifficulty.EASY, ChallengeCategory.BIT_MANIPULATION, 40, 10, "divide-and-conquer,bit-manipulation"));

        list.add(new ChallengeEntity("Counting Bits", "counting-bits",
                "Given an integer `n`, return an array `ans` of length `n + 1` such that for each `i` (`0 <= i <= n`), `ans[i]` is the number of `1`'s in the binary representation of `i`.",
                ChallengeDifficulty.EASY, ChallengeCategory.BIT_MANIPULATION, 50, 15, "dynamic-programming,bit-manipulation"));

        list.add(new ChallengeEntity("Reverse Bits", "reverse-bits",
                "Reverse bits of a given 32 bits unsigned integer.",
                ChallengeDifficulty.EASY, ChallengeCategory.BIT_MANIPULATION, 50, 10, "divide-and-conquer,bit-manipulation"));

        list.add(new ChallengeEntity("Missing Number", "missing-number",
                "Given an array `nums` containing `n` distinct numbers in the range `[0, n]`, return the only number in the range that is missing from the array.",
                ChallengeDifficulty.EASY, ChallengeCategory.BIT_MANIPULATION, 45, 10, "array,hash-table,math,binary-search,bit-manipulation,sorting"));

        // ==========================================
        // 13. GREEDY & INTERVALS
        // ==========================================
        list.add(new ChallengeEntity("Non-overlapping Intervals", "non-overlapping-intervals",
                "Given an array of intervals `intervals` where `intervals[i] = [starti, endi]`, return the minimum number of intervals you need to remove to make the rest of the intervals non-overlapping.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.GREEDY, 130, 20, "array,dynamic-programming,greedy,sorting"));

        list.add(new ChallengeEntity("Gas Station", "gas-station",
                "There are `n` gas stations along a circular route. Return the starting gas station's index if you can travel around the circuit once in the clockwise direction, otherwise return `-1`.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.GREEDY, 135, 25, "array,greedy"));

        // ==========================================
        // 14. SQL, DBMS, OS, NETWORKING & APTITUDE
        // ==========================================
        ChallengeEntity sql1 = new ChallengeEntity("Department Highest Salary", "department-highest-salary",
                "Write an SQL query to find employees who have the highest salary in each of the departments.\n\n### Tables:\n- `Employee (id, name, salary, departmentId)`\n- `Department (id, name)`\n\nReturn the result table with Department, Employee, and Salary columns.",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.SQL, ProblemType.SQL, 80, 15, "sql,database,group-by");
        sql1.setCorrectAnswer("SELECT d.name AS Department, e.name AS Employee, e.salary AS Salary FROM Employee e JOIN Department d ON e.departmentId = d.id WHERE (e.departmentId, e.salary) IN (SELECT departmentId, MAX(salary) FROM Employee GROUP BY departmentId)");
        list.add(sql1);

        ChallengeEntity sql2 = new ChallengeEntity("Second Highest Salary", "second-highest-salary",
                "Write an SQL query to report the second highest salary from the `Employee` table. If there is no second highest salary, the query should report `null`.",
                ChallengeDifficulty.EASY, ChallengeCategory.SQL, ProblemType.SQL, 60, 10, "sql,database,subquery");
        sql2.setCorrectAnswer("SELECT (SELECT DISTINCT salary FROM Employee ORDER BY salary DESC LIMIT 1 OFFSET 1) AS SecondHighestSalary");
        list.add(sql2);

        ChallengeEntity mcq1 = new ChallengeEntity("ACID Properties: Isolation Level Anomalies", "acid-isolation-levels",
                "Which transaction isolation level prevents Dirty Reads and Non-Repeatable Reads, but may still be subject to Phantom Reads in standard ANSI SQL?",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.DBMS, ProblemType.MCQ, 70, 10, "dbms,transactions,concurrency");
        mcq1.setOptions("[\"READ UNCOMMITTED\", \"READ COMMITTED\", \"REPEATABLE READ\", \"SERIALIZABLE\"]");
        mcq1.setCorrectAnswer("REPEATABLE READ");
        mcq1.setSolutionApproach("REPEATABLE READ locks rows accessed during reading to prevent non-repeatable reads, but new rows matching a range query (phantoms) can still be inserted unless range locking/SERIALIZABLE is used.");
        list.add(mcq1);

        ChallengeEntity mcq2 = new ChallengeEntity("OS Deadlock: Necessary Conditions", "os-deadlock-conditions",
                "Which of the following is NOT one of Coffman's four necessary conditions for deadlock in operating systems?",
                ChallengeDifficulty.EASY, ChallengeCategory.OPERATING_SYSTEMS, ProblemType.MCQ, 50, 10, "os,deadlock,concurrency");
        mcq2.setOptions("[\"Mutual Exclusion\", \"Hold and Wait\", \"Preemption Allowed\", \"Circular Wait\"]");
        mcq2.setCorrectAnswer("Preemption Allowed");
        mcq2.setSolutionApproach("The four Coffman conditions are: Mutual Exclusion, Hold and Wait, No Preemption, and Circular Wait. 'Preemption Allowed' prevents deadlocks.");
        list.add(mcq2);

        ChallengeEntity mcq3 = new ChallengeEntity("TCP 3-Way Handshake Flags Sequence", "tcp-handshake-flags",
                "In what exact sequence are TCP control flags exchanged between client and server during connection establishment?",
                ChallengeDifficulty.EASY, ChallengeCategory.NETWORKING, ProblemType.MCQ, 50, 10, "networking,tcp,protocols");
        mcq3.setOptions("[\"SYN -> SYN-ACK -> ACK\", \"SYN -> ACK -> SYN-ACK\", \"ACK -> SYN -> ACK\", \"SYN-ACK -> SYN -> ACK\"]");
        mcq3.setCorrectAnswer("SYN -> SYN-ACK -> ACK");
        mcq3.setSolutionApproach("Client sends SYN, server responds with SYN + ACK, and client concludes the handshake with ACK.");
        list.add(mcq3);

        ChallengeEntity apt1 = new ChallengeEntity("The 25 Horses Puzzle", "the-25-horses-puzzle",
                "You have 25 horses and can race 5 horses at a time. There is no stopwatch (you only know relative positions in each race). What is the minimum number of races needed to find the top 3 fastest horses?",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.PUZZLES, ProblemType.PUZZLE, 90, 15, "puzzles,interview,logic");
        apt1.setOptions("[\"5 races\", \"6 races\", \"7 races\", \"8 races\"]");
        apt1.setCorrectAnswer("7 races");
        apt1.setSolutionApproach("Step 1: 5 group races (25 horses). Step 2: 1 race of the winners (race #6) to identify the absolute fastest. Step 3: 1 race with the 5 candidate horses for 2nd and 3rd place (race #7). Total = 7 races.");
        list.add(apt1);

        ChallengeEntity apt2 = new ChallengeEntity("Monty Hall Probability Paradox", "monty-hall-paradox",
                "In a game show, you choose 1 of 3 doors (one has a car, two have goats). The host opens another door showing a goat and gives you the option to switch. What is your probability of winning if you switch?",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.MATH_APTITUDE, ProblemType.APTITUDE, 75, 10, "math,probability,aptitude");
        apt2.setOptions("[\"1/3 (33.3%)\", \"1/2 (50.0%)\", \"2/3 (66.7%)\", \"3/4 (75.0%)\"]");
        apt2.setCorrectAnswer("2/3 (66.7%)");
        apt2.setSolutionApproach("Your initial choice had a 1/3 chance of winning. The remaining 2/3 probability concentrates on the single remaining closed door when the host eliminates a goat door.");
        list.add(apt2);

        ChallengeEntity int1 = new ChallengeEntity("CAP Theorem in Distributed Systems", "cap-theorem-tradeoffs",
                "According to Brewer's CAP Theorem, in the presence of a Network Partition (P), which trade-off must a distributed datastore make?",
                ChallengeDifficulty.MEDIUM, ChallengeCategory.SYSTEM_DESIGN, ProblemType.INTERVIEW, 95, 15, "system-design,distributed-systems");
        int1.setOptions("[\"Consistency vs Availability (CP vs AP)\", \"Latency vs Throughput\", \"Security vs Scalability\", \"Durability vs Atomicity\"]");
        int1.setCorrectAnswer("Consistency vs Availability (CP vs AP)");
        int1.setSolutionApproach("Since network partitions are inevitable in real-world distributed networks, a system must choose between returning consistent latest data or always being available to respond.");
        list.add(int1);

        return list;
    }
}

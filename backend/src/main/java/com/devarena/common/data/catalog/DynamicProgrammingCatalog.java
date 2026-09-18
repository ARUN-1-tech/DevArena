package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicProgrammingCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Climbing Stairs
        map.put("climbing-stairs", new ChallengeProblemDef(
                "climbing-stairs",
                """
import java.util.*;

public class Solution {
    public static int climbStairs(int n) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        System.out.println(climbStairs(n));
    }
}
""",
                """
import sys

def climb_stairs(n: int) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    raw = sys.stdin.read().strip()
    if raw:
        print(climb_stairs(int(raw)))
""",
                """
const fs = require('fs');

function climbStairs(n) {
    // Write your solution here
    return null;
}

const raw = fs.readFileSync(0, 'utf-8').trim();
if (raw) {
    console.log(climbStairs(parseInt(raw, 10)));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2", "2", "2 steps: 1+1 or 2."),
                        TestCaseDef.publicCase(2, "3", "3", "3 steps: 1+1+1, 1+2, 2+1."),
                        TestCaseDef.hiddenCase(3, "4", "5", "4 steps: 5 ways."),
                        TestCaseDef.hiddenCase(4, "5", "8", "5 steps: 8 ways.")
                )
        ));

        // 2. Coin Change
        map.put("coin-change", new ChallengeProblemDef(
                "coin-change",
                """
import java.util.*;

public class Solution {
    public static int coinChange(int[] coins, int amount) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int amount = Integer.parseInt(sc.nextLine().trim());
        String[] parts = line1.split(",");
        int[] coins = new int[parts.length];
        for (int i = 0; i < parts.length; i++) coins[i] = Integer.parseInt(parts[i].trim());
        System.out.println(coinChange(coins, amount));
    }
}
""",
                """
import sys

def coin_change(coins: list[int], amount: int) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        coins = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        amount = int(lines[1].strip())
        print(coin_change(coins, amount))
""",
                """
const fs = require('fs');

function coinChange(coins, amount) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const coins = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const amount = parseInt(lines[1].trim(), 10);
    console.log(coinChange(coins, amount));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,5\n11", "3", "11 = 5 + 5 + 1 (3 coins)."),
                        TestCaseDef.publicCase(2, "2\n3", "-1", "Cannot make 3 from coins of value 2."),
                        TestCaseDef.hiddenCase(3, "1\n0", "0", "Amount 0 requires 0 coins."),
                        TestCaseDef.hiddenCase(4, "186,419,83,408\n6249", "20", "Optimal combination for large amount is 20 coins.")
                )
        ));

        // 3. Longest Increasing Subsequence
        map.put("longest-increasing-subsequence", new ChallengeProblemDef(
                "longest-increasing-subsequence",
                """
import java.util.*;

public class Solution {
    public static int lengthOfLIS(int[] nums) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(lengthOfLIS(nums));
    }
}
""",
                """
import sys
import bisect

def length_of_lis(nums: list[int]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(length_of_lis(nums))
""",
                """
const fs = require('fs');

function lengthOfLIS(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(lengthOfLIS(nums));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "10,9,2,5,3,7,101,18", "4", "The longest increasing subsequence is [2, 3, 7, 101], length 4."),
                        TestCaseDef.publicCase(2, "0,1,0,3,2,3", "4", "Longest increasing subsequence length 4."),
                        TestCaseDef.hiddenCase(3, "7,7,7,7,7,7,7", "1", "All identical elements has LIS of 1.")
                )
        ));

        // 4. Longest Common Subsequence
        map.put("longest-common-subsequence", new ChallengeProblemDef(
                "longest-common-subsequence",
                """
import java.util.*;

public class Solution {
    public static int longestCommonSubsequence(String text1, String text2) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String t1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String t2 = sc.nextLine().trim();
        System.out.println(longestCommonSubsequence(t1, t2));
    }
}
""",
                """
import sys

def longest_common_subsequence(text1: str, text2: str) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(longest_common_subsequence(lines[0].strip(), lines[1].strip()))
""",
                """
const fs = require('fs');

function longestCommonSubsequence(text1, text2) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    console.log(longestCommonSubsequence(lines[0].trim(), lines[1].trim()));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "abcde\nace", "3", "LCS is 'ace' with length 3."),
                        TestCaseDef.publicCase(2, "abc\nabc", "3", "LCS is 'abc'."),
                        TestCaseDef.hiddenCase(3, "abc\ndef", "0", "No common subsequence.")
                )
        ));

        // 5. Word Break
        map.put("word-break", new ChallengeProblemDef(
                "word-break",
                """
import java.util.*;

public class Solution {
    public static boolean wordBreak(String s, List<String> wordDict) {
        // Write your solution here
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String s = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String line2 = sc.nextLine().trim();
        List<String> words = Arrays.asList(line2.split(","));
        System.out.println(wordBreak(s, words));
    }
}
""",
                """
import sys

def word_break(s: str, word_dict: list[str]) -> bool:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        s = lines[0].strip()
        words = [x.strip() for x in lines[1].split(",")]
        print(str(word_break(s, words)).lower())
""",
                """
const fs = require('fs');

function wordBreak(s, wordDict) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    console.log(wordBreak(lines[0].trim(), lines[1].split(',').map(s => s.trim())));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "leetcode\nleet,code", "true", "'leetcode' can be segmented as 'leet code'."),
                        TestCaseDef.publicCase(2, "applepenapple\napple,pen", "true", "'applepenapple' can be segmented as 'apple pen apple'."),
                        TestCaseDef.hiddenCase(3, "catsandog\ncats,dog,sand,and,cat", "false", "Cannot be segmented completely.")
                )
        ));

        // 6. House Robber
        map.put("house-robber", new ChallengeProblemDef(
                "house-robber",
                """
import java.util.*;

public class Solution {
    public static int rob(int[] nums) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(rob(nums));
    }
}
""",
                """
import sys

def rob(nums: list[int]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(rob(nums))
""",
                """
const fs = require('fs');

function rob(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(rob(nums));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3,1", "4", "Rob house 1 (1) and house 3 (3), total = 4."),
                        TestCaseDef.publicCase(2, "2,7,9,3,1", "12", "Rob house 1 (2), house 3 (9), house 5 (1), total = 12."),
                        TestCaseDef.hiddenCase(3, "2,1,1,2", "4", "Rob houses at ends, total = 4.")
                )
        ));

        // 7. House Robber II
        map.put("house-robber-ii", new ChallengeProblemDef(
                "house-robber-ii",
                """
import java.util.*;

public class Solution {
    public static int rob(int[] nums) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(rob(nums));
    }
}
""",
                """
import sys

def rob(nums: list[int]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(rob(nums))
""",
                """
const fs = require('fs');

function rob(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(rob(nums));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2,3,2", "3", "Houses in circle: cannot rob house 1 and 3 together, max = 3."),
                        TestCaseDef.publicCase(2, "1,2,3,1", "4", "Rob house 2 (2) and house 4 (1) or house 1 (1) and house 3 (3)."),
                        TestCaseDef.hiddenCase(3, "1,2,3", "3", "Max rob is 3.")
                )
        ));

        // 8. Unique Paths
        map.put("unique-paths", new ChallengeProblemDef(
                "unique-paths",
                """
import java.util.*;

public class Solution {
    public static int uniquePaths(int m, int n) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        int m = Integer.parseInt(sc.nextLine().trim());
        if (!sc.hasNextLine()) return;
        int n = Integer.parseInt(sc.nextLine().trim());
        System.out.println(uniquePaths(m, n));
    }
}
""",
                """
import sys

def unique_paths(m: int, n: int) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(unique_paths(int(lines[0].strip()), int(lines[1].strip())))
""",
                """
const fs = require('fs');

function uniquePaths(m, n) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    console.log(uniquePaths(parseInt(lines[0].trim(), 10), parseInt(lines[1].trim(), 10)));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "3\n7", "28", "3x7 grid has 28 distinct paths."),
                        TestCaseDef.publicCase(2, "3\n2", "3", "3x2 grid has 3 distinct paths."),
                        TestCaseDef.hiddenCase(3, "3\n3", "6", "3x3 grid has 6 distinct paths.")
                )
        ));

        // 9. Jump Game
        map.put("jump-game", new ChallengeProblemDef(
                "jump-game",
                """
import java.util.*;

public class Solution {
    public static boolean canJump(int[] nums) {
        // Write your solution here
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(canJump(nums));
    }
}
""",
                """
import sys

def can_jump(nums: list[int]) -> bool:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(can_jump(nums)).lower())
""",
                """
const fs = require('fs');

function canJump(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(canJump(nums));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2,3,1,1,4", "true", "Jump 1 step from index 0 to 1, then 3 steps to the last index."),
                        TestCaseDef.publicCase(2, "3,2,1,0,4", "false", "Will always arrive at index 3 where maximum jump length is 0."),
                        TestCaseDef.hiddenCase(3, "0", "true", "Single element array starts at last index.")
                )
        ));

        // 10. Edit Distance
        map.put("edit-distance", new ChallengeProblemDef(
                "edit-distance",
                """
import java.util.*;

public class Solution {
    public static int minDistance(String word1, String word2) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String w1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String w2 = sc.nextLine().trim();
        System.out.println(minDistance(w1, w2));
    }
}
""",
                """
import sys

def min_distance(word1: str, word2: str) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(min_distance(lines[0].strip(), lines[1].strip()))
""",
                """
const fs = require('fs');

function minDistance(word1, word2) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    console.log(minDistance(lines[0].trim(), lines[1].trim()));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "horse\nros", "3", "horse -> rorse -> rose -> ros (3 operations)."),
                        TestCaseDef.publicCase(2, "intention\nexecution", "5", "5 edit operations.")
                )
        ));

        // 11. Burst Balloons
        map.put("burst-balloons", new ChallengeProblemDef(
                "burst-balloons",
                """
import java.util.*;

public class Solution {
    public static int maxCoins(int[] nums) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(maxCoins(nums));
    }
}
""",
                """
import sys

def max_coins(nums: list[int]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(max_coins(nums))
""",
                """
const fs = require('fs');

function maxCoins(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(maxCoins(nums));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "3,1,5,8", "167", "Max coins is 167."),
                        TestCaseDef.publicCase(2, "1,5", "10", "Max coins is 10."),
                        TestCaseDef.hiddenCase(3, "7", "7", "Single balloon gives 1*7*1 = 7.")
                )
        ));

        return map;
    }
}

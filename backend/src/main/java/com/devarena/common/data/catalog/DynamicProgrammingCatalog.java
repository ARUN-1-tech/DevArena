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
        if (n <= 2) return n;
        int a = 1, b = 2;
        for (int i = 3; i <= n; i++) {
            int c = a + b;
            a = b;
            b = c;
        }
        return b;
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
    if n <= 2:
        return n
    a, b = 1, 2
    for _ in range(3, n + 1):
        a, b = b, a + b
    return b

if __name__ == "__main__":
    raw = sys.stdin.read().strip()
    if raw:
        print(climb_stairs(int(raw)))
""",
                """
const fs = require('fs');

function climbStairs(n) {
    if (n <= 2) return n;
    let a = 1, b = 2;
    for (let i = 3; i <= n; i++) {
        const c = a + b;
        a = b;
        b = c;
    }
    return b;
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
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (i >= coin) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
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
    dp = [float('inf')] * (amount + 1)
    dp[0] = 0
    for i in range(1, amount + 1):
        for c in coins:
            if i >= c:
                dp[i] = min(dp[i], dp[i - c] + 1)
    return dp[amount] if dp[amount] != float('inf') else -1

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
    const dp = new Array(amount + 1).fill(Infinity);
    dp[0] = 0;
    for (let i = 1; i <= amount; i++) {
        for (const coin of coins) {
            if (i >= coin) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
        }
    }
    return dp[amount] === Infinity ? -1 : dp[amount];
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
        List<Integer> piles = new ArrayList<>();
        for (int num : nums) {
            int idx = Collections.binarySearch(piles, num);
            if (idx < 0) idx = -(idx + 1);
            if (idx == piles.size()) piles.add(num);
            else piles.set(idx, num);
        }
        return piles.size();
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
    piles = []
    for n in nums:
        idx = bisect.bisect_left(piles, n)
        if idx == len(piles):
            piles.append(n)
        else:
            piles[idx] = n
    return len(piles)

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(length_of_lis(nums))
""",
                """
const fs = require('fs');

function lengthOfLIS(nums) {
    const piles = [];
    for (const num of nums) {
        let l = 0, r = piles.length;
        while (l < r) {
            const mid = Math.floor((l + r) / 2);
            if (piles[mid] < num) l = mid + 1;
            else r = mid;
        }
        if (l === piles.length) piles.push(num);
        else piles[l] = num;
    }
    return piles.length;
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
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
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
    m, n = len(text1), len(text2)
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if text1[i - 1] == text2[j - 1]:
                dp[i][j] = dp[i - 1][j - 1] + 1
            else:
                dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])
    return dp[m][n]

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(longest_common_subsequence(lines[0].strip(), lines[1].strip()))
""",
                """
const fs = require('fs');

function longestCommonSubsequence(text1, text2) {
    const m = text1.length, n = text2.length;
    const dp = Array.from({ length: m + 1 }, () => new Array(n + 1).fill(0));
    for (let i = 1; i <= m; i++) {
        for (let j = 1; j <= n; j++) {
            if (text1[i - 1] === text2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1] + 1;
            } else {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
    }
    return dp[m][n];
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
        Set<String> set = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && set.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[s.length()];
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
    word_set = set(word_dict)
    dp = [False] * (len(s) + 1)
    dp[0] = True
    for i in range(1, len(s) + 1):
        for j in range(i):
            if dp[j] and s[j:i] in word_set:
                dp[i] = True
                break
    return dp[len(s)]

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
    const set = new Set(wordDict);
    const dp = new Array(s.length + 1).fill(false);
    dp[0] = true;
    for (let i = 1; i <= s.length; i++) {
        for (let j = 0; j < i; j++) {
            if (dp[j] && set.has(s.substring(j, i))) {
                dp[i] = true;
                break;
            }
        }
    }
    return dp[s.length];
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
        if (nums == null || nums.length == 0) return 0;
        int prev1 = 0, prev2 = 0;
        for (int num : nums) {
            int tmp = prev1;
            prev1 = Math.max(prev2 + num, prev1);
            prev2 = tmp;
        }
        return prev1;
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
    rob1, rob2 = 0, 0
    for n in nums:
        temp = max(n + rob1, rob2)
        rob1 = rob2
        rob2 = temp
    return rob2

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(rob(nums))
""",
                """
const fs = require('fs');

function rob(nums) {
    let rob1 = 0, rob2 = 0;
    for (const n of nums) {
        const temp = Math.max(n + rob1, rob2);
        rob1 = rob2;
        rob2 = temp;
    }
    return rob2;
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
        if (nums.length == 1) return nums[0];
        return Math.max(robRange(nums, 0, nums.length - 2), robRange(nums, 1, nums.length - 1));
    }

    private static int robRange(int[] nums, int start, int end) {
        int rob1 = 0, rob2 = 0;
        for (int i = start; i <= end; i++) {
            int temp = Math.max(nums[i] + rob1, rob2);
            rob1 = rob2;
            rob2 = temp;
        }
        return rob2;
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
    if len(nums) == 1:
        return nums[0]
    def rob_range(start, end):
        rob1, rob2 = 0, 0
        for i in range(start, end + 1):
            temp = max(nums[i] + rob1, rob2)
            rob1 = rob2
            rob2 = temp
        return rob2
    return max(rob_range(0, len(nums) - 2), rob_range(1, len(nums) - 1))

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(rob(nums))
""",
                """
const fs = require('fs');

function rob(nums) {
    if (nums.length === 1) return nums[0];
    function robRange(start, end) {
        let rob1 = 0, rob2 = 0;
        for (let i = start; i <= end; i++) {
            const temp = Math.max(nums[i] + rob1, rob2);
            rob1 = rob2;
            rob2 = temp;
        }
        return rob2;
    }
    return Math.max(robRange(0, nums.length - 2), robRange(1, nums.length - 1));
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
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] += dp[j - 1];
            }
        }
        return dp[n - 1];
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
    row = [1] * n
    for _ in range(m - 1):
        for j in range(1, n):
            row[j] += row[j - 1]
    return row[-1]

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(unique_paths(int(lines[0].strip()), int(lines[1].strip())))
""",
                """
const fs = require('fs');

function uniquePaths(m, n) {
    const dp = new Array(n).fill(1);
    for (let i = 1; i < m; i++) {
        for (let j = 1; j < n; j++) {
            dp[j] += dp[j - 1];
        }
    }
    return dp[n - 1];
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
        int reachable = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > reachable) return false;
            reachable = Math.max(reachable, i + nums[i]);
        }
        return true;
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
    reachable = 0
    for i, n in enumerate(nums):
        if i > reachable:
            return False
        reachable = max(reachable, i + n)
    return True

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(can_jump(nums)).lower())
""",
                """
const fs = require('fs');

function canJump(nums) {
    let reachable = 0;
    for (let i = 0; i < nums.length; i++) {
        if (i > reachable) return false;
        reachable = Math.max(reachable, i + nums[i]);
    }
    return true;
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
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[m][n];
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
    m, n = len(word1), len(word2)
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    for i in range(m + 1): dp[i][0] = i
    for j in range(n + 1): dp[0][j] = j
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if word1[i - 1] == word2[j - 1]:
                dp[i][j] = dp[i - 1][j - 1]
            else:
                dp[i][j] = 1 + min(dp[i - 1][j - 1], dp[i - 1][j], dp[i][j - 1])
    return dp[m][n]

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(min_distance(lines[0].strip(), lines[1].strip()))
""",
                """
const fs = require('fs');

function minDistance(word1, word2) {
    const m = word1.length, n = word2.length;
    const dp = Array.from({ length: m + 1 }, () => new Array(n + 1).fill(0));
    for (let i = 0; i <= m; i++) dp[i][0] = i;
    for (let j = 0; j <= n; j++) dp[0][j] = j;
    for (let i = 1; i <= m; i++) {
        for (let j = 1; j <= n; j++) {
            if (word1[i - 1] === word2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], dp[i - 1][j], dp[i][j - 1]);
            }
        }
    }
    return dp[m][n];
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
        int n = nums.length;
        int[] arr = new int[n + 2];
        arr[0] = 1; arr[n + 1] = 1;
        for (int i = 0; i < n; i++) arr[i + 1] = nums[i];
        int[][] dp = new int[n + 2][n + 2];

        for (int len = 1; len <= n; len++) {
            for (int l = 1; l <= n - len + 1; l++) {
                int r = l + len - 1;
                for (int k = l; k <= r; k++) {
                    dp[l][r] = Math.max(dp[l][r], dp[l][k - 1] + dp[k + 1][r] + arr[l - 1] * arr[k] * arr[r + 1]);
                }
            }
        }
        return dp[1][n];
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
    arr = [1] + nums + [1]
    n = len(nums)
    dp = [[0] * (n + 2) for _ in range(n + 2)]
    for length in range(1, n + 1):
        for l in range(1, n - length + 2):
            r = l + length - 1
            for k in range(l, r + 1):
                dp[l][r] = max(dp[l][r], dp[l][k - 1] + dp[k + 1][r] + arr[l - 1] * arr[k] * arr[r + 1])
    return dp[1][n]

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(max_coins(nums))
""",
                """
const fs = require('fs');

function maxCoins(nums) {
    const n = nums.length;
    const arr = [1, ...nums, 1];
    const dp = Array.from({ length: n + 2 }, () => new Array(n + 2).fill(0));
    for (let len = 1; len <= n; len++) {
        for (let l = 1; l <= n - len + 1; l++) {
            const r = l + len - 1;
            for (let k = l; k <= r; k++) {
                dp[l][r] = Math.max(dp[l][r], dp[l][k - 1] + dp[k + 1][r] + arr[l - 1] * arr[k] * arr[r + 1]);
            }
        }
    }
    return dp[1][n];
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

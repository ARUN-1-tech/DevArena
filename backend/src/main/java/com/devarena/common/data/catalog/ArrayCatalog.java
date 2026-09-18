package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Two Sum
        map.put("two-sum", new ChallengeProblemDef(
                "two-sum",
                """
import java.util.*;

public class Solution {
    public static int[] twoSum(int[] nums, int target) {
        // Write your solution here
        return new int[]{};
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextLine()) return;
        String line1 = scanner.nextLine().trim();
        if (!scanner.hasNextLine()) return;
        String line2 = scanner.nextLine().trim();

        String[] parts = line1.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        int target = Integer.parseInt(line2.trim());

        int[] result = twoSum(nums, target);
        System.out.println(Arrays.toString(result));
    }
}
""",
                """
import sys

def two_sum(nums: list[int], target: int) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        target = int(lines[1].strip())
        res = two_sum(nums, target)
        print(f"[{res[0]}, {res[1]}]")
""",
                """
const fs = require('fs');

function twoSum(nums, target) {
    // Write your solution here
    return null;
}

const input = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (input.length >= 2) {
    const nums = input[0].split(',').map(s => parseInt(s.trim(), 10));
    const target = parseInt(input[1].trim(), 10);
    const res = twoSum(nums, target);
    console.log(`[${res[0]}, ${res[1]}]`);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2,7,11,15\n9", "[0, 1]", "Because nums[0] + nums[1] == 9, we return [0, 1]."),
                        TestCaseDef.publicCase(2, "3,2,4\n6", "[1, 2]", "nums[1] + nums[2] == 6, return [1, 2]."),
                        TestCaseDef.hiddenCase(3, "3,3\n6", "[0, 1]", "Duplicate target values matching"),
                        TestCaseDef.hiddenCase(4, "1,5,8,11,14\n19", "[2, 3]", "Larger elements (8 + 11 = 19)")
                )
        ));

        // 2. Contains Duplicate
        map.put("contains-duplicate", new ChallengeProblemDef(
                "contains-duplicate",
                """
import java.util.*;

public class Solution {
    public static boolean containsDuplicate(int[] nums) {
        // Write your solution here
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("false");
            return;
        }
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(containsDuplicate(nums));
    }
}
""",
                """
import sys

def contains_duplicate(nums: list[int]) -> bool:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(contains_duplicate(nums)).lower())
    else:
        print("false")
""",
                """
const fs = require('fs');

function containsDuplicate(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(containsDuplicate(nums));
} else {
    console.log(false);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3,1", "true", "1 appears twice in the array."),
                        TestCaseDef.publicCase(2, "1,2,3,4", "false", "All elements are distinct."),
                        TestCaseDef.hiddenCase(3, "1,1,1,3,3,4,3,2,4,2", "true", "Multiple duplicate values"),
                        TestCaseDef.hiddenCase(4, "99", "false", "Single element array cannot have duplicates")
                )
        ));

        // 3. Best Time to Buy and Sell Stock
        map.put("best-time-to-buy-and-sell-stock", new ChallengeProblemDef(
                "best-time-to-buy-and-sell-stock",
                """
import java.util.*;

public class Solution {
    public static int maxProfit(int[] prices) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] prices = new int[parts.length];
        for (int i = 0; i < parts.length; i++) prices[i] = Integer.parseInt(parts[i].trim());
        System.out.println(maxProfit(prices));
    }
}
""",
                """
import sys

def max_profit(prices: list[int]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        prices = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(max_profit(prices))
""",
                """
const fs = require('fs');

function maxProfit(prices) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const prices = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(maxProfit(prices));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "7,1,5,3,6,4", "5", "Buy on day 2 (price = 1) and sell on day 5 (price = 6), profit = 6-1 = 5."),
                        TestCaseDef.publicCase(2, "7,6,4,3,1", "0", "In this case, no transactions are done and max profit = 0."),
                        TestCaseDef.hiddenCase(3, "2,4,1", "2", "Buy at 2, sell at 4"),
                        TestCaseDef.hiddenCase(4, "1,2", "1", "Two elements minimal trading sequence")
                )
        ));

        // 4. Product of Array Except Self
        map.put("product-of-array-except-self", new ChallengeProblemDef(
                "product-of-array-except-self",
                """
import java.util.*;

public class Solution {
    public static int[] productExceptSelf(int[] nums) {
        // Write your solution here
        return new int[]{};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(Arrays.toString(productExceptSelf(nums)));
    }
}
""",
                """
import sys

def product_except_self(nums: list[int]) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(f"[{', '.join(map(str, product_except_self(nums)))}]")
""",
                """
const fs = require('fs');

function productExceptSelf(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(`[${productExceptSelf(nums).join(', ')}]`);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3,4", "[24, 12, 8, 6]", "Product except self for each element."),
                        TestCaseDef.publicCase(2, "-1,1,0,-3,3", "[0, 0, 9, 0, 0]", "Handles zeros correctly."),
                        TestCaseDef.hiddenCase(3, "2,3", "[3, 2]", "Two elements"),
                        TestCaseDef.hiddenCase(4, "1,0", "[0, 1]", "Zero boundary")
                )
        ));

        // 5. Maximum Subarray
        map.put("maximum-subarray", new ChallengeProblemDef(
                "maximum-subarray",
                """
import java.util.*;

public class Solution {
    public static int maxSubArray(int[] nums) {
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
        System.out.println(maxSubArray(nums));
    }
}
""",
                """
import sys

def max_sub_array(nums: list[int]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(max_sub_array(nums))
""",
                """
const fs = require('fs');

function maxSubArray(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(maxSubArray(nums));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "-2,1,-3,4,-1,2,1,-5,4", "6", "The subarray [4,-1,2,1] has the largest sum 6."),
                        TestCaseDef.publicCase(2, "1", "1", "Single element array."),
                        TestCaseDef.hiddenCase(3, "5,4,-1,7,8", "23", "Subarray with large positives"),
                        TestCaseDef.hiddenCase(4, "-1", "-1", "Single negative number")
                )
        ));

        // 6. 3Sum
        map.put("3sum", new ChallengeProblemDef(
                "3sum",
                """
import java.util.*;

public class Solution {
    public static List<List<Integer>> threeSum(int[] nums) {
        // Write your solution here
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("[]");
            return;
        }
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(threeSum(nums));
    }
}
""",
                """
import sys

def three_sum(nums: list[int]) -> list[list[int]]:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        res = three_sum(nums)
        print(str(res).replace(" ", " "))
    else:
        print("[]")
""",
                """
const fs = require('fs');

function threeSum(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(JSON.stringify(threeSum(nums)).replace(/,/g, ', '));
} else {
    console.log("[]");
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "-1,0,1,2,-1,-4", "[[-1, -1, 2], [-1, 0, 1]]", "Unique triplets that sum to 0."),
                        TestCaseDef.publicCase(2, "0,1,1", "[]", "No valid triplets."),
                        TestCaseDef.hiddenCase(3, "0,0,0", "[[0, 0, 0]]", "Three zeros"),
                        TestCaseDef.hiddenCase(4, "-2,0,1,1,2", "[[-2, 0, 2], [-2, 1, 1]]", "Multiple solution pairs")
                )
        ));

        // 7. Container With Most Water
        map.put("container-with-most-water", new ChallengeProblemDef(
                "container-with-most-water",
                """
import java.util.*;

public class Solution {
    public static int maxArea(int[] height) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] h = new int[parts.length];
        for (int i = 0; i < parts.length; i++) h[i] = Integer.parseInt(parts[i].trim());
        System.out.println(maxArea(h));
    }
}
""",
                """
import sys

def max_area(height: list[int]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        height = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(max_area(height))
""",
                """
const fs = require('fs');

function maxArea(height) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const h = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(maxArea(h));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,8,6,2,5,4,8,3,7", "49", "The vertical lines [8] and [7] form max area of 49."),
                        TestCaseDef.publicCase(2, "1,1", "1", "Min width container."),
                        TestCaseDef.hiddenCase(3, "4,3,2,1,4", "16", "Equal height boundaries"),
                        TestCaseDef.hiddenCase(4, "1,2,1", "2", "Peak in middle")
                )
        ));

        // 8. Trapping Rain Water
        map.put("trapping-rain-water", new ChallengeProblemDef(
                "trapping-rain-water",
                """
import java.util.*;

public class Solution {
    public static int trap(int[] height) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] h = new int[parts.length];
        for (int i = 0; i < parts.length; i++) h[i] = Integer.parseInt(parts[i].trim());
        System.out.println(trap(h));
    }
}
""",
                """
import sys

def trap(height: list[int]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        height = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(trap(height))
""",
                """
const fs = require('fs');

function trap(height) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const h = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(trap(h));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "0,1,0,2,1,0,1,3,2,1,2,1", "6", "6 units of rain water are trapped."),
                        TestCaseDef.publicCase(2, "4,2,0,3,2,5", "9", "9 units of rain water trapped in bowl."),
                        TestCaseDef.hiddenCase(3, "2,0,2", "2", "Simple valley"),
                        TestCaseDef.hiddenCase(4, "3,0,0,2,0,4", "10", "Deep valley")
                )
        ));

        // 9. Rotate Image
        map.put("rotate-image", new ChallengeProblemDef(
                "rotate-image",
                """
import java.util.*;

public class Solution {
    public static void rotate(int[][] matrix) {
        // Write your solution here
        
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        // Parse format [[1,2,3],[4,5,6],[7,8,9]]
        line = line.substring(1, line.length() - 1);
        String[] rows = line.split("(?<=\\]),\\s*(?=\\[)");
        int n = rows.length;
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            String r = rows[i].replaceAll("[\\[\\]]", "").trim();
            String[] nums = r.split(",");
            for (int j = 0; j < n; j++) matrix[i][j] = Integer.parseInt(nums[j].trim());
        }
        rotate(matrix);
        System.out.println(Arrays.deepToString(matrix));
    }
}
""",
                """
import sys
import json

def rotate(matrix: list[list[int]]) -> None:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        matrix = json.loads(line)
        rotate(matrix)
        print(str(matrix))
""",
                """
const fs = require('fs');

function rotate(matrix) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const matrix = JSON.parse(line);
    rotate(matrix);
    console.log(JSON.stringify(matrix).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "[[1,2,3],[4,5,6],[7,8,9]]", "[[7, 4, 1], [8, 5, 2], [9, 6, 3]]", "3x3 matrix rotated 90 degrees clockwise."),
                        TestCaseDef.publicCase(2, "[[5,1,9,11],[2,4,8,10],[13,3,6,7],[15,14,12,16]]", "[[15, 13, 2, 5], [14, 3, 4, 1], [12, 6, 8, 9], [16, 7, 10, 11]]", "4x4 matrix rotated 90 degrees."),
                        TestCaseDef.hiddenCase(3, "[[1]]", "[[1]]", "1x1 base case")
                )
        ));

        // 10. Subarray Sum Equals K
        map.put("subarray-sum-equals-k", new ChallengeProblemDef(
                "subarray-sum-equals-k",
                """
import java.util.*;

public class Solution {
    public static int subarraySum(int[] nums, int k) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int k = Integer.parseInt(sc.nextLine().trim());
        String[] parts = line1.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(subarraySum(nums, k));
    }
}
""",
                """
import sys

def subarray_sum(nums: list[int], k: int) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        k = int(lines[1].strip())
        print(subarray_sum(nums, k))
""",
                """
const fs = require('fs');

function subarraySum(nums, k) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const nums = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const k = parseInt(lines[1].trim(), 10);
    console.log(subarraySum(nums, k));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,1,1\n2", "2", "Subarrays [1,1] from index 0-1 and index 1-2 sum to 2."),
                        TestCaseDef.publicCase(2, "1,2,3\n3", "2", "Subarrays [1,2] and [3] sum to 3."),
                        TestCaseDef.hiddenCase(3, "1,-1,0\n0", "3", "Negative numbers and zero handling"),
                        TestCaseDef.hiddenCase(4, "1\n0", "0", "Single element mismatch")
                )
        ));

        // 11. Longest Consecutive Sequence
        map.put("longest-consecutive-sequence", new ChallengeProblemDef(
                "longest-consecutive-sequence",
                """
import java.util.*;

public class Solution {
    public static int longestConsecutive(int[] nums) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println(0);
            return;
        }
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(longestConsecutive(nums));
    }
}
""",
                """
import sys

def longest_consecutive(nums: list[int]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(longest_consecutive(nums))
    else:
        print(0)
""",
                """
const fs = require('fs');

function longestConsecutive(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(longestConsecutive(nums));
} else {
    console.log(0);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "100,4,200,1,3,2", "4", "Longest consecutive elements sequence is [1, 2, 3, 4], length = 4."),
                        TestCaseDef.publicCase(2, "0,3,7,2,5,8,4,6,0,1", "9", "Sequence is [0, 1, 2, 3, 4, 5, 6, 7, 8], length = 9."),
                        TestCaseDef.hiddenCase(3, "", "0", "Empty array"),
                        TestCaseDef.hiddenCase(4, "9,1,4,7,3,-1,0,5,8,-1,6", "7", "Sequence [-1, 0, 1, 3, 4, 5, 6, 7, 8, 9] longest is 7 (3 to 9)")
                )
        ));

        // 12. Find All Numbers Disappeared in an Array
        map.put("find-all-numbers-disappeared-in-an-array", new ChallengeProblemDef(
                "find-all-numbers-disappeared-in-an-array",
                """
import java.util.*;

public class Solution {
    public static List<Integer> findDisappearedNumbers(int[] nums) {
        // Write your solution here
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(findDisappearedNumbers(nums));
    }
}
""",
                """
import sys

def find_disappeared_numbers(nums: list[int]) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(find_disappeared_numbers(nums)))
""",
                """
const fs = require('fs');

function findDisappearedNumbers(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(JSON.stringify(findDisappearedNumbers(nums)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "4,3,2,7,8,2,3,1", "[5, 6]", "5 and 6 are missing from [1, 8]."),
                        TestCaseDef.publicCase(2, "1,1", "[2]", "2 is missing."),
                        TestCaseDef.hiddenCase(3, "1", "[]", "Single element present"),
                        TestCaseDef.hiddenCase(4, "2,2", "[1]", "1 is missing")
                )
        ));

        // 13. Merge Intervals
        map.put("merge-intervals", new ChallengeProblemDef(
                "merge-intervals",
                """
import java.util.*;

public class Solution {
    public static int[][] merge(int[][] intervals) {
        // Write your solution here
        return new int[][]{};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        line = line.substring(1, line.length() - 1);
        String[] parts = line.split("(?<=\\]),\\s*(?=\\[)");
        int[][] intervals = new int[parts.length][2];
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].replaceAll("[\\[\\]]", "").trim();
            String[] nums = p.split(",");
            intervals[i][0] = Integer.parseInt(nums[0].trim());
            intervals[i][1] = Integer.parseInt(nums[1].trim());
        }
        System.out.println(Arrays.deepToString(merge(intervals)));
    }
}
""",
                """
import sys
import json

def merge(intervals: list[list[int]]) -> list[list[int]]:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        intervals = json.loads(line)
        print(str(merge(intervals)))
""",
                """
const fs = require('fs');

function merge(intervals) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const intervals = JSON.parse(line);
    console.log(JSON.stringify(merge(intervals)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "[[1,3],[2,6],[8,10],[15,18]]", "[[1, 6], [8, 10], [15, 18]]", "Intervals [1,3] and [2,6] overlap into [1,6]."),
                        TestCaseDef.publicCase(2, "[[1,4],[4,5]]", "[[1, 5]]", "Adjacent intervals overlap at 4."),
                        TestCaseDef.hiddenCase(3, "[[1,4],[2,3]]", "[[1, 4]]", "Subsumed interval"),
                        TestCaseDef.hiddenCase(4, "[[1,4],[0,4]]", "[[0, 4]]", "Earlier starting interval")
                )
        ));

        // 14. Spiral Matrix
        map.put("spiral-matrix", new ChallengeProblemDef(
                "spiral-matrix",
                """
import java.util.*;

public class Solution {
    public static List<Integer> spiralOrder(int[][] matrix) {
        // Write your solution here
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        line = line.substring(1, line.length() - 1);
        String[] rows = line.split("(?<=\\]),\\s*(?=\\[)");
        int m = rows.length;
        String firstRow = rows[0].replaceAll("[\\[\\]]", "").trim();
        int n = firstRow.split(",").length;
        int[][] matrix = new int[m][n];
        for (int i = 0; i < m; i++) {
            String r = rows[i].replaceAll("[\\[\\]]", "").trim();
            String[] nums = r.split(",");
            for (int j = 0; j < n; j++) matrix[i][j] = Integer.parseInt(nums[j].trim());
        }
        System.out.println(spiralOrder(matrix));
    }
}
""",
                """
import sys
import json

def spiral_order(matrix: list[list[int]]) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        matrix = json.loads(line)
        print(str(spiral_order(matrix)))
""",
                """
const fs = require('fs');

function spiralOrder(matrix) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const matrix = JSON.parse(line);
    console.log(JSON.stringify(spiralOrder(matrix)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "[[1,2,3],[4,5,6],[7,8,9]]", "[1, 2, 3, 6, 9, 8, 7, 4, 5]", "Spiral traversal of 3x3."),
                        TestCaseDef.publicCase(2, "[[1,2,3,4],[5,6,7,8],[9,10,11,12]]", "[1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7]", "Spiral traversal of 3x4."),
                        TestCaseDef.hiddenCase(3, "[[1]]", "[1]", "Single 1x1 matrix")
                )
        ));

        // 15. Set Matrix Zeroes
        map.put("set-matrix-zeroes", new ChallengeProblemDef(
                "set-matrix-zeroes",
                """
import java.util.*;

public class Solution {
    public static void setZeroes(int[][] matrix) {
        // Write your solution here
        
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        line = line.substring(1, line.length() - 1);
        String[] rows = line.split("(?<=\\]),\\s*(?=\\[)");
        int m = rows.length;
        String firstRow = rows[0].replaceAll("[\\[\\]]", "").trim();
        int n = firstRow.split(",").length;
        int[][] matrix = new int[m][n];
        for (int i = 0; i < m; i++) {
            String r = rows[i].replaceAll("[\\[\\]]", "").trim();
            String[] nums = r.split(",");
            for (int j = 0; j < n; j++) matrix[i][j] = Integer.parseInt(nums[j].trim());
        }
        setZeroes(matrix);
        System.out.println(Arrays.deepToString(matrix));
    }
}
""",
                """
import sys
import json

def set_zeroes(matrix: list[list[int]]) -> None:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        matrix = json.loads(line)
        set_zeroes(matrix)
        print(str(matrix))
""",
                """
const fs = require('fs');

function setZeroes(matrix) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const matrix = JSON.parse(line);
    setZeroes(matrix);
    console.log(JSON.stringify(matrix).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "[[1,1,1],[1,0,1],[1,1,1]]", "[[1, 0, 1], [0, 0, 0], [1, 0, 1]]", "Sets middle row and column to 0."),
                        TestCaseDef.publicCase(2, "[[0,1,2,0],[3,4,5,2],[1,3,1,5]]", "[[0, 0, 0, 0], [0, 4, 5, 0], [0, 3, 1, 0]]", "Multiple zeroes."),
                        TestCaseDef.hiddenCase(3, "[[1,0]]", "[[0, 0]]", "1x2 matrix")
                )
        ));

        // 16. Insert Interval
        map.put("insert-interval", new ChallengeProblemDef(
                "insert-interval",
                """
import java.util.*;

public class Solution {
    public static int[][] insert(int[][] intervals, int[] newInterval) {
        // Write your solution here
        return new int[][]{};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String line2 = sc.nextLine().trim();
        int[] newInt = new int[2];
        String clean2 = line2.replaceAll("[\\[\\]]", "").trim();
        String[] parts2 = clean2.split(",");
        newInt[0] = Integer.parseInt(parts2[0].trim());
        newInt[1] = Integer.parseInt(parts2[1].trim());

        if (line1.equals("[]") || line1.isEmpty()) {
            System.out.println(Arrays.deepToString(new int[][]{newInt}));
            return;
        }
        String clean1 = line1.substring(1, line1.length() - 1);
        String[] parts1 = clean1.split("(?<=\\]),\\s*(?=\\[)");
        int[][] intervals = new int[parts1.length][2];
        for (int i = 0; i < parts1.length; i++) {
            String p = parts1[i].replaceAll("[\\[\\]]", "").trim();
            String[] nums = p.split(",");
            intervals[i][0] = Integer.parseInt(nums[0].trim());
            intervals[i][1] = Integer.parseInt(nums[1].trim());
        }
        System.out.println(Arrays.deepToString(insert(intervals, newInt)));
    }
}
""",
                """
import sys
import json

def insert(intervals: list[list[int]], new_interval: list[int]) -> list[list[int]]:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        intervals = json.loads(lines[0])
        new_interval = json.loads(lines[1])
        print(str(insert(intervals, new_interval)))
""",
                """
const fs = require('fs');

function insert(intervals, newInterval) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const intervals = JSON.parse(lines[0]);
    const newInterval = JSON.parse(lines[1]);
    console.log(JSON.stringify(insert(intervals, newInterval)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "[[1,3],[6,9]]\n[2,5]", "[[1, 5], [6, 9]]", "Merges with [1,3] into [1,5]."),
                        TestCaseDef.publicCase(2, "[[1,2],[3,5],[6,7],[8,10],[12,16]]\n[4,8]", "[[1, 2], [3, 10], [12, 16]]", "Spans and merges multiple intervals."),
                        TestCaseDef.hiddenCase(3, "[]\n[5,7]", "[[5, 7]]", "Empty intervals list"),
                        TestCaseDef.hiddenCase(4, "[[1,5]]\n[2,3]", "[[1, 5]]", "Subsumed interval inside [1,5]")
                )
        ));

        return map;
    }
}

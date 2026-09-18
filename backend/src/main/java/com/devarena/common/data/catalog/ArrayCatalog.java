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
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int comp = target - nums[i];
            if (map.containsKey(comp)) {
                return new int[]{ map.get(comp), i };
            }
            map.put(nums[i], i);
        }
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
    lookup = {}
    for i, n in enumerate(nums):
        diff = target - n
        if diff in lookup:
            return [lookup[diff], i]
        lookup[n] = i
    return []

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
    const map = new Map();
    for (let i = 0; i < nums.length; i++) {
        const diff = target - nums[i];
        if (map.has(diff)) {
            return [map.get(diff), i];
        }
        map.set(nums[i], i);
    }
    return [];
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
        Set<Integer> set = new HashSet<>();
        for (int n : nums) {
            if (!set.add(n)) return true;
        }
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
    return len(nums) != len(set(nums))

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
    return new Set(nums).size !== nums.length;
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
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        for (int price : prices) {
            if (price < minPrice) minPrice = price;
            else if (price - minPrice > maxProfit) maxProfit = price - minPrice;
        }
        return maxProfit;
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
    min_price = float('inf')
    max_p = 0
    for p in prices:
        if p < min_price:
            min_price = p
        elif p - min_price > max_p:
            max_p = p - min_price
    return max_p

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        prices = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(max_profit(prices))
""",
                """
const fs = require('fs');

function maxProfit(prices) {
    let minPrice = Infinity;
    let maxP = 0;
    for (const p of prices) {
        if (p < minPrice) minPrice = p;
        else if (p - minPrice > maxP) maxP = p - minPrice;
    }
    return maxP;
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
        int n = nums.length;
        int[] res = new int[n];
        res[0] = 1;
        for (int i = 1; i < n; i++) res[i] = res[i - 1] * nums[i - 1];
        int right = 1;
        for (int i = n - 1; i >= 0; i--) {
            res[i] *= right;
            right *= nums[i];
        }
        return res;
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
    n = len(nums)
    res = [1] * n
    prefix = 1
    for i in range(n):
        res[i] = prefix
        prefix *= nums[i]
    postfix = 1
    for i in range(n - 1, -1, -1):
        res[i] *= postfix
        postfix *= nums[i]
    return res

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(f"[{', '.join(map(str, product_except_self(nums)))}]")
""",
                """
const fs = require('fs');

function productExceptSelf(nums) {
    const n = nums.length;
    const res = new Array(n).fill(1);
    let prefix = 1;
    for (let i = 0; i < n; i++) {
        res[i] = prefix;
        prefix *= nums[i];
    }
    let postfix = 1;
    for (let i = n - 1; i >= 0; i--) {
        res[i] *= postfix;
        postfix *= nums[i];
    }
    return res;
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
        int max = nums[0], curr = nums[0];
        for (int i = 1; i < nums.length; i++) {
            curr = Math.max(nums[i], curr + nums[i]);
            max = Math.max(max, curr);
        }
        return max;
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
    cur = max_s = nums[0]
    for n in nums[1:]:
        cur = max(n, cur + n)
        max_s = max(max_s, cur)
    return max_s

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(max_sub_array(nums))
""",
                """
const fs = require('fs');

function maxSubArray(nums) {
    let max = nums[0], curr = nums[0];
    for (let i = 1; i < nums.length; i++) {
        curr = Math.max(nums[i], curr + nums[i]);
        max = Math.max(max, curr);
    }
    return max;
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
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            int l = i + 1, r = nums.length - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (sum == 0) {
                    res.add(List.of(nums[i], nums[l], nums[r]));
                    while (l < r && nums[l] == nums[l + 1]) l++;
                    while (l < r && nums[r] == nums[r - 1]) r--;
                    l++; r--;
                } else if (sum < 0) l++;
                else r--;
            }
        }
        return res;
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
    nums.sort()
    res = []
    for i in range(len(nums) - 2):
        if i > 0 and nums[i] == nums[i - 1]:
            continue
        l, r = i + 1, len(nums) - 1
        while l < r:
            s = nums[i] + nums[l] + nums[r]
            if s == 0:
                res.append([nums[i], nums[l], nums[r]])
                while l < r and nums[l] == nums[l + 1]:
                    l += 1
                while l < r and nums[r] == nums[r - 1]:
                    r -= 1
                l += 1
                r -= 1
            elif s < 0:
                l += 1
            else:
                r -= 1
    return res

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
    nums.sort((a, b) => a - b);
    const res = [];
    for (let i = 0; i < nums.length - 2; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) continue;
        let l = i + 1, r = nums.length - 1;
        while (l < r) {
            const sum = nums[i] + nums[l] + nums[r];
            if (sum === 0) {
                res.push([nums[i], nums[l], nums[r]]);
                while (l < r && nums[l] === nums[l + 1]) l++;
                while (l < r && nums[r] === nums[r - 1]) r--;
                l++; r--;
            } else if (sum < 0) l++;
            else r--;
        }
    }
    return res;
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
        int l = 0, r = height.length - 1;
        int max = 0;
        while (l < r) {
            int h = Math.min(height[l], height[r]);
            max = Math.max(max, h * (r - l));
            if (height[l] < height[r]) l++;
            else r--;
        }
        return max;
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
    l, r = 0, len(height) - 1
    max_a = 0
    while l < r:
        h = min(height[l], height[r])
        max_a = max(max_a, h * (r - l))
        if height[l] < height[r]:
            l += 1
        else:
            r -= 1
    return max_a

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        height = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(max_area(height))
""",
                """
const fs = require('fs');

function maxArea(height) {
    let l = 0, r = height.length - 1;
    let max = 0;
    while (l < r) {
        const h = Math.min(height[l], height[r]);
        max = Math.max(max, h * (r - l));
        if (height[l] < height[r]) l++;
        else r--;
    }
    return max;
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
        int l = 0, r = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int res = 0;
        while (l < r) {
            if (height[l] < height[r]) {
                if (height[l] >= leftMax) leftMax = height[l];
                else res += leftMax - height[l];
                l++;
            } else {
                if (height[r] >= rightMax) rightMax = height[r];
                else res += rightMax - height[r];
                r--;
            }
        }
        return res;
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
    l, r = 0, len(height) - 1
    left_max, right_max = 0, 0
    res = 0
    while l < r:
        if height[l] < height[r]:
            if height[l] >= left_max:
                left_max = height[l]
            else:
                res += left_max - height[l]
            l += 1
        else:
            if height[r] >= right_max:
                right_max = height[r]
            else:
                res += right_max - height[r]
            r -= 1
    return res

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        height = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(trap(height))
""",
                """
const fs = require('fs');

function trap(height) {
    let l = 0, r = height.length - 1;
    let leftMax = 0, rightMax = 0;
    let res = 0;
    while (l < r) {
        if (height[l] < height[r]) {
            if (height[l] >= leftMax) leftMax = height[l];
            else res += leftMax - height[l];
            l++;
        } else {
            if (height[r] >= rightMax) rightMax = height[r];
            else res += rightMax - height[r];
            r--;
        }
    }
    return res;
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
        int n = matrix.length;
        // Transpose
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
        // Reverse rows
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n / 2; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[i][n - 1 - j];
                matrix[i][n - 1 - j] = temp;
            }
        }
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
    n = len(matrix)
    for i in range(n):
        for j in range(i + 1, n):
            matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]
    for i in range(n):
        matrix[i].reverse()

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
    const n = matrix.length;
    for (let i = 0; i < n; i++) {
        for (let j = i + 1; j < n; j++) {
            [matrix[i][j], matrix[j][i]] = [matrix[j][i], matrix[i][j]];
        }
    }
    for (let i = 0; i < n; i++) matrix[i].reverse();
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
        int count = 0, sum = 0;
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        for (int n : nums) {
            sum += n;
            if (map.containsKey(sum - k)) count += map.get(sum - k);
            map.put(sum, map.getOrDefault(sum, 0) + 1);
        }
        return count;
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
    count = 0
    s = 0
    lookup = {0: 1}
    for n in nums:
        s += n
        count += lookup.get(s - k, 0)
        lookup[s] = lookup.get(s, 0) + 1
    return count

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
    let count = 0, sum = 0;
    const map = new Map();
    map.set(0, 1);
    for (const n of nums) {
        sum += n;
        if (map.has(sum - k)) count += map.get(sum - k);
        map.set(sum, (map.get(sum) || 0) + 1);
    }
    return count;
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
        Set<Integer> set = new HashSet<>();
        for (int n : nums) set.add(n);
        int longest = 0;
        for (int n : set) {
            if (!set.contains(n - 1)) {
                int curr = n;
                int streak = 1;
                while (set.contains(curr + 1)) {
                    curr++;
                    streak++;
                }
                longest = Math.max(longest, streak);
            }
        }
        return longest;
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
    num_set = set(nums)
    longest = 0
    for n in num_set:
        if n - 1 not in num_set:
            curr = n
            streak = 1
            while curr + 1 in num_set:
                curr += 1
                streak += 1
            longest = max(longest, streak)
    return longest

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
    const set = new Set(nums);
    let longest = 0;
    for (const n of set) {
        if (!set.has(n - 1)) {
            let curr = n;
            let streak = 1;
            while (set.has(curr + 1)) {
                curr++;
                streak++;
            }
            longest = Math.max(longest, streak);
        }
    }
    return longest;
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
        for (int i = 0; i < nums.length; i++) {
            int idx = Math.abs(nums[i]) - 1;
            if (nums[idx] > 0) nums[idx] = -nums[idx];
        }
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) res.add(i + 1);
        }
        return res;
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
    for i in range(len(nums)):
        idx = abs(nums[i]) - 1
        if nums[idx] > 0:
            nums[idx] = -nums[idx]
    return [i + 1 for i in range(len(nums)) if nums[i] > 0]

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(find_disappeared_numbers(nums)))
""",
                """
const fs = require('fs');

function findDisappearedNumbers(nums) {
    for (let i = 0; i < nums.length; i++) {
        const idx = Math.abs(nums[i]) - 1;
        if (nums[idx] > 0) nums[idx] = -nums[idx];
    }
    const res = [];
    for (let i = 0; i < nums.length; i++) {
        if (nums[i] > 0) res.push(i + 1);
    }
    return res;
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
        if (intervals.length <= 1) return intervals;
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> result = new ArrayList<>();
        int[] current = intervals[0];
        result.add(current);
        for (int[] interval : intervals) {
            if (interval[0] <= current[1]) {
                current[1] = Math.max(current[1], interval[1]);
            } else {
                current = interval;
                result.add(current);
            }
        }
        return result.toArray(new int[result.size()][]);
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
    if not intervals:
        return []
    intervals.sort(key=lambda x: x[0])
    merged = [intervals[0]]
    for current in intervals[1:]:
        prev = merged[-1]
        if current[0] <= prev[1]:
            prev[1] = max(prev[1], current[1])
        else:
            merged.append(current)
    return merged

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        intervals = json.loads(line)
        print(str(merge(intervals)))
""",
                """
const fs = require('fs');

function merge(intervals) {
    if (intervals.length <= 1) return intervals;
    intervals.sort((a, b) => a[0] - b[0]);
    const merged = [intervals[0]];
    for (let i = 1; i < intervals.length; i++) {
        const prev = merged[merged.length - 1];
        const curr = intervals[i];
        if (curr[0] <= prev[1]) {
            prev[1] = Math.max(prev[1], curr[1]);
        } else {
            merged.push(curr);
        }
    }
    return merged;
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
        List<Integer> res = new ArrayList<>();
        if (matrix.length == 0) return res;
        int top = 0, bottom = matrix.length - 1;
        int left = 0, right = matrix[0].length - 1;
        while (top <= bottom && left <= right) {
            for (int i = left; i <= right; i++) res.add(matrix[top][i]);
            top++;
            for (int i = top; i <= bottom; i++) res.add(matrix[i][right]);
            right--;
            if (top <= bottom) {
                for (int i = right; i >= left; i--) res.add(matrix[bottom][i]);
                bottom--;
            }
            if (left <= right) {
                for (int i = bottom; i >= top; i--) res.add(matrix[i][left]);
                left++;
            }
        }
        return res;
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
    res = []
    if not matrix:
        return res
    top, bottom = 0, len(matrix) - 1
    left, right = 0, len(matrix[0]) - 1
    while top <= bottom and left <= right:
        for i in range(left, right + 1):
            res.append(matrix[top][i])
        top += 1
        for i in range(top, bottom + 1):
            res.append(matrix[i][right])
        right -= 1
        if top <= bottom:
            for i in range(right, left - 1, -1):
                res.append(matrix[bottom][i])
            bottom -= 1
        if left <= right:
            for i in range(bottom, top - 1, -1):
                res.append(matrix[i][left])
            left += 1
    return res

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        matrix = json.loads(line)
        print(str(spiral_order(matrix)))
""",
                """
const fs = require('fs');

function spiralOrder(matrix) {
    const res = [];
    if (!matrix || matrix.length === 0) return res;
    let top = 0, bottom = matrix.length - 1;
    let left = 0, right = matrix[0].length - 1;
    while (top <= bottom && left <= right) {
        for (let i = left; i <= right; i++) res.push(matrix[top][i]);
        top++;
        for (let i = top; i <= bottom; i++) res.push(matrix[i][right]);
        right--;
        if (top <= bottom) {
            for (let i = right; i >= left; i--) res.push(matrix[bottom][i]);
            bottom--;
        }
        if (left <= right) {
            for (let i = bottom; i >= top; i--) res.push(matrix[i][left]);
            left++;
        }
    }
    return res;
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
        int m = matrix.length, n = matrix[0].length;
        boolean firstRow = false, firstCol = false;
        for (int i = 0; i < m; i++) {
            if (matrix[i][0] == 0) firstCol = true;
        }
        for (int j = 0; j < n; j++) {
            if (matrix[0][j] == 0) firstRow = true;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                }
            }
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;
            }
        }
        if (firstCol) {
            for (int i = 0; i < m; i++) matrix[i][0] = 0;
        }
        if (firstRow) {
            for (int j = 0; j < n; j++) matrix[0][j] = 0;
        }
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
    m, n = len(matrix), len(matrix[0])
    first_row = any(matrix[0][j] == 0 for j in range(n))
    first_col = any(matrix[i][0] == 0 for i in range(m))
    for i in range(1, m):
        for j in range(1, n):
            if matrix[i][j] == 0:
                matrix[i][0] = 0
                matrix[0][j] = 0
    for i in range(1, m):
        for j in range(1, n):
            if matrix[i][0] == 0 or matrix[0][j] == 0:
                matrix[i][j] = 0
    if first_col:
        for i in range(m):
            matrix[i][0] = 0
    if first_row:
        for j in range(n):
            matrix[0][j] = 0

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
    const m = matrix.length, n = matrix[0].length;
    let firstRow = false, firstCol = false;
    for (let i = 0; i < m; i++) if (matrix[i][0] === 0) firstCol = true;
    for (let j = 0; j < n; j++) if (matrix[0][j] === 0) firstRow = true;
    for (let i = 1; i < m; i++) {
        for (let j = 1; j < n; j++) {
            if (matrix[i][j] === 0) {
                matrix[i][0] = 0;
                matrix[0][j] = 0;
            }
        }
    }
    for (let i = 1; i < m; i++) {
        for (let j = 1; j < n; j++) {
            if (matrix[i][0] === 0 || matrix[0][j] === 0) matrix[i][j] = 0;
        }
    }
    if (firstCol) for (let i = 0; i < m; i++) matrix[i][0] = 0;
    if (firstRow) for (let j = 0; j < n; j++) matrix[0][j] = 0;
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
        List<int[]> result = new ArrayList<>();
        int i = 0, n = intervals.length;
        while (i < n && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i++]);
        }
        while (i < n && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval);
        while (i < n) {
            result.add(intervals[i++]);
        }
        return result.toArray(new int[result.size()][]);
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
    res = []
    i, n = 0, len(intervals)
    while i < n and intervals[i][1] < new_interval[0]:
        res.append(intervals[i])
        i += 1
    while i < n and intervals[i][0] <= new_interval[1]:
        new_interval[0] = min(new_interval[0], intervals[i][0])
        new_interval[1] = max(new_interval[1], intervals[i][1])
        i += 1
    res.append(new_interval)
    while i < n:
        res.append(intervals[i])
        i += 1
    return res

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
    const res = [];
    let i = 0, n = intervals.length;
    while (i < n && intervals[i][1] < newInterval[0]) {
        res.push(intervals[i++]);
    }
    while (i < n && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
        newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
        i++;
    }
    res.push(newInterval);
    while (i < n) {
        res.push(intervals[i++]);
    }
    return res;
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

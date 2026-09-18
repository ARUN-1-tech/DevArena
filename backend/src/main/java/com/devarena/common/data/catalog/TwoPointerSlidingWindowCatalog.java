package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwoPointerSlidingWindowCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Two Sum II - Input Array Is Sorted
        map.put("two-sum-ii-input-array-is-sorted", new ChallengeProblemDef(
                "two-sum-ii-input-array-is-sorted",
                """
import java.util.*;

public class Solution {
    public static int[] twoSum(int[] numbers, int target) {
        int l = 0, r = numbers.length - 1;
        while (l < r) {
            int sum = numbers[l] + numbers[r];
            if (sum == target) return new int[]{l + 1, r + 1};
            else if (sum < target) l++;
            else r--;
        }
        return new int[]{};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int target = Integer.parseInt(sc.nextLine().trim());
        String[] parts = line1.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(Arrays.toString(twoSum(nums, target)));
    }
}
""",
                """
import sys

def two_sum(numbers: list[int], target: int) -> list[int]:
    l, r = 0, len(numbers) - 1
    while l < r:
        s = numbers[l] + numbers[r]
        if s == target:
            return [l + 1, r + 1]
        elif s < target:
            l += 1
        else:
            r -= 1
    return []

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        target = int(lines[1].strip())
        print(f"[{two_sum(nums, target)[0]}, {two_sum(nums, target)[1]}]")
""",
                """
const fs = require('fs');

function twoSum(numbers, target) {
    let l = 0, r = numbers.length - 1;
    while (l < r) {
        const sum = numbers[l] + numbers[r];
        if (sum === target) return [l + 1, r + 1];
        else if (sum < target) l++;
        else r--;
    }
    return [];
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const nums = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const target = parseInt(lines[1].trim(), 10);
    const res = twoSum(nums, target);
    console.log(`[${res[0]}, ${res[1]}]`);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2,7,11,15\n9", "[1, 2]", "1-indexed position of 2 and 7 is [1, 2]."),
                        TestCaseDef.publicCase(2, "2,3,4\n6", "[1, 3]", "1-indexed position of 2 and 4 is [1, 3]."),
                        TestCaseDef.hiddenCase(3, "-1,0\n-1", "[1, 2]", "Negative numbers [1, 2]")
                )
        ));

        // 2. 3Sum Closest
        map.put("3sum-closest", new ChallengeProblemDef(
                "3sum-closest",
                """
import java.util.*;

public class Solution {
    public static int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int closest = nums[0] + nums[1] + nums[2];
        for (int i = 0; i < nums.length - 2; i++) {
            int l = i + 1, r = nums.length - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (Math.abs(sum - target) < Math.abs(closest - target)) {
                    closest = sum;
                }
                if (sum < target) l++;
                else r--;
            }
        }
        return closest;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int target = Integer.parseInt(sc.nextLine().trim());
        String[] parts = line1.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(threeSumClosest(nums, target));
    }
}
""",
                """
import sys

def three_sum_closest(nums: list[int], target: int) -> int:
    nums.sort()
    closest = nums[0] + nums[1] + nums[2]
    for i in range(len(nums) - 2):
        l, r = i + 1, len(nums) - 1
        while l < r:
            s = nums[i] + nums[l] + nums[r]
            if abs(s - target) < abs(closest - target):
                closest = s
            if s < target:
                l += 1
            else:
                r -= 1
    return closest

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        target = int(lines[1].strip())
        print(three_sum_closest(nums, target))
""",
                """
const fs = require('fs');

function threeSumClosest(nums, target) {
    nums.sort((a, b) => a - b);
    let closest = nums[0] + nums[1] + nums[2];
    for (let i = 0; i < nums.length - 2; i++) {
        let l = i + 1, r = nums.length - 1;
        while (l < r) {
            const sum = nums[i] + nums[l] + nums[r];
            if (Math.abs(sum - target) < Math.abs(closest - target)) {
                closest = sum;
            }
            if (sum < target) l++;
            else r--;
        }
    }
    return closest;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const nums = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const target = parseInt(lines[1].trim(), 10);
    console.log(threeSumClosest(nums, target));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "-1,2,1,-4\n1", "2", "The sum that is closest to target is 2 (-1 + 2 + 1 = 2)."),
                        TestCaseDef.publicCase(2, "0,0,0\n1", "0", "Closest sum is 0."),
                        TestCaseDef.hiddenCase(3, "1,1,1,1\n0", "3", "All identical values")
                )
        ));

        // 3. 4Sum
        map.put("4sum", new ChallengeProblemDef(
                "4sum",
                """
import java.util.*;

public class Solution {
    public static List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        int n = nums.length;
        for (int i = 0; i < n - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            for (int j = i + 1; j < n - 2; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;
                int l = j + 1, r = n - 1;
                while (l < r) {
                    long sum = (long) nums[i] + nums[j] + nums[l] + nums[r];
                    if (sum == target) {
                        res.add(List.of(nums[i], nums[j], nums[l], nums[r]));
                        while (l < r && nums[l] == nums[l + 1]) l++;
                        while (l < r && nums[r] == nums[r - 1]) r--;
                        l++; r--;
                    } else if (sum < target) l++;
                    else r--;
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int target = Integer.parseInt(sc.nextLine().trim());
        String[] parts = line1.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(fourSum(nums, target));
    }
}
""",
                """
import sys

def four_sum(nums: list[int], target: int) -> list[list[int]]:
    nums.sort()
    res = []
    n = len(nums)
    for i in range(n - 3):
        if i > 0 and nums[i] == nums[i - 1]:
            continue
        for j in range(i + 1, n - 2):
            if j > i + 1 and nums[j] == nums[j - 1]:
                continue
            l, r = j + 1, n - 1
            while l < r:
                s = nums[i] + nums[j] + nums[l] + nums[r]
                if s == target:
                    res.append([nums[i], nums[j], nums[l], nums[r]])
                    while l < r and nums[l] == nums[l + 1]:
                        l += 1
                    while l < r and nums[r] == nums[r - 1]:
                        r -= 1
                    l += 1
                    r -= 1
                elif s < target:
                    l += 1
                else:
                    r -= 1
    return res

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        target = int(lines[1].strip())
        print(str(four_sum(nums, target)))
""",
                """
const fs = require('fs');

function fourSum(nums, target) {
    nums.sort((a, b) => a - b);
    const res = [];
    const n = nums.length;
    for (let i = 0; i < n - 3; i++) {
        if (i > 0 && nums[i] === nums[i - 1]) continue;
        for (let j = i + 1; j < n - 2; j++) {
            if (j > i + 1 && nums[j] === nums[j - 1]) continue;
            let l = j + 1, r = n - 1;
            while (l < r) {
                const sum = nums[i] + nums[j] + nums[l] + nums[r];
                if (sum === target) {
                    res.push([nums[i], nums[j], nums[l], nums[r]]);
                    while (l < r && nums[l] === nums[l + 1]) l++;
                    while (l < r && nums[r] === nums[r - 1]) r--;
                    l++; r--;
                } else if (sum < target) l++;
                else r--;
            }
        }
    }
    return res;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const nums = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const target = parseInt(lines[1].trim(), 10);
    console.log(JSON.stringify(fourSum(nums, target)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,0,-1,0,-2,2\n0", "[[-2, -1, 1, 2], [-2, 0, 0, 2], [-1, 0, 0, 1]]", "Unique quadruplets summing to 0."),
                        TestCaseDef.publicCase(2, "2,2,2,2,2\n8", "[[2, 2, 2, 2]]", "Repeated elements single quadruplet.")
                )
        ));

        // 4. Sliding Window Maximum
        map.put("sliding-window-maximum", new ChallengeProblemDef(
                "sliding-window-maximum",
                """
import java.util.*;

public class Solution {
    public static int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || k <= 0) return new int[0];
        int n = nums.length;
        int[] res = new int[n - k + 1];
        int ri = 0;
        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < nums.length; i++) {
            while (!q.isEmpty() && q.peek() < i - k + 1) q.poll();
            while (!q.isEmpty() && nums[q.peekLast()] < nums[i]) q.pollLast();
            q.offer(i);
            if (i >= k - 1) res[ri++] = nums[q.peek()];
        }
        return res;
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
        System.out.println(Arrays.toString(maxSlidingWindow(nums, k)));
    }
}
""",
                """
import sys
from collections import deque

def max_sliding_window(nums: list[int], k: int) -> list[int]:
    d = deque()
    out = []
    for i, n in enumerate(nums):
        while d and nums[d[-1]] < n:
            d.pop()
        d.append(i)
        if d[0] == i - k:
            d.popleft()
        if i >= k - 1:
            out.append(nums[d[0]])
    return out

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        k = int(lines[1].strip())
        print(f"[{', '.join(map(str, max_sliding_window(nums, k)))}]")
""",
                """
const fs = require('fs');

function maxSlidingWindow(nums, k) {
    const q = [];
    const res = [];
    for (let i = 0; i < nums.length; i++) {
        while (q.length && q[0] < i - k + 1) q.shift();
        while (q.length && nums[q[q.length - 1]] < nums[i]) q.pop();
        q.push(i);
        if (i >= k - 1) res.push(nums[q[0]]);
    }
    return res;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const nums = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const k = parseInt(lines[1].trim(), 10);
    console.log(`[${maxSlidingWindow(nums, k).join(', ')}]`);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,3,-1,-3,5,3,6,7\n3", "[3, 3, 5, 5, 6, 7]", "Max values across sliding window of width 3."),
                        TestCaseDef.publicCase(2, "1\n1", "[1]", "Single element array."),
                        TestCaseDef.hiddenCase(3, "9,11\n2", "[11]", "Two elements"),
                        TestCaseDef.hiddenCase(4, "4,-2\n2", "[4]", "Positive and negative")
                )
        ));

        // 5. Minimum Size Subarray Sum
        map.put("minimum-size-subarray-sum", new ChallengeProblemDef(
                "minimum-size-subarray-sum",
                """
import java.util.*;

public class Solution {
    public static int minSubArrayLen(int target, int[] nums) {
        int left = 0, sum = 0, minLen = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum >= target) {
                minLen = Math.min(minLen, right - left + 1);
                sum -= nums[left++];
            }
        }
        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        int target = Integer.parseInt(sc.nextLine().trim());
        if (!sc.hasNextLine()) return;
        String line2 = sc.nextLine().trim();
        String[] parts = line2.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(minSubArrayLen(target, nums));
    }
}
""",
                """
import sys

def min_sub_array_len(target: int, nums: list[int]) -> int:
    l, s = 0, 0
    res = float('inf')
    for r in range(len(nums)):
        s += nums[r]
        while s >= target:
            res = min(res, r - l + 1)
            s -= nums[l]
            l += 1
    return 0 if res == float('inf') else res

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        target = int(lines[0].strip())
        nums = [int(x.strip()) for x in lines[1].split(",") if x.strip()]
        print(min_sub_array_len(target, nums))
""",
                """
const fs = require('fs');

function minSubArrayLen(target, nums) {
    let left = 0, sum = 0, minLen = Infinity;
    for (let right = 0; right < nums.length; right++) {
        sum += nums[right];
        while (sum >= target) {
            minLen = Math.min(minLen, right - left + 1);
            sum -= nums[left++];
        }
    }
    return minLen === Infinity ? 0 : minLen;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const target = parseInt(lines[0].trim(), 10);
    const nums = lines[1].split(',').map(s => parseInt(s.trim(), 10));
    console.log(minSubArrayLen(target, nums));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "7\n2,3,1,2,4,3", "2", "The subarray [4,3] has minimal length 2 under problem constraint."),
                        TestCaseDef.publicCase(2, "4\n1,4,4", "1", "Single element [4] fulfills target 4."),
                        TestCaseDef.hiddenCase(3, "11\n1,1,1,1,1,1,1,1", "0", "No subarray sum reaches 11.")
                )
        ));

        return map;
    }
}

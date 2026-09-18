package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinarySearchCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Binary Search
        map.put("binary-search", new ChallengeProblemDef(
                "binary-search",
                """
import java.util.*;

public class Solution {
    public static int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
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
        System.out.println(search(nums, target));
    }
}
""",
                """
import sys

def search(nums: list[int], target: int) -> int:
    left, right = 0, len(nums) - 1
    while left <= right:
        mid = (left + right) // 2
        if nums[mid] == target:
            return mid
        elif nums[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return -1

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        target = int(lines[1].strip())
        print(search(nums, target))
""",
                """
const fs = require('fs');

function search(nums, target) {
    let left = 0, right = nums.length - 1;
    while (left <= right) {
        const mid = Math.floor((left + right) / 2);
        if (nums[mid] === target) return mid;
        if (nums[mid] < target) left = mid + 1;
        else right = mid - 1;
    }
    return -1;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const nums = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const target = parseInt(lines[1].trim(), 10);
    console.log(search(nums, target));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "-1,0,3,5,9,12\n9", "4", "9 exists in nums and its index is 4."),
                        TestCaseDef.publicCase(2, "-1,0,3,5,9,12\n2", "-1", "2 does not exist in nums so return -1."),
                        TestCaseDef.hiddenCase(3, "5\n5", "0", "Single element target match."),
                        TestCaseDef.hiddenCase(4, "2,5\n5", "1", "Two elements match right side.")
                )
        ));

        // 2. Search in Rotated Sorted Array
        map.put("search-in-rotated-sorted-array", new ChallengeProblemDef(
                "search-in-rotated-sorted-array",
                """
import java.util.*;

public class Solution {
    public static int search(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == target) return mid;
            if (nums[l] <= nums[mid]) {
                if (target >= nums[l] && target < nums[mid]) r = mid - 1;
                else l = mid + 1;
            } else {
                if (target > nums[mid] && target <= nums[r]) l = mid + 1;
                else r = mid - 1;
            }
        }
        return -1;
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
        System.out.println(search(nums, target));
    }
}
""",
                """
import sys

def search(nums: list[int], target: int) -> int:
    l, r = 0, len(nums) - 1
    while l <= r:
        mid = (l + r) // 2
        if nums[mid] == target:
            return mid
        if nums[l] <= nums[mid]:
            if nums[l] <= target < nums[mid]:
                r = mid - 1
            else:
                l = mid + 1
        else:
            if nums[mid] < target <= nums[r]:
                l = mid + 1
            else:
                r = mid - 1
    return -1

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        target = int(lines[1].strip())
        print(search(nums, target))
""",
                """
const fs = require('fs');

function search(nums, target) {
    let l = 0, r = nums.length - 1;
    while (l <= r) {
        const mid = Math.floor((l + r) / 2);
        if (nums[mid] === target) return mid;
        if (nums[l] <= nums[mid]) {
            if (target >= nums[l] && target < nums[mid]) r = mid - 1;
            else l = mid + 1;
        } else {
            if (target > nums[mid] && target <= nums[r]) l = mid + 1;
            else r = mid - 1;
        }
    }
    return -1;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const nums = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const target = parseInt(lines[1].trim(), 10);
    console.log(search(nums, target));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "4,5,6,7,0,1,2\n0", "4", "Index of target 0 in rotated array is 4."),
                        TestCaseDef.publicCase(2, "4,5,6,7,0,1,2\n3", "-1", "Target 3 is not found."),
                        TestCaseDef.hiddenCase(3, "1\n0", "-1", "Single element mismatch.")
                )
        ));

        // 3. Find Minimum in Rotated Sorted Array
        map.put("find-minimum-in-rotated-sorted-array", new ChallengeProblemDef(
                "find-minimum-in-rotated-sorted-array",
                """
import java.util.*;

public class Solution {
    public static int findMin(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] > nums[r]) l = mid + 1;
            else r = mid;
        }
        return nums[l];
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(findMin(nums));
    }
}
""",
                """
import sys

def find_min(nums: list[int]) -> int:
    l, r = 0, len(nums) - 1
    while l < r:
        mid = (l + r) // 2
        if nums[mid] > nums[r]:
            l = mid + 1
        else:
            r = mid
    return nums[l]

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(find_min(nums))
""",
                """
const fs = require('fs');

function findMin(nums) {
    let l = 0, r = nums.length - 1;
    while (l < r) {
        const mid = Math.floor((l + r) / 2);
        if (nums[mid] > nums[r]) l = mid + 1;
        else r = mid;
    }
    return nums[l];
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(findMin(nums));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "3,4,5,1,2", "1", "Minimum element is 1."),
                        TestCaseDef.publicCase(2, "4,5,6,7,0,1,2", "0", "Minimum element is 0."),
                        TestCaseDef.hiddenCase(3, "11,13,15,17", "11", "Fully sorted without rotation min is 11.")
                )
        ));

        // 4. Median of Two Sorted Arrays
        map.put("median-of-two-sorted-arrays", new ChallengeProblemDef(
                "median-of-two-sorted-arrays",
                """
import java.util.*;

public class Solution {
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int[] merged = new int[nums1.length + nums2.length];
        int i = 0, j = 0, k = 0;
        while (i < nums1.length && j < nums2.length) {
            if (nums1[i] <= nums2[j]) merged[k++] = nums1[i++];
            else merged[k++] = nums2[j++];
        }
        while (i < nums1.length) merged[k++] = nums1[i++];
        while (j < nums2.length) merged[k++] = nums2[j++];
        int n = merged.length;
        if (n % 2 == 1) return (double) merged[n / 2];
        else return (merged[n / 2 - 1] + merged[n / 2]) / 2.0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line1 = sc.hasNextLine() ? sc.nextLine().trim() : "";
        String line2 = sc.hasNextLine() ? sc.nextLine().trim() : "";
        int[] n1 = line1.isEmpty() ? new int[0] : Arrays.stream(line1.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        int[] n2 = line2.isEmpty() ? new int[0] : Arrays.stream(line2.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        System.out.println(String.format(Locale.US, "%.1f", findMedianSortedArrays(n1, n2)));
    }
}
""",
                """
import sys

def find_median_sorted_arrays(nums1: list[int], nums2: list[int]) -> float:
    merged = sorted(nums1 + nums2)
    n = len(merged)
    if n % 2 == 1:
        return float(merged[n // 2])
    return (merged[n // 2 - 1] + merged[n // 2]) / 2.0

if __name__ == "__main__":
    lines = sys.stdin.read().splitlines()
    n1 = [int(x.strip()) for x in lines[0].split(",") if x.strip()] if len(lines) > 0 and lines[0].strip() else []
    n2 = [int(x.strip()) for x in lines[1].split(",") if x.strip()] if len(lines) > 1 and lines[1].strip() else []
    print(f"{find_median_sorted_arrays(n1, n2):.1f}")
""",
                """
const fs = require('fs');

function findMedianSortedArrays(nums1, nums2) {
    const merged = [...nums1, ...nums2].sort((a, b) => a - b);
    const n = merged.length;
    if (n % 2 === 1) return merged[Math.floor(n / 2)].toFixed(1);
    return ((merged[n / 2 - 1] + merged[n / 2]) / 2).toFixed(1);
}

const lines = fs.readFileSync(0, 'utf-8').split('\\n');
const n1 = lines[0] && lines[0].trim() ? lines[0].trim().split(',').map(s => parseInt(s.trim(), 10)) : [];
const n2 = lines[1] && lines[1].trim() ? lines[1].trim().split(',').map(s => parseInt(s.trim(), 10)) : [];
console.log(findMedianSortedArrays(n1, n2));
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,3\n2", "2.0", "Merged array = [1,2,3] and median is 2.0."),
                        TestCaseDef.publicCase(2, "1,2\n3,4", "2.5", "Merged array = [1,2,3,4] and median is (2 + 3) / 2 = 2.5."),
                        TestCaseDef.hiddenCase(3, "0,0\n0,0", "0.0", "All zeroes median is 0.0.")
                )
        ));

        // 5. Find First and Last Position of Element in Sorted Array
        map.put("find-first-and-last-position-of-element-in-sorted-array", new ChallengeProblemDef(
                "find-first-and-last-position-of-element-in-sorted-array",
                """
import java.util.*;

public class Solution {
    public static int[] searchRange(int[] nums, int target) {
        int first = findBound(nums, target, true);
        if (first == -1) return new int[]{-1, -1};
        int last = findBound(nums, target, false);
        return new int[]{first, last};
    }

    private static int findBound(int[] nums, int target, boolean isFirst) {
        int l = 0, r = nums.length - 1, ans = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == target) {
                ans = mid;
                if (isFirst) r = mid - 1;
                else l = mid + 1;
            } else if (nums[mid] < target) l = mid + 1;
            else r = mid - 1;
        }
        return ans;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line1 = sc.hasNextLine() ? sc.nextLine().trim() : "";
        if (!sc.hasNextLine()) return;
        int target = Integer.parseInt(sc.nextLine().trim());
        int[] nums = line1.isEmpty() ? new int[0] : Arrays.stream(line1.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        System.out.println(Arrays.toString(searchRange(nums, target)));
    }
}
""",
                """
import sys

def search_range(nums: list[int], target: int) -> list[int]:
    if target not in nums:
        return [-1, -1]
    return [nums.index(target), len(nums) - 1 - nums[::-1].index(target)]

if __name__ == "__main__":
    lines = sys.stdin.read().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()] if lines[0].strip() else []
        target = int(lines[1].strip())
        print(f"[{search_range(nums, target)[0]}, {search_range(nums, target)[1]}]")
""",
                """
const fs = require('fs');

function searchRange(nums, target) {
    const first = nums.indexOf(target);
    const last = nums.lastIndexOf(target);
    return [first, last];
}

const lines = fs.readFileSync(0, 'utf-8').split('\\n');
if (lines.length >= 2) {
    const nums = lines[0].trim() ? lines[0].trim().split(',').map(s => parseInt(s.trim(), 10)) : [];
    const target = parseInt(lines[1].trim(), 10);
    const res = searchRange(nums, target);
    console.log(`[${res[0]}, ${res[1]}]`);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "5,7,7,8,8,10\n8", "[3, 4]", "Starting position 3, ending position 4."),
                        TestCaseDef.publicCase(2, "5,7,7,8,8,10\n6", "[-1, -1]", "Target 6 not found."),
                        TestCaseDef.hiddenCase(3, "\n0", "[-1, -1]", "Empty array.")
                )
        ));

        return map;
    }
}

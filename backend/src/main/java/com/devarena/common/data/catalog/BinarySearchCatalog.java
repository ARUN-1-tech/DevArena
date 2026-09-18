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
        // Write your solution here
        return 0;
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
    # Write your solution here
    pass

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
    // Write your solution here
    return null;
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
        // Write your solution here
        return 0;
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
    # Write your solution here
    pass

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
    // Write your solution here
    return null;
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
        System.out.println(findMin(nums));
    }
}
""",
                """
import sys

def find_min(nums: list[int]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(find_min(nums))
""",
                """
const fs = require('fs');

function findMin(nums) {
    // Write your solution here
    return null;
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
        // Write your solution here
        return 0.0;
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
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().splitlines()
    n1 = [int(x.strip()) for x in lines[0].split(",") if x.strip()] if len(lines) > 0 and lines[0].strip() else []
    n2 = [int(x.strip()) for x in lines[1].split(",") if x.strip()] if len(lines) > 1 and lines[1].strip() else []
    print(f"{find_median_sorted_arrays(n1, n2):.1f}")
""",
                """
const fs = require('fs');

function findMedianSortedArrays(nums1, nums2) {
    // Write your solution here
    return null;
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
        // Write your solution here
        return new int[]{};
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
    # Write your solution here
    pass

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
    // Write your solution here
    return null;
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

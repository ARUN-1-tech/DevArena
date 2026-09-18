package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeapCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Kth Largest Element in an Array
        map.put("kth-largest-element-in-an-array", new ChallengeProblemDef(
                "kth-largest-element-in-an-array",
                """
import java.util.*;

public class Solution {
    public static int findKthLargest(int[] nums, int k) {
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
        System.out.println(findKthLargest(nums, k));
    }
}
""",
                """
import sys
import heapq

def find_kth_largest(nums: list[int], k: int) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        k = int(lines[1].strip())
        print(find_kth_largest(nums, k))
""",
                """
const fs = require('fs');

function findKthLargest(nums, k) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const nums = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const k = parseInt(lines[1].trim(), 10);
    console.log(findKthLargest(nums, k));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "3,2,1,5,6,4\n2", "5", "2nd largest element is 5."),
                        TestCaseDef.publicCase(2, "3,2,3,1,2,4,5,5,6\n4", "4", "4th largest element is 4.")
                )
        ));

        // 2. Top K Frequent Elements
        map.put("top-k-frequent-elements", new ChallengeProblemDef(
                "top-k-frequent-elements",
                """
import java.util.*;

public class Solution {
    public static List<Integer> topKFrequent(int[] nums, int k) {
        // Write your solution here
        return new ArrayList<>();
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
        System.out.println(topKFrequent(nums, k));
    }
}
""",
                """
import sys
from collections import Counter

def top_k_frequent(nums: list[int], k: int) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        k = int(lines[1].strip())
        print(str(top_k_frequent(nums, k)))
""",
                """
const fs = require('fs');

function topKFrequent(nums, k) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const nums = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const k = parseInt(lines[1].trim(), 10);
    console.log(JSON.stringify(topKFrequent(nums, k)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,1,1,2,2,3\n2", "[1, 2]", "Top 2 most frequent numbers are 1 and 2."),
                        TestCaseDef.publicCase(2, "1\n1", "[1]", "Single element top 1 frequent.")
                )
        ));

        // 3. Task Scheduler
        map.put("task-scheduler", new ChallengeProblemDef(
                "task-scheduler",
                """
import java.util.*;

public class Solution {
    public static int leastInterval(char[] tasks, int n) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int n = Integer.parseInt(sc.nextLine().trim());
        String[] parts = line1.split(",");
        char[] tasks = new char[parts.length];
        for (int i = 0; i < parts.length; i++) tasks[i] = parts[i].trim().charAt(0);
        System.out.println(leastInterval(tasks, n));
    }
}
""",
                """
import sys
from collections import Counter

def least_interval(tasks: list[str], n: int) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        tasks = [x.strip() for x in lines[0].split(",") if x.strip()]
        n = int(lines[1].strip())
        print(least_interval(tasks, n))
""",
                """
const fs = require('fs');

function leastInterval(tasks, n) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const tasks = lines[0].split(',').map(s => s.trim());
    const n = parseInt(lines[1].trim(), 10);
    console.log(leastInterval(tasks, n));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "A,A,A,B,B,B\n2", "8", "A -> B -> idle -> A -> B -> idle -> A -> B (8 units)."),
                        TestCaseDef.publicCase(2, "A,A,A,B,B,B\n0", "6", "Zero cooling time takes length of tasks = 6."),
                        TestCaseDef.hiddenCase(3, "A,A,A,A,A,A,B,C,D,E,F,G\n2", "16", "Least intervals required is 16.")
                )
        ));

        // 4. K Closest Points to Origin
        map.put("k-closest-points-to-origin", new ChallengeProblemDef(
                "k-closest-points-to-origin",
                """
import java.util.*;

public class Solution {
    public static int[][] kClosest(int[][] points, int k) {
        // Write your solution here
        return new int[][]{};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int k = Integer.parseInt(sc.nextLine().trim());
        line1 = line1.substring(1, line1.length() - 1);
        String[] parts = line1.split("(?<=\\]),\\s*(?=\\[)");
        int[][] points = new int[parts.length][2];
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].replaceAll("[\\[\\]]", "").trim();
            String[] nums = p.split(",");
            points[i][0] = Integer.parseInt(nums[0].trim());
            points[i][1] = Integer.parseInt(nums[1].trim());
        }
        System.out.println(Arrays.deepToString(kClosest(points, k)));
    }
}
""",
                """
import sys
import json
import heapq

def k_closest(points: list[list[int]], k: int) -> list[list[int]]:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        points = json.loads(lines[0])
        k = int(lines[1].strip())
        print(str(k_closest(points, k)))
""",
                """
const fs = require('fs');

function kClosest(points, k) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const points = JSON.parse(lines[0]);
    const k = parseInt(lines[1].trim(), 10);
    console.log(JSON.stringify(kClosest(points, k)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "[[1,3],[-2,2]]\n1", "[[-2, 2]]", "(-2,2) has distance sqrt(8) closest to origin."),
                        TestCaseDef.publicCase(2, "[[3,3],[5,-1],[-2,4]]\n2", "[[3, 3], [-2, 4]]", "The 2 closest points are [3,3] and [-2,4].")
                )
        ));

        return map;
    }
}

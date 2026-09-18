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
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int val : nums) {
            pq.offer(val);
            if (pq.size() > k) pq.poll();
        }
        return pq.peek();
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
    return heapq.nlargest(k, nums)[-1]

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
    nums.sort((a, b) => b - a);
    return nums[k - 1];
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
        Map<Integer, Integer> count = new HashMap<>();
        for (int n : nums) count.put(n, count.getOrDefault(n, 0) + 1);
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(count::get));
        for (int n : count.keySet()) {
            pq.offer(n);
            if (pq.size() > k) pq.poll();
        }
        List<Integer> res = new ArrayList<>();
        while (!pq.isEmpty()) res.add(pq.poll());
        Collections.sort(res);
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
        System.out.println(topKFrequent(nums, k));
    }
}
""",
                """
import sys
from collections import Counter

def top_k_frequent(nums: list[int], k: int) -> list[int]:
    c = Counter(nums)
    return sorted([x[0] for x in c.most_common(k)])

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
    const map = new Map();
    for (const n of nums) map.set(n, (map.get(n) || 0) + 1);
    const sorted = Array.from(map.entries()).sort((a, b) => b[1] - a[1]);
    return sorted.slice(0, k).map(e => e[0]).sort((a, b) => a - b);
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
        int[] freq = new int[26];
        int max = 0, maxCount = 0;
        for (char task : tasks) {
            freq[task - 'A']++;
            if (freq[task - 'A'] > max) {
                max = freq[task - 'A'];
                maxCount = 1;
            } else if (freq[task - 'A'] == max) {
                maxCount++;
            }
        }
        int partCount = max - 1;
        int partLength = n - (maxCount - 1);
        int emptySlots = partCount * partLength;
        int availableTasks = tasks.length - max * maxCount;
        int idles = Math.max(0, emptySlots - availableTasks);
        return tasks.length + idles;
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
    counts = list(Counter(tasks).values())
    max_f = max(counts)
    max_f_count = counts.count(max_f)
    return max(len(tasks), (max_f - 1) * (n + 1) + max_f_count)

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
    const map = {};
    for (const t of tasks) map[t] = (map[t] || 0) + 1;
    const values = Object.values(map);
    const maxF = Math.max(...values);
    const maxCount = values.filter(v => v === maxF).length;
    return Math.max(tasks.length, (maxF - 1) * (n + 1) + maxCount);
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
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare((b[0]*b[0] + b[1]*b[1]), (a[0]*a[0] + a[1]*a[1])));
        for (int[] p : points) {
            pq.offer(p);
            if (pq.size() > k) pq.poll();
        }
        int[][] res = new int[k][2];
        while (k > 0) res[--k] = pq.poll();
        return res;
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
    return heapq.nsmallest(k, points, key=lambda p: p[0]**2 + p[1]**2)

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
    points.sort((a, b) => (a[0]**2 + a[1]**2) - (b[0]**2 + b[1]**2));
    return points.slice(0, k);
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

package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitMathGreedyCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Single Number
        map.put("single-number", new ChallengeProblemDef(
                "single-number",
                """
import java.util.*;

public class Solution {
    public static int singleNumber(int[] nums) {
        int res = 0;
        for (int n : nums) res ^= n;
        return res;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(singleNumber(nums));
    }
}
""",
                """
import sys

def single_number(nums: list[int]) -> int:
    res = 0
    for n in nums:
        res ^= n
    return res

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(single_number(nums))
""",
                """
const fs = require('fs');

function singleNumber(nums) {
    return nums.reduce((acc, n) => acc ^ n, 0);
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(singleNumber(nums));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2,2,1", "1", "1 appears once."),
                        TestCaseDef.publicCase(2, "4,1,2,1,2", "4", "4 appears once."),
                        TestCaseDef.hiddenCase(3, "1", "1", "Single element array.")
                )
        ));

        // 2. Number of 1 Bits
        map.put("number-of-1-bits", new ChallengeProblemDef(
                "number-of-1-bits",
                """
import java.util.*;

public class Solution {
    public static int hammingWeight(long n) {
        int count = 0;
        while (n != 0) {
            n &= (n - 1);
            count++;
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        long n = Long.parseLong(sc.nextLine().trim());
        System.out.println(hammingWeight(n));
    }
}
""",
                """
import sys

def hamming_weight(n: int) -> int:
    return bin(n).count("1")

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        print(hamming_weight(int(line)))
""",
                """
const fs = require('fs');

function hammingWeight(n) {
    return n.toString(2).split('').filter(c => c === '1').length;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    console.log(hammingWeight(BigInt(line)));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "11", "3", "Binary of 11 is 1011 which has 3 ones."),
                        TestCaseDef.publicCase(2, "128", "1", "Binary of 128 is 10000000 which has 1 one."),
                        TestCaseDef.hiddenCase(3, "2147483645", "30", "Has 30 ones.")
                )
        ));

        // 3. Counting Bits
        map.put("counting-bits", new ChallengeProblemDef(
                "counting-bits",
                """
import java.util.*;

public class Solution {
    public static int[] countBits(int n) {
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            dp[i] = dp[i >> 1] + (i & 1);
        }
        return dp;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        int n = Integer.parseInt(sc.nextLine().trim());
        System.out.println(Arrays.toString(countBits(n)));
    }
}
""",
                """
import sys

def count_bits(n: int) -> list[int]:
    dp = [0] * (n + 1)
    for i in range(1, n + 1):
        dp[i] = dp[i >> 1] + (i & 1)
    return dp

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        print(f"[{', '.join(map(str, count_bits(int(line))))}]")
""",
                """
const fs = require('fs');

function countBits(n) {
    const dp = new Array(n + 1).fill(0);
    for (let i = 1; i <= n; i++) {
        dp[i] = dp[i >> 1] + (i & 1);
    }
    return dp;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    console.log(`[${countBits(parseInt(line, 10)).join(', ')}]`);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2", "[0, 1, 1]", "Bit counts from 0 to 2."),
                        TestCaseDef.publicCase(2, "5", "[0, 1, 1, 2, 1, 2]", "Bit counts from 0 to 5."),
                        TestCaseDef.hiddenCase(3, "0", "[0]", "Zero bits.")
                )
        ));

        // 4. Missing Number
        map.put("missing-number", new ChallengeProblemDef(
                "missing-number",
                """
import java.util.*;

public class Solution {
    public static int missingNumber(int[] nums) {
        int n = nums.length;
        int expected = n * (n + 1) / 2;
        int actual = 0;
        for (int x : nums) actual += x;
        return expected - actual;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(missingNumber(nums));
    }
}
""",
                """
import sys

def missing_number(nums: list[int]) -> int:
    n = len(nums)
    return n * (n + 1) // 2 - sum(nums)

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(missing_number(nums))
""",
                """
const fs = require('fs');

function missingNumber(nums) {
    const n = nums.length;
    const expected = (n * (n + 1)) / 2;
    const actual = nums.reduce((a, b) => a + b, 0);
    return expected - actual;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(missingNumber(nums));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "3,0,1", "2", "Array length 3, numbers in range [0,3], missing 2."),
                        TestCaseDef.publicCase(2, "0,1", "2", "Missing 2."),
                        TestCaseDef.hiddenCase(3, "9,6,4,2,3,5,7,0,1", "8", "Missing 8 in range [0, 9].")
                )
        ));

        // 5. Non-overlapping Intervals
        map.put("non-overlapping-intervals", new ChallengeProblemDef(
                "non-overlapping-intervals",
                """
import java.util.*;

public class Solution {
    public static int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length == 0) return 0;
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
        int count = 0;
        int end = intervals[0][1];
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < end) {
                count++;
            } else {
                end = intervals[i][1];
            }
        }
        return count;
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
        System.out.println(eraseOverlapIntervals(intervals));
    }
}
""",
                """
import sys
import json

def erase_overlap_intervals(intervals: list[list[int]]) -> int:
    if not intervals:
        return 0
    intervals.sort(key=lambda x: x[1])
    count = 0
    end = intervals[0][1]
    for i in range(1, len(intervals)):
        if intervals[i][0] < end:
            count += 1
        else:
            end = intervals[i][1]
    return count

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        intervals = json.loads(line)
        print(erase_overlap_intervals(intervals))
""",
                """
const fs = require('fs');

function eraseOverlapIntervals(intervals) {
    if (!intervals.length) return 0;
    intervals.sort((a, b) => a[1] - b[1]);
    let count = 0;
    let end = intervals[0][1];
    for (let i = 1; i < intervals.length; i++) {
        if (intervals[i][0] < end) {
            count++;
        } else {
            end = intervals[i][1];
        }
    }
    return count;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const intervals = JSON.parse(line);
    console.log(eraseOverlapIntervals(intervals));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "[[1,2],[2,3],[3,4],[1,3]]", "1", "Remove [1,3] to make the rest non-overlapping."),
                        TestCaseDef.publicCase(2, "[[1,2],[1,2],[1,2]]", "2", "Remove two [1,2]s."),
                        TestCaseDef.hiddenCase(3, "[[1,2],[2,3]]", "0", "Already non-overlapping.")
                )
        ));

        // 6. Gas Station
        map.put("gas-station", new ChallengeProblemDef(
                "gas-station",
                """
import java.util.*;

public class Solution {
    public static int canCompleteCircuit(int[] gas, int[] cost) {
        int totalGas = 0, totalCost = 0;
        int tank = 0, start = 0;
        for (int i = 0; i < gas.length; i++) {
            totalGas += gas[i];
            totalCost += cost[i];
            tank += gas[i] - cost[i];
            if (tank < 0) {
                start = i + 1;
                tank = 0;
            }
        }
        return totalGas >= totalCost ? start : -1;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String line2 = sc.nextLine().trim();
        int[] gas = Arrays.stream(line1.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        int[] cost = Arrays.stream(line2.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        System.out.println(canCompleteCircuit(gas, cost));
    }
}
""",
                """
import sys

def can_complete_circuit(gas: list[int], cost: list[int]) -> int:
    if sum(gas) < sum(cost):
        return -1
    total, start = 0, 0
    for i in range(len(gas)):
        total += gas[i] - cost[i]
        if total < 0:
            total = 0
            start = i + 1
    return start

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        g = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        c = [int(x.strip()) for x in lines[1].split(",") if x.strip()]
        print(can_complete_circuit(g, c))
""",
                """
const fs = require('fs');

function canCompleteCircuit(gas, cost) {
    if (gas.reduce((a, b) => a + b, 0) < cost.reduce((a, b) => a + b, 0)) return -1;
    let total = 0, start = 0;
    for (let i = 0; i < gas.length; i++) {
        total += gas[i] - cost[i];
        if (total < 0) {
            total = 0;
            start = i + 1;
        }
    }
    return start;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const g = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const c = lines[1].split(',').map(s => parseInt(s.trim(), 10));
    console.log(canCompleteCircuit(g, c));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3,4,5\n3,4,5,1,2", "3", "Starting at index 3 allows completing full circuit."),
                        TestCaseDef.publicCase(2, "2,3,4\n3,4,3", "-1", "Cannot complete circuit.")
                )
        ));

        return map;
    }
}

package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BacktrackingCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Subsets
        map.put("subsets", new ChallengeProblemDef(
                "subsets",
                """
import java.util.*;

public class Solution {
    public static List<List<Integer>> subsets(int[] nums) {
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
        System.out.println(subsets(nums));
    }
}
""",
                """
import sys

def subsets(nums: list[int]) -> list[list[int]]:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(subsets(nums)))
""",
                """
const fs = require('fs');

function subsets(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(JSON.stringify(subsets(nums)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3", "[[], [1], [1, 2], [1, 2, 3], [1, 3], [2], [2, 3], [3]]", "All power set subsets."),
                        TestCaseDef.publicCase(2, "0", "[[], [0]]", "Power set of single element.")
                )
        ));

        // 2. Permutations
        map.put("permutations", new ChallengeProblemDef(
                "permutations",
                """
import java.util.*;

public class Solution {
    public static List<List<Integer>> permute(int[] nums) {
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
        System.out.println(permute(nums));
    }
}
""",
                """
import sys

def permute(nums: list[int]) -> list[list[int]]:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(permute(nums)))
""",
                """
const fs = require('fs');

function permute(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(JSON.stringify(permute(nums)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3", "[[1, 2, 3], [1, 3, 2], [2, 1, 3], [2, 3, 1], [3, 1, 2], [3, 2, 1]]", "All 6 permutations."),
                        TestCaseDef.publicCase(2, "0,1", "[[0, 1], [1, 0]]", "2 permutations."),
                        TestCaseDef.hiddenCase(3, "1", "[[1]]", "Single element permutation.")
                )
        ));

        // 3. Combination Sum
        map.put("combination-sum", new ChallengeProblemDef(
                "combination-sum",
                """
import java.util.*;

public class Solution {
    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        // Write your solution here
        return new ArrayList<>();
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
        System.out.println(combinationSum(nums, target));
    }
}
""",
                """
import sys

def combination_sum(candidates: list[int], target: int) -> list[list[int]]:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        c = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        t = int(lines[1].strip())
        print(str(combination_sum(c, t)))
""",
                """
const fs = require('fs');

function combinationSum(candidates, target) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const c = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const t = parseInt(lines[1].trim(), 10);
    console.log(JSON.stringify(combinationSum(c, t)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2,3,6,7\n7", "[[2, 2, 3], [7]]", "Combinations summing to 7."),
                        TestCaseDef.publicCase(2, "2,3,5\n8", "[[2, 2, 2, 2], [2, 3, 3], [3, 5]]", "Combinations summing to 8."),
                        TestCaseDef.hiddenCase(3, "2\n1", "[]", "No combination reaches target 1.")
                )
        ));

        // 4. Letter Combinations of a Phone Number
        map.put("letter-combinations-of-a-phone-number", new ChallengeProblemDef(
                "letter-combinations-of-a-phone-number",
                """
import java.util.*;

public class Solution {
    private static final String[] MAPPINGS = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

    public static List<String> letterCombinations(String digits) {
        // Write your solution here
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine().trim() : "";
        System.out.println(letterCombinations(s));
    }
}
""",
                """
import sys

MAPPINGS = ["", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"]

def letter_combinations(digits: str) -> list[str]:
    # Write your solution here
    pass

if __name__ == "__main__":
    s = sys.stdin.read().strip()
    res = letter_combinations(s)
    print(str(res).replace("'", '"'))
""",
                """
const fs = require('fs');

const MAPPINGS = ["", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"];

function letterCombinations(digits) {
    // Write your solution here
    return null;
}

const s = fs.readFileSync(0, 'utf-8').trim();
console.log(JSON.stringify(letterCombinations(s)).replace(/,/g, ', '));
""",
                List.of(
                        TestCaseDef.publicCase(1, "23", "[\"ad\", \"ae\", \"af\", \"bd\", \"be\", \"bf\", \"cd\", \"ce\", \"cf\"]", "Phone keypad letter combinations."),
                        TestCaseDef.publicCase(2, "", "[]", "Empty digits returns []."),
                        TestCaseDef.hiddenCase(3, "2", "[\"a\", \"b\", \"c\"]", "Single digit returns its mapped characters.")
                )
        ));

        // 5. Word Search
        map.put("word-search", new ChallengeProblemDef(
                "word-search",
                """
import java.util.*;

public class Solution {
    public static boolean exist(char[][] board, String word) {
        // Write your solution here
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String word = sc.nextLine().trim();
        String[] rows = line1.split(",");
        char[][] board = new char[rows.length][rows[0].length()];
        for (int i = 0; i < rows.length; i++) board[i] = rows[i].toCharArray();
        System.out.println(exist(board, word));
    }
}
""",
                """
import sys

def exist(board: list[list[str]], word: str) -> bool:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        rows = [list(r) for r in lines[0].strip().split(",")]
        word = lines[1].strip()
        print(str(exist(rows, word)).lower())
""",
                """
const fs = require('fs');

function exist(board, word) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const board = lines[0].trim().split(',').map(r => r.split(''));
    const word = lines[1].trim();
    console.log(exist(board, word));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "ABCE,SFCS,ADEE\nABCCED", "true", "Word 'ABCCED' found in board."),
                        TestCaseDef.publicCase(2, "ABCE,SFCS,ADEE\nSEE", "true", "Word 'SEE' found in board."),
                        TestCaseDef.hiddenCase(3, "ABCE,SFCS,ADEE\nABCB", "false", "Word 'ABCB' cannot reuse letters.")
                )
        ));

        return map;
    }
}
